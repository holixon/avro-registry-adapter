package io.holixon.avro.lib.test.schema

import io.holixon.avro.lib.test.AvroAdapterTestLib
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import test.fixture.SampleEvent

const val SCHEMA_CONTEXT = "test.fixture"
const val SCHEMA_NAME = "SampleEvent"

/**
 * Test schema provider.
 */
object SampleEventV4711 : TestSchemaDataProvider {
  const val REVISION = "4711"

  override val schemaJson by lazy { AvroAdapterTestLib.loadArvoResource("$SCHEMA_CONTEXT.$SCHEMA_NAME-v$REVISION") }


  fun createGenericRecord(value: String) = GenericData.Record(schema).apply {
    put("value", value)
  }

  fun createSpecificRecord(value: String) = SampleEvent(value)
}

/**
 * Test schema provider.
 */
object SampleEventV4712 : TestSchemaDataProvider {
  const val REVISION = "4712"

  override val schemaJson: String = """{
      "type": "record",
      "namespace": "$SCHEMA_CONTEXT",
      "name": "$SCHEMA_NAME",
      "revision": "$REVISION",
      "doc": "used for testing, has optional field",
      "fields": [
        {
          "name": "value",
          "doc": "this is the first value, it is required",
          "type": {
            "type": "string",
            "avro.java.string": "String"
          }
        },
        {
          "name": "anotherValue",
          "doc": "this is the second value, it is optional",
          "type": [
            {
              "type": "string",
              "avro.java.string": "String"
            },
            "null"
          ],
          "default": "null"
        }
      ],
      "__comment": "some additional comment"
    }"""

  fun create(value: String, anotherValue:String?= null) : GenericRecord = GenericData.Record(schema).apply {
    put("value", value)
    anotherValue?.also { put("anotherValue", anotherValue) }
  }
}

/**
 * Test provider.
 */
object SampleEventV4713 : TestSchemaDataProvider {
  const val REVISION = "4713"
  const val SUFFIX = "WithAdditionalFieldWithDefault"

  fun addSuffix(fqn: String) = fqn + SUFFIX
  fun removeSuffix(fqn:String) = fqn.removeSuffix(SUFFIX)

  override val schemaJson by lazy { AvroAdapterTestLib.loadArvoResource("$SCHEMA_CONTEXT.$SCHEMA_NAME-v$REVISION") }
}
