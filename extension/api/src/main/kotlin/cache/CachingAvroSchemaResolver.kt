package io.holixon.avro.adapter.api.cache

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.api.AvroSchemaWithId
import java.util.*
import javax.cache.annotation.CacheKey
import javax.cache.annotation.CacheResult

/**
 * Marks an [AvroSchemaResolver] that uses a cache to resolve a schema by its id.
 */
fun interface CachingAvroSchemaResolver : AvroSchemaResolver {

  @CacheResult
  override fun apply(@CacheKey schemaId: AvroSchemaId): Optional<AvroSchemaWithId>
}
