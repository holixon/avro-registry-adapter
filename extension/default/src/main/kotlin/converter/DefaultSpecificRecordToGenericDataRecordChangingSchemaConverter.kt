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
 * Converts any instance derived from [SpecificRecordBase] (generated from avsc) to a [GenericData.Record].
 * On decoding, the schema change from Writer to Reader takes place.
 */
class DefaultSpecificRecordToGenericDataRecordChangingSchemaConverter @JvmOverloads constructor(
  schemaResolver: SchemaResolver,
  decoderSpecificRecordClassResolver: DecoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
  schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver = AvroAdapterDefault.defaultSchemaCompatibilityResolver
) : SpecificRecordToGenericDataRecordConverter {

  private val schemaResolutionSupport: SchemaResolutionSupport =
    SchemaResolutionSupport(schemaResolver, decoderSpecificRecordClassResolver, schemaIncompatibilityResolver)

  override fun <T : SpecificRecordBase> encode(data: T): GenericData.Record {
    return data.toGenericDataRecord()
  }

  /**
   * Decodes the generic record into specific record.
   * Will change the schema from writer schema to reader schema.
   */
  override fun <T : SpecificRecordBase> decode(record: GenericData.Record): T {
    val readerSchema = schemaResolutionSupport.resolveReaderSchema(record)
    return record.toSpecificDataRecord(readerSchema.schema)
  }
}

