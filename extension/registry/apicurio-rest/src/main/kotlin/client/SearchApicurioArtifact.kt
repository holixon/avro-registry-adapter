package io.holixon.avro.adapter.registry.apicurio.client

import io.apicurio.registry.rest.client.RegistryClient
import io.holixon.avro.adapter.api.AvroSchemaFqn
import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.SchemaRevisionResolver
import io.holixon.avro.adapter.api.ext.FunctionalExt.invoke
import io.holixon.avro.adapter.api.ext.SchemaExt.schema
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.adapter.registry.apicurio.ArtifactId
import io.holixon.avro.adapter.registry.apicurio.GroupId
import io.holixon.avro.adapter.registry.apicurio.client.RegistryClientExt.queryArtifacts
import io.holixon.avro.adapter.registry.apicurio.type.ApicurioSearchedArtifact
import io.holixon.avro.adapter.registry.apicurio.type.apicurioSearchedArtifact
import java.util.*

/**
 * The `read` part of the [RegistryClient], abstracted for use with kotlin and avro.
 */
interface SearchApicurioArtifact {

  /**
   * @return all groupIds (aka namespaces) of all registered avro schemas.
   */
  fun findDistinctGroupIds(): Set<GroupId>

  /**
   * @return all registered avro schema artifacts with metaData.
   */
  fun findAll(): List<ApicurioSearchedArtifact>

  /**
   * @param groupId the groupId (aka namespace) of the schema
   * @param artifactId the artifactId (aka name) of the schema
   * @return list of all matching versions for the given fqn, might be empty.
   */
  fun findAllByGroupIdAndArtifactId(groupId: GroupId, artifactId: ArtifactId): List<ApicurioSearchedArtifact>

  /**
   * @param schemaId the schemaId (aka fingerprint)
   * @return the found schema artifact (or empty)
   */
  fun findBySchemaId(schemaId: AvroSchemaId): Optional<ApicurioSearchedArtifact>

  /**
   * @param schemaId the schemaId (aka fingerprint)
   * @return the adapter-api schema wrapper, or empty
   */
  fun findSchemaBySchemaId(schemaId: AvroSchemaId): Optional<AvroSchemaWithId>

  /**
   * @param searchedArtifact a search result found by another query
   * @return the adapter-api schema wrapper for the given search result (contains schema)
   */
  fun findSchemaBySearchedArtifact(searchedArtifact: ApicurioSearchedArtifact): AvroSchemaWithId

}

/**
 * Default implementation of [SearchApicurioArtifact].
 */
open class DefaultSearchApicurioArtifact(
  private val registryClient: RegistryClient,
  private val schemaRevisionResolver: SchemaRevisionResolver
) : SearchApicurioArtifact {

  override fun findDistinctGroupIds(): Set<GroupId> = registryClient.queryArtifacts().map {
    it.map { it.groupId }.toSet()
  }.getOrThrow()

  override fun findAll(): List<ApicurioSearchedArtifact> = findDistinctGroupIds().flatMap { groupId ->
    registryClient.listArtifactsInGroup(groupId).artifacts
  }.flatMap { findAllByGroupIdAndArtifactId(it.groupId, it.id) }

  override fun findAllByGroupIdAndArtifactId(groupId: GroupId, artifactId: ArtifactId): List<ApicurioSearchedArtifact> =
    registryClient.runCatching {
      listArtifactVersions(groupId, artifactId, null, null).versions
    }.getOrDefault(listOf())
      .map {
        apicurioSearchedArtifact(groupId, artifactId, it)
      }


  override fun findBySchemaId(schemaId: AvroSchemaId): Optional<ApicurioSearchedArtifact> = Optional.ofNullable(findAll()
    .firstOrNull { schemaId == it.avroSchemaId })

  override fun findSchemaBySchemaId(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> = findBySchemaId(schemaId)
    .map { findSchemaBySearchedArtifact(it) }

  override fun findSchemaBySearchedArtifact(searchedArtifact: ApicurioSearchedArtifact): AvroSchemaWithId {
    val schema = registryClient.getContentByGlobalId(searchedArtifact.globalId).schema()

    return AvroSchemaWithIdData(
      schemaId = searchedArtifact.avroSchemaId ?: throw java.lang.IllegalArgumentException("schemaId is null!"),
      schema = schema,
      revision = schemaRevisionResolver.invoke(schema).orElse(null),
      namespace = schema.namespace,
      name = schema.name
    )
  }
}

fun SearchApicurioArtifact.findAllByArtifactFqn(fqn: AvroSchemaFqn): List<ApicurioSearchedArtifact> =
  findAllByGroupIdAndArtifactId(fqn.namespace, fqn.name)
