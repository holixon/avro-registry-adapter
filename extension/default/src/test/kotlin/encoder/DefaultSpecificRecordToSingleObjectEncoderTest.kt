package io.holixon.avro.adapter.common.encoder

import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultSpecificRecordToSingleObjectEncoderTest {

  private val encoder = DefaultSpecificRecordToSingleObjectEncoder()

  @Test
  fun `encode sample event 4711`() {
    val record = SampleEventV4711.createSpecificRecord("foo")

    assertThat(encoder.encode(record).toHexString()).isEqualTo(AvroAdapterTestLib.sampleFooHex)
  }
}
