package io.holixon.avro.adapter.api.cache

import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper
import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper.sampleEvent4711
import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper.sampleEvent4712
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class SchemaResolverCacheLoaderTest {

  private val cacheLoader = SchemaResolverCacheLoader(AvroAdapterApiTestHelper.sampleEventsSchemaResolver)

  @Test
  internal fun `load single - non existing`() {
    assertThat(cacheLoader.load("xxx")).isNull()
  }

  @Test
  internal fun `load single - existing`() {
    assertThat(cacheLoader.load(sampleEvent4711.schemaId)).isEqualTo(sampleEvent4711)
  }

  @Test
  internal fun `loadAll - existing and non existing`() {
    val map = cacheLoader.loadAll(listOf(sampleEvent4711.schemaId, sampleEvent4712.schemaId, "xxx"))
    assertThat(map).hasSize(3)

    assertThat(map["xxx"]).isNull()
    assertThat(map[sampleEvent4711.schemaId]).isEqualTo(sampleEvent4711)
  }
}
