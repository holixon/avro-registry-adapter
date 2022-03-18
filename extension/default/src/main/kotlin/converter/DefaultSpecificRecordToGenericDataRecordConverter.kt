package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.converter.SpecificRecordToGenericDataRecordConverter
import io.holixon.avro.adapter.common.AvroAdapterDefault.toGenericDataRecord
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Converts any instance derived from [SpecificRecordBase] (generated from avsc) to a [GenericData.Record].
 * On decoding, the schema change from Writer to Reader takes place.
 */
open class DefaultSpecificRecordToGenericDataRecordConverter() : SpecificRecordToGenericDataRecordConverter {

  override fun <T : SpecificRecordBase> convert(data: T): GenericData.Record = data.toGenericDataRecord()
}
