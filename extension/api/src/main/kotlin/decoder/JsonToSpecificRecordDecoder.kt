package io.holixon.avro.adapter.api.decoder

import io.holixon.avro.adapter.api.JsonString
import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecordBase

/**
 * Decodes an instance of [org.apache.avro.specific.SpecificRecordBase] from given [JsonStringAndSchemaId].
 */
interface JsonToSpecificRecordDecoder {

  /**
   * Decode the given json and schemaId to specific record.
   *
   * @param T the type of the [SpecificRecordBase]
   * @param jsonStringAndSchemaId wrapper around json string containing the schemaId
   * @return the decoded record value
   */
  fun <T : SpecificRecordBase> decode(jsonStringAndSchemaId: JsonStringAndSchemaId): T

  /**
   * Decode the given json string using the schema.
   *
   * @param T the type of the [SpecificRecordBase]
   * @param schema the schema used to decode
   * @param json the json string
   * @return the decoded record value
   */
  fun <T : SpecificRecordBase> decode(schema: Schema, json: JsonString) : T
}
