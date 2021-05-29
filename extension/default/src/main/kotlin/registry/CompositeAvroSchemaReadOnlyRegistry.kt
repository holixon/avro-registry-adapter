package io.holixon.avro.adapter.common.registry

import io.holixon.avro.adapter.api.*
import java.util.*

/**
 * Composite Avro registry allowing registry composition.
 */
class CompositeAvroSchemaReadOnlyRegistry(
  private val registries: Collection<AvroSchemaReadOnlyRegistry>
) : AvroSchemaReadOnlyRegistry {

  /**
   * Using registries or read-only registries as varargs.
   */
  constructor(vararg registry: AvroSchemaRegistry) : this(registry.asList())

  /**
   * Using registries or read-only registries as varargs.
   */
  constructor(vararg registry: AvroSchemaReadOnlyRegistry) : this(registry.asList())

  init {
    require(registries.isNotEmpty()) { "Composite Avro Schema Registry must contain at least one registry." }
  }

  override fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> {
    for (registry in registries) {
      val result = registry.findById(schemaId)
      if (result.isPresent) {
        return result
      }
    }
    return Optional.empty()
  }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> {
    for (registry in registries) {
      val result = registry.findByInfo(info)
      if (result.isPresent) {
        return result
      }
    }
    return Optional.empty()
  }

  override fun findAllByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId> {
    for (registry in registries) {
      val result = registry.findAllByCanonicalName(namespace, name)
      if (result.isNotEmpty()) {
        return result
      }
    }
    return listOf()
  }

  override fun findAll(): List<AvroSchemaWithId> {
    for (registry in registries) {
      val result = registry.findAll()
      if (result.isNotEmpty()) {
        return result
      }
    }
    return listOf()
  }
}

