package io.holixon.avro.adapter.common.encoder

import io.holixon.avro.adapter.common.AvroAdapterDefault.toByteArray
import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class DefaultGenericDataRecordToSingleObjectEncoderTest {

  private val encoder = DefaultGenericDataRecordToSingleObjectEncoder()

  @Test
  internal fun `encode sample event 4711`() {
    val generic = SampleEventV4711.createGenericRecord("foo")
    val specific = SampleEventV4711.createSpecificRecord("foo")

    assertThat(encoder.encode(generic))
      .`as`("bytes of generic and specific encoding must be equal")
      .isEqualTo(specific.toByteArray())
  }
}
