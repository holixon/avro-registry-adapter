package io.holixon.avro.adapter.api.converter

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.JsonString
import io.holixon.avro.adapter.api.JsonStringAndSchemaId

/**
 * Converts encoded bytes to [JsonStringAndSchemaId] representation, using encoded schema.
 */
interface SingleObjectToJsonConverter {

  /**
   * @param bytes the encoded bytes
   * @return encoded content as json string
   */
  fun convert(bytes: AvroSingleObjectEncoded): JsonStringAndSchemaId

}
