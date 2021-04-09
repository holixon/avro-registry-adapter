package io.holixon.avro.adapter.common.converter

import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class DefaultSpecificRecordToSingleObjectConverterTest {
  private val registry = AvroAdapterDefault.inMemorySchemaRepository();
  private val converter = DefaultSpecificRecordToSingleObjectConverter(registry.schemaResolver())
  private val schema = AvroAdapterTestLib.schemaSampleEvent4711

  @BeforeEach
  internal fun setUp() {
    registry.register(schema)
  }

  @Test
  internal fun `encode and decode`() {
    val data = AvroAdapterTestLib.sampleFoo
    val encoded = converter.encode(data)

    val decoded: SampleEvent = converter.decode(encoded)

    assertThat(decoded).isEqualTo(data)
  }

}
