package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.AvroPayloadAndSchemaId
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.ext.ByteArrayExt.buffer
import io.holixon.avro.adapter.api.ext.ByteArrayExt.extract
import io.holixon.avro.adapter.api.ext.ByteArrayExt.split
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.api.repository.InMemoryAvroSchemaRegistry
import io.holixon.avro.adapter.api.type.AvroPayloadAndSchemaIdData
import io.holixon.avro.adapter.common.AvroAdapterDefault.DecoderSpecificRecordClassResolver
import org.apache.avro.SchemaNormalization
import org.apache.avro.specific.SpecificRecordBase
import org.apache.avro.util.ClassUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.function.Function

object AvroAdapterDefault {

  const val PROPERTY_REVISION = "revision"

  /**
   * Marker bytes according to Avro schema specification v1.
   */
  @JvmField
  val AVRO_V1_HEADER = byteArrayOf(-61, 1) // [C3 01]
  private val AVRO_HEADER_LENGTH = AVRO_V1_HEADER.size + Long.SIZE_BYTES

  /**
   * Read payload and schema id from Avro single object encoded ball.
   */
  fun AvroSingleObjectEncoded.readPayloadAndSchemaId(): AvroPayloadAndSchemaId {
    require(this.size > AVRO_HEADER_LENGTH) { "Single object encoded bytes must have at least length > $AVRO_HEADER_LENGTH, was: $size." }
    require(this.isAvroSingleObjectEncoded()) { "Single object encoded bytes need to start with ${AVRO_V1_HEADER.toHexString()}." }

    val (_, idBytes, payloadBytes) = this.split(AVRO_V1_HEADER.size, AVRO_HEADER_LENGTH)

    return AvroPayloadAndSchemaIdData(
      schemaId = "${idBytes.readLong()}",
      payload = payloadBytes
    )
  }

  private fun ByteArray.readLong(): Long {
    require(this.size == Long.SIZE_BYTES) { "Size must be exactly Long.SIZE_BYTES (${Long.SIZE_BYTES}." }
    return this.buffer().order(ByteOrder.LITTLE_ENDIAN).long
  }

  @JvmStatic
  fun ByteBuffer.isAvroSingleObjectEncoded(): Boolean = extract(0, AVRO_V1_HEADER.size).contentEquals(AVRO_V1_HEADER)

  @JvmStatic
  fun ByteArray.isAvroSingleObjectEncoded(): Boolean = buffer().isAvroSingleObjectEncoded()

  @JvmField
  val schemaRevisionResolver = DefaultSchemaRevisionResolver()

  /**
   * Implements [SchemaIdSupplier] by using SchemaNormalization#parsingFingerprint64(Schema).
   */
  @JvmField
  val schemaIdSupplier = DefaultSchemaIdSupplier()

  /**
   * Create a in-memory schema registry using [SchemaNormalization.parsingFingerprint64] and [DefaultSchemaRevisionResolver].
   */
  fun inMemorySchemaRepository() = InMemoryAvroSchemaRegistry(
    schemaIdSupplier = schemaIdSupplier,
    schemaRevisionResolver = schemaRevisionResolver
  )

  /**
   * Reflective access using the method of generated specific record to access byte buffer representation.
   */
  @JvmStatic
  fun SpecificRecordBase.toByteBuffer(): ByteBuffer = this.javaClass.getDeclaredMethod("toByteBuffer").invoke(this) as ByteBuffer

  /**
   * Reflective access using the method of generated specific record to access byte array representation.
   * @see toByteBuffer
   */
  @JvmStatic
  fun SpecificRecordBase.toByteArray(): ByteArray = this.toByteBuffer().array()

  /**
   * Reflective access using the method of generated specific record to access specific record base representation.
   */
  @JvmStatic
  fun Class<SpecificRecordBase>.fromByteArray(bytes: ByteArray) = this.getDeclaredMethod("fromByteBuffer", ByteBuffer::class.java)
    .invoke(null, ByteBuffer.wrap(bytes)) as SpecificRecordBase

  /**
   * Resolver for a concrete class used by decoding of Avro single object into Avro specific record.
   */
  fun interface DecoderSpecificRecordClassResolver : Function<AvroSchemaWithId, Class<SpecificRecordBase>>

  /**
   * Default implementation using [Class.forName].
   */
  @Suppress("UNCHECKED_CAST")
  val reflectionBasedDecoderSpecificRecordClassResolver = DecoderSpecificRecordClassResolver {
    ClassUtils.forName(it.canonicalName) as Class<SpecificRecordBase>
  }
}
