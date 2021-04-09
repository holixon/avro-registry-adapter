package io.holixon.avro.adapter.api.type

import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.Test

internal class AvroSchemaWithIdTest {

  private val sampleEventSchema = AvroAdapterTestLib.loadArvoResource("test.fixture.SampleEvent-v4711")

  @Test
  internal fun `read schema and derive values`() {
    val schema = Schema.Parser().parse(sampleEventSchema)

    val avroSchema = AvroSchemaWithIdData(schemaId = "1", schema = schema, revision = AvroAdapterApiTestHelper.schemaRevisionResolver.apply(schema).orElse(null))

    assertThat(avroSchema.schemaId).isEqualTo("1")
    assertThat(avroSchema.revision).isEqualTo("4711")
    assertThat(avroSchema.name).isEqualTo("SampleEvent")
    assertThat(avroSchema.context).isEqualTo("test.fixture")
    assertThat(avroSchema.canonicalName).isEqualTo("test.fixture.SampleEvent")
  }
}
