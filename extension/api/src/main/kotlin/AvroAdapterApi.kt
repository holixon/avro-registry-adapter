package io.holixon.avro.adapter.api

import io.holixon.avro.adapter.api.type.*
import org.apache.avro.Schema
import org.apache.avro.message.SchemaStore
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecordBase
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate
import kotlin.reflect.KClass

/**
 * Returns a unique id for the given schema that is used to load the schema from a repository.
 */
interface SchemaIdSupplier : Function<Schema, SchemaId>

/**
 * Search schema based on schemaId.
 */
interface SchemaResolver : Function<SchemaId, Optional<AvroSchemaWithId>>

/**
 * Takes encoded avro bytes (containing schema reference) and return decoded payload and the resolved schema.
 */
interface SingleObjectDecoder : Function<AvroSingleObjectEncoded, AvroPayloadAndSchema>

/**
 * Use given Schema information and bytecode paylod to return decoded bytes.
 */
interface SingleObjectEncoder : BiFunction<AvroSchemaWithId, ByteArray, AvroSingleObjectEncoded>

/**
 * Returns the revision for a given schema.
 */
interface SchemaRevisionResolver : Function<Schema, Optional<SchemaRevision>>

/**
 * Returns `true` if the [ByteBuffer] conforms to the singleObject encoding specification.
 */
interface IsAvroSingleObjectEncodedPredicate : Predicate<ByteBuffer>

/**
 * Global utility methods.
 */
object AvroAdapterApi {

  fun Schema.byteContent() = this.toString().byteInputStream(StandardCharsets.UTF_8)

  /**
   * Determines the revision of a given schema by reading the String value of the given object-property.
   */
  fun propertyBasedSchemaRevisionResolver(propertyKey: String): SchemaRevisionResolver = object : SchemaRevisionResolver {
    override fun apply(schema: Schema): Optional<SchemaRevision> = Optional.ofNullable(schema.getObjectProp(propertyKey) as String?)
  }


  @JvmStatic
  fun schemaForClass(recordClass: Class<*>): Schema = SpecificData(recordClass.classLoader).getSchema(recordClass)

  @JvmStatic
  fun schemaForClass(recordClass: KClass<*>) = schemaForClass(recordClass.java)

  @JvmStatic
  fun Schema.extractSchemaInfo(schemaRevisionResolver: SchemaRevisionResolver) = AvroSchemaInfoData(
    context = namespace,
    name = name,
    revision = schemaRevisionResolver.apply(this).orElse(null)
  )

  /**
   * @return [SchemaResolver] derived from registry
   */
  fun AvroSchemaRegistry.schemaResolver() = object : SchemaResolver {
    override fun apply(schemaId: SchemaId): Optional<AvroSchemaWithId> = this@schemaResolver.findById(schemaId)
  }

  fun SpecificRecordBase.toByteBuffer() = this.javaClass.getDeclaredMethod("toByteBuffer").invoke(this) as ByteBuffer
  fun SpecificRecordBase.toByteArray() = this.toByteBuffer().array()

  fun Class<SpecificRecordBase>.fromByteArray(bytes: ByteArray) = this.getDeclaredMethod("fromByteBuffer", ByteBuffer::class.java)
    .invoke(null, ByteBuffer.wrap(bytes)) as SpecificRecordBase
}

