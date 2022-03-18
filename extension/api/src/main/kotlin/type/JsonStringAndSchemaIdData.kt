package io.holixon.avro.adapter.api.type

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.JsonString
import io.holixon.avro.adapter.api.JsonStringAndSchemaId
import java.io.Serializable

/**
 * Data class implementation of the [JsonStringAndSchemaId].
 */
data class JsonStringAndSchemaIdData(
  override val schemaId: AvroSchemaId,
  override val json: JsonString,
) : JsonStringAndSchemaId, Serializable
