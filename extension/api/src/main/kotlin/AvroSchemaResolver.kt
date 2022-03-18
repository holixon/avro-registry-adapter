package io.holixon.avro.adapter.api

import java.util.*
import java.util.function.Function

/**
 * Search schema based on schemaId.
 *
 * @see AvroSchemaRegistry.findById
 */
fun interface AvroSchemaResolver : Function<AvroSchemaId, Optional<AvroSchemaWithId>>

/**
 * Non-optional load of the schema.
 *
 * @throws IllegalStateException when schema can not be found
 * @param schemaId the id (fingerprint) of the schema to load
 * @see AvroSchemaResolver.apply
 * @return the resolved schema
 */
fun AvroSchemaResolver.loadById(schemaId: AvroSchemaId) = apply(schemaId)
  .orElseThrow { SchemaNotFoundException(schemaId) }

/**
 * Indicates that a schema could not be resolved for a given [AvroSchemaId].
 */
class SchemaNotFoundException(schemaId: AvroSchemaId) : IllegalStateException("Can not resolve schema for id=$schemaId.")
