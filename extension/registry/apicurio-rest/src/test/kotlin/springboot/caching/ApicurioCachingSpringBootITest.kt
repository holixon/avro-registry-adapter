package io.holixon.avro.adapter.registry.apicurio.springboot.caching

import io.apicurio.registry.rest.client.RegistryClient
import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.SchemaRevisionResolver
import io.holixon.avro.adapter.api.cache.CachingAvroSchemaResolver
import io.holixon.avro.adapter.api.cache.Jsr107AvroAdapterCache
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRestHelper.ApicurioRegistryTestContainer
import io.holixon.avro.adapter.registry.apicurio.springboot.caching.ApicurioCachingSpringBootITest.Companion.ApicurioCachingSpringBootITestApplication
import io.holixon.avro.adapter.registry.apicurio.springboot.caching.ApicurioCachingSpringBootITest.Companion.ApicurioCachingSpringBootITestApplication.EhCachingSchemaResolver
import io.holixon.avro.lib.test.schema.SampleEventV4711
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.ehcache.event.CacheEvent
import org.ehcache.event.CacheEventListener
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@SpringBootTest(
  classes = [ApicurioCachingSpringBootITestApplication::class],
  properties = ["spring.cache.jcache.config=classpath:ehcache.xml"],
  webEnvironment = NONE
)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@ActiveProfiles(ApicurioCachingSpringBootITest.PROFILE)
class ApicurioCachingSpringBootITest {
  companion object : KLogging() {
    const val PROFILE = "apicurioCachingSpringBootITest"

    @Container
    val apicurio = ApicurioRegistryTestContainer()

    @JvmStatic
    @DynamicPropertySource
    fun propertySource(registry: DynamicPropertyRegistry) {
      registry.add("apicurio.api.url") { apicurio.apiUrl() }
    }

    @SpringBootApplication
    @EnableCaching
    @Profile(PROFILE)
    class ApicurioCachingSpringBootITestApplication {

      @Bean
      fun schemaIdSupplier() = AvroAdapterDefault.schemaIdSupplier

      @Bean
      fun schemaRevisionResolver() = AvroAdapterDefault.schemaRevisionResolver

      @Bean
      fun apicurioRegistryClient(@Value("\${apicurio.api.url}") url: String): RegistryClient =
        AvroAdapterApicurioRest.registryRestClient(url)

      @Bean
      fun schemaRegistry(
        registryClient: RegistryClient,
        schemaIdSupplier: SchemaIdSupplier,
        schemaRevisionResolver: SchemaRevisionResolver
      ): ApicurioAvroSchemaRegistry = ApicurioAvroSchemaRegistry(
        registryClient, AvroAdapterApicurioRest.DEFAULT_GROUP,
        schemaIdSupplier,
        schemaRevisionResolver
      )

      @Component
      class EhCachingSchemaResolver(private val schemaRegistry: ApicurioAvroSchemaRegistry) : CachingAvroSchemaResolver {

        @Cacheable(value = [Jsr107AvroAdapterCache.DEFAULT_CACHE_NAME], unless = "#result == null")
        override fun apply(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> = schemaRegistry.findById(schemaId)
      }
    }

    class ApicurioCacheEventListener : CacheEventListener<AvroSchemaId, AvroSchemaWithId> {
      override fun onEvent(event: CacheEvent<out AvroSchemaId, out AvroSchemaWithId>) {
        logger.info { "cacheListener: ${event::class.simpleName} - ${event.key} - old=${event.oldValue}, new=${event.newValue}" }
      }
    }
  }

  @SpyBean
  private lateinit var schemaRegistry: ApicurioAvroSchemaRegistry

  @Autowired
  private lateinit var cachingResolver: EhCachingSchemaResolver

  @Test
  @Order(1)
  internal fun `not registered - empty`() {
    // this would fill the cache with empty value if we would not use "unless".
    assertThat(cachingResolver.apply(SampleEventV4711.schemaData.schemaId)).isEmpty
  }

  @Test
  @Order(2)
  internal fun `registered - can be found`() {
    val registered = schemaRegistry.register(SampleEventV4711.schema)

    assertThat(cachingResolver.apply(registered.schemaId)).isNotEmpty

    // 2nd call
    cachingResolver.apply(registered.schemaId)

    verify(schemaRegistry, times(1)).findById(registered.schemaId)
  }
}
