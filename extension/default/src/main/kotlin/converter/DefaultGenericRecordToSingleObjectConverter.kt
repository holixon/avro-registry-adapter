package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSchemaIncompatibilityResolver
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaResolver
import io.holixon.avro.adapter.api.converter.GenericRecordToSingleObjectConverter
import io.holixon.avro.adapter.common.AvroAdapterDefault
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.message.BinaryMessageDecoder
import org.apache.avro.message.BinaryMessageEncoder
import org.apache.avro.specific.SpecificRecordBase
import java.io.ByteArrayOutputStream

class DefaultGenericRecordToSingleObjectConverter(
  schemaResolver: SchemaResolver,
  decoderSpecificRecordClassResolver: AvroAdapterDefault.DecoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
  schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver = AvroAdapterDefault.defaultSchemaCompatibilityResolver,
) : GenericRecordToSingleObjectConverter {

  private val readerSchemaResolver: DefaultReaderSchemaResolver =
    DefaultReaderSchemaResolver(schemaResolver, decoderSpecificRecordClassResolver, schemaIncompatibilityResolver)

  override fun <T : SpecificRecordBase> encode(data: T): AvroSingleObjectEncoded {
    val byteStream = ByteArrayOutputStream()
    BinaryMessageEncoder<T>(GenericData.get(), data.schema).encode(data, byteStream)
    return byteStream.toByteArray()
  }

  override fun decode(bytes: AvroSingleObjectEncoded): GenericRecord {
    // resolve reader schema
    val resolvedReaderSchema = readerSchemaResolver.resolveReaderSchema(bytes)
    return BinaryMessageDecoder<GenericRecord>(GenericData.get(), resolvedReaderSchema).decode(bytes)
  }
}
