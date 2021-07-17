package io.holixon.avro.adapter.api.cache

import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper
import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper.InMemorySchemaResolver
import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper.avroSchemaWithId
import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaWithId
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
import javax.cache.Cache
import javax.cache.CacheManager
import javax.cache.Caching

internal class CachingSchemaResolverTest {
  companion object {
    // to test eviction, lets assume a max size of two, so the third entry will force eviction
    const val MAX_CACHE_SIZE = 2L

    val sampleEvent4711 = avroSchemaWithId(SampleEventV4711)
    val sampleEvent4712 = avroSchemaWithId(SampleEventV4712)
    val sampleEvent4713 = avroSchemaWithId(SampleEventV4713)
  }

  private lateinit var cache: Cache<AvroSchemaId /* = kotlin.String */, AvroSchemaWithId?>
  private lateinit var spyingSchemaResolver: InMemorySchemaResolver
  private lateinit var cachingSchemaResolver: CachingSchemaResolver

  @BeforeEach
  internal fun setUp() {
    val cacheManager: CacheManager = Caching.getCachingProvider().cacheManager

    spyingSchemaResolver = spy(AvroAdapterApiTestHelper.sampleEventsSchemaResolver)


    cache = cacheManager.createCache(
      CachingSchemaResolver.DEFAULT_CACHE_NAME,
      AvroAdapterCache.mutableConfiguration(spyingSchemaResolver)
    )


    cachingSchemaResolver = CachingSchemaResolver(cache)
  }

  @Test
  internal fun `cache is filled on first hit`() {
    assertThat(cache.name).isEqualTo(CachingSchemaResolver.DEFAULT_CACHE_NAME)
    assertContainsKeys(
      sampleEvent4711.schemaId to false,
      sampleEvent4712.schemaId to false,
      sampleEvent4713.schemaId to false,
    )

    val resolve4711 = cachingSchemaResolver.apply(sampleEvent4711.schemaId)

    assertThat(resolve4711).hasValue(sampleEvent4711)

    assertContainsKeys(
      sampleEvent4711.schemaId to true,
      sampleEvent4712.schemaId to false,
      sampleEvent4713.schemaId to false,
    )
  }

  @Test
  internal fun `empty when not found via loader`() {
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
