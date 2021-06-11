package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSchemaIncompatibilityResolver
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.AvroAdapterDefault.getSchema
import io.holixon.avro.adapter.common.AvroAdapterDefault.readPayloadAndSchemaId
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecordBase

/**
 * Default reader schema resolver.
 */
class DefaultReaderSchemaResolver(
  val schemaResolver: SchemaResolver,
  val decoderSpecificRecordClassResolver: AvroAdapterDefault.DecoderSpecificRecordClassResolver,
  val schemaIncompatibilityResolver: AvroSchemaIncompatibilityResolver
) {
  /**
   * Resolves reader schema for incoming SingleObjectEncoded.
   */
  fun resolveReaderSchema(bytes: AvroSingleObjectEncoded): Schema {
    // get the reader schema id from the single object encoded bytes
    val schemaId = bytes.readPayloadAndSchemaId().schemaId
    // load writer schema info from schema resolver
    val writerSchemaWithId =
      schemaResolver.apply(schemaId).orElseThrow { IllegalArgumentException("Can not resolve writer schema for id=$schemaId.") }
    // we have to assume that the namespace and name of the message payload did not change, so we try to load the class based on the schema info
    // of the writer schema. This might lead to another (earlier or later) revision, but the canonical name should not have changed.
    val targetClass: Class<SpecificRecordBase> = decoderSpecificRecordClassResolver.apply(writerSchemaWithId)
    // get reader schema from the class
    val readerSchema = targetClass.getSchema()
    // resolve incompatibilities if any and return the resulting reader schema
    return schemaIncompatibilityResolver.resolve(readerSchema, writerSchemaWithId.schema)
  }
}
