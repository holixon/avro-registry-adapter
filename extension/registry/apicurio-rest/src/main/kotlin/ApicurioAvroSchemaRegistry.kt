package io.holixon.avro.adapter.registry.apicurio

import io.apicurio.registry.rest.client.RegistryClient
import io.holixon.avro.adapter.api.*
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.adapter.registry.apicurio.client.GroupAwareRegistryClient
import mu.KLogging
import org.apache.avro.Schema
import java.util.*

/**
 * Implementation of the registry using Apicurio.
 */
open class ApicurioAvroSchemaRegistry(
  private val client: GroupAwareRegistryClient,
  private val schemaIdSupplier: SchemaIdSupplier,
  private val schemaRevisionResolver: SchemaRevisionResolver
) : AvroSchemaRegistry {
  companion object : KLogging()

  constructor(
    client: RegistryClient,
    group: String,
    schemaIdSupplier: SchemaIdSupplier,
    schemaRevisionResolver: SchemaRevisionResolver
  ) : this(
    client = GroupAwareRegistryClient(client, schemaIdSupplier, schemaRevisionResolver, group),
    schemaIdSupplier = schemaIdSupplier,
    schemaRevisionResolver = schemaRevisionResolver
  )

  override fun register(schema: Schema): AvroSchemaWithId {
    return client.registerSchema(schema).getOrThrow()
  }

  override fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> {
    fun findAndUpdateMetaDataIfNotFound(schemaId: AvroSchemaId, recover: Boolean = true): Result<AvroSchemaWithId> {
      val result = client.findSchemaById(schemaId)
        .map {
          AvroSchemaWithIdData(
            schemaId = schemaIdSupplier.apply(it),
            schema = it,
            revision = schemaRevisionResolver.apply(it).orElse(null)
          )
        }

      if (result.isFailure && recover) {
        client.updateAllNotInitializedArtifactMetaData()
        return findAndUpdateMetaDataIfNotFound(schemaId, !recover)
      }

      return result
    }

    return findAndUpdateMetaDataIfNotFound(schemaId).map { Optional.of(it) }.getOrDefault(Optional.empty())
  }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> {
    return findAllByCanonicalName(info.namespace, info.name).singleOrNull { info.revision == it.revision }
      .let { Optional.ofNullable(it) }
  }

  override fun findAllByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId> {
    return client.findAllArtifacts().getOrDefault(emptyList())
      .asSequence()
      .filter { it.name == name }
      .map { client.findArtifactMetaData(it.id).onFailure { throw it }.getOrNull()!! }
      .filter { it.namespaceProperty ?: "" == namespace }
      .map { findById(it.id) }
      .filter { it.isPresent }
      .map { it.get() }
      .toList()
  }

  override fun findAll(): List<AvroSchemaWithId> = client.findAllArtifacts()
    .getOrDefault(emptyList())
    .map { findById(it.id) }
    .filter { it.isPresent }
    .map { it.get() }

}
