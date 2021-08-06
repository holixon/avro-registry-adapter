package io.holixon.avro.adapter.api

import java.util.*
import java.util.function.Function

/**
 * Search schema based on schemaId.
 *
 * @see AvroSchemaRegistry.findById
 */
fun interface AvroSchemaResolver : Function<AvroSchemaId, Optional<AvroSchemaWithId>>
