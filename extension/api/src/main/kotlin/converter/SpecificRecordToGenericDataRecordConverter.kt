package io.holixon.avro.adapter.api.converter

import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificRecordBase

/**
 * Converts a typed [SpecificRecordBase] to [GenericRecord].
 *
 * The generic record will have the same schema as the specific record.
 */
interface SpecificRecordToGenericDataRecordConverter {

  /**
   * Converts instance of GenericRecord to bytes containing the SchemaId.
   *
   * @param T type of data
   * @param data the specific record instance
   * @return generic record
   */
  fun <T : SpecificRecordBase> convert(data: T): GenericData.Record

}
