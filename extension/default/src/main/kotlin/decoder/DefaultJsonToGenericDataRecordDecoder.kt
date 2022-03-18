package io.holixon.avro.adapter.common.decoder

import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.api.JsonString
import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import io.holixon.avro.adapter.api.decoder.JsonToGenericDataRecordDecoder
import io.holixon.avro.adapter.api.loadById
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.io.DatumReader
import org.apache.avro.io.Decoder
import org.apache.avro.io.DecoderFactory

/**
 * Default implementation of JsonToGenericDataRecordDecoder.
 */
class DefaultJsonToGenericDataRecordDecoder(
  private val schemaResolver: AvroSchemaResolver
) : JsonToGenericDataRecordDecoder {

  private val decoderFactory = DecoderFactory()

  override fun decode(jsonStringAndSchemaId: JsonStringAndSchemaId): GenericData.Record = decode(
    schema = schemaResolver.loadById(jsonStringAndSchemaId.schemaId).schema,
    json = jsonStringAndSchemaId.json
  )

  override fun decode(schema: Schema, json: JsonString): GenericData.Record {
    val decoder: Decoder = decoderFactory.jsonDecoder(schema, json)
    val reader: DatumReader<GenericData.Record> = GenericDatumReader(schema)
    return reader.read(null, decoder)!!
  }
}
