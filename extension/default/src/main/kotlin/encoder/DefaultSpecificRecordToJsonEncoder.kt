package io.holixon.avro.adapter.common.encoder

import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.encoder.SpecificRecordToJsonEncoder
import io.holixon.avro.adapter.api.type.JsonStringAndSchemaIdData
import io.holixon.avro.adapter.common.AvroAdapterDefault
import org.apache.avro.specific.SpecificRecordBase

class DefaultSpecificRecordToJsonEncoder
@JvmOverloads
constructor(
  private val schemaIdSupplier: SchemaIdSupplier = AvroAdapterDefault.schemaIdSupplier
) : SpecificRecordToJsonEncoder {

  override fun <T : SpecificRecordBase> encode(data: T): JsonStringAndSchemaId = JsonStringAndSchemaIdData(
    schemaId = schemaIdSupplier.apply(data.schema),
    json = data.toString()
  )
}
