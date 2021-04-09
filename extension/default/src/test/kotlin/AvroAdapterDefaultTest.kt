package io.holixon.avro.adapter.common

import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.adapter.api.AvroAdapterApi.toByteArray
import io.holixon.avro.adapter.api.AvroAdapterApi.toHexString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroAdapterDefaultTest {

  private val bytes = AvroAdapterTestLib.sampleFoo.toByteArray()

  @Test
  internal fun `extract schemaId and payload`() {
    val data = AvroAdapterDefault.SchemaIdAndPayload(bytes)

    assertThat(data.schemaId).isEqualTo(AvroAdapterTestLib.sampleEventFingerprint.toString())
    assertThat(data.payload.toHexString()).isEqualTo("[06 66 6F 6F]")
  }
}
