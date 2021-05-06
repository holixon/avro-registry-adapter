package io.holixon.avro.lib.test

import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4712
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.fixture.SampleEvent

internal class AvroAdapterTestLibTest {

  @Test
  internal fun `schema matches`() {
    assertThat(SampleEvent.`SCHEMA$`).isEqualTo(SampleEventV4711.schema)
  }

  @Test
  internal fun `create specific from generic`() {
    val generic = GenericData.Record(SampleEvent.`SCHEMA$`).apply {
      put("value", "foo")
    }

    val specific = SpecificData.get().deepCopy(SampleEvent.`SCHEMA$`, generic) as SampleEvent

    assertThat(specific.value).isEqualTo("foo")
  }

  @Test
  internal fun `generic record from schema 4712`() {
    val schema4712 = SampleEventV4712.schema

    println(SampleEventV4712.create("foo"))
    println(SampleEventV4712.create(value = "foo", anotherValue = "bar"))
    println(SampleEventV4712.create(value = "foo", anotherValue = null))

    val record4712: GenericRecord = SampleEventV4712.create("foo")

    println("rec: $record4712")

    val sd4711 = SpecificData.get().deepCopy(SampleEventV4711.schema, record4712) as SampleEvent

    println("spec: $sd4711")
  }
}
