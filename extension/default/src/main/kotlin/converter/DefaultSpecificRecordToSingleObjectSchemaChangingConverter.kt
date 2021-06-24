package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSchemaIncompatibilityResolver
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaResolver
import io.holixon.avro.adapter.api.converter.SpecificRecordToSingleObjectConverter
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.AvroAdapterDefault.DecoderSpecificRecordClassResolver
import io.holixon.avro.adapter.common.decoder.DefaultSingleObjectToSpecificRecordDecoder
import io.holixon.avro.adapter.common.encoder.DefaultSpecificRecordToSingleObjectEncoder
import org.apache.avro.specific.SpecificRecordBase

/**
 * Converts any instance derived from [SpecificRecordBase] (generated from avsc) to a [ByteArray] that follows the format specified
 * in the [avro specs](https://avro.apache.org/docs/current/spec.html#single_object_encoding).
 *
 * If encoding back, the schema change is performed from Writer Schema to Reader Schema.
 */
@Deprecated("single object should be done with decoder/encoder, not converter")
class DefaultSpecificRecordToSingleObjectSchemaChangingConverter
@JvmOverloads
constructor(
  schemaResolver: SchemaResolver,
  decoderSpecificRecordClassResolver: DecoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
  schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver = AvroAdapterDefault.defaultSchemaCompatibilityResolver
) : SpecificRecordToSingleObjectConverter {

  private val encoder = DefaultSpecificRecordToSingleObjectEncoder()
  private val decoder =
    DefaultSingleObjectToSpecificRecordDecoder(schemaResolver, decoderSpecificRecordClassResolver, schemaIncompatibilityResolver)

  override fun <T : SpecificRecordBase> encode(data: T): AvroSingleObjectEncoded = encoder.encode(data)

  override fun <T : SpecificRecordBase> decode(bytes: AvroSingleObjectEncoded): T = decoder.decode(bytes)
}

