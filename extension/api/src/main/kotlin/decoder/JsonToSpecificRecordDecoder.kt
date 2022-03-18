package io.holixon.avro.adapter.api.decoder

import io.holixon.avro.adapter.api.JsonString
import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecordBase

/**
 * Decodes an instance of [org.apache.avro.specific.SpecificRecordBase] from given [JsonStringAndSchemaId].
 */
interface JsonToSpecificRecordDecoder {

  fun <T : SpecificRecordBase> decode(jsonStringAndSchemaId: JsonStringAndSchemaId): T

  fun <T : SpecificRecordBase> decode(schema: Schema, json: JsonString) : T
}
