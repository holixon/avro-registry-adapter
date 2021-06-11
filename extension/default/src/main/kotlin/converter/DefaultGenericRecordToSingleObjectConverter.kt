package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.converter.GenericRecordToSingleObjectConverter
import org.apache.avro.generic.GenericRecord

class DefaultGenericRecordToSingleObjectConverter : GenericRecordToSingleObjectConverter {
  override fun encode(data: GenericRecord): AvroSingleObjectEncoded {
    TODO("Not yet implemented")
  }

  override fun decode(bytes: AvroSingleObjectEncoded): GenericRecord {
    TODO("Not yet implemented")
  }
}
