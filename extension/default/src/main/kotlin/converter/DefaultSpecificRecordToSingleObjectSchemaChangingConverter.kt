package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSchemaIncompatibilityResolver
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaResolver
import io.holixon.avro.adapter.api.converter.SpecificRecordToSingleObjectConverter
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.AvroAdapterDefault.DecoderSpecificRecordClassResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault.toByteArray
import io.holixon.avro.adapter.common.DefaultSchemaStore
import org.apache.avro.message.BinaryMessageDecoder
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Converts any instance derived from [SpecificRecordBase] (generated from avsc) to a [ByteArray] that follows the format specified
 * in the [avro specs](https://avro.apache.org/docs/current/spec.html#single_object_encoding).
 *
 * If encoding back, the schema change is performed from Writer Schema to Reader Schema.
 */
class DefaultSpecificRecordToSingleObjectSchemaChangingConverter @JvmOverloads constructor(
  schemaResolver: SchemaResolver,
  decoderSpecificRecordClassResolver: DecoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
  schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver = AvroAdapterDefault.defaultSchemaCompatibilityResolver
) : SpecificRecordToSingleObjectConverter {

  private val schemaStore = DefaultSchemaStore(schemaResolver)
  private val schemaResolutionSupport: SchemaResolutionSupport =
    SchemaResolutionSupport(schemaResolver, decoderSpecificRecordClassResolver, schemaIncompatibilityResolver)

  override fun <T : SpecificRecordBase> encode(data: T): AvroSingleObjectEncoded = data.toByteArray()

  override fun <T : SpecificRecordBase> decode(bytes: AvroSingleObjectEncoded): T {
    // resolve reader schema
    val resolvedReaderSchema = schemaResolutionSupport.resolveReaderSchema(bytes).schema
    // construct decoder and decode
    return BinaryMessageDecoder<T>(SpecificData(), resolvedReaderSchema, schemaStore).decode(bytes)
  }
}

