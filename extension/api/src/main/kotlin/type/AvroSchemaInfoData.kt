package io.toolisticon.avro.adapter.api.type

import io.toolisticon.avro.adapter.api.AvroSchemaInfo
import io.toolisticon.avro.adapter.api.SchemaRevision

/**
 * Data class implementing [AvroSchemaInfo].
 */
data class AvroSchemaInfoData(
  override val context: String,
  override val name: String,
  override val revision: SchemaRevision?
) : AvroSchemaInfo
