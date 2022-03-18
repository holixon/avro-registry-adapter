package io.holixon.avro.adapter.common.encoder

import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.encoder.GenericRecordToJsonEncoder
import io.holixon.avro.adapter.api.type.JsonStringAndSchemaIdData
import io.holixon.avro.adapter.common.AvroAdapterDefault
import org.apache.avro.generic.GenericData

/**
 * Default implementation of [GenericRecordToJsonEncoder].
 */
open class DefaultGenericRecordToJsonEncoder
@JvmOverloads
constructor(
  private val schemaIdSupplier: SchemaIdSupplier = AvroAdapterDefault.schemaIdSupplier
) : GenericRecordToJsonEncoder {

  override fun encode(data: GenericData.Record): JsonStringAndSchemaId = JsonStringAndSchemaIdData(
    schemaId = schemaIdSupplier.apply(data.schema),
    json = data.toString()
  )
}
