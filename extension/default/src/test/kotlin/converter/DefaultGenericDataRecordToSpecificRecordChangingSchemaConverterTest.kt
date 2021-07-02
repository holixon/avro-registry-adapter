package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.createGenericRecord
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4712
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class DefaultGenericDataRecordToSpecificRecordChangingSchemaConverterTest {
  private val schema4711 = SampleEventV4711.schema
  private val schema4712 = SampleEventV4712.schema

  private val resolver = InMemoryAvroSchemaReadOnlyRegistry.createWithSchemas(
    schema4711,
    schema4712
  ).schemaResolver()

  private val converter = DefaultGenericDataRecordToSpecificRecordChangingSchemaConverter(resolver)

  @Test
  internal fun `convert generic record same schema`() {
    val r4711 = schema4711.createGenericRecord {
      put("value", "foo")
    }

    val s4711 : SampleEvent = converter.convert(r4711)

    assertThat(s4711.value).isEqualTo("foo")
    assertThat(s4711.schema).isEqualTo(r4711.schema)
  }

  @Test
  internal fun `convert generic record downgrade schema`() {
    val r4712 = schema4712.createGenericRecord {
      put("value", "foo")
      put("anotherValue", "bar")

    }

    val s4711 : SampleEvent = converter.convert(r4712)

    assertThat(s4711.value).isEqualTo("foo")
    assertThat(s4711.schema).isNotEqualTo(r4712.schema)
  }
}
