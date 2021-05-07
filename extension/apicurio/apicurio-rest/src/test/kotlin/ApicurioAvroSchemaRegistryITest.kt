package io.toolisticon.avro.adapter.apicurio

import io.toolisticon.avro.adapter.api.type.AvroSchemaInfoData
import io.toolisticon.avro.adapter.common.AvroAdapterDefault
import io.toolisticon.avro.lib.test.AvroAdapterTestLib
import mu.KLogging
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import io.apicurio.registry.rest.client.RegistryClientFactory
import io.toolisticon.avro.adapter.apicurio.ApicurioRegistryTestContainer.Companion.EXPOSED_PORT
import io.toolisticon.avro.lib.test.schema.SampleEventV4711

class ApicurioRegistryTestContainer : GenericContainer<ApicurioRegistryTestContainer>("apicurio/apicurio-registry-mem:2.0.0.Final") {
  companion object {
    const val EXPOSED_PORT = 8080
  }
  fun registryApiUrl() = AvroAdapterApicurioRest.registryApiUrl(containerIpAddress, getMappedPort(EXPOSED_PORT))
  fun restClient() = RegistryClientFactory.create(registryApiUrl())
}

@Testcontainers
internal class ApicurioAvroSchemaRegistryITest {
  companion object : KLogging() {

    @Container
    @JvmStatic
    val CONTAINER = ApicurioRegistryTestContainer().apply {
      withExposedPorts(EXPOSED_PORT)
      withLogConsumer(Slf4jLogConsumer(logger))
      setWaitStrategy(HostPortWaitStrategy())
    }
  }

  private val registryClient by lazy {
    ApicurioAvroSchemaRegistry(
      CONTAINER.restClient(),
      AvroAdapterDefault.schemaIdSupplier,
      AvroAdapterDefault.schemaRevisionResolver
    )
  }

  @Test
  internal fun `get empty`() {
    assertThat(registryClient.findAll()).isEmpty()
  }

  @Test
  internal fun `find by id`() {
    val schema: Schema = SampleEventV4711.schema
    val fingerprint = AvroAdapterDefault.schemaIdSupplier.apply(schema)

    val created = registryClient.register(schema)
    logger.info { "created: $created" }

    val (schemaId, foundSchema, revision) = registryClient.findById(fingerprint).orElseThrow()

    assertThat(schemaId).isEqualTo(fingerprint)
    assertThat(foundSchema).isEqualTo(schema)
    assertThat(revision).isEqualTo("4711")
  }

  @Test
  internal fun `find by info`() {
    val schema: Schema = AvroAdapterTestLib.schemaSampleEvent4711
    val fingerprint = AvroAdapterDefault.schemaIdSupplier.apply(schema)

    val created = registryClient.register(schema)
    logger.info { "created: $created" }

    val found = registryClient.findByInfo(
      AvroSchemaInfoData(
        namespace = schema.namespace,
        name = schema.name,
        revision = "4711"
      )
    )

    assertThat(found).isNotEmpty
    assertThat(found.get().schemaId).isEqualTo(fingerprint)

    assertThat(
      registryClient.findByInfo(
        AvroSchemaInfoData(
          namespace = schema.namespace,
          name = schema.name,
          revision = "4712"
        )
      )
    ).isEmpty
  }

  @Test
  internal fun `find by context and name`() {
    val schema = SampleEventV4711.schema
    val fingerprint = SampleEventV4711.schemaData.fingerPrint.toString()

    val created = registryClient.register(schema)
    logger.info { "created: $created" }

    val found = registryClient.findByCanonicalName(
      namespace = schema.namespace,
      name = schema.name
    )

    assertThat(found).hasSize(1)
    assertThat(found.first().schemaId).isEqualTo(fingerprint)
  }
}
