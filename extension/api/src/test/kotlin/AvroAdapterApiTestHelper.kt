package io.holixon.avro.adapter.api

import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization


object AvroAdapterApiTestHelper {

  val schemaIdSupplier = object : SchemaIdSupplier {
    override fun apply(schema: Schema): SchemaId = SchemaNormalization.parsingFingerprint64(schema).toString()
  }

  val schemaRevisionResolver = AvroAdapterApi.propertyBasedSchemaRevisionResolver("revision")

}
