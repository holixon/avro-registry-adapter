package io.holixon.avro.adapter.api.cache

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.SchemaResolver
import java.util.*
import javax.cache.Cache

/**
 * A [SchemaResolver] that uses a [Cache] to quickly resolve [AvroSchemaWithId] by [AvroSchemaId]
 *
 * @param cache - a loading cache that has to be configured so it uses a [SchemaResolver] to fill the cache on miss.
 */
class CachingSchemaResolver(
  private val cache: Cache<AvroSchemaId, AvroSchemaWithId?>
) : SchemaResolver {
  companion object {
    const val DEFAULT_CACHE_NAME = "cachingSchemaResolver"
  }

  override fun apply(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> = Optional.ofNullable(cache.get(schemaId))

}
