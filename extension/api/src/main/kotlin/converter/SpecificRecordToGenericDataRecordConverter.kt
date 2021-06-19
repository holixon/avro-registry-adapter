package io.holixon.avro.adapter.api.converter

import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificRecordBase

/**
 * Converts a typed [SpecificRecordBase] to [GenericRecord].
 */
interface SpecificRecordToGenericDataRecordConverter {

  /**
   * Converts instance of GenericRecord to bytes containing the SchemaId.
   */
  fun <T : Any> encode(data: T): GenericData.Record

  /**
   * Extracts SchemaId from given bytes amd converts the contents of the bytes payload
   * to a GenericRecord instance of that writer schema.
   */
  fun <T : Any> decode(record: GenericData.Record): T

}
