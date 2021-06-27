package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.decoder.DefaultSingleObjectToGenericDataRecordDecoder
import io.holixon.avro.adapter.common.encoder.DefaultGenericDataRecordToSingleObjectEncoder
import io.holixon.avro.adapter.common.encoder.DefaultSpecificRecordToSingleObjectEncoder
import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.lib.test.schema.SampleEventV4713
import org.apache.avro.SchemaCompatibility
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent
import test.fixture.SampleEventWithAdditionalFieldWithDefault

internal class DefaultConvertersTests {
  private val registry = AvroAdapterDefault.inMemorySchemaRegistry()

  @Test
  internal fun `convert bytes to generic data record to specific record`() {

    registry.register(AvroAdapterTestLib.schemaSampleEvent4711)
    registry.register(AvroAdapterTestLib.schemaSampleEvent4713)

    val resolver = AvroAdapterDefault.reflectionBasedDecoderSpecificRecordClassResolver

    val genericRecordEncoder = DefaultGenericDataRecordToSingleObjectEncoder()
    val genericRecordDecoder = DefaultSingleObjectToGenericDataRecordDecoder(registry.schemaResolver())
    val specificRecordEncoder = DefaultSpecificRecordToSingleObjectEncoder()

    val specificRecordToGenericRecordConverter = DefaultSpecificRecordToGenericDataRecordConverter()

    @Suppress("UNCHECKED_CAST")
    val genericRecordToSpecificRecordConverter = DefaultGenericDataRecordToSpecificRecordChangingSchemaConverter(
      schemaResolver = registry.schemaResolver(),
      decoderSpecificRecordClassResolver = {
        Class.forName(SampleEventV4713.removeSuffix(it.canonicalName)) as Class<SpecificRecordBase>
      },
      schemaIncompatibilityResolver = DefaultSchemaCompatibilityResolver(setOf(SchemaCompatibility.SchemaIncompatibilityType.NAME_MISMATCH))
    )

    // specific event
    val data: SampleEventWithAdditionalFieldWithDefault = AvroAdapterTestLib.sampleFooWithAdditionalFieldWithDefault
    assertThat(data.schema).isEqualTo(AvroAdapterTestLib.schemaSampleEvent4713)

    // convert to generic record
    val genericDataRecord: GenericData.Record = specificRecordToGenericRecordConverter.convert(data)
    assertThat(genericDataRecord.get("value")).isEqualTo(data.value)
    assertThat(genericDataRecord.get("otherValue")).isEqualTo(data.otherValue)
    assertThat(genericDataRecord.schema).isEqualTo(AvroAdapterTestLib.schemaSampleEvent4713)

    // bytes
    val bytes = genericRecordEncoder.encode(genericDataRecord)

    // generic record
    val decodedGenericDataRecord: GenericData.Record = genericRecordDecoder.decode(bytes)
    assertThat(decodedGenericDataRecord).isEqualTo(genericDataRecord)
    assertThat(decodedGenericDataRecord.get("value")).isEqualTo(data.value)
    assertThat(decodedGenericDataRecord.get("otherValue")).isEqualTo(data.otherValue)
    // still writer schema => 4713
    assertThat(decodedGenericDataRecord.schema).isEqualTo(AvroAdapterTestLib.schemaSampleEvent4713)

    // specific event (read with a compatible but different schema)
    val decodedEvent: SampleEvent = genericRecordToSpecificRecordConverter.convert(decodedGenericDataRecord)

    // the transformation removed one property defined in 4713
    assertThat(decodedEvent).isEqualTo(SampleEvent(data.value))
    // changed to reader schema => 4711
    assertThat(decodedEvent.schema).isEqualTo(AvroAdapterTestLib.schemaSampleEvent4711)
  }

}
