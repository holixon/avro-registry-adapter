package io.holixon.avro.adapter.api.cache

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.SchemaResolver
import javax.cache.integration.CacheLoader

/**
 * JSR-107 [CacheLoader] that uses a given [SchemaResolver].
 */
class SchemaResolverCacheLoader(
  private val schemaResolver: SchemaResolver
) : CacheLoader<AvroSchemaId, AvroSchemaWithId?> {

  override fun load(schemaId: AvroSchemaId): AvroSchemaWithId? = schemaResolver.apply(schemaId).orElse(null)

  override fun loadAll(schemaIds: Iterable<AvroSchemaId>): Map<AvroSchemaId, AvroSchemaWithId?> = schemaIds.associateWith { load(it) }
}
