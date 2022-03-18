package io.holixon.avro.adapter.common.encoder

import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class DefaultGenericRecordToJsonEncoderTest {

  private val encoder = DefaultGenericRecordToJsonEncoder()

  @Test
  internal fun `encode genericRecord`() {
    val record: GenericData.Record = SampleEventV4711.createGenericRecord("foo")

    val encoded = encoder.encode(record)

    assertThat(encoded.json).isEqualTo("""{"value": "foo"}""")
    assertThat(encoded.schemaId).isEqualTo(SampleEventV4711.schemaData.schemaId)
  }
}
