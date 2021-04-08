package io.toolisticon.avro.adapter.apicurio

import io.apicurio.registry.client.RegistryRestClient
import io.apicurio.registry.rest.beans.ArtifactMetaData
import io.apicurio.registry.rest.beans.EditableMetaData
import io.apicurio.registry.rest.beans.IfExistsType
import io.apicurio.registry.types.ArtifactType
import io.toolisticon.avro.adapter.api.*
import io.toolisticon.avro.adapter.api.AvroAdapterApi.byteContent
import io.toolisticon.avro.adapter.api.type.AvroSchemaWithIdData
import io.toolisticon.avro.adapter.apicurio.AvroAdapterApicurioRest.description
import org.apache.avro.Schema
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.*

class ApicurioAvroSchemaRegistry(
  private val client: RegistryRestClient,
  private val schemaIdSupplier: SchemaIdSupplier,
  private val schemaRevisionResolver: SchemaRevisionResolver
) : AvroSchemaRegistry {
  companion object {
    val UTF_8 = Charsets.UTF_8

    const val KEY_REVISION = "revision"
    const val KEY_CONTEXT = "context"

  }

  private val logger = LoggerFactory.getLogger(ApicurioAvroSchemaRegistry::class.java)

  override fun register(schema: Schema): AvroSchemaWithId {
    val content = schema.byteContent()
    val schemaId = schemaIdSupplier.apply(schema)
    val revision = schemaRevisionResolver.apply(schema).orElse(null)

    val metaData: ArtifactMetaData = client.createArtifact(schemaId, ArtifactType.AVRO, IfExistsType.RETURN_OR_UPDATE, content)

    client.updateArtifactMetaData(schemaId, EditableMetaData().apply {
      description = schema.description()
      properties = mapOf(
        KEY_CONTEXT to schema.namespace,
        KEY_REVISION to revision
      )
    })

    logger.info("meta date: ${client.getArtifactMetaData(schemaId)}")

    return AvroSchemaWithIdData(
      id = schemaId,
      schema = schema,
      revision = revision
    )
  }

  override fun findById(schemaId: SchemaId): Optional<AvroSchemaWithId> {
    val schema = client.getLatestArtifact(schemaId).schema()
    return Optional.of(AvroSchemaWithIdData(id = schemaId, schema = schema, revision = schemaRevisionResolver.apply(schema).orElse(null)))
  }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> {
    return findByContextAndName(info.context, info.name).singleOrNull { info.revision == it.revision }
      .let { Optional.ofNullable(it) }

  }

  override fun findByContextAndName(context: String, name: String): List<AvroSchemaWithId> {
    return client.listArtifacts()
      .asSequence()
      .map { client.getArtifactMetaData(it) }
      .filter { it.name == name && it.properties[KEY_CONTEXT] == context }
      .map { findById(it.id) }
      .filter { it.isPresent }
      .map { it.get() }
      .toList()
  }

  override fun findAll(): List<AvroSchemaWithId> {
    val artifactIds = client.listArtifacts()
    return artifactIds.map { findById(it) }.filter { it.isPresent }.map { it.get() }
  }

  private fun InputStream.schema(): Schema = this.bufferedReader(UTF_8).use {
    val text = it.readText()
    Schema.Parser().parse(text)
  }

}
