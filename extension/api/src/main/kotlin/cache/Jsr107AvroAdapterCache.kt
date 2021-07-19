package io.holixon.avro.adapter.api.cache

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.api.AvroSchemaWithId
import mu.KLogging
import java.util.*
import javax.cache.Cache
import javax.cache.configuration.FactoryBuilder.SingletonFactory
import javax.cache.configuration.MutableConfiguration
import javax.cache.integration.CacheLoader

/**
 * Convenient jcache configuration.
 */
object Jsr107AvroAdapterCache {

  /**
   * Provides a [SingletonFactory] for the [AvroSchemaResolverCacheLoader] used in [MutableConfiguration].
   */
  fun singletonFactory(schemaResolver: AvroSchemaResolver): SingletonFactory<Jsr107AvroSchemaResolverCacheLoader> = SingletonFactory(
    Jsr107AvroSchemaResolverCacheLoader(schemaResolver)
  )

  /**
   * Provides a default configuration for [javax.cache.Cache] setup.
   */
  fun mutableConfiguration(schemaResolver: AvroSchemaResolver): MutableConfiguration<AvroSchemaId, AvroSchemaWithId?> =
    MutableConfiguration<AvroSchemaId, AvroSchemaWithId>()
      .setTypes(AvroSchemaId::class.java, AvroSchemaWithId::class.java)
      .setReadThrough(true)
      .setCacheLoaderFactory(singletonFactory(schemaResolver))
      .setStoreByValue(true)
      .setStatisticsEnabled(true)
      .setWriteThrough(false)

  /**
   * A [AvroSchemaResolver] that uses a [Cache] to quickly resolve [AvroSchemaWithId] by [AvroSchemaId]
   *
   * @param cache - a jsr-107 loading cache that has to be configured so it uses a [AvroSchemaResolver] to fill the cache on miss.
   */
  class Jsr107CachingAvroSchemaResolver(
    private val cache: Cache<AvroSchemaId, AvroSchemaWithId?>
  ) : CachingAvroSchemaResolver {
    companion object {
      const val DEFAULT_CACHE_NAME = "cachingSchemaResolver"
    }

    override fun apply(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> = Optional.ofNullable(cache.get(schemaId))
  }

  /**
   * JSR-107 [CacheLoader] that uses a given [AvroSchemaResolver].
   */
  open class Jsr107AvroSchemaResolverCacheLoader(
    private val schemaResolver: AvroSchemaResolver
  ) : CacheLoader<AvroSchemaId, AvroSchemaWithId?> {
    companion object : KLogging()

    override fun load(schemaId: AvroSchemaId): AvroSchemaWithId? = schemaResolver.apply(schemaId).orElse(null).also {
      logger.debug("load - schemaId={}, schemaWithId={}", schemaId, it)
    }

    override fun loadAll(schemaIds: Iterable<AvroSchemaId>): Map<AvroSchemaId, AvroSchemaWithId?> = schemaIds.associateWith { load(it) }
  }
}
