package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.AvroSchemaResolver
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaWithId
import io.holixon.avro.lib.test.AvroAdapterTestLib
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.*

internal class DefaultAvroSchemaRevisionForSchemaIdResolverTest {

  private val schemaResolver = mock<AvroSchemaResolver>()
  private val revisionForSchemaIdResolver = DefaultAvroSchemaRevisionForSchemaIdResolver(schemaResolver)

  @Test
  fun `resolve revision by redirecting to SchemaRevisionResolver`() {
    val schema = AvroAdapterTestLib.schemaSampleEvent4711.avroSchemaWithId

    whenever(schemaResolver.apply(schema.schemaId)).thenReturn(Optional.of(schema))

    assertThat(revisionForSchemaIdResolver.apply(schema.schemaId)).hasValue(schema.revision)
  }
}
