package io.holixon.avro.adapter.api.encoder

import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import org.apache.avro.specific.SpecificRecordBase

/**
 * Encodes a [SpecificRecordBase] to [JsonStringAndSchemaId].
 */
interface SpecificRecordToJsonEncoder {

  /**
   * Encodes instance of SpecificRecord to json and schemaId.
   */
  fun <T: SpecificRecordBase> encode(data: T) : JsonStringAndSchemaId

}
