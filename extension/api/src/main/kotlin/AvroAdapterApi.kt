package io.holixon.avro.adapter.api

import io.holixon.avro.adapter.api.type.AvroSchemaInfoData
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificData
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
fun interface SchemaIdSupplier : Function<Schema, AvroSchemaId>

/**
 * Search schema based on schemaId.
 */
fun interface SchemaResolver : Function<AvroSchemaId, Optional<AvroSchemaWithId>>

/**
 * Takes encoded avro bytes (containing schema reference) and return decoded payload and the resolved schema.
 */
fun interface SingleObjectDecoder : Function<AvroSingleObjectEncoded, AvroPayloadAndSchema>

/**
 * Use given Schema information and bytecode payload to return decoded bytes.
 */
fun interface SingleObjectEncoder : BiFunction<AvroSchemaWithId, ByteArray, AvroSingleObjectEncoded>

/**
 * Returns the revision for a given schema.
 */
fun interface SchemaRevisionResolver : Function<Schema, Optional<AvroSchemaRevision>>

/**
 * Returns `true` if the [ByteBuffer] conforms to the singleObject encoding specification.
 */
fun interface IsAvroSingleObjectEncodedPredicate : Predicate<ByteBuffer>

/**
 * Global utility methods.
 */
object AvroAdapterApi {

  fun Schema.byteContent() = this.toString().byteInputStream(StandardCharsets.UTF_8)

  /**
   * Determines the revision of a given schema by reading the String value of the given object-property.
   */
  @JvmStatic
  fun propertyBasedSchemaRevisionResolver(propertyKey: String): SchemaRevisionResolver =
    SchemaRevisionResolver { schema -> Optional.ofNullable(schema.getObjectProp(propertyKey) as String?) }

  @JvmStatic
  fun schemaForClass(recordClass: Class<*>): Schema = SpecificData(recordClass.classLoader).getSchema(recordClass)

  @JvmStatic
  fun schemaForClass(recordClass: KClass<*>): Schema = schemaForClass(recordClass.java)

  @JvmStatic
  fun Schema.extractSchemaInfo(schemaRevisionResolver: SchemaRevisionResolver): AvroSchemaInfoData = AvroSchemaInfoData(
    namespace = namespace,
    name = name,
    revision = schemaRevisionResolver.apply(this).orElse(null)
  )

  /**
   * Creates a schema resolver out of a read-only registry.
   * @return [SchemaResolver] derived from registry.
   */
  fun AvroSchemaReadOnlyRegistry.schemaResolver(): SchemaResolver = SchemaResolver { schemaId -> this@schemaResolver.findById(schemaId) }

}

