package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.AvroAdapterDefault.DecoderSpecificRecordClassResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver
import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.lib.test.schema.SampleEventV4713
import org.apache.avro.SchemaCompatibility
import org.apache.avro.SchemaCompatibility.SchemaIncompatibilityType.NAME_MISMATCH
import org.apache.avro.specific.SpecificRecordBase
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent
import test.fixture.SampleEventWithAdditionalFieldWithDefault

/**
 * Test for converter.
 */
internal class DefaultSpecificRecordToSingleObjectConverterTest {

  private val registry = AvroAdapterDefault.inMemorySchemaRepository()

  @Test
  internal fun `encode and decode same writer and reader schema`() {

    registry.register(AvroAdapterTestLib.schemaSampleEvent4711)
    val converter = createConverter()

    val data = AvroAdapterTestLib.sampleFoo
    val encoded = converter.encode(data)

    val decoded: SampleEvent = converter.decode(encoded)

    assertThat(decoded).isEqualTo(data)
  }

  @Test
  internal fun `encode and decode different writer and reader schema`() {

    val reg1 = registry.register(AvroAdapterTestLib.schemaSampleEvent4711)
    val reg2 = registry.register(AvroAdapterTestLib.schemaSampleEvent4713)

    @Suppress("UNCHECKED_CAST")
    val converter = createConverter(decoderSpecificRecordClassResolver = {
      Class.forName(SampleEventV4713.addSuffix(it.canonicalName)) as Class<SpecificRecordBase>
    })

    val data = AvroAdapterTestLib.sampleFoo
    val encoded = converter.encode(data)

    assertThatThrownBy {
      converter.decode<SampleEventWithAdditionalFieldWithDefault>(encoded)
    }.hasMessage("Reader schema[${reg2.schemaId}] is not compatible with Writer schema[${reg1.schemaId}]. The incompatibilities are: [NAME_MISMATCH]")

  }


  @Test
  internal fun `encode and decode different writer and reader schema ignoring NAME_MISMATCH`() {

    registry.register(AvroAdapterTestLib.schemaSampleEvent4711)
    registry.register(AvroAdapterTestLib.schemaSampleEvent4713)

    @Suppress("UNCHECKED_CAST")
    val converter = createConverter(decoderSpecificRecordClassResolver = {
      Class.forName(SampleEventV4713.addSuffix(it.canonicalName)) as Class<SpecificRecordBase>
    }, ignoredIncompatibilities = setOf(NAME_MISMATCH))

    val data = AvroAdapterTestLib.sampleFoo
    val encoded = converter.encode(data)

    val decoded: SampleEventWithAdditionalFieldWithDefault = converter.decode(encoded)

    assertThat(decoded).isEqualTo(AvroAdapterTestLib.sampleFooWithAdditionalFieldWithDefault)

  }

  @Test
  internal fun `encode and decode different writer=4713 and reader=4711 schema ignoring NAME_MISMATCH`() {

    registry.register(AvroAdapterTestLib.schemaSampleEvent4711)
    registry.register(AvroAdapterTestLib.schemaSampleEvent4713)

    @Suppress("UNCHECKED_CAST")
    val converter = createConverter(decoderSpecificRecordClassResolver = {
      Class.forName(SampleEventV4713.removeSuffix(it.canonicalName)) as Class<SpecificRecordBase>
    }, ignoredIncompatibilities = setOf(NAME_MISMATCH))

    val data = AvroAdapterTestLib.sampleFooWithAdditionalFieldWithDefault
    val encoded = converter.encode(data)

    val decoded: SampleEvent = converter.decode(encoded)

    assertThat(decoded).isEqualTo(AvroAdapterTestLib.sampleFoo)
  }

  private fun createConverter(
    decoderSpecificRecordClassResolver: DecoderSpecificRecordClassResolver = reflectionBasedDecoderSpecificRecordClassResolver,
    ignoredIncompatibilities: Set<SchemaCompatibility.SchemaIncompatibilityType> = setOf()
  ) =
    DefaultSpecificRecordToSingleObjectConverter(
      registry.schemaResolver(),
      decoderSpecificRecordClassResolver,
      DefaultSchemaCompatibilityResolver(ignoredIncompatibilities)
    )

}
