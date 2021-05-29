package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSchemaIncompatibilityResolver
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaResolver
import io.holixon.avro.adapter.api.converter.SpecificRecordToSingleObjectConverter
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.AvroAdapterDefault.DecoderSpecificRecordClassResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault.getSchema
import io.holixon.avro.adapter.common.AvroAdapterDefault.readPayloadAndSchemaId
import io.holixon.avro.adapter.common.AvroAdapterDefault.toByteArray
import io.holixon.avro.adapter.common.DefaultSchemaStore
import org.apache.avro.message.BinaryMessageDecoder
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Converts any instance derived from [SpecificRecordBase] (generated from avsc) to a [ByteArray] that follows the format specified
 * in the [avro specs](https://avro.apache.org/docs/current/spec.html#single_object_encoding).
 */
class DefaultSpecificRecordToSingleObjectConverter @JvmOverloads constructor(
  private val schemaResolver: SchemaResolver,
  private val decoderSpecificRecordClassResolver: DecoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
  private val schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver = AvroAdapterDefault.defaultSchemaCompatibilityResolver
) : SpecificRecordToSingleObjectConverter {

  private val schemaStore = DefaultSchemaStore(schemaResolver)

  override fun <T : SpecificRecordBase> encode(data: T): AvroSingleObjectEncoded = data.toByteArray()


  override fun <T : SpecificRecordBase> decode(bytes: AvroSingleObjectEncoded): T {

    // get the reader schema id from the single object encoded bytes
    val schemaId = bytes.readPayloadAndSchemaId().schemaId
    // load writer schema info from schema resolver
    val writerSchemaWithId =
      schemaResolver.apply(schemaId).orElseThrow { IllegalArgumentException("Can not resolve writer schema for id=$schemaId.") }
    // we have to assume that the namespace and name of the message payload did not change, so we try to load the class based on the schema info
    // of the writer schema. This might lead to another (earlier or later) revision, but the canonical name should not have changed.
    val targetClass: Class<SpecificRecordBase> = decoderSpecificRecordClassResolver.apply(writerSchemaWithId)
    // get reader schema from the class
    val readerSchema = targetClass.getSchema()

    // resolve incompatibilities if any and return the resulting reader schema
    val resolvedReaderSchema = schemaIncompatibilityResolver.resolve(readerSchema, writerSchemaWithId.schema)

    // construct decoder and decode
    return BinaryMessageDecoder<T>(SpecificData(), resolvedReaderSchema, schemaStore).decode(bytes)
  }
}

