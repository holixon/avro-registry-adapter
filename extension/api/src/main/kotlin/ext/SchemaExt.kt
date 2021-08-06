package io.holixon.avro.adapter.api.ext

import org.apache.avro.Schema
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Extensions for [org.apache.avro.Schema].
 */
object SchemaExt {

  /**
   * Get [Schema] as [ByteArrayInputStream].
   */
  @JvmStatic
  fun Schema.byteContent(): ByteArrayInputStream = this.toString().byteInputStream(Charsets.UTF_8)

  /**
   * Reads a [Schema] from [InputStream].
   */
  @JvmStatic
  fun InputStream.schema(): Schema = bufferedReader(Charsets.UTF_8).use {
    Schema.Parser().parse(it.readText())
  }

}
