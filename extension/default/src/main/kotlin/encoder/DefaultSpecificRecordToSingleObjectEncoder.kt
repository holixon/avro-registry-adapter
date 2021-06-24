package io.holixon.avro.adapter.common.encoder

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.encoder.GenericDataRecordToSingleObjectEncoder
import io.holixon.avro.adapter.api.encoder.SpecificRecordToSingleObjectEncoder
import io.holixon.avro.adapter.common.AvroAdapterDefault.toByteArray
import org.apache.avro.generic.GenericData
import org.apache.avro.message.BinaryMessageEncoder
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecord
import org.apache.avro.specific.SpecificRecordBase
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

/**
 * Uses [BinaryMessageEncoder] default to convert [org.apache.avro.generic.GenericData.Record] to [AvroSingleObjectEncoded] bytes.
 */
class DefaultSpecificRecordToSingleObjectEncoder : SpecificRecordToSingleObjectEncoder {

  override fun <T : SpecificRecordBase> encode(data: T): AvroSingleObjectEncoded = ByteArrayOutputStream().use {
    BinaryMessageEncoder<T>(SpecificData(), data.schema).encode(data, it)
    return it.toByteArray()
  }
}

