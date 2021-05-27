package io.holixon.avro.lib.test

import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4713
import org.apache.avro.Schema
import test.fixture.SampleEvent
import test.fixture.SampleEventWithAdditionalFieldWithDefault

object AvroAdapterTestLib {

  val schemaSampleEvent4711 = SampleEventV4711.schema
  val schemaSampleEvent4713 = SampleEventV4713.schema

  fun loadResource(resName:String): String = {}::class.java.getResource(resName.trailingSlash())?.readText() ?: throw IllegalStateException("resource not found: $resName")
  fun loadArvoResource(avroFileName:String): String = loadResource("/avro/$avroFileName.avsc")

  private fun String.trailingSlash() = if (startsWith("/")) this else "/$this"

  fun loadSchema(resName:String): Schema = Schema.Parser().parse(loadArvoResource(resName))

  val sampleFoo = SampleEvent("foo")
  val sampleFooWithAdditionalFieldWithDefault = SampleEventWithAdditionalFieldWithDefault("foo", "default value")
  const val sampleFooHex = "[C3 01 CC 98 1F E7 56 D4 1C A5 06 66 6F 6F]"

  const val sampleEventFingerprint = -6549126288393660212L

}
