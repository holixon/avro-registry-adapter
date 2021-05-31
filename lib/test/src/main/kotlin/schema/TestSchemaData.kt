package io.holixon.avro.lib.test.schema

import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization

data class TestSchemaData(
  val schema: Schema,
  val name: String,
  val namespace: String,
  val doc: String,
  val fingerPrint: Long,
  val revision: String?
) {
  constructor(schema: Schema) : this(
    schema = schema,
    name = schema.name,
    namespace = schema.namespace,
    doc = schema.doc,
    fingerPrint = SchemaNormalization.parsingFingerprint64(schema),
    revision = schema.getObjectProp("revision") as String?
  )

  constructor(schemaJson: String) : this(Schema.Parser().parse(schemaJson))
}

interface TestSchemaDataProvider {
  val schemaJson: String

  val schemaData: TestSchemaData
    get() = TestSchemaData(schemaJson)

  val schema: Schema
    get() = schemaData.schema

  fun toJson() = schema.toString(true)
}
