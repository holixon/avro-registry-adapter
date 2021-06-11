package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.lib.test.AvroAdapterTestLib
import org.apache.avro.SchemaCompatibility
import org.apache.avro.generic.GenericRecord
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultGenericRecordToSingleObjectConverterTest {

  private val registry = AvroAdapterDefault.inMemorySchemaRegistry()

  @Test
  internal fun `convert to bytes and back to generic record`() {

    registry.register(AvroAdapterTestLib.schemaSampleEvent4711)
    val converter = createConverter()
    val data = AvroAdapterTestLib.sampleFoo

    val decoded: GenericRecord = converter.decode(converter.encode(data))
    assertThat(decoded.get("value")).isEqualTo(data.value)
  }

  private fun createConverter(
    decoderSpecificRecordClassResolver: AvroAdapterDefault.DecoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
    ignoredIncompatibilities: Set<SchemaCompatibility.SchemaIncompatibilityType> = setOf()
  ) =
    DefaultGenericRecordToSingleObjectConverter(
      registry.schemaResolver(),
      decoderSpecificRecordClassResolver,
      DefaultSchemaCompatibilityResolver(ignoredIncompatibilities)
    )

}
