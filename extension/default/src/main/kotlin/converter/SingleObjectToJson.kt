package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.SchemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import org.apache.avro.Schema
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.io.*
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.function.Function

/**
 * Uses the schema encoded in the single object bytes to create a generic datum and convert it to json.
 */
class SingleObjectToJson(private val schemaResolver: SchemaResolver) : Function<ByteArray, String> {

  override fun apply(avroSingleObject: AvroSingleObjectEncoded): String {
    val (schemaId, payload) = AvroAdapterDefault.SchemaIdAndPayload(avroSingleObject)
    val writerSchema = schemaResolver.apply(schemaId).orElseThrow().schema

    val genericDatum = readGenericDatum(writerSchema, payload)

    return writeToJson(writerSchema, genericDatum)
  }

  private fun writeToJson(schema: Schema, genericDatum: Any): String = ByteArrayOutputStream().use {
    val writer: DatumWriter<Any> = GenericDatumWriter(schema)
    val encoder = EncoderFactory.get().jsonEncoder(schema, it, false)
    writer.write(genericDatum, encoder)
    encoder.flush()
    it.flush()
    return String(it.toByteArray(), StandardCharsets.UTF_8)
  }

  private fun readGenericDatum(schema: Schema, avroBinary: ByteArray): Any {
    val datumReader: DatumReader<Any> = GenericDatumReader(schema)
    val decoder: Decoder = DecoderFactory.get().binaryDecoder(avroBinary, null)
    return datumReader.read(null, decoder)
  }
}
