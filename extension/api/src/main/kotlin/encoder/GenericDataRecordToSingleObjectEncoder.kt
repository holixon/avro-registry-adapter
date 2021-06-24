package io.holixon.avro.adapter.api.encoder

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import org.apache.avro.generic.GenericData

/**
 * Encodes a [org.apache.avro.generic.GenericData.Record] to [AvroSingleObjectEncoded].
 */
fun interface GenericDataRecordToSingleObjectEncoder {

  /**
   * @param data the record to encode
   * @return single object bytes encoding
   */
  fun encode(data: GenericData.Record): AvroSingleObjectEncoded

}
