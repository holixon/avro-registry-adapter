package io.holixon.avro.adapter.registry.reflection

import io.github.classgraph.ClassGraph
import io.holixon.avro.adapter.api.AvroAdapterApi.schemaForClass
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecordBase
import kotlin.reflect.KClass

/**
 * Encapsulates usage of [ClassGraph] to all [Schema]s by reflection.
 */
internal object ClassGraphReflection {

  /**
   * Find and load all classes implementing [SpecificRecordBase].
   *
   * @param packageNames - if set, classes are only searched in given packages, if not, search everywhere
   *
   * @return list of found classes, empty if none found
   */
  @Suppress("UNCHECKED_CAST")
  fun findSpecificRecordBaseClasses(vararg packageNames: String): List<KClass<out SpecificRecordBase>> = ClassGraph()
    .acceptPackages(*packageNames)
    .scan()
    .getSubclasses(SpecificRecordBase::class.java)
    .standardClasses
    .loadClasses()
    .map { it.kotlin }
    .map { it as KClass<SpecificRecordBase> }

  /**
   * Finds all [Schema] that are defined in [SpecificRecordBase] classes in given packages.
   *
   * @see findSpecificRecordBaseClasses
   * @param packageNames - if set, classes are only searched in given packages, if not, search everywhere
   *
   * @return list of found schemas, empty if none found
   */
  fun findSpecificRecordBaseSchemas(vararg packageNames: String): List<Schema> = findSpecificRecordBaseClasses(*packageNames)
    .map { schemaForClass(it) }
}
