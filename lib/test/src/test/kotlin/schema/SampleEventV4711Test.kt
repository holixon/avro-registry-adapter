package io.holixon.avro.lib.test.schema

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SampleEventV4711Test {

  @Test
  internal fun `load from resource`() {
    println(SampleEventV4711.schemaJson)

    val schemaData = SampleEventV4711.schemaData()

    assertThat(schemaData.name).isEqualTo("SampleEvent")
    assertThat(schemaData.revision).isEqualTo("4711")
  }
}
