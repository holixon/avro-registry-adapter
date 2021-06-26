package io.holixon.avro.adapter.common.ext

import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization
import org.apache.avro.generic.GenericData

/**
 * Extension functions for convenient development with the avro [Schema].
 */
object SchemaExt {

  /**
   * The schema fingerprint as defined by [SchemaNormalization.parsingFingerprint64].
   */
  val Schema.fingerprint get() = SchemaNormalization.parsingFingerprint64(this)

  /**
   * Creates a schema compliant generic record.
   *
   * @param receiver - a lambda that can modify the record (basically: use `put` to fill in data
   * @return a schema compliant record instance
   */
  fun Schema.createGenericRecord(receiver: GenericData.Record.()-> Unit) = GenericData.Record(this).apply {receiver.invoke(this)}
}
