package io.holixon.avro.adapter.api.converter

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificRecordBase

/**
 * Converts a typed [GenericRecord] to single object bytes.
 */
@Deprecated("remove as soon byte -> generic data.record -> specific record")
interface AsymmetricGenericRecordToSingleObjectConverterAttempt {

  /**
   * Converts instance of GenericRecord to bytes containing the SchemaId.
   */
  fun <T: SpecificRecordBase> encode(data: T) : AvroSingleObjectEncoded

  /**
   * Extracts SchemaId from given bytes amd converts the contents of the bytes payload
   * to a GenericRecord instance of that writer schema.
   */
  fun decode(bytes: AvroSingleObjectEncoded) : GenericData.Record

}
