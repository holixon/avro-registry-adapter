package io.holixon.avro.adapter.registry.apicurio.client

import io.apicurio.registry.rest.client.RegistryClient
import io.apicurio.registry.rest.v2.beans.ArtifactMetaData
import io.apicurio.registry.rest.v2.beans.EditableMetaData
import io.apicurio.registry.rest.v2.beans.IfExists
import io.apicurio.registry.rest.v2.beans.SearchedArtifact
import io.apicurio.registry.types.ArtifactType
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.SchemaRevisionResolver
import io.holixon.avro.adapter.api.ext.FunctionalExt.invoke
import io.holixon.avro.adapter.api.ext.SchemaExt.byteContent
import io.holixon.avro.adapter.api.ext.SchemaExt.schema
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.DEFAULT_GROUP
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.PropertyKey
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.properties
import io.holixon.avro.adapter.registry.apicurio.type.ApicurioArtifactMetaData
import io.holixon.avro.adapter.registry.apicurio.type.ApicurioSchemaData
import mu.KLogging
import org.apache.avro.Schema

/**
 * Encapsulates calls to apicurio [RegistryClient] with error handling.
 */
class GroupAwareRegistryClient
@JvmOverloads
constructor(
  /**
   * The apicurio [RegistryClient].
   */
  private val client: RegistryClient,
  private val schemaIdSupplier: SchemaIdSupplier,
  private val schemaRevisionResolver: SchemaRevisionResolver,
  private val group: String = DEFAULT_GROUP
) {
  companion object : KLogging()

  /**
   * Returns [RegistryClient.getLatestArtifact] input stream converted to [Schema] if found.
   */
  fun findSchemaById(id: String): Result<Schema> = client.runCatching {
    getLatestArtifact(group, id)
  }
    .recoverCatching { ex ->
      val meta = findAllMetaData().getOrThrow().map { it to it.properties[PropertyKey.SCHEMA_ID] }.filterNot { it.second == null }
        .find { it.second == id }?.first ?: throw IllegalArgumentException("no schema found for schemaId= $id")
      client.getLatestArtifact(group, meta.id)
    }.mapCatching { it.schema() }


  /**
   * Returns [RegistryClient.listArtifactsInGroup]. Defaults to empty list.
   */
  fun findAllArtifacts(filter: (SearchedArtifact) -> Boolean = { true }): Result<List<SearchedArtifact>> = client.runCatching {
    listArtifactsInGroup(group)
  }.map { it.artifacts.filter(filter) }

  fun findAllMetaData(filter: (ApicurioArtifactMetaData) -> Boolean = { true }): Result<List<ApicurioArtifactMetaData>> =
    client.runCatching {
      val artifacts = this@GroupAwareRegistryClient.findAllArtifacts().getOrThrow()

      artifacts.mapNotNull { this@GroupAwareRegistryClient.findArtifactMetaData(it.id).getOrNull() }
        .filter { it.type == ArtifactType.AVRO }
        .filter(filter)
    }

  /**
   * Returns [RegistryClient.getArtifactMetaData] if found.
   */
  fun findArtifactMetaData(artifactId: String): Result<ApicurioArtifactMetaData> = client.runCatching {
    getArtifactMetaData(group, artifactId)
  }.map { ApicurioArtifactMetaData(it) }

  fun updateAllNotInitializedArtifactMetaData(): List<ApicurioSchemaData> {
    val uninitializedMeta: List<ApicurioArtifactMetaData> = findAllMetaData { !it.isInitialized }.getOrThrow()

    return uninitializedMeta.mapNotNull { updateArtifactMetaData(it.id).getOrNull() }
  }

  fun updateArtifactMetaData(apicurioArtifactId: String): Result<ApicurioSchemaData> = client.runCatching {
    val schema = this@GroupAwareRegistryClient.findSchemaById(apicurioArtifactId).getOrThrow()
    val updatedMeta = this@GroupAwareRegistryClient.updateArtifactMetaData(apicurioArtifactId, schema).getOrThrow()
    ApicurioSchemaData(
      apicurioArtifactId = apicurioArtifactId,
      schema = schema,
      metaData = updatedMeta
    )
  }

  /**
   * Update metaData for given artifact with avro adapter values.
   */
  fun updateArtifactMetaData(apicurioArtifactId: String, schema: Schema): Result<ApicurioArtifactMetaData> = client.runCatching {
    val data = AvroSchemaWithIdData(
      schemaId = schemaIdSupplier.apply(schema),
      schema = schema,
      revision = schemaRevisionResolver.apply(schema).orElse(null)
    )

    val editableMetaData = EditableMetaData().apply {
      name = schema.name
      description = schema.doc
      properties = data.properties()
    }

    updateArtifactMetaData(group, apicurioArtifactId, editableMetaData)
    val updatedMeta: Result<ApicurioArtifactMetaData> = findArtifactMetaData(apicurioArtifactId)

    findArtifactMetaData(apicurioArtifactId).getOrThrow()
  }

  /**
   * Register new schema using [RegistryClient.createArtifact].
   */
  fun registerSchema(schema: Schema): Result<ApicurioSchemaData> =
    client.runCatching {
      val schemaId = schemaIdSupplier.invoke(schema)

      val created: ArtifactMetaData = createArtifact(
        group,
        schemaId,
        ArtifactType.AVRO,
        IfExists.RETURN_OR_UPDATE,
        schema.byteContent()
      )

      val updated = this@GroupAwareRegistryClient.updateArtifactMetaData(created.id, schema).getOrThrow()

      ApicurioSchemaData(metaData = updated, schema = schema).also {
        logger.info { "registered: $it" }
      }
    }
}
