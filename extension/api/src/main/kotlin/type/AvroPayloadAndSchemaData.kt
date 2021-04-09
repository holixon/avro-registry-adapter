package io.holixon.avro.adapter.api.type

import io.holixon.avro.adapter.api.AvroPayloadAndSchema
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded

data class AvroPayloadAndSchemaData(
  override val schema: AvroSchemaWithId,
  override val payload: AvroSingleObjectEncoded
) : AvroPayloadAndSchema {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as AvroPayloadAndSchemaData

    if (schema != other.schema) return false
    if (!payload.contentEquals(other.payload)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = schema.hashCode()
    result = 31 * result + payload.contentHashCode()
    return result
  }
}
