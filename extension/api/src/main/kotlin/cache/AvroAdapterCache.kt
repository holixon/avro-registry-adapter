package io.holixon.avro.adapter.api.cache

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.SchemaResolver
import javax.cache.configuration.FactoryBuilder
import javax.cache.configuration.MutableConfiguration

/**
 * Convenient cache configuration.
 */
object AvroAdapterCache {

  /**
   * Provides a [SingletonFactory] for the [SchemaResolverCacheLoader] used in [MutableConfiguration].
   */
  fun singletonFactory(schemaResolver: SchemaResolver): FactoryBuilder.SingletonFactory<SchemaResolverCacheLoader> = FactoryBuilder.SingletonFactory(SchemaResolverCacheLoader(schemaResolver))

  /**
   * Provides a default configuration for [javax.cache.Cache] setup.
   */
  fun mutableConfiguration(schemaResolver: SchemaResolver) : MutableConfiguration<AvroSchemaId, AvroSchemaWithId?> = MutableConfiguration<AvroSchemaId, AvroSchemaWithId>()
    .setTypes(AvroSchemaId::class.java, AvroSchemaWithId::class.java)
    .setReadThrough(true)
    .setCacheLoaderFactory(singletonFactory(schemaResolver))
    .setStoreByValue(true)
    .setStatisticsEnabled(true)
    .setWriteThrough(false)
}
