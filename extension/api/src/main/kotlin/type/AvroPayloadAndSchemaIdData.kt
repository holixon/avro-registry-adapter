package io.holixon.avro.adapter.api.type

import io.holixon.avro.adapter.api.AvroPayloadAndSchemaId
import io.holixon.avro.adapter.api.AvroSingleObjectPayload
import io.holixon.avro.adapter.api.AvroSchemaId

data class AvroPayloadAndSchemaIdData(
  override val schemaId : AvroSchemaId,
  override val payload : AvroSingleObjectPayload
) : AvroPayloadAndSchemaId {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as AvroPayloadAndSchemaIdData

    if (schemaId != other.schemaId) return false
    if (!payload.contentEquals(other.payload)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = schemaId.hashCode()
    result = 31 * result + payload.contentHashCode()
    return result
  }

}
