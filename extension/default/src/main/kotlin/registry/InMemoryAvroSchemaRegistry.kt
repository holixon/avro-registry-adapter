package io.holixon.avro.adapter.common.registry

import io.holixon.avro.adapter.api.*
import io.holixon.avro.adapter.api.AvroAdapterApi.extractSchemaInfo
import org.apache.avro.Schema
import java.util.concurrent.ConcurrentHashMap

/**
 * An implementation of [AvroSchemaRegistry] that does not use persistent storage.
 * Obviously, this is primarily meant for testing and should not be used for real life projects.
 */
open class InMemoryAvroSchemaRegistry(
  store: ConcurrentHashMap<String, Pair<AvroSchemaInfo, Schema>>,
  val schemaIdSupplier: SchemaIdSupplier,
  val schemaRevisionResolver: SchemaRevisionResolver
) : InMemoryAvroSchemaReadOnlyRegistry(store), AvroSchemaRegistry {

  /**
   * Create instance with default empty [ConcurrentHashMap] as store.
   */
  constructor(schemaIdSupplier: SchemaIdSupplier, schemaRevisionResolver: SchemaRevisionResolver) : this(
    ConcurrentHashMap(),
    schemaIdSupplier,
    schemaRevisionResolver
  )

  override fun register(schema: Schema): AvroSchemaWithId {
    val info = schema.extractSchemaInfo(schemaRevisionResolver)

    return findByInfo(info)
      .orElseGet {
        val id = schemaIdSupplier.apply(schema)
        store[id] = info to schema
        findById(id).get()
      }
  }

  /**
   * Creates a [AvroSchemaReadOnlyRegistry] copy containing the current registered schemas.
   */
  fun toReadOnly() = InMemoryAvroSchemaReadOnlyRegistry(store)
}
