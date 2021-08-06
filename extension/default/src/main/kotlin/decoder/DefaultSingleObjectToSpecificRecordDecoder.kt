package io.holixon.avro.adapter.common.decoder

import io.holixon.avro.adapter.api.AvroSchemaIncompatibilityResolver
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.api.decoder.SingleObjectToSpecificRecordDecoder
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.DefaultSchemaStore
import io.holixon.avro.adapter.common.converter.SchemaResolutionSupport
import org.apache.avro.message.BinaryMessageDecoder
import org.apache.avro.message.SchemaStore
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Decoder that extracts the writer schema from the given single object bytes and resolves a
 * matching reader class using [SchemaResolutionSupport].
 *
 * Attention: The returned instance is compliant to the reader schema, since the concrete schema
 * used to decode might differ from the one used for encoding.
 */
class DefaultSingleObjectToSpecificRecordDecoder(
  private val schemaStore: SchemaStore,
  private val schemaResolutionSupport: SchemaResolutionSupport
) : SingleObjectToSpecificRecordDecoder {

  @JvmOverloads
  constructor(
    schemaResolver: AvroSchemaResolver,
    decoderSpecificRecordClassResolver: AvroAdapterDefault.DecoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
    schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver = AvroAdapterDefault.defaultSchemaCompatibilityResolver
  ) : this(
    schemaStore = DefaultSchemaStore(schemaResolver),
    schemaResolutionSupport = SchemaResolutionSupport(schemaResolver, decoderSpecificRecordClassResolver, schemaIncompatibilityResolver)
  )

  override fun <T : SpecificRecordBase> decode(bytes: AvroSingleObjectEncoded): T {
    // resolve reader schema
    val resolvedReaderSchema = schemaResolutionSupport.resolveReaderSchema(bytes).schema
    // construct decoder and decode
    return BinaryMessageDecoder<T>(SpecificData(), resolvedReaderSchema, schemaStore).decode(bytes)
  }
}
