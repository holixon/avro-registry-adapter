package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaResolver
import io.holixon.avro.adapter.api.converter.GenericDataRecordToSingleObjectConverter
import io.holixon.avro.adapter.common.decoder.DefaultSingleObjectToGenericDataRecordDecoder
import io.holixon.avro.adapter.common.encoder.DefaultGenericDataRecordToSingleObjectEncoder
import org.apache.avro.generic.GenericData

/**
 * Default converter implementation between single object bytes and [GenericData.Record].
 * The [GenericData.Record] will have the same schema as the bytes.
 */
@Deprecated("single object should be done with decoder/encoder, not converter")
class DefaultGenericDataRecordToSingleObjectConverter(
  schemaResolver: SchemaResolver
) : GenericDataRecordToSingleObjectConverter {

  private val encoder = DefaultGenericDataRecordToSingleObjectEncoder()
  private val decoder = DefaultSingleObjectToGenericDataRecordDecoder(schemaResolver)

  override fun encode(data: GenericData.Record): AvroSingleObjectEncoded  = encoder.encode(data)

  override fun decode(bytes: AvroSingleObjectEncoded): GenericData.Record = decoder.decode(bytes)
}
