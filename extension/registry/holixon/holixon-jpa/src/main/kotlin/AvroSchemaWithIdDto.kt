package io.holixon.avro.adapter.registry.jpa

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaRevision
import io.holixon.avro.adapter.api.AvroSchemaWithId
import org.apache.avro.Schema

/**
 * Simple Avro Schema with id implementation.
 */
data class AvroSchemaWithIdDto(
  override val namespace: String,
  override val name: String,
  override val revision: AvroSchemaRevision?,
  override val schemaId: AvroSchemaId,
  override val schema: Schema
) : AvroSchemaWithId
