package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSchemaIncompatibilityResolver
import io.holixon.avro.adapter.api.SchemaResolver
import io.holixon.avro.adapter.api.converter.SpecificRecordToGenericDataRecordConverter
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.AvroAdapterDefault.DecoderSpecificRecordClassResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault.toGenericDataRecord
import io.holixon.avro.adapter.common.AvroAdapterDefault.toSpecificDataRecord
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Converts any instance derived from [SpecificRecordBase] (generated from avsc) to a [GenericData.Record] that follows the format specified
 * in the [avro specs](https://avro.apache.org/docs/current/spec.html#single_object_encoding).
 */
class DefaultSpecificRecordToGenericDataRecordConverter @JvmOverloads constructor(
  schemaResolver: SchemaResolver,
  decoderSpecificRecordClassResolver: DecoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
  schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver = AvroAdapterDefault.defaultSchemaCompatibilityResolver
) : SpecificRecordToGenericDataRecordConverter {

  private val defaultReaderSchemaResolver: DefaultReaderSchemaResolver =
    DefaultReaderSchemaResolver(schemaResolver, decoderSpecificRecordClassResolver, schemaIncompatibilityResolver)

  override fun <T : SpecificRecordBase> encode(data: T): GenericData.Record {
    return data.toGenericDataRecord()
  }

  override fun <T : SpecificRecordBase> decode(record: GenericData.Record): T {
    val readerSchema = defaultReaderSchemaResolver.resolveReaderSchema(record)
    return record.toSpecificDataRecord(defaultReaderSchemaResolver.getClassForSchema(readerSchema))
  }
}

