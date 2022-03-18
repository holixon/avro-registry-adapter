package io.holixon.avro.adapter.common.decoder

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.api.type.JsonStringAndSchemaIdData
import io.holixon.avro.adapter.common.converter.DefaultGenericDataRecordToSpecificRecordChangingSchemaConverter
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry
import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent


internal class DefaultJsonToSpecificRecordDecoderTest {
  private val registry = InMemoryAvroSchemaReadOnlyRegistry.createWithSchemas(SampleEventV4711.schema)

  private val jsonDecoder = DefaultJsonToGenericDataRecordDecoder(registry.schemaResolver())
  private val specificRecordConverter = DefaultGenericDataRecordToSpecificRecordChangingSchemaConverter(registry.schemaResolver())

  private val decoder = DefaultJsonToSpecificRecordDecoder(jsonDecoder, specificRecordConverter)

  @Test
  internal fun `decode specificRecord from json`() {
    val record = SampleEventV4711.createSpecificRecord("bar")

    val encoded = JsonStringAndSchemaIdData(
      schemaId = SampleEventV4711.schemaData.schemaId,
      json = """{"value": "bar"}"""
    )

    val decoded: SampleEvent = decoder.decode(encoded)

    assertThat(decoded).isEqualTo(record)
  }
}
