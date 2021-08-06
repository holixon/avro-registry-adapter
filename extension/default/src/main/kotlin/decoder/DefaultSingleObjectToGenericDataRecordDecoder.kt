package io.holixon.avro.adapter.common.decoder

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.api.decoder.SingleObjectToGenericDataRecordDecoder
import io.holixon.avro.adapter.common.AvroAdapterDefault.readPayloadAndSchemaId
import org.apache.avro.generic.GenericData
import org.apache.avro.message.BinaryMessageDecoder

/**
 * Uses default [BinaryMessageDecoder] and given [AvroSchemaResolver] to decode the single object
 * bytes to a generic record.
 */
class DefaultSingleObjectToGenericDataRecordDecoder(private val schemaResolver: AvroSchemaResolver) : SingleObjectToGenericDataRecordDecoder {

  override fun decode(bytes: AvroSingleObjectEncoded): GenericData.Record {
    val schemaId = bytes.readPayloadAndSchemaId().schemaId
    val avroSchemaWithId =
      schemaResolver.apply(schemaId).orElseThrow { IllegalArgumentException("Can not resolve writer schema for id=$schemaId.") }

    return BinaryMessageDecoder<GenericData.Record>(GenericData.get(), avroSchemaWithId.schema).decode(bytes)
  }
}
