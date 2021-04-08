package io.toolisticon.avro.adapter.common

import io.toolisticon.avro.adapter.api.AvroAdapterApi.toHexString
import io.toolisticon.avro.adapter.api.AvroSingleObjectEncoded
import io.toolisticon.avro.adapter.api.SchemaId
import io.toolisticon.avro.adapter.api.SchemaIdSupplier
import io.toolisticon.avro.adapter.api.repository.InMemoryAvroSchemaRegistry
import java.nio.ByteBuffer
import java.nio.ByteOrder

object AvroAdapterDefault {

  const val PROPERTY_REVISION = "revision"
  val V1_HEADER = byteArrayOf(-61, 1)
  private const val LONG_LENGTH = 8
  private val HEADER_LENGTH = V1_HEADER.size + LONG_LENGTH

  /**
   * Extracts [SchemaId] and remaining bytes payload from [AvroSingleObjectEncoded] byte array.
   *
   * Fails if given bytes do not follow the avro single object v1 spec.
   */
  data class SchemaIdAndPayload(val schemaId: SchemaId, val payload: ByteArray) {
    companion object {
      operator fun invoke(bytes: AvroSingleObjectEncoded): SchemaIdAndPayload {
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        require(buffer.remaining() > HEADER_LENGTH) { "Needs to have minlength $HEADER_LENGTH" }
        require(buffer[0] == V1_HEADER[0] && buffer[1] == V1_HEADER[1]) { "bytes need to start with ${V1_HEADER.toHexString()}" }

        return SchemaIdAndPayload(
          schemaId = buffer.getLong(2).toString(),
          payload = bytes.copyOfRange(HEADER_LENGTH, bytes.size)
        )
      }
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as SchemaIdAndPayload

      if (schemaId != other.schemaId) return false
      if (!payload.contentEquals(other.payload)) return false

      return true
    }

    override fun hashCode(): Int {
      var result = schemaId.hashCode()
      result = 31 * result + payload.contentHashCode()
      return result
    }

    override fun toString(): String {
      return "SchemaIdAndPayload(schemaId=$schemaId, payload=${payload.toHexString()})"
    }
  }

  val schemaRevisionResolver = DefaultSchemaRevisionResolver()

  /**
   * Implements [SchemaIdSupplier] by using SchemaNormalization#parsingFingerprint64(Schema).
   */
  val schemaIdSupplier = DefaultSchemaIdSupplier()

  fun inMemorySchemaRepository() = InMemoryAvroSchemaRegistry(
    schemaIdSupplier = schemaIdSupplier,
    schemaRevisionResolver = schemaRevisionResolver
  )
}
