package io.toolisticon.avro.adapter.api.type

import io.holixon.axon.avro.lib.test.AvroAdapterTestLib
import io.toolisticon.avro.adapter.api.AvroAdapterApiTestHelper
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.Test

internal class AvroSchemaWithIdTest {

  private val sampleEventSchema = AvroAdapterTestLib.loadArvoResource("test.fixture.SampleEvent-v4711")

  @Test
  internal fun `read schema and derive values`() {
    val schema = Schema.Parser().parse(sampleEventSchema)

    val axonSchema = AvroSchemaWithIdData(id = "1", schema = schema, revision = AvroAdapterApiTestHelper.schemaRevisionResolver.apply(schema).orElse(null))

    assertThat(axonSchema.id).isEqualTo("1")
    assertThat(axonSchema.revision).isEqualTo("4711")
    assertThat(axonSchema.name).isEqualTo("SampleEvent")
    assertThat(axonSchema.context).isEqualTo("test.fixture")
    assertThat(axonSchema.canonicalName).isEqualTo("test.fixture.SampleEvent")
  }
}
