package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.SchemaId
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.common.ext.SchemaExt.fingerprint
import org.apache.avro.Schema

class DefaultSchemaIdSupplier : SchemaIdSupplier {
  override fun apply(schema: Schema): SchemaId = schema.fingerprint.toString()
}
