package io.holixon.avro.adapter.common.registry

import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import io.holixon.avro.adapter.api.AvroSchemaRegistry
import io.holixon.avro.adapter.api.AvroSchemaWithId
import org.apache.avro.Schema

/**
 * Composite registry delegating the registration to a dedicated registry and using a composite read only registry for schema retrieval.
 */
class CompositeAvroSchemaRegistry(
  private val registry: AvroSchemaRegistry,
  private val compositeAvroSchemaReadOnlyRegistry: CompositeAvroSchemaReadOnlyRegistry
) : AvroSchemaReadOnlyRegistry by compositeAvroSchemaReadOnlyRegistry, AvroSchemaRegistry {

  /**
   * Convenience constructor for a composite registry.
   * @param readOnlyRegistries list of read-only registries used for finding schemas.
   * @param registry registry used for schema registration.
   */
  constructor(registry: AvroSchemaRegistry, vararg readOnlyRegistries: AvroSchemaReadOnlyRegistry) : this(
    registry,
    readOnlyRegistries.toList()
  )

  /**
   * Convenience constructor for a composite registry.
   * @param readOnlyRegistries list of read-only registries used for finding schemas.
   * @param registry registry used for schema registration.
   */
  constructor(registry: AvroSchemaRegistry, readOnlyRegistries: List<AvroSchemaReadOnlyRegistry>) : this(
    registry,
    CompositeAvroSchemaReadOnlyRegistry(readOnlyRegistries)
  )

  /*
   * Delegate registration to the registry.
   */
  override fun register(schema: Schema): AvroSchemaWithId {
    return registry.register(schema)
  }
}
