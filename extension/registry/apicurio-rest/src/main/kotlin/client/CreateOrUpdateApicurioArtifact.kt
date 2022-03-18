package io.holixon.avro.adapter.registry.apicurio.client

import io.apicurio.registry.rest.client.RegistryClient
import io.apicurio.registry.rest.v2.beans.ArtifactMetaData
import io.apicurio.registry.rest.v2.beans.EditableMetaData
import io.apicurio.registry.rest.v2.beans.IfExists
import io.apicurio.registry.types.ArtifactType
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.ext.FunctionalExt.invoke
import io.holixon.avro.adapter.api.ext.SchemaExt.avroSchemaFqn
import io.holixon.avro.adapter.api.ext.SchemaExt.byteContent
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.description
import io.holixon.avro.adapter.registry.apicurio.type.ApicurioArtifactWithMetaData
import org.apache.avro.Schema


/**
 * Registers a schema file in the apicurio registry.
 *
 * The caller does not need to decide if it is a new schema or an update to an existing schema
 * (assuming that a schema is identified by its FQN (namespace + name)).
 */
interface CreateOrUpdateArtifact {

  /**
   * Upload given schema.
   *
   * @param the avro schema content
   * @return the uploaded schema with relevant meta
   */
  fun upload(schema:Schema) : ApicurioArtifactWithMetaData

}

/**
 * Default implementation of [CreateOrUpdateArtifact].
 */
open class DefaultCreateOrUpdateArtifact(
  private val registryClient: RegistryClient,
  private val schemaIdSupplier: SchemaIdSupplier
) : CreateOrUpdateArtifact {

  override fun upload(schema: Schema): ApicurioArtifactWithMetaData  = registryClient.runCatching {
    val schemaId = schemaIdSupplier(schema)
    val fqn = schema.avroSchemaFqn()

    val created: ArtifactMetaData = createArtifact(
      fqn.namespace,
      fqn.name,
      ArtifactType.AVRO,
      IfExists.UPDATE,
      schema.byteContent()
    )

    updateArtifactVersionMetaData(fqn.namespace, fqn.name, created.version, EditableMetaData().apply {
      name = fqn.name
      description = schema.description()
      properties = (created.properties ?: mutableMapOf()).apply {
        put(AvroAdapterApicurioRest.PropertyKey.SCHEMA_ID, schemaId)
      }
      labels = created.labels
    })

    ApicurioArtifactWithMetaData(
      globalId = created.globalId,
      contentId = created.contentId,
      groupId = created.groupId,
      name = created.name,
      version = created.version,
      schemaId = schemaId,
      schema = schema
    )
  }.getOrThrow()

}
