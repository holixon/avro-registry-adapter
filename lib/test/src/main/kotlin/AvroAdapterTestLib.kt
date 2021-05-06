package io.holixon.avro.lib.test

import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization
import test.fixture.SampleEvent
import java.lang.IllegalStateException

object AvroAdapterTestLib {

  const val SCHEMA_CONTEXT = "test.fixture"
  const val SCHEMA_NAME = "SampleEvent"


  data class SampleSchema(val resource: String, val schema : Schema, val fingerprint: Long) {
    companion object {
      operator fun invoke(resource: String): SampleSchema {
        val schema = loadSchema(resource)
        val fingerprint = SchemaNormalization.parsingFingerprint64(schema)
        val revision = schema.getObjectProp("revision")

        return SampleSchema(resource, schema, fingerprint)
      }
    }
  }

  val sampleSchema4711 = SampleSchema("$SCHEMA_CONTEXT.$SCHEMA_NAME-v4711")
  val sampleSchema4712 = SampleSchema("$SCHEMA_CONTEXT.$SCHEMA_NAME-v4712")

  const val SCHEMA_REVISION_4711 = "4711"

  const val SCHEMA_SAMPLE_4711 = "$SCHEMA_CONTEXT.$SCHEMA_NAME-v$SCHEMA_REVISION_4711"


  val schemaSampleEvent4711 = loadSchema(SCHEMA_SAMPLE_4711)

  fun loadResource(resName:String): String = {}::class.java.getResource(resName.trailingSlash())?.readText() ?: throw IllegalStateException("resource not found: $resName")
  fun loadArvoResource(avroFileName:String): String = loadResource("/avro/$avroFileName.avsc")

  private fun String.trailingSlash() = if (startsWith("/")) this else "/$this"

  fun loadSchema(resName:String): Schema = Schema.Parser().parse(loadArvoResource(resName))

  val sampleFoo = SampleEvent("foo")
  const val sampleFooHex = "[C3 01 CC 98 1F E7 56 D4 1C A5 06 66 6F 6F]"

  const val sampleEventRevision = "4711"
  const val sampleEventFingerprint = -6549126288393660212L

}
