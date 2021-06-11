package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.AvroPayloadAndSchemaId
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.ext.ByteArrayExt.buffer
import io.holixon.avro.adapter.api.ext.ByteArrayExt.extract
import io.holixon.avro.adapter.api.ext.ByteArrayExt.split
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.api.type.AvroPayloadAndSchemaIdData
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.adapter.common.AvroAdapterDefault.DecoderSpecificRecordClassResolver
import io.holixon.avro.adapter.common.converter.DefaultSchemaCompatibilityResolver
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaRegistry
import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization
import org.apache.avro.data.RecordBuilderBase
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase
import org.apache.avro.util.ClassUtils
import java.lang.reflect.Method
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.function.Function

/**
 * Collection of default adapter helper functions.
 */
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
  fun inMemorySchemaRegistry() = InMemoryAvroSchemaRegistry(
    schemaIdSupplier = schemaIdSupplier,
    schemaRevisionResolver = schemaRevisionResolver
  )

  /**
   * Helper to create [AvroSchemaWithId] from given [Schema], using [DefaultSchemaIdSupplier] and [DefaultSchemaRevisionResolver].
   */
  fun Schema.toAvroSchemaWithId() = AvroSchemaWithIdData(
    schemaId = schemaIdSupplier.apply(this),
    schema = this,
    revision = schemaRevisionResolver.apply(this).orElse(null)
  )

  /**
   * Reflective access using the method of generated specific record to access byte buffer representation.
   */
  @JvmStatic
  fun SpecificRecordBase.toByteBuffer(): ByteBuffer = this.javaClass.getDeclaredMethod("toByteBuffer").invoke(this) as ByteBuffer

  /**
   * Retrieves the embedded builder from the specific record base type.
   */
  @Suppress("UNCHECKED_CAST")
  fun <T : SpecificRecordBase> Class<T>.getDefaultBuilder(): RecordBuilderBase<T> =
    this.getDeclaredMethod("newBuilder").invoke(null) as RecordBuilderBase<T>

  /**
   * Retrieves the embedded builder from the specific record base type.
   */
  @Suppress("UNCHECKED_CAST")
  fun SpecificRecordBase.getCopyingBuilder(): RecordBuilderBase<*> =
    this.javaClass.getDeclaredMethod("newBuilder", this.javaClass).invoke(null, this) as RecordBuilderBase<*>

  /**
   * Retrieves fields from the builder.
   */
  @Suppress("UNCHECKED_CAST")
  fun RecordBuilderBase<*>.getFields(): Array<Schema.Field> =
    RecordBuilderBase::class.java.getDeclaredMethod("fields").apply { trySetAccessible() }.invoke(this) as Array<Schema.Field>

  /**
   * Retrieves fields from the builder.
   */
  @Suppress("UNCHECKED_CAST")
  fun RecordBuilderBase<*>.getFieldFlags(): BooleanArray =
    RecordBuilderBase::class.java.getDeclaredMethod("fieldSetFlags").apply { trySetAccessible() }.invoke(this) as BooleanArray

  /**
   * Retrieves the validate method from the builder.
   */
  fun RecordBuilderBase<*>.validateMethod(): Method =
    RecordBuilderBase::class.java
      .getDeclaredMethod("validate", Schema.Field::class.java, Object::class.java)
      .apply {
        trySetAccessible()
      }

  /**
   * Reflective access using the method of generated specific record to access data fields.
   */
  @JvmStatic
  fun <T : SpecificRecordBase> T.toGenericDataRecord(): GenericData.Record {

    val builder = this.getCopyingBuilder()
    val fields = builder.getFields()
    val record = GenericData.Record(this.schema)
    fields.forEach {
      record.put(it.name(), this[it.name()])
    }
    return record
  }

  /**
   * Reflective calling the builder and building the specific record.
   */
  @JvmStatic
  fun <T : SpecificRecordBase> GenericData.Record.toSpecificDataRecord(clazz: Class<T>): T {

    val builder = clazz.getDefaultBuilder()
    val fields = builder.getFields()
    val fieldFlags: BooleanArray = builder.getFieldFlags()
    val validateMethod = builder.validateMethod()

    val specificRecord: T = clazz.getConstructor().newInstance()

    fields.forEach {
      validateMethod.invoke(builder, it, this[it.name()])
      fieldFlags[it.pos()] = true
      specificRecord.put(it.pos(), this[it.name()])
    }
    return specificRecord
  }

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
   * Retrieves the schema from generated class extending specific record class.
   */
  @JvmStatic
  fun Class<SpecificRecordBase>.getSchema() = this.getDeclaredField("SCHEMA$").get(null) as Schema

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

  /**
   * Default schema compatibility resolver throwing exception on any incompatibility.
   */
  @JvmField
  val defaultSchemaCompatibilityResolver = DefaultSchemaCompatibilityResolver()
}
