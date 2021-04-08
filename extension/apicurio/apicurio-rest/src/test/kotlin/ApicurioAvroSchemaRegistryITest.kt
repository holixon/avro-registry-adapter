package io.toolisticon.avro.adapter.apicurio

import io.apicurio.registry.client.RegistryRestClient
import io.apicurio.registry.client.RegistryRestClientFactory
import io.holixon.axon.avro.lib.test.AvroAdapterTestLib
import io.toolisticon.avro.adapter.api.type.AvroSchemaInfoData
import io.toolisticon.avro.adapter.common.AvroAdapterDefault
import mu.KLogging
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


class ApicurioRegistryTestContainer : GenericContainer<ApicurioRegistryTestContainer>("apicurio/apicurio-registry-mem:1.3.2.Final")

@Testcontainers
internal class ApicurioAvroSchemaRegistryITest {
  companion object : KLogging()

  @Container
  val container = ApicurioRegistryTestContainer().apply {
    withExposedPorts(8080)
    withLogConsumer(Slf4jLogConsumer(logger))
    setWaitStrategy(HostPortWaitStrategy())
  }

  val registryUrl: String by lazy {
    // TODO url change with 2.0.0
    "http://${container.containerIpAddress}:${container.getMappedPort(8080)}/api"
    //"http://localhost:7777/api" // from docker-compose
  }

  private val client: RegistryRestClient by lazy { RegistryRestClientFactory.create(registryUrl) }

  private val registryClient by lazy { ApicurioAvroSchemaRegistry(client, AvroAdapterDefault.schemaIdSupplier, AvroAdapterDefault.schemaRevisionResolver)}

  @Test
  internal fun `get empty`() {
    assertThat(registryClient.findAll()).isEmpty()
  }

  @Test
  internal fun `find by id`() {

    val schema: Schema = AvroAdapterTestLib.schemaSampleEvent4711
    val fingerprint = AvroAdapterDefault.schemaIdSupplier.apply(schema)

    val created = registryClient.register(schema)
    logger.info { "created: $created" }

    val found = registryClient.findById(fingerprint).orElseThrow()

    assertThat(found.revision).isEqualTo("4711")
    assertThat(found.id).isEqualTo(fingerprint)

  }

  @Test
  internal fun `find by info`() {
    val schema: Schema = AvroAdapterTestLib.schemaSampleEvent4711
    val fingerprint = AvroAdapterDefault.schemaIdSupplier.apply(schema)

    val created = registryClient.register(schema)
    logger.info { "created: $created" }

    val found = registryClient.findByInfo(AvroSchemaInfoData(
      context = schema.namespace,
      name = schema.name,
      revision = "4711"
    ))

    assertThat(found).isNotEmpty
    assertThat(found.get().id).isEqualTo(fingerprint)

    assertThat(registryClient.findByInfo(AvroSchemaInfoData(
      context = schema.namespace,
      name = schema.name,
      revision = "4712"
    ))).isEmpty
  }

  @Test
  internal fun `find by context and name`() {
    val schema: Schema = AvroAdapterTestLib.schemaSampleEvent4711
    val fingerprint = AvroAdapterDefault.schemaIdSupplier.apply(schema)

    val created = registryClient.register(schema)
    logger.info { "created: $created" }

    val found = registryClient.findByContextAndName(
      context = schema.namespace,
      name = schema.name
    )

    assertThat(found).hasSize(1)
    assertThat(found.first().id).isEqualTo(fingerprint)
  }
}
