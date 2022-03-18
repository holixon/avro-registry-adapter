package io.holixon.avro.adapter.common.encoder

import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class DefaultSpecificRecordToJsonEncoderTest {

  private val encoder = DefaultSpecificRecordToJsonEncoder()

  @Test
  fun `encode specificRecord`() {
    val record = SampleEventV4711.createSpecificRecord("foo")

    val encoded = encoder.encode(record)

    assertThat(encoded.json).isEqualTo("""{"value": "foo"}""")
    assertThat(encoded.schemaId).isEqualTo(SampleEventV4711.schemaData.schemaId)
  }
}
