package io.holixon.avro.adapter.api.type

import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaRevision
import org.apache.avro.Schema

/**
 * Data class implementing [AvroSchemaWithId].
 */
data class AvroSchemaWithIdData(
  override val schemaId: AvroSchemaId,
  override val schema: Schema,
  override val revision: AvroSchemaRevision?,
  override val namespace: String,
  override val name: String
) : AvroSchemaWithId {
  constructor(
    schemaId: AvroSchemaId,
    schema: Schema,
    revision: AvroSchemaRevision? = null
  ) : this(
    schemaId = schemaId,
    schema = schema,
    revision = revision,
    namespace = schema.namespace,
    name = schema.name
  )
}
