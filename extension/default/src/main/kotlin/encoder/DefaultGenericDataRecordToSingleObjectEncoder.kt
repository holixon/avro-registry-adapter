package io.holixon.avro.adapter.common.encoder

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.encoder.GenericDataRecordToSingleObjectEncoder
import org.apache.avro.generic.GenericData
import org.apache.avro.message.BinaryMessageEncoder
import java.io.ByteArrayOutputStream

/**
 * Uses [BinaryMessageEncoder] default to convert [org.apache.avro.generic.GenericData.Record] to [AvroSingleObjectEncoded] bytes.
 */
class DefaultGenericDataRecordToSingleObjectEncoder : GenericDataRecordToSingleObjectEncoder {

  override fun encode(data: GenericData.Record): AvroSingleObjectEncoded = ByteArrayOutputStream().use {
    BinaryMessageEncoder<GenericData.Record>(GenericData.get(), data.schema).encode(data, it)
    return it.toByteArray()
  }

}
