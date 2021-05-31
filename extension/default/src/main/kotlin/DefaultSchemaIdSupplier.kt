package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.common.ext.SchemaExt.fingerprint
import org.apache.avro.Schema

/**
 * Delivers schema id based on fingerprint.
 */
class DefaultSchemaIdSupplier : SchemaIdSupplier {
  override fun apply(schema: Schema): AvroSchemaId = schema.fingerprint.toString()
}
