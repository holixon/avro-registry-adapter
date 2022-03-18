package io.holixon.avro.adapter.registry.axon.itest

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.security.AnyTypePermission
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaId
import io.holixon.avro.adapter.registry.axon.AxonAvroRegistry
import io.holixon.avro.adapter.registry.axon.EnableAxonAvroRegistry
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.axon.testcontainer.AxonServerContainer
import io.holixon.axon.testcontainer.spring.addDynamicProperties
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(classes = [AxonAvroRegistryTCTestApplication::class], webEnvironment = NONE)
@Testcontainers
@ActiveProfiles("tctest")
internal class AxonAvroRegistryTCTest {
  companion object : KLogging() {

    @Container
    val axon = AxonServerContainer.builder()
      .enableDevMode()
      .build().apply {
        logger.info { "Started $this" }
      }

    @JvmStatic
    @DynamicPropertySource
    fun axonProperties(registry: DynamicPropertyRegistry) = axon.addDynamicProperties(registry)

    @JvmStatic
    @AfterAll
    internal fun tearDown() {
      axon.stop()
    }
  }

  @Autowired
  private lateinit var registry: AxonAvroRegistry

  @Test
  fun `register and find by id`() {
    val schema = SampleEventV4711.schema

    assertThat(registry.findAll()).isEmpty()
    val registered = registry.register(schema)
    val found = registry.findById(schema.avroSchemaId)

    assertThat(found).isNotEmpty
    assertThat(registered.schemaId).isEqualTo(found.get().schemaId)
    assertThat(registered.schemaId).isEqualTo(schema.avroSchemaId)

  }
}

@SpringBootApplication
@EnableAxonAvroRegistry
internal class AxonAvroRegistryTCTestApplication {

  @Bean
  @ConditionalOnMissingBean(XStream::class)
  fun xStream(): XStream = XStream().apply { addPermission(AnyTypePermission.ANY) }
}
