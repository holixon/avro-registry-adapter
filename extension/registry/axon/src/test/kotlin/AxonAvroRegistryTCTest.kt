package io.holixon.avro.adapter.registry.axon

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.security.AnyTypePermission
import com.thoughtworks.xstream.security.TypePermission
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaId
import io.holixon.avro.adapter.registry.axon.api.RegisterAvroSchemaCommand
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.axon.testcontainer.AxonServerContainer
import io.holixon.axon.testcontainer.AxonServerContainerSpring.addDynamicProperties
import mu.KLogging
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.serialization.xml.XStreamSerializer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(classes = [AxonAvroRegistryTCTestApplication::class], webEnvironment = NONE)
@Testcontainers
internal class AxonAvroRegistryTCTest {
  companion object : KLogging() {

    @Container
    val axon = AxonServerContainer()

    @JvmStatic
    @DynamicPropertySource
    fun axonProperties(registry: DynamicPropertyRegistry) = axon.addDynamicProperties(registry)
  }


  @Autowired
  private lateinit var registry : AxonAvroRegistry

  @Test
  fun `register and find by id`() {
    val schema = SampleEventV4711.schema

    assertThat(registry.findAll()).isEmpty()

    val registered = registry.register(schema)

    assertThat(registry.findById(schema.avroSchemaId)).isNotEmpty
  }
}

@SpringBootApplication
@Import(AxonAvroRegistryConfiguration::class)
class AxonAvroRegistryTCTestApplication {

  @Bean
  fun objectMapper() = jacksonObjectMapper().findAndRegisterModules()
}
