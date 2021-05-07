package io.toolisticon.avro.lib.test.schema

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SampleEventV4711Test {

  @Test
  internal fun `load from resource`() {
    val schemaData = SampleEventV4711.schemaData

    assertThat(schemaData.name).isEqualTo("SampleEvent")
    assertThat(schemaData.revision).isEqualTo("4711")
    assertThat(schemaData.doc).isEqualTo("a sample event for testing")
  }
}
