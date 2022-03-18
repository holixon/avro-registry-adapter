package io.holixon.avro.adapter.api.ext

import io.holixon.avro.adapter.api.AvroSchemaFqn
import io.holixon.avro.adapter.api.type.AvroSchemaFqnData
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

  /**
   * Get [AvroSchemaFqn] from [Schema].
   */
  @JvmStatic
  fun Schema.avroSchemaFqn(): AvroSchemaFqn = AvroSchemaFqnData(namespace = namespace, name = name)

}
