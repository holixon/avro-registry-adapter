package io.holixon.avro.adapter.api

import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization

object AvroAdapterApiTestHelper {


  fun avroSchemaWithId(schema : Schema): AvroSchemaWithIdData {
    return AvroSchemaWithIdData(
      schemaId = SchemaNormalization.parsingFingerprint64(schema).toString(),
      schema = schema,
      revision = AvroAdapterApi.propertyBasedSchemaRevisionResolver("revision").apply(schema).orElse(null)
    )
  }

}
