package io.holixon.avro.adapter.common.cache

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaWithId
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4712
import io.holixon.avro.lib.test.schema.SampleEventV4713
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.spy
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.verify

internal class ConcurrentMapCachingAvroSchemaResolverTest {

  private val schemaRegistry: InMemoryAvroSchemaReadOnlyRegistry = spy(
    InMemoryAvroSchemaReadOnlyRegistry.createWithSchemas(
      SampleEventV4711.schema,
      SampleEventV4712.schema,
      SampleEventV4713.schema
    )
  )

  private val cachingResolver = ConcurrentMapCachingAvroSchemaResolver(schemaRegistry.schemaResolver())

  @Test
  fun `cache is empty`() {
    assertThat(cachingResolver.cache).isEmpty()
    verifyNoInteractions(schemaRegistry)
  }

  @Test
  fun `resolver is only called once`() {
    val schema4711 = cachingResolver.apply(SampleEventV4711.schemaData.schemaId)
    assertThat(schema4711).hasValue(SampleEventV4711.schema.avroSchemaWithId)

    // second call: cache hit:
    cachingResolver.apply(SampleEventV4711.schemaData.schemaId)

    // resolver only called once
    verify(schemaRegistry).findById(SampleEventV4711.schemaData.schemaId)
    assertThat(cachingResolver.cache).hasSize(1)
  }
}
