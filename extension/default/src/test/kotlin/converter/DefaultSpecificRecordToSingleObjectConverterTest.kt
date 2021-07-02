package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.decoder.DefaultSingleObjectToSpecificRecordDecoder
import io.holixon.avro.adapter.common.encoder.DefaultSpecificRecordToSingleObjectEncoder
import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.lib.test.schema.SampleEventV4713
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

  private val registry = AvroAdapterDefault.inMemorySchemaRegistry()

  @Test
  internal fun `encode and decode same writer and reader schema`() {
    registry.register(AvroAdapterTestLib.schemaSampleEvent4711)

    val data: SampleEvent = AvroAdapterTestLib.sampleFoo
    val encoded = DefaultSpecificRecordToSingleObjectEncoder().encode(data)

    val decoder = DefaultSingleObjectToSpecificRecordDecoder(
      schemaResolver = registry.schemaResolver(),
      decoderSpecificRecordClassResolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver,
      schemaIncompatibilityResolver = DefaultSchemaCompatibilityResolver()
    )

    val decoded: SampleEvent = decoder.decode(encoded)

    assertThat(decoded).isEqualTo(data)
  }

  @Test
  internal fun `encode and decode different writer and reader schema`() {

    val reg1 = registry.register(AvroAdapterTestLib.schemaSampleEvent4711)
    val reg2 = registry.register(AvroAdapterTestLib.schemaSampleEvent4713)

    val data: SampleEvent = AvroAdapterTestLib.sampleFoo
    val encoded = DefaultSpecificRecordToSingleObjectEncoder().encode(data)

    @Suppress("UNCHECKED_CAST")
    val decoder = DefaultSingleObjectToSpecificRecordDecoder(
      schemaResolver = registry.schemaResolver(),
      decoderSpecificRecordClassResolver = {
        Class.forName(SampleEventV4713.addSuffix(it.canonicalName)) as Class<SpecificRecordBase>
      },
      schemaIncompatibilityResolver = DefaultSchemaCompatibilityResolver()
    )

    assertThatThrownBy {
      decoder.decode<SampleEventWithAdditionalFieldWithDefault>(encoded)
    }.hasMessage("Reader schema[${reg2.schemaId}] is not compatible with Writer schema[${reg1.schemaId}]. The incompatibilities are: [NAME_MISMATCH]")

  }


  @Test
  internal fun `encode and decode different writer and reader schema ignoring NAME_MISMATCH`() {

    registry.register(AvroAdapterTestLib.schemaSampleEvent4711)
    registry.register(AvroAdapterTestLib.schemaSampleEvent4713)

    val data: SampleEvent = AvroAdapterTestLib.sampleFoo

    (SampleEvent.newBuilder(data))

    val encoded: AvroSingleObjectEncoded /* = kotlin.ByteArray */ = DefaultSpecificRecordToSingleObjectEncoder().encode(data)

    @Suppress("UNCHECKED_CAST")
    val decoder = DefaultSingleObjectToSpecificRecordDecoder(
      schemaResolver = registry.schemaResolver(),
      decoderSpecificRecordClassResolver = {
        Class.forName(SampleEventV4713.addSuffix(it.canonicalName)) as Class<SpecificRecordBase>
      },
      schemaIncompatibilityResolver = DefaultSchemaCompatibilityResolver(setOf(NAME_MISMATCH))
    )

    val decoded: SampleEventWithAdditionalFieldWithDefault = decoder.decode(encoded)

    assertThat(decoded).isEqualTo(AvroAdapterTestLib.sampleFooWithAdditionalFieldWithDefault)

  }

  @Test
  internal fun `encode and decode different writer=4713 and reader=4711 schema ignoring NAME_MISMATCH`() {

    registry.register(AvroAdapterTestLib.schemaSampleEvent4711)
    registry.register(AvroAdapterTestLib.schemaSampleEvent4713)

    val data: SampleEventWithAdditionalFieldWithDefault = AvroAdapterTestLib.sampleFooWithAdditionalFieldWithDefault
    val encoded = DefaultSpecificRecordToSingleObjectEncoder().encode(data)

    @Suppress("UNCHECKED_CAST")
    val decoded: SampleEvent = DefaultSingleObjectToSpecificRecordDecoder(
      schemaResolver = registry.schemaResolver(),
      decoderSpecificRecordClassResolver = {
        Class.forName(SampleEventV4713.removeSuffix(it.canonicalName)) as Class<SpecificRecordBase>
      },
      schemaIncompatibilityResolver = DefaultSchemaCompatibilityResolver(setOf(NAME_MISMATCH))

    ).decode(encoded)

    assertThat(decoded).isEqualTo(AvroAdapterTestLib.sampleFoo)
  }
}
