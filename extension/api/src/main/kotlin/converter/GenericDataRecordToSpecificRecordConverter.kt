package io.holixon.avro.adapter.api.converter

import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Type safe conversion of a generic record to a specific record of type T.
 *
 * Schema might change from writer to reader, this is handled by concrete implementations.
 */
interface GenericDataRecordToSpecificRecordConverter {

  /**
   * @param record the generic record
   * @param T the type of the specific record
   * @return the specific record
   */
  fun <T : SpecificRecordBase> convert(record: GenericData.Record) : T

}
