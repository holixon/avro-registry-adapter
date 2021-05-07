package io.holixon.avro.lib.test.schema

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SampleEventV4712Test {

  @Test
  internal fun `parse schema data`() {
    val schemaData = SampleEventV4712.schemaData

    assertThat(schemaData.name).isEqualTo("SampleEvent")

    assertThat(schemaData.schema.fields.map { it.name() }).containsExactlyInAnyOrder("value", "anotherValue")
  }
}
