package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.*
import java.util.*

class DefaultAvroSchemaRevisionForSchemaIdResolver(
  private val schemaResolver: AvroSchemaResolver,
  private val schemaRevisionResolver: SchemaRevisionResolver
) : AvroSchemaRevisionForSchemaIdResolver {

  constructor(schemaResolver: AvroSchemaResolver) : this(schemaResolver, AvroAdapterDefault.schemaRevisionResolver)

  override fun apply(id: AvroSchemaId): Optional<AvroSchemaRevision> = schemaResolver.apply(id)
    .map { it.schema }
    .flatMap(schemaRevisionResolver::apply)
}
