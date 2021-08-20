package io.holixon.avro.adapter.registry.reflection

import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry.Companion.createWithSchemas
import io.holixon.avro.adapter.registry.reflection.ClassGraphReflection.findSpecificRecordBaseSchemas
import mu.KLogging


/**
 * Implementation of the registry using classpath scan.
 *
 * Warning: This can only hold one version of each schema and thus does not support schema evolution, use only for demo and testing.
 */
open class ReflectionReadOnlySchemaRegistry private constructor(private val registry: AvroSchemaReadOnlyRegistry) :
  AvroSchemaReadOnlyRegistry by registry {
  companion object : KLogging() {

    operator fun invoke(vararg packageNames: String): ReflectionReadOnlySchemaRegistry = ReflectionReadOnlySchemaRegistry(
      createWithSchemas(*findSpecificRecordBaseSchemas(*packageNames).toTypedArray())
    )
  }
}
