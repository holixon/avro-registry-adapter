package io.holixon.avro.adapter.api.converter

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.JsonString

/**
 * Converts encoded bytes to [JsonString] representation, using encoded schema.
 */
interface SingleObjectToJsonConverter {

  /**
   * @param bytes the encoded bytes
   * @return encoded content as json string
   */
  fun convert(bytes: AvroSingleObjectEncoded): JsonString

}
