package io.holixon.avro.adapter.api.converter

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificRecordBase

/**
 * Converts a typed [GenericData.Record] to single object bytes [AvroSingleObjectEncoded] and back.
 */
interface GenericDataRecordToSingleObjectConverter {

  /**
   * Converts instance of GenericRecord to bytes containing the SchemaId.
   */
  fun encode(data: GenericData.Record) : AvroSingleObjectEncoded

  /**
   * Extracts SchemaId from given bytes amd converts the contents of the bytes payload
   * to a GenericRecord instance of that writer schema.
   */
  fun decode(bytes: AvroSingleObjectEncoded) : GenericData.Record

}
