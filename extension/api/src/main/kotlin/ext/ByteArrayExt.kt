package io.holixon.avro.adapter.api.ext

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import java.nio.ByteBuffer

object ByteArrayExt {

  /**
   * Converts a byte array into its hexadecimal string representation
   * e.g. for the V1_HEADER => [C3 01]
   *
   * @param separator - what to print between the bytes, defaults to " "
   * @param prefix - start of string, defaults to "["
   * @param suffix - end of string, defaults to "]"
   * @return the hexadecimal string representation of the input byte array
   */
  @JvmStatic
  fun ByteArray.toHexString(): String = this.joinToString(
    separator = " ",
    prefix = "[",
    postfix = "]"
  ) { "%02X".format(it) }

  fun ByteArray.buffer(): ByteBuffer = ByteBuffer.wrap(this)

  fun ByteArray.split(vararg indexes: Int): List<ByteArray> {
    require(indexes.none { it < 0 || it > size - 1 }) { "all indexes have to match '0 < index < size-1', was: indexes=${indexes.toList()}, size=$size" }
    require(indexes.toList() == indexes.sorted()) { "indexes must be ordered, was: ${indexes.toList()}" }
    require(indexes.size == indexes.distinct().size) { "indexes must be unique, was: ${indexes.toList()}" }

    val allButLast =
      indexes.fold(0 to emptyList<ByteArray>()) { pair, nextIndex -> nextIndex to pair.second + this.copyOfRange(pair.first, nextIndex) }
    return allButLast.second + this.copyOfRange(allButLast.first, size)
  }

  fun ByteBuffer.split(vararg indexes: Int): List<ByteArray> = this.array().split(*indexes)

  fun ByteBuffer.extract(position: Int, size: Int? = null): ByteArray {
    val originalPosition = this.position()
    try {
      this.position(position)
      require(size == null || size > 0) { "size < 1: ($size < 1)" }
      val maxSize = remaining()
      require(size == null || size <= maxSize) { "Cannot extract from position=$position, size=$size, remaining=${this.remaining()}" }

      val bytes = ByteArray(size ?: this.remaining())
      this.get(bytes)
      return bytes
    } finally {
      this.position(originalPosition)
    }
  }
}
