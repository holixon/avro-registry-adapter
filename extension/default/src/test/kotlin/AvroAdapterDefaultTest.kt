package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.AvroAdapterApi.toByteArray
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.lib.test.AvroAdapterTestLib
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

internal class AvroAdapterDefaultTest {

  private val bytes = AvroAdapterTestLib.sampleFoo.toByteArray()

  @Test
  internal fun `extract schemaId and payload`() {
    val data = AvroAdapterDefault.SchemaIdAndPayload(bytes)

    assertThat(data.schemaId).isEqualTo(AvroAdapterTestLib.sampleEventFingerprint.toString())
    assertThat(data.payload.toHexString()).isEqualTo("[06 66 6F 6F]")
  }

  @Test
  @Disabled("see predicate")
  internal fun `isAvroSingleObjectEncoded true`() {
    val buffer = ByteBuffer.wrap(bytes)
    buffer.position(10)

    assertThat(AvroAdapterDefault.isAvroSingleObjectEncoded(buffer)).isTrue
    assertThat(buffer.position()).isEqualTo(10)
  }

  @Test
  internal fun `isAvroSingleObjectEncoded false`() {
    val firstByteRemoved = bytes.copyOfRange(1, bytes.size)

    val buffer = ByteBuffer.wrap(bytes)
    buffer.position(10)

    assertThat(AvroAdapterDefault.isAvroSingleObjectEncoded(buffer)).isFalse
    assertThat(buffer.position()).isEqualTo(10)
  }
}
