package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSchemaIncompatibilityResolver
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaResolver
import io.holixon.avro.adapter.api.converter.GenericDataRecordToSingleObjectConverter
import io.holixon.avro.adapter.common.AvroAdapterDefault
import org.apache.avro.generic.GenericData
import org.apache.avro.message.BinaryMessageDecoder
import org.apache.avro.message.BinaryMessageEncoder
import java.io.ByteArrayOutputStream

/**
 * Default converter implementation between single object bytes and [GenericData.Record].
 */
class DefaultGenericDataRecordToSingleObjectConverter(
  schemaResolver: SchemaResolver,
  decoderSpecificRecordClassResolver: AvroAdapterDefault.DecoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
  schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver = AvroAdapterDefault.defaultSchemaCompatibilityResolver,
) : GenericDataRecordToSingleObjectConverter {

  private val readerSchemaResolver: DefaultReaderSchemaResolver =
    DefaultReaderSchemaResolver(schemaResolver, decoderSpecificRecordClassResolver, schemaIncompatibilityResolver)

  override fun encode(data: GenericData.Record): AvroSingleObjectEncoded {
    val byteStream = ByteArrayOutputStream()
    BinaryMessageEncoder<GenericData.Record>(GenericData.get(), data.schema).encode(data, byteStream)
    return byteStream.toByteArray()
  }

  override fun decode(bytes: AvroSingleObjectEncoded): GenericData.Record {
    // resolve reader schema
    val resolvedReaderSchema = readerSchemaResolver.resolveReaderSchema(bytes)
    return BinaryMessageDecoder<GenericData.Record>(GenericData.get(), resolvedReaderSchema.schema).decode(bytes)
  }
}
