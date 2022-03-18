package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.*
import java.util.*

/**
 * An implmentation of [AvroSchemaRevisionForSchemaIdResolver] that uses an [AvroSchemaResolver] (aka registry) to
 * load a schema and then forwards the revision resolving to a [SchemaRevisionResolver].
 */
class DefaultAvroSchemaRevisionForSchemaIdResolver(
  private val schemaResolver: AvroSchemaResolver,
  private val schemaRevisionResolver: SchemaRevisionResolver
) : AvroSchemaRevisionForSchemaIdResolver {

  /**
   * Creates a new instance using the [DefaultSchemaRevisionResolver].
   */
  constructor(schemaResolver: AvroSchemaResolver) : this(schemaResolver, AvroAdapterDefault.schemaRevisionResolver)

  override fun apply(id: AvroSchemaId): Optional<AvroSchemaRevision> = schemaResolver.apply(id)
    .map { it.schema }
    .flatMap(schemaRevisionResolver::apply)
}
