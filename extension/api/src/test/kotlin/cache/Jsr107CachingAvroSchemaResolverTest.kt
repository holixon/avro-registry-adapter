package io.holixon.avro.adapter.api.cache

import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper
import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper.InMemoryAvroSchemaResolver
import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper.avroSchemaWithId
import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.cache.Jsr107AvroAdapterCache.DEFAULT_CACHE_NAME
import io.holixon.avro.adapter.api.cache.Jsr107AvroAdapterCache.Jsr107CachingAvroSchemaResolver
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4712
import io.holixon.avro.lib.test.schema.SampleEventV4713
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import javax.cache.Cache
import javax.cache.CacheManager
import javax.cache.Caching

internal class Jsr107CachingAvroSchemaResolverTest {
  companion object {
    val sampleEvent4711 = avroSchemaWithId(SampleEventV4711)
    val sampleEvent4712 = avroSchemaWithId(SampleEventV4712)
    val sampleEvent4713 = avroSchemaWithId(SampleEventV4713)
  }

  private lateinit var cache: Cache<AvroSchemaId /* = kotlin.String */, AvroSchemaWithId?>
  private lateinit var spyingSchemaResolver: InMemoryAvroSchemaResolver
  private lateinit var cachingSchemaResolver: Jsr107CachingAvroSchemaResolver

  @BeforeEach
  internal fun setUp() {
    val cacheManager: CacheManager = Caching.getCachingProvider().cacheManager

    spyingSchemaResolver = spy(AvroAdapterApiTestHelper.sampleEventsSchemaResolver)

    cache = cacheManager.createCache(
      DEFAULT_CACHE_NAME,
      Jsr107AvroAdapterCache.mutableConfiguration(spyingSchemaResolver)
    )
    cachingSchemaResolver = Jsr107CachingAvroSchemaResolver(cache)
  }

  @Test
  fun `cache is filled on first hit`() {
    assertThat(cache.name).isEqualTo(DEFAULT_CACHE_NAME)
    assertContainsKeys(
      sampleEvent4711.schemaId to false,
      sampleEvent4712.schemaId to false,
      sampleEvent4713.schemaId to false,
    )

    val resolve4711 = cachingSchemaResolver.apply(sampleEvent4711.schemaId)
    cachingSchemaResolver.apply(sampleEvent4711.schemaId)

    assertThat(resolve4711).hasValue(sampleEvent4711)

    // resolver only called once
    verify(spyingSchemaResolver).apply(sampleEvent4711.schemaId)

    assertContainsKeys(
      sampleEvent4711.schemaId to true,
      sampleEvent4712.schemaId to false,
      sampleEvent4713.schemaId to false,
    )
  }

  @Test
  fun `empty when not found via loader`() {
    assertContainsKeys(
      sampleEvent4711.schemaId to false,
      sampleEvent4712.schemaId to false,
      sampleEvent4713.schemaId to false,
    )

    assertThat(cachingSchemaResolver.apply("xxx")).isEmpty
  }

  @AfterEach
  internal fun tearDown() {
    Caching.getCachingProvider().cacheManager.close()
  }

  private fun assertContainsKeys(vararg pairs: Pair<String, Boolean>) = SoftAssertions()
    .apply {
      pairs.forEach {
        Assertions.assertThat(cache.containsKey(it.first))
          .`as`("expected: containsKey(%s)=%s", it.first, it.second)
          .isEqualTo(it.second)
      }
    }
    .assertAll()
}
