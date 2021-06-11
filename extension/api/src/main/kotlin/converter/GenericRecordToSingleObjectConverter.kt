package io.holixon.avro.adapter.api.converter

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import org.apache.avro.generic.GenericRecord

/**
 * Converts a typed [GenericRecord] to single object bytes.
 */
interface GenericRecordToSingleObjectConverter {

  /**
   * Converts instance of GenericRecord to bytes containing the SchemaId.
   */
  fun  encode(data: GenericRecord) : AvroSingleObjectEncoded

  /**
   * Extracts SchemaId from given bytes amd converts the contents of the bytes payload
   * to a GenericRecord instance of that writer schema.
   */
  fun decode(bytes: AvroSingleObjectEncoded) : GenericRecord

}
