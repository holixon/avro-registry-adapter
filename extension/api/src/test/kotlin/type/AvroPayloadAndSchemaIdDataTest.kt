package io.holixon.avro.adapter.api.type

import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper
import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class AvroPayloadAndSchemaIdDataTest {

  private val avroSchemaWithId4711 = AvroAdapterApiTestHelper.avroSchemaWithId(SampleEventV4711.schema)

  @Test
  internal fun `equals and hashcode`() {
    val a1 = AvroPayloadAndSchemaIdData(
      schemaId = avroSchemaWithId4711.schemaId,
      payload = SampleEvent("foo").toByteBuffer().array()
    )
    val a2 = AvroPayloadAndSchemaIdData(
      schemaId = avroSchemaWithId4711.schemaId,
      payload = SampleEvent("foo").toByteBuffer().array()
    )

    assertThat(a1).isEqualTo(a2)
    assertThat(a1.hashCode()).isEqualTo(a2.hashCode())
    assertThat(a1.schemaId).isEqualTo(a2.schemaId)
  }
}
