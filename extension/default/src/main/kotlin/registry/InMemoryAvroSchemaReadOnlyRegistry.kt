package io.holixon.avro.adapter.common.registry

import io.holixon.avro.adapter.api.*
import io.holixon.avro.adapter.api.AvroSchemaInfo.Companion.equalsByFields
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.adapter.common.AvroAdapterDefault
import org.apache.avro.Schema
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * An implementation of [AvroSchemaReadOnlyRegistry] that does not use persistent storage.
 * Obviously, this is primarily meant for testing and should not be used for real life projects.
 */
open class InMemoryAvroSchemaReadOnlyRegistry(
  protected val store: ConcurrentHashMap<String, Pair<AvroSchemaInfo, Schema>>
) : AvroSchemaReadOnlyRegistry, AutoCloseable {
  companion object {

    /**
     * Registers given schemas and returns read only representation.
     *
     * @param schemas - the schemas to register.
     * @return read only registry
     */
    fun createWithSchemas(vararg schemas: Schema) = AvroAdapterDefault.inMemorySchemaRegistry().apply {
      schemas.forEach { register(it) }
    }.toReadOnly()
  }

  /**
   * Create instance with default empty [ConcurrentHashMap] as store.
   */
  constructor() : this(ConcurrentHashMap())

  override fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> = Optional.ofNullable(
    store[schemaId]
  ).map {
    schemaData(
      schemaId = schemaId,
      schema = it.second,
      schemaRevision = it.first.revision
    )
  }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> = Optional.ofNullable(store.entries
    .filter { it.value.first.equalsByFields(info) }
    .map { it.toSchemaData() }
    .firstOrNull())

  override fun findAllByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId> = store
    .filter { it.value.first.name == name }
    .filter { it.value.first.namespace == namespace }
    .map { it.toSchemaData() }

  override fun findAll(): List<AvroSchemaWithId> = store.map { it.toSchemaData() }

  override fun close() {
    store.clear()
  }

  private fun Map.Entry<AvroSchemaId, Pair<AvroSchemaInfo, Schema>>.toSchemaData() = schemaData(
    schemaId = key,
    schema = value.second,
    schemaRevision = value.first.revision
  )

  private fun schemaData(schemaId: AvroSchemaId, schema: Schema, schemaRevision: AvroSchemaRevision?) = AvroSchemaWithIdData(
    schemaId = schemaId,
    schema = schema,
    revision = schemaRevision
  )
}
