package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.JsonString
import io.holixon.avro.adapter.api.converter.SingleObjectToJsonConverter
import io.holixon.avro.adapter.common.AvroAdapterDefault.readPayloadAndSchemaId
import org.apache.avro.Schema
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.io.*
import java.io.ByteArrayOutputStream

/**
 * Uses the schema encoded in the single object bytes to create a generic datum and convert it to json.
 */
class DefaultSingleObjectToJsonConverter(private val schemaResolver: AvroSchemaResolver) : SingleObjectToJsonConverter {

  override fun convert(bytes: AvroSingleObjectEncoded): JsonString {
    val (schemaId, payload) = bytes.readPayloadAndSchemaId().let { it.schemaId to it.payload }
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
    return String(it.toByteArray(), Charsets.UTF_8)
  }

  private fun readGenericDatum(schema: Schema, avroBinary: ByteArray): Any {
    val datumReader: DatumReader<Any> = GenericDatumReader(schema)
    val decoder: Decoder = DecoderFactory.get().binaryDecoder(avroBinary, null)
    return datumReader.read(null, decoder)
  }
}
