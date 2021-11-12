package io.holixon.avro.adapter.registry.axon.api

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaInfo
import java.util.function.Predicate

/**
 * Find the query by id.
 */
data class FindSchemaById(
  val schemaId: AvroSchemaId
) {
  companion object {
    internal fun predicate(schemaId: AvroSchemaId): Predicate<FindSchemaById> = Predicate { query -> query.schemaId == schemaId }
  }
}

/**
 * Find the query by info.
 */
data class FindSchemaByInfo(
  val info: AvroSchemaInfo
)

/**
 * Find all schema by canonical name.
 */
data class FindAllSchemaByCanonicalName(
  val namespace: String,
  val name: String
)

/**
 * Find all schema.
 */
class FindAllSchema
