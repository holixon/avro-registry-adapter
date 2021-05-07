package io.toolisticon.avro.adapter.api.type

import io.toolisticon.avro.adapter.api.AvroSchemaWithId
import io.toolisticon.avro.adapter.api.SchemaId
import io.toolisticon.avro.adapter.api.SchemaRevision
import org.apache.avro.Schema

/**
 * Data class implementing [AvroSchemaWithId].
 */
data class AvroSchemaWithIdData(
  override val schemaId: SchemaId,
  override val schema: Schema,
  override val revision: SchemaRevision?,
  override val namespace: String,
  override val name: String
) : AvroSchemaWithId {
  constructor(
    schemaId: SchemaId,
    schema: Schema,
    revision: SchemaRevision? = null
  ) : this(
    schemaId = schemaId,
    schema = schema,
    revision = revision,
    namespace = schema.namespace,
    name = schema.name
  )
}
