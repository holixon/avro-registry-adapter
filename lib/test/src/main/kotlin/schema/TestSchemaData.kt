package io.holixon.avro.lib.test.schema

import io.holixon.avro.lib.test.AvroAdapterTestLib
import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization

data class TestSchemaData(
  val schema: Schema,
  val fingerPrint: Long,
  val revision: String? = null
) {
  val name: String = schema.name
  val context: String = schema.namespace
}

fun parse(schemaJson: String ) = Schema.Parser().parse(schemaJson)

fun parseData(schemaJson: String): TestSchemaData {
  val schema = parse(schemaJson)
  val fingerprint = SchemaNormalization.parsingFingerprint64(schema)
  val revision : String? = schema.getObjectProp("revision") as String?
  return TestSchemaData(schema, fingerprint, revision)
}

interface TestSchemaDataProvider {
  fun schemaData() : TestSchemaData
  fun schema() = schemaData().schema
  fun json() = schema().toString(true)
}
