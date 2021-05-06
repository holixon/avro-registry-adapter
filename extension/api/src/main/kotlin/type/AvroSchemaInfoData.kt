package io.holixon.avro.adapter.api.type

import io.holixon.avro.adapter.api.AvroSchemaInfo
import io.holixon.avro.adapter.api.SchemaRevision

/**
 * Data class implementing [AvroSchemaInfo].
 */
data class AvroSchemaInfoData(
  override val namespace: String,
  override val name: String,
  override val revision: SchemaRevision?
) : AvroSchemaInfo
