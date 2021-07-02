package io.holixon.avro.adapter.api

import io.holixon.avro.lib.test.schema.SampleEventV4712
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SchemaRevisionResolverTest {

  @Test
  internal fun `resolve revision for sample 4712`() {
    assertThat(AvroAdapterApi.propertyBasedSchemaRevisionResolver("revision").apply(SampleEventV4712.schema)).hasValue("4712")
  }
}
