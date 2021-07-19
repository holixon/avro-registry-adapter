package io.holixon.avro.adapter.registry.apicurio.client

import io.apicurio.registry.rest.client.RegistryClient
import io.apicurio.registry.rest.v2.beans.EditableMetaData
import io.apicurio.registry.rest.v2.beans.IfExists
import io.apicurio.registry.rest.v2.beans.SearchedArtifact
import io.apicurio.registry.types.ArtifactType
import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaRevision
import io.holixon.avro.adapter.api.ext.SchemaExt.byteContent
import io.holixon.avro.adapter.api.ext.SchemaExt.schema
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry.Companion.KEY_CANONICAL_NAME
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry.Companion.KEY_NAME
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry.Companion.KEY_NAMESPACE
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry.Companion.KEY_REVISION
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.description
import io.holixon.avro.adapter.registry.apicurio.type.ApicurioArtifactMetaData
import mu.KLogging
import org.apache.avro.Schema

/**
 * Encapsulates calls to apicurio [RegistryClient] with error handling.
 */
class GroupAwareRegistryClient(
  private val client: RegistryClient,
  private val group: String
) {
  companion object : KLogging()

  /**
   * Returns [RegistryClient.getLatestArtifact] input stream converted to [Schema] if found.
   */
  fun findSchemaById(id: String): Result<Schema> = client.runCatching {
    getLatestArtifact(group, id)
  }.mapCatching { it.schema() }

  /**
   * Returns [RegistryClient.listArtifactsInGroup]. Defaults to empty list.
   */
  fun findAllArtifacts(): Result<List<SearchedArtifact>> = client.runCatching {
    listArtifactsInGroup(group)
  }.map { it.artifacts }

  /**
   * Returns [RegistryClient.getArtifactMetaData] if found.
   */
  fun findArtifactMetaData(id: String): Result<ApicurioArtifactMetaData> = client.runCatching {
    getArtifactMetaData(group, id)
  }.map { ApicurioArtifactMetaData(it) }

  /**
   * Register new schema using [RegistryClient.createArtifact] and [RegistryClient.updateArtifactMetaData].
   */
  fun registerSchema(schema: Schema, schemaId: AvroSchemaId, revision: AvroSchemaRevision?): Result<ApicurioArtifactMetaData> =
    client.runCatching {
      val metaData: ApicurioArtifactMetaData = ApicurioArtifactMetaData(
        createArtifact(group, schemaId, ArtifactType.AVRO, IfExists.RETURN_OR_UPDATE, schema.byteContent())
      )
      logger.info { "Registered schema and received the following metadata: $metaData" }

      updateArtifactMetaData(group, schemaId, EditableMetaData().apply {
        name = schema.name
        description = schema.description()
        properties = mapOf(
          KEY_NAME to schema.name,
          KEY_NAMESPACE to schema.namespace,
          KEY_CANONICAL_NAME to schema.fullName,
          KEY_REVISION to revision
        )
      })

      findArtifactMetaData(requireNotNull(metaData.id)).getOrThrow()
    }
}
