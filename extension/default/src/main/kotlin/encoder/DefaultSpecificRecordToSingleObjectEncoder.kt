package io.holixon.avro.adapter.common.encoder

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.encoder.SpecificRecordToSingleObjectEncoder
import org.apache.avro.message.BinaryMessageEncoder
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecordBase
import java.io.ByteArrayOutputStream

/**
 * Uses [BinaryMessageEncoder] default to convert [org.apache.avro.generic.GenericData.Record] to [AvroSingleObjectEncoded] bytes.
 */
open class DefaultSpecificRecordToSingleObjectEncoder : SpecificRecordToSingleObjectEncoder {

  override fun <T : SpecificRecordBase> encode(data: T): AvroSingleObjectEncoded = ByteArrayOutputStream().use {
    BinaryMessageEncoder<T>(SpecificData(), data.schema).encode(data, it)
    return it.toByteArray()
  }
}

