package io.holixon.avro.adapter.common.decoder

import io.holixon.avro.adapter.api.JsonString
import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import io.holixon.avro.adapter.api.converter.GenericDataRecordToSpecificRecordConverter
import io.holixon.avro.adapter.api.decoder.JsonToGenericDataRecordDecoder
import io.holixon.avro.adapter.api.decoder.JsonToSpecificRecordDecoder
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecordBase

/**
 * Default implementation of [JsonToSpecificRecordDecoder]. This decodes to `GenericData.Record` first
 * and then uses the [GenericDataRecordToSpecificRecordConverter] to create a specific record value.
 */
open class DefaultJsonToSpecificRecordDecoder(
  private val decoder: JsonToGenericDataRecordDecoder,
  private val converter: GenericDataRecordToSpecificRecordConverter
) : JsonToSpecificRecordDecoder {

  override fun <T : SpecificRecordBase> decode(jsonStringAndSchemaId: JsonStringAndSchemaId): T = converter
    .convert(decoder.decode(jsonStringAndSchemaId))

  override fun <T : SpecificRecordBase> decode(schema: Schema, json: JsonString): T = converter
    .convert(decoder.decode(schema, json))
}
