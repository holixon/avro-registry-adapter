package io.holixon.avro.adapter.api

import org.apache.avro.SchemaNormalization

/**
 * Test helper.
 */
object AvroAdapterApiTestHelper {

  val schemaIdSupplier = SchemaIdSupplier { schema -> SchemaNormalization.parsingFingerprint64(schema).toString() }

  val schemaRevisionResolver = AvroAdapterApi.propertyBasedSchemaRevisionResolver("revision")
}
