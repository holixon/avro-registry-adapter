package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.lib.test.AvroAdapterTestLib
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import test.fixture.SampleEventWithAdditionalFieldWithDefault

internal class DefaultConvertersTests {
  private val registry = AvroAdapterDefault.inMemorySchemaRegistry()

  @Test
  internal fun `convert bytes to generic data record to specific record`() {
    registry.register(AvroAdapterTestLib.schemaSampleEvent4713)

    val resolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver

    val gdr2srConverter =
      DefaultSpecificRecordToGenericDataRecordConverter(registry.schemaResolver(), resolver, DefaultSchemaCompatibilityResolver(setOf()))
    val b2gdrConverter =
      DefaultGenericDataRecordToSingleObjectConverter(registry.schemaResolver(), resolver, DefaultSchemaCompatibilityResolver(setOf()))

    val data = AvroAdapterTestLib.sampleFooWithAdditionalFieldWithDefault
    val gd: GenericData.Record = gdr2srConverter.encode(data)
    Assertions.assertThat(gd.get("value")).isEqualTo(data.value)
    Assertions.assertThat(gd.get("otherValue")).isEqualTo(data.otherValue)

    val bytes = b2gdrConverter.encode(gd)

    val decodedGd = b2gdrConverter.decode(bytes)
    Assertions.assertThat(decodedGd).isEqualTo(gd)
    Assertions.assertThat(decodedGd.get("value")).isEqualTo(data.value)
    Assertions.assertThat(decodedGd.get("otherValue")).isEqualTo(data.otherValue)

    val decodedEvent: SampleEventWithAdditionalFieldWithDefault = gdr2srConverter.decode(decodedGd)

    Assertions.assertThat(decodedEvent).isEqualTo(data)
  }

}
