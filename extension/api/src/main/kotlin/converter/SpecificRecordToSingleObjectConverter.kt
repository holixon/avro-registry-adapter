package io.toolisticon.avro.adapter.api.converter

import io.toolisticon.avro.adapter.api.AvroSingleObjectEncoded
import org.apache.avro.specific.SpecificRecordBase

/**
 * Converts a typed SpecificRecord to single object bytes.
 */
interface SpecificRecordToSingleObjectConverter {

  /**
   * Converts instance of SpecificRecord to bytes containing the SchemaId.
   */
  fun <T: SpecificRecordBase> encode(data: T) : AvroSingleObjectEncoded

  /**
   * Extracts SchemaId from given bytes amd converts the contents of the bytes payload
   * to a SpecificRecord instance of that writer schema.
   */
  fun <T: SpecificRecordBase> decode(bytes: AvroSingleObjectEncoded) : T

}
