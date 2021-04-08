package io.toolisticon.avro.adapter.common

import io.toolisticon.avro.adapter.api.SchemaId
import io.toolisticon.avro.adapter.api.SchemaIdSupplier
import io.toolisticon.avro.adapter.common.ext.SchemaExt.fingerprint
import org.apache.avro.Schema

class DefaultSchemaIdSupplier : SchemaIdSupplier {
  override fun apply(schema: Schema): SchemaId = schema.fingerprint.toString()
}
