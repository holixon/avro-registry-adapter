package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.AvroPayloadAndSchemaId
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.ext.ByteArrayExt.buffer
import io.holixon.avro.adapter.api.ext.ByteArrayExt.extract
import io.holixon.avro.adapter.api.ext.ByteArrayExt.split
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.api.repository.InMemoryAvroSchemaRegistry
import io.holixon.avro.adapter.api.type.AvroPayloadAndSchemaIdData
import java.nio.ByteBuffer
import java.nio.ByteOrder

object AvroAdapterDefault {

  const val PROPERTY_REVISION = "revision"
  val V1_HEADER = byteArrayOf(-61, 1) // [C3 01]
  private val HEADER_LENGTH = V1_HEADER.size + Long.SIZE_BYTES

  fun AvroSingleObjectEncoded.readPayloadAndSchemaId(): AvroPayloadAndSchemaId {
    require(this.size > HEADER_LENGTH) { "single object encoded bytes must have at least length > $HEADER_LENGTH, was: $size" }
    require(this.isAvroSingleObjectEncoded()) { "single object encoded bytes need to start with ${V1_HEADER.toHexString()}" }

    val (_, idBytes, payloadBytes) = this.split(V1_HEADER.size, HEADER_LENGTH)

    return AvroPayloadAndSchemaIdData(
      schemaId = "${idBytes.readLong()}",
      payload = payloadBytes
    )
  }

  fun ByteArray.readLong(): Long {
    require(this.size == Long.SIZE_BYTES) { "size must be exactly Long.SIZE_BYTES (${Long.SIZE_BYTES}" }
    return this.buffer().order(ByteOrder.LITTLE_ENDIAN).long
  }

  @JvmStatic
  fun ByteBuffer.isAvroSingleObjectEncoded(): Boolean = extract(0, V1_HEADER.size).contentEquals(V1_HEADER)

  @JvmStatic
  fun ByteArray.isAvroSingleObjectEncoded(): Boolean = buffer().isAvroSingleObjectEncoded()

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
