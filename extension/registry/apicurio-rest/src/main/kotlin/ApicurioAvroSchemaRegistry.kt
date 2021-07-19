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
  companion object : KLogging() {
    const val KEY_NAME = "name"
    const val KEY_NAMESPACE = "namespace"
    const val KEY_CANONICAL_NAME = "canonicalName"
    const val KEY_REVISION = "revision"
  }

  constructor(
    client: RegistryClient,
    group: String,
    schemaIdSupplier: SchemaIdSupplier,
    schemaRevisionResolver: SchemaRevisionResolver
  ) : this(
    GroupAwareRegistryClient(client, group),
    schemaIdSupplier,
    schemaRevisionResolver
  )

  override fun register(schema: Schema): AvroSchemaWithId {
    val schemaId = schemaIdSupplier.apply(schema)
    val revision = schemaRevisionResolver.apply(schema).orElse(null)

    client.registerSchema(schema, schemaId, revision).onFailure {
      throw it
    }

    return AvroSchemaWithIdData(
      schemaId = schemaId,
      schema = schema,
      revision = revision
    )
  }

  override fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> = Optional.ofNullable(
    client.findSchemaById(schemaId)
      .map {
        AvroSchemaWithIdData(
          schemaId = schemaId,
          schema = it,
          revision = schemaRevisionResolver.apply(it).orElse(null)
        )
      }.getOrNull()
  )

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> {
    return findAllByCanonicalName(info.namespace, info.name).singleOrNull { info.revision == it.revision }
      .let { Optional.ofNullable(it) }
  }

  override fun findAllByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId> {
    return client.findAllArtifacts().getOrDefault(emptyList())
      .asSequence()
      .filter { it.name == name }
      .map { client.findArtifactMetaData(it.id).onFailure { throw it }.getOrNull()!! }
      .filter { it.namespace ?: "" == namespace }
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
