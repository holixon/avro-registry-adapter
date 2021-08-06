package io.holixon.avro.adapter.common.cache

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.cache.CachingAvroSchemaResolver
import mu.KLogging
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * A simple implementation of [CachingAvroSchemaResolver] using an internal [ConcurrentHashMap] to cache the results.
 *
 * Note: this keeps an append only (thus ever growing) in memory cache that is not configurable. Assuming that the [AvroSchemaWithId] for a given [AvroSchemaId]
 * never changes.
 */
open class ConcurrentMapCachingAvroSchemaResolver(
  protected val schemaResolver: AvroSchemaResolver
) : CachingAvroSchemaResolver {
  companion object : KLogging()

  internal val cache = ConcurrentHashMap<AvroSchemaId, Optional<AvroSchemaWithId>>()

  override fun apply(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> =
    cache.computeIfAbsent(schemaId) {
      schemaResolver.apply(schemaId).also {
        logger.debug { "load - schemaId=$schemaId, schema=$it" }
      }
    }
}
