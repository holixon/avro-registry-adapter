package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.AvroAdapterApi
import io.holixon.avro.adapter.api.SchemaIdSupplier
import org.apache.avro.SchemaNormalization

/**
 * Test helper.
 */
object AvroAdapterApiTestHelper {

  val schemaIdSupplier = SchemaIdSupplier { schema -> SchemaNormalization.parsingFingerprint64(schema).toString() }

  val schemaRevisionResolver = AvroAdapterApi.propertyBasedSchemaRevisionResolver("revision")
}
