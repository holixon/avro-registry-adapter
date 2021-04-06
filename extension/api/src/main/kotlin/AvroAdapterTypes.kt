package io.toolisticon.avro.adapter.api

import org.apache.avro.Schema

import org.apache.avro.generic.GenericDatumWriter

import java.io.ByteArrayOutputStream

import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.io.*

import java.io.IOException
import java.nio.charset.StandardCharsets


typealias SchemaId = Long
typealias SchemaRevision = String
typealias AvroSingleObjectEncoded = ByteArray


interface AvroPayloadAndSchema {

  val schema: AvroSchemaWithId

  val payload: ByteArray

}

/**
 * The schema info provides the relevant identifiers for a schema:
 *
 * * context (aka namespace)
 * * name
 * * revision
 */
interface AvroSchemaInfo {

  val context: String

  val name: String

  val revision: SchemaRevision?

  val canonicalName : String
    get() = "$context.$name"
}

/**
 * Tuple wrapping the schema and its id.
 */
interface AvroSchemaWithId : AvroSchemaInfo {

  val id: SchemaId

  val schema: Schema

}

@Throws(IOException::class)
fun avroToJson(schema: Schema?, avroBinary: ByteArray?): String? {
  // byte to datum
  val datumReader: DatumReader<Any?> = GenericDatumReader(schema)
  val decoder: Decoder = DecoderFactory.get().binaryDecoder(avroBinary, null)
  val avroDatum = datumReader.read(null, decoder)

  // datum to json
  val json: String? = null
  ByteArrayOutputStream().use { baos ->
    val writer: DatumWriter<Any?> = GenericDatumWriter(schema)
    val encoder = EncoderFactory.get().jsonEncoder(schema, baos, false)
    writer.write(avroDatum, encoder)
    encoder.flush()
    baos.flush()
    return String(baos.toByteArray(), StandardCharsets.UTF_8)
  }
}
