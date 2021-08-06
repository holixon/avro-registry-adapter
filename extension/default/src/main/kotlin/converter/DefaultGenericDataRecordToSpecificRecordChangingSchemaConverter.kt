package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSchemaIncompatibilityResolver
import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.api.converter.GenericDataRecordToSpecificRecordConverter
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.AvroAdapterDefault.toSpecificDataRecord
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase


/**
 * Converts the generic record into specific record.
 * Will change the schema from writer schema to reader schema.
 */
class DefaultGenericDataRecordToSpecificRecordChangingSchemaConverter(
  private val schemaResolutionSupport: SchemaResolutionSupport
) : GenericDataRecordToSpecificRecordConverter {

  @JvmOverloads
  constructor(
    schemaResolver: AvroSchemaResolver,
    decoderSpecificRecordClassResolver: AvroAdapterDefault.DecoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
    schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver = AvroAdapterDefault.defaultSchemaCompatibilityResolver
  ) : this(
    schemaResolutionSupport = SchemaResolutionSupport(schemaResolver, decoderSpecificRecordClassResolver, schemaIncompatibilityResolver)
  )

  override fun <T : SpecificRecordBase> convert(record: GenericData.Record): T {
    val readerSchema = schemaResolutionSupport.resolveReaderSchema(record)
    return record.toSpecificDataRecord(readerSchema.schema)
  }
}
