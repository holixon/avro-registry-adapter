package io.holixon.avro.adapter.common.decoder

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault.toByteArray
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry
import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultSingleObjectToGenericDataRecordDecoderTest {

  private val registry = InMemoryAvroSchemaReadOnlyRegistry.createWithSchemas(SampleEventV4711.schema)

  private val decoder = DefaultSingleObjectToGenericDataRecordDecoder(registry.schemaResolver())

  @Test
  fun `decode sample event 4711`() {
    val bytes = SampleEventV4711.createSpecificRecord("foo").toByteArray()

    val record = decoder.decode(bytes)

    assertThat(record.get("value")).isEqualTo("foo")
  }
}
