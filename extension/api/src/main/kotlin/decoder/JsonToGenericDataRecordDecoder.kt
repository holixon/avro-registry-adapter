package io.holixon.avro.adapter.api.decoder

import io.holixon.avro.adapter.api.JsonString
import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData

/**
 * Decodes a [org.apache.avro.generic.GenericData.Record] from given [JsonStringAndSchemaId].
 */
interface JsonToGenericDataRecordDecoder {

  fun decode(jsonStringAndSchemaId: JsonStringAndSchemaId): GenericData.Record

  fun decode(schema: Schema, json: JsonString): GenericData.Record

}
