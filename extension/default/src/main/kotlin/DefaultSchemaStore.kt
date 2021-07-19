package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.AvroSchemaResolver
import org.apache.avro.Schema
import org.apache.avro.message.SchemaStore

/**
 * Schema store using the schema resolver.
 */
class DefaultSchemaStore(private val schemaResolver: AvroSchemaResolver) : SchemaStore {

  override fun findByFingerprint(fingerprint: Long): Schema? = schemaResolver
    .apply(fingerprint.toString())
    .map { it.schema }
    .orElse(null)
}
