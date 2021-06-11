package io.holixon.avro.adapter.common.converter

import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultGenericRecordToSingleObjectConverterTest {

  private val converter = DefaultGenericRecordToSingleObjectConverter()

  @Test
  internal fun `convert to bytes`() {
    val schema = SampleEventV4711.schema

    val record : GenericRecord = GenericData.Record(schema).apply {
      put("value", "foo")
    }

    val bytes = converter.encode(record)

    assertThat(converter.decode(bytes)).isEqualTo(record)
  }
}
