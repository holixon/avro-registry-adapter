package io.holixon.avro.adapter.api.encoder

import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Encodes a [org.apache.avro.generic.GenericData.Record] to [JsonStringAndSchemaId].
 */
interface GenericRecordToJsonEncoder {

  /**
   * Encodes instance of SpecificRecord to json and schemaId.
   */
  fun encode(data: GenericData.Record) : JsonStringAndSchemaId

}
