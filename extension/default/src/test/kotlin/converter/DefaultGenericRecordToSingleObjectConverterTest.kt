package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.apache.avro.SchemaCompatibility
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultGenericRecordToSingleObjectConverterTest {

  private val registry = AvroAdapterDefault.inMemorySchemaRegistry()

  @Test
  internal fun `convert to bytes`() {

    registry.register(AvroAdapterTestLib.schemaSampleEvent4711)
    val converter = createConverter()
    val data = AvroAdapterTestLib.sampleFoo

    val bytes = converter.encode(data)

    val decoded: GenericRecord = converter.decode(bytes)
    print(decoded)
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
