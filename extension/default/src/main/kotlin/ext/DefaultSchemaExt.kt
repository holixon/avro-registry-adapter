package io.holixon.avro.adapter.common.ext

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaInfo
import io.holixon.avro.adapter.api.AvroSchemaRevision
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.type.AvroSchemaInfoData
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.DefaultSchemaIdSupplier
import io.holixon.avro.adapter.common.DefaultSchemaRevisionResolver
import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization
import org.apache.avro.generic.GenericData

/**
 * Extension functions for convenient development with the avro [Schema].
 * This uses the default conventions for encoding and revision and must not be used if your setup
 * requires specific configurations.
 */
object DefaultSchemaExt {

  /**
   * The schema fingerprint as defined by [SchemaNormalization.parsingFingerprint64].
   */
  val Schema.fingerprint get() = SchemaNormalization.parsingFingerprint64(this)

  /**
   * The [AvroSchemaId] (defaults to [fingerprint] as String).
   */
  val Schema.avroSchemaId: AvroSchemaId get() = AvroAdapterDefault.schemaIdSupplier.apply(this)

  /**
   * The schema revision by [DefaultSchemaRevisionResolver].
   */
  val Schema.avroSchemaRevision: AvroSchemaRevision? get() = AvroAdapterDefault.schemaRevisionResolver.apply(this).orElse(null)

  /**
   * Helper to create [AvroSchemaWithId] from given [Schema], using [DefaultSchemaIdSupplier] and [DefaultSchemaRevisionResolver].
   */
  val Schema.avroSchemaWithId: AvroSchemaWithId
    get() = AvroSchemaWithIdData(
      schemaId = avroSchemaId,
      schema = this,
      revision = avroSchemaRevision
    )

  /**
   * The schema info using [Schema.getNamespace], [Schema.getName] and [Schema.avroSchemaRevision].
   */
  val Schema.avroSchemaInfo: AvroSchemaInfo
    get() = AvroSchemaInfoData(
      namespace = namespace,
      name = name,
      revision = avroSchemaRevision
    )

  /**
   * Creates a schema compliant generic record.
   *
   * @param receiver - a lambda that can modify the record (basically: use `put` to fill in data
   * @return a schema compliant record instance
   */
  fun Schema.createGenericRecord(receiver: GenericData.Record.() -> Unit) = GenericData.Record(this).apply { receiver.invoke(this) }
}
