package io.holixon.avro.adapter.api.decoder

import io.holixon.avro.adapter.api.JsonString
import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData

/**
 * Decodes a [org.apache.avro.generic.GenericData.Record] from given [JsonStringAndSchemaId].
 */
interface JsonToGenericDataRecordDecoder {

  /**
   * Decode the given json and schemaId to generic record.
   *
   * @param jsonStringAndSchemaId wrapper around json string containing the schemaId
   * @return the decoded record value
   */
  fun decode(jsonStringAndSchemaId: JsonStringAndSchemaId): GenericData.Record

  /**
   * Decode the given json string using the schema.
   *
   * @param schema the schema used to decode
   * @param json the json string
   * @return the decoded record value
   */
  fun decode(schema: Schema, json: JsonString): GenericData.Record

}
