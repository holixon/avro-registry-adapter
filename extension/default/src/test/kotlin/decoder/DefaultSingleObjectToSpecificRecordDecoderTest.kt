package io.holixon.avro.adapter.common.decoder

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry
import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent


internal class DefaultSingleObjectToSpecificRecordDecoderTest {

  private val schemaResolver = InMemoryAvroSchemaReadOnlyRegistry.createWithSchemas(SampleEventV4711.schema).schemaResolver()

  private val decoder = DefaultSingleObjectToSpecificRecordDecoder(
    schemaResolver = schemaResolver
  )

  @Test
  fun `decode sample event 4711`() {
    val record = SampleEvent("foo")

    val bytes = record.toByteBuffer().array()

    val decoded: SampleEvent = decoder.decode(bytes)

    assertThat(decoded).isEqualTo(record)
  }
}
