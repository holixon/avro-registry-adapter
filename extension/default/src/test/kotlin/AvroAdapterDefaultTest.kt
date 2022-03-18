package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.holixon.avro.adapter.common.AvroAdapterDefault.isAvroSingleObjectEncoded
import io.holixon.avro.adapter.common.AvroAdapterDefault.readPayloadAndSchemaId
import io.holixon.avro.adapter.common.AvroAdapterDefault.toByteArray
import io.holixon.avro.adapter.common.AvroAdapterDefault.toGenericDataRecord
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.fingerprint
import io.holixon.avro.lib.test.AvroAdapterTestLib
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class AvroAdapterDefaultTest {
  companion object : KLogging()

  private val bytes = AvroAdapterTestLib.sampleFoo.toByteArray()

  @Test
  fun `read payload and schemaId from encoded bytes`() {
    logger.info { bytes.toHexString() }

    // too short
    assertThatThrownBy { "foo".encodeToByteArray().readPayloadAndSchemaId() }.isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("Single object encoded bytes must have at least length > 10, was: 3.")

    // long enough, but does not start with V1_Header
    assertThatThrownBy {
      "hello my precious world".encodeToByteArray().readPayloadAndSchemaId()
    }.isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("Single object encoded bytes need to start with [C3 01].")

    val payloadAndSchemaId = bytes.readPayloadAndSchemaId()
    assertThat(payloadAndSchemaId.schemaId).isEqualTo(AvroAdapterTestLib.sampleFoo.schema.fingerprint.toString())
    assertThat(payloadAndSchemaId.payload.toHexString()).isEqualTo("[06 66 6F 6F]")
  }

  @Test
  fun `extract schemaId and payload`() {
    val (schemaId, payload) = bytes.readPayloadAndSchemaId().let { it.schemaId to it.payload }

    assertThat(schemaId).isEqualTo(AvroAdapterTestLib.sampleEventFingerprint.toString())
    assertThat(payload.toHexString()).isEqualTo("[06 66 6F 6F]")
  }

  @Test
  fun `is avro single object encoded`() {
    assertThat(bytes.isAvroSingleObjectEncoded()).isTrue

    assertThat("foo".encodeToByteArray().isAvroSingleObjectEncoded()).isFalse
  }

  @Test
  fun `get generic data from specific record`() {
    val genericRecord = AvroAdapterTestLib.sampleFoo.toGenericDataRecord()
    assertThat(genericRecord.get("value")).isEqualTo("foo")
  }
}
