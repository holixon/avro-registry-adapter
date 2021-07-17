package io.holixon.avro.adapter.api.cache

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.SchemaResolver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.cache.integration.CacheLoader

/**
 * JSR-107 [CacheLoader] that uses a given [SchemaResolver].
 */
class SchemaResolverCacheLoader
@JvmOverloads constructor(
  private val schemaResolver: SchemaResolver,
  private val logger: Logger = LoggerFactory.getLogger(SchemaResolverCacheLoader::class.java)
) : CacheLoader<AvroSchemaId, AvroSchemaWithId?> {
  companion object {
    private val logger = LoggerFactory.getLogger(SchemaResolverCacheLoader::class.java)
  }

  override fun load(schemaId: AvroSchemaId): AvroSchemaWithId? = schemaResolver.apply(schemaId).orElse(null).also {
    logger.debug("schemaId={}, schemaWithId={}", schemaId, it)
  }

  override fun loadAll(schemaIds: Iterable<AvroSchemaId>): Map<AvroSchemaId, AvroSchemaWithId?> = schemaIds.associateWith { load(it) }
}
