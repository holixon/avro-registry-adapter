package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.*
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.AvroAdapterDefault.getSchema
import io.holixon.avro.adapter.common.AvroAdapterDefault.readPayloadAndSchemaId
import io.holixon.avro.adapter.common.AvroAdapterDefault.toAvroSchemaWithId
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Default reader schema resolver.
 */
class DefaultReaderSchemaResolver(
  private val schemaResolver: SchemaResolver,
  private val decoderSpecificRecordClassResolver: AvroAdapterDefault.DecoderSpecificRecordClassResolver,
  private val schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver
) {
  /**
   * Resolves reader schema for incoming SingleObjectEncoded.
   */
  fun resolveReaderSchema(bytes: AvroSingleObjectEncoded): AvroSchemaWithId {
    // get the reader schema id from the single object encoded bytes
    val schemaId = bytes.readPayloadAndSchemaId().schemaId
    return resolveReaderSchema(schemaId)
  }

  /**
   * Resolves reader schema for incoming record.
   */
  fun resolveReaderSchema(record: GenericData.Record): AvroSchemaWithId {
    return resolveReaderSchema(record.schema.toAvroSchemaWithId().schemaId)
  }

  /**
   * Resolves the class for given schema id.
   * @param avroSchemaWithId schema with id.
   * @return class for given schema.
   */
  @Suppress("UNCHECKED_CAST")
  fun <T : SpecificRecordBase> getClassForSchema(avroSchemaWithId: AvroSchemaWithId): Class<T> =
    decoderSpecificRecordClassResolver.apply(avroSchemaWithId) as Class<T>
  
  /**
   * Resolve reader schema for a given writer schema.
   */
  private fun resolveReaderSchema(schemaId: AvroSchemaId): AvroSchemaWithId {
    // load writer schema info from schema resolver
    val writerSchemaWithId =
      schemaResolver.apply(schemaId).orElseThrow { IllegalArgumentException("Can not resolve writer schema for id=$schemaId.") }
    // we have to assume that the namespace and name of the message payload did not change, so we try to load the class based on the schema info
    // of the writer schema. This might lead to another (earlier or later) revision, but the canonical name should not have changed.
    val targetClass: Class<SpecificRecordBase> = getClassForSchema(writerSchemaWithId)
    // get reader schema from the class
    val readerSchema = targetClass.getSchema()
    // resolve incompatibilities if any and return the resulting reader schema
    return schemaIncompatibilityResolver.resolve(readerSchema, writerSchemaWithId.schema).toAvroSchemaWithId()
  }
}
