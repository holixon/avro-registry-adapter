package io.holixon.avro.adapter.common.decoder

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.api.type.JsonStringAndSchemaIdData
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry
import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultJsonToGenericDataRecordDecoderTest {

  private val registry = InMemoryAvroSchemaReadOnlyRegistry.createWithSchemas(SampleEventV4711.schema)

  private val decoder = DefaultJsonToGenericDataRecordDecoder(registry.schemaResolver())

  @Test
  internal fun `decode to genericRecord`() {
    val record = SampleEventV4711.createGenericRecord("bar")

    val encoded = JsonStringAndSchemaIdData(
      schemaId = SampleEventV4711.schemaData.schemaId,
      json = """{"value": "bar"}"""
    )

    val decoded = decoder.decode(encoded)

    assertThat(decoded).isEqualTo(record)
  }
}
