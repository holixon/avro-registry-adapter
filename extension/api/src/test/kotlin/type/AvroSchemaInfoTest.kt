package io.holixon.avro.adapter.api.type

import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.adapter.api.AvroSchemaInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroSchemaInfoTest {

  private val sampleEventSchema = AvroAdapterTestLib.loadArvoResource("test.fixture.SampleEvent-v4711")

  @Test
  internal fun `avro schema info`() {
    val data : AvroSchemaInfo = AvroSchemaInfoData(context = "foo", name = "bar", revision = "1")

    assertThat(data.context).isEqualTo("foo")
    assertThat(data.name).isEqualTo("bar")
    assertThat(data.revision).isEqualTo("1")
    assertThat(data.canonicalName).isEqualTo("foo.bar")
  }
}
