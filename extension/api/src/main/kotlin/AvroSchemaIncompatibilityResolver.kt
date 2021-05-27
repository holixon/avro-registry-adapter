package io.holixon.avro.adapter.api

import org.apache.avro.Schema
import org.apache.avro.SchemaCompatibility
import org.apache.avro.message.SchemaStore

/**
 * Resolves incompatibility check result and delivers the reader schema used by decoding.
 */
fun interface AvroSchemaIncompatibilityResolver {
  /**
   * Resolves possible schema incompatibility.
   * @return reader schema to use.
   */
  fun resolve(
      readerSchema: Schema,
      writerSchema: Schema
  ): Schema
}
