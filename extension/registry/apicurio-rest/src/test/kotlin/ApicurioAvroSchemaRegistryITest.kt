package io.holixon.avro.adapter.registry.apicurio

import io.holixon.avro.adapter.api.type.AvroSchemaInfoData
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.registry.apicurio.ApicurioRegistryTestContainer.Companion.EXPOSED_PORT
import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.lib.test.schema.SampleEventV4711
import mu.KLogging
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

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
  internal fun `find by id`() {
    val schema: Schema = SampleEventV4711.schema
    val fingerprint = AvroAdapterDefault.schemaIdSupplier.apply(schema)

    val created = registryClient.register(schema)
    logger.info { "created: $created" }

    val found = registryClient.findById(fingerprint).orElseThrow()

    assertThat(found.schemaId).isEqualTo(fingerprint)
    assertThat(found.schema).isEqualTo(schema)
    assertThat(found.revision).isEqualTo("4711")
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

    val found = registryClient.findAllByCanonicalName(
      namespace = schema.namespace,
      name = schema.name
    )

    assertThat(found).hasSize(1)
    assertThat(found.first().schemaId).isEqualTo(fingerprint)
  }
}
