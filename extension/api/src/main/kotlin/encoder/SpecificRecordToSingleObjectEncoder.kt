package io.holixon.avro.adapter.api.encoder

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Encodes a [SpecificRecordBase] to [AvroSingleObjectEncoded].
 */
interface SpecificRecordToSingleObjectEncoder {

  /**
   * Encodes instance of SpecificRecord to bytes containing the SchemaId.
   */
  fun <T: SpecificRecordBase> encode(data: T) : AvroSingleObjectEncoded

}
