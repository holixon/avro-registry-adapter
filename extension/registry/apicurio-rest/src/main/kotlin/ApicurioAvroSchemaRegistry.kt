package io.holixon.avro.adapter.registry.apicurio

import io.apicurio.registry.rest.client.RegistryClient
import io.apicurio.registry.rest.v2.beans.ArtifactMetaData
import io.apicurio.registry.rest.v2.beans.EditableMetaData
import io.apicurio.registry.rest.v2.beans.IfExists
import io.apicurio.registry.types.ArtifactType
import io.holixon.avro.adapter.api.*
import io.holixon.avro.adapter.api.AvroAdapterApi.byteContent
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.description
import org.apache.avro.Schema
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.*

/**
 * Implementation of the registry using Apicurio.
 */
class ApicurioAvroSchemaRegistry(
  private val client: RegistryClient,
  private val schemaIdSupplier: SchemaIdSupplier,
  private val schemaRevisionResolver: SchemaRevisionResolver
) : AvroSchemaRegistry {
  companion object {
    const val KEY_NAME = "name"
    const val KEY_NAMESPACE = "namespace"
    const val KEY_CANONICAL_NAME = "canonicalName"
    const val KEY_REVISION = "revision"
    const val DEFAULT_GROUP = "default" // TODO: should this be configurable?
  }

  private val logger = LoggerFactory.getLogger(ApicurioAvroSchemaRegistry::class.java)

  override fun register(schema: Schema): AvroSchemaWithId {
    val content = schema.byteContent()
    val schemaId = schemaIdSupplier.apply(schema)
    val revision = schemaRevisionResolver.apply(schema).orElse(null)


    val metaData: ArtifactMetaData = client.createArtifact(DEFAULT_GROUP, schemaId, ArtifactType.AVRO, IfExists.RETURN_OR_UPDATE, content)
    logger.trace("Registered schema and received the following metadata: $metaData")

    client.updateArtifactMetaData(DEFAULT_GROUP, schemaId, EditableMetaData().apply {
      name = schema.name
      description = schema.description()
      properties = mapOf(
        KEY_NAME to schema.name,
        KEY_NAMESPACE to schema.namespace,
        KEY_CANONICAL_NAME to schema.fullName,
        KEY_REVISION to revision
      )
    })

    logger.info("meta date: ${client.getArtifactMetaData(DEFAULT_GROUP, schemaId)}")

    return AvroSchemaWithIdData(
      schemaId = schemaId,
      schema = schema,
      revision = revision
    )
  }

  override fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> {

    val schema = client.getLatestArtifact(DEFAULT_GROUP, schemaId).schema()
    return Optional.of(
      AvroSchemaWithIdData(
        schemaId = schemaId,
        schema = schema,
        revision = schemaRevisionResolver.apply(schema).orElse(null)
      )
    )
  }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> {
    return findAllByCanonicalName(info.namespace, info.name).singleOrNull { info.revision == it.revision }
      .let { Optional.ofNullable(it) }
  }

  override fun findAllByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId> {
    return client.listArtifactsInGroup(DEFAULT_GROUP).artifacts
      .asSequence()
      .filter { it.name == name }
      .map { client.getArtifactMetaData(DEFAULT_GROUP, it.id) }
      .filter { it.namespace().orElse("") == namespace }
      .map { findById(it.id) }
      .filter { it.isPresent }
      .map { it.get() }
      .toList()
  }

  override fun findAll(): List<AvroSchemaWithId> {
    val artifactIds = client.listArtifactsInGroup(DEFAULT_GROUP).artifacts
    return artifactIds.map { findById(it.id) }.filter { it.isPresent }.map { it.get() }
  }

  private fun InputStream.schema(): Schema = this.bufferedReader(Charsets.UTF_8).use {
    val text = it.readText()
    Schema.Parser().parse(text)
  }

  private fun ArtifactMetaData.revision(): AvroSchemaRevision? = this.properties[KEY_REVISION]
  private fun ArtifactMetaData.namespace(): Optional<String> = Optional.ofNullable(this.properties[KEY_NAMESPACE])
}
