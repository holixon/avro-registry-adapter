package io.holixon.avro.adapter.registry.apicurio

import org.testcontainers.containers.GenericContainer

/**
 * Test container for apicurio tests.
 */
class ApicurioRegistryTestContainer : GenericContainer<ApicurioRegistryTestContainer>("apicurio/apicurio-registry-mem:2.0.1.Final") {
  companion object {
    const val EXPOSED_PORT = 8080
  }

  /**
   * Delivers the REST client for Apicurio access.
   */
  fun restClient() =
    AvroAdapterApicurioRest.registryRestClient(containerIpAddress, getMappedPort(EXPOSED_PORT))
}
