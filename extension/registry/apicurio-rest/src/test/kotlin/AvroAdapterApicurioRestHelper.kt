package io.holixon.avro.adapter.registry.apicurio

import io.apicurio.registry.rest.client.RegistryClient
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.SchemaRevisionResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.registry.apicurio.client.RegistryClientExt.queryArtifacts
import mu.KLogging
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy

/**
 * Test Helpers
 */
object AvroAdapterApicurioRestHelper {

  /**
   * Test container for apicurio tests.
   */
  class ApicurioRegistryTestContainer : GenericContainer<ApicurioRegistryTestContainer>("apicurio/apicurio-registry-mem:2.1.3.Final") {
    companion object : KLogging() {
      const val EXPOSED_PORT = 8080
    }

    init {
      withExposedPorts(EXPOSED_PORT)
      withLogConsumer(Slf4jLogConsumer(logger))
      setWaitStrategy(HostPortWaitStrategy())
    }

    /**
     * The Url of the rest API v2.
     */
    fun apiUrl() = AvroAdapterApicurioRest.registryApiUrl(containerIpAddress, getMappedPort(EXPOSED_PORT))

    /**
     * Delivers the REST client for Apicurio access.
     */
    fun restClient(): RegistryClient = AvroAdapterApicurioRest.registryRestClient(apiUrl())

    fun schemaRegistry(
      registryClient: RegistryClient = restClient(),
      schemaIdSupplier: SchemaIdSupplier = AvroAdapterDefault.schemaIdSupplier,
      schemaRevisionResolver: SchemaRevisionResolver = AvroAdapterDefault.schemaRevisionResolver
    ) = ApicurioAvroSchemaRegistry(registryClient, schemaIdSupplier, schemaRevisionResolver)

    fun clear() = with(restClient()) {

      queryArtifacts().map {
        it.map { it.groupId }.toSet()
      }.getOrThrow().forEach {
        deleteArtifactsInGroup(it)
      }

    }
  }
}
