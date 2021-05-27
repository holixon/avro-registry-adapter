package io.holixon.avro.adapter.api.repository

import io.holixon.avro.adapter.api.*
import io.holixon.avro.adapter.api.AvroAdapterApi.extractSchemaInfo
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import org.apache.avro.Schema
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * An implementation of [AvroSchemaRegistry] that does not use persistent storage.
 * Obviously, this is primarily meant for testing and should not be used for real life projects.
 */
class InMemoryAvroSchemaRegistry(
  private val store: ConcurrentHashMap<String, Pair<AvroSchemaInfo, Schema>>,
  val schemaIdSupplier: SchemaIdSupplier,
  val schemaRevisionResolver: SchemaRevisionResolver
) : AvroSchemaRegistry, AutoCloseable {

  /**
   * Create instance with default empty [ConcurrentHashMap] as store.
   */
  constructor(schemaIdSupplier: SchemaIdSupplier, schemaRevisionResolver: SchemaRevisionResolver) : this(ConcurrentHashMap(), schemaIdSupplier, schemaRevisionResolver)

  override fun register(schema: Schema): AvroSchemaWithId {
    val info = schema.extractSchemaInfo(schemaRevisionResolver)

    return findByInfo(info)
      .orElseGet {
        val id = schemaIdSupplier.apply(schema)
        store[id] = info to schema
        findById(id).get()
      }
  }

  override fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> = Optional.ofNullable(
    store[schemaId]
  ).map { AvroSchemaWithIdData(schemaId, it.second, it.first.revision) }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> = Optional.ofNullable(store.entries
    .filter { it.value.first == info }
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

  private fun Map.Entry<AvroSchemaId, Pair<AvroSchemaInfo, Schema>>.toSchemaData() = AvroSchemaWithIdData(key, value.second)

}
