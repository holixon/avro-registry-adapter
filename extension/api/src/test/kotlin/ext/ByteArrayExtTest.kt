package io.holixon.avro.adapter.api.ext

import io.holixon.avro.adapter.api.ext.ByteArrayExt.extract
import io.holixon.avro.adapter.api.ext.ByteArrayExt.split
import io.holixon.avro.adapter.api.ext.ByteArrayExt.buffer
import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class ByteArrayExtTest {
  companion object : KLogging()

  private val helloBytes: ByteArray = "Hello World!".encodeToByteArray()

  @Test
  fun `to hex String`() {
    assertThat(helloBytes.toHexString()).isEqualTo("[48 65 6C 6C 6F 20 57 6F 72 6C 64 21]")
    assertThat(byteArrayOf(-61, 1).toHexString()).isEqualTo("[C3 01]")
  }

  @Test
  fun `extract from byte buffer`() {
    val buffer = helloBytes.buffer()
    buffer.position(7)

    // extract 3,4,5
    assertThat(buffer.extract(2, 3).toHexString()).isEqualTo("[6C 6C 6F]")
    assertThat(buffer.position()).isEqualTo(7)

    // fails when extracting too much
    assertThatThrownBy { buffer.extract(0, 100) }
      .isInstanceOf(IllegalArgumentException::class.javaObjectType)
      .hasMessage("Cannot extract from position=0, size=100, remaining=${helloBytes.size}")
    assertThat(buffer.position()).isEqualTo(7)

    // fails when extracting too much
    assertThatThrownBy { buffer.extract(0, 100) }
      .isInstanceOf(IllegalArgumentException::class.javaObjectType)
      .hasMessage("Cannot extract from position=0, size=100, remaining=${helloBytes.size}")
    assertThat(buffer.position()).isEqualTo(7)

    assertThatThrownBy { byteArrayOf(-61).buffer().extract(1, 1) }
      .isInstanceOf(IllegalArgumentException::class.javaObjectType)
      .hasMessage("Cannot extract from position=1, size=1, remaining=0")

    // fails when pos < 0
    assertThatThrownBy { buffer.extract(-1, 5) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("newPosition < 0: (-1 < 0)")

    // fails when size < 1
    assertThatThrownBy { buffer.extract(0, 0) }
      .isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("size < 1: (0 < 1)")

    // extract after
    assertThat(buffer.extract(9).toHexString()).isEqualTo("[6C 64 21]")
  }

  @Test
  fun `split byteArray`() {
    assertThatThrownBy { helloBytes.split(-1) }.hasMessage("all indexes have to match '0 < index < size-1', was: indexes=[-1], size=12")
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThatThrownBy { helloBytes.split(100) }.hasMessage("all indexes have to match '0 < index < size-1', was: indexes=[100], size=12")
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThatThrownBy { helloBytes.split(10, 5) }.hasMessage("indexes must be ordered, was: [10, 5]")
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThatThrownBy { helloBytes.split(10, 10) }.hasMessage("indexes must be unique, was: [10, 10]")
      .isInstanceOf(IllegalArgumentException::class.java)

    val parts = helloBytes.split(5, 10)

    assertThat(parts).hasSize(3)
    assertThat(parts[0].toHexString()).isEqualTo("[48 65 6C 6C 6F]")
    assertThat(parts[1].toHexString()).isEqualTo("[20 57 6F 72 6C]")
    assertThat(parts[2].toHexString()).isEqualTo("[64 21]")
  }
}
