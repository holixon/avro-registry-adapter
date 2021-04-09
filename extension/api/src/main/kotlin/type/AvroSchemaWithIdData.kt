package io.holixon.avro.adapter.api.type

import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.SchemaId
import io.holixon.avro.adapter.api.SchemaRevision
import org.apache.avro.Schema

/**
 * Data class implementing [AvroSchemaWithId].
 */
data class AvroSchemaWithIdData(
  override val id: SchemaId,
  override val schema: Schema,
  override val revision: SchemaRevision? = null
) : AvroSchemaWithId {
  override val context: String = schema.namespace
  override val name: String = schema.name
}
