package io.holixon.avro.adapter.api.type

import io.holixon.avro.adapter.api.AvroSchemaInfo
import io.holixon.avro.adapter.api.AvroSchemaInfo.Companion.equalsByFields
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroSchemaInfoTest {

  @Test
  internal fun `avro schema info`() {
    val data: AvroSchemaInfo = AvroSchemaInfoData(namespace = "foo", name = "bar", revision = "1")

    assertThat(data.namespace).isEqualTo("foo")
    assertThat(data.name).isEqualTo("bar")
    assertThat(data.revision).isEqualTo("1")
    assertThat(data.canonicalName).isEqualTo("foo.bar")
  }

  @Test
  internal fun `equals by field`() {
    assertThat(AvroSchemaInfoData("foo","bar", "47")
      .equalsByFields(AvroSchemaInfoData("foo","bar", "47"))
    ).isTrue
    assertThat(AvroSchemaInfoData("foo","bar", null)
      .equalsByFields(AvroSchemaInfoData("foo","bar", "47"))
    ).isFalse
    assertThat(AvroSchemaInfoData("foo","bar", null)
      .equalsByFields(AvroSchemaInfoData("foo","bar", null))
    ).isTrue

  }

}
