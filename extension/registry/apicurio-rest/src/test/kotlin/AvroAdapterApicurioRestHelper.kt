package io.holixon.avro.adapter.registry.apicurio

import io.apicurio.registry.rest.client.RegistryClient
import io.apicurio.registry.types.ArtifactType
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.SchemaRevisionResolver
import io.holixon.avro.adapter.api.ext.SchemaExt.byteContent
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.DEFAULT_GROUP
import io.holixon.avro.adapter.registry.apicurio.client.GroupAwareRegistryClient
import io.holixon.avro.adapter.registry.apicurio.type.ApicurioArtifactMetaData
import mu.KLogging
import org.apache.avro.Schema
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import java.util.*

/**
 * Test Helpers
 */
object AvroAdapterApicurioRestHelper {

  /**
   * Test container for apicurio tests.
   */
  class ApicurioRegistryTestContainer : GenericContainer<ApicurioRegistryTestContainer>("apicurio/apicurio-registry-mem:2.1.2.Final") {
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
    ) = ApicurioAvroSchemaRegistry(registryClient, DEFAULT_GROUP, schemaIdSupplier, schemaRevisionResolver)
  }

  fun RegistryClient.registerDefaultRandomId(schema: Schema) =
    ApicurioArtifactMetaData(createArtifact(DEFAULT_GROUP, UUID.randomUUID().toString(), ArtifactType.AVRO, schema.byteContent()))
}

fun main() {
  val registryClient = AvroAdapterApicurioRest.registryRestClient("localhost", 7777)
  //val metaDataPropertiesFactory = ApicurioArtifactMetaDataPropertiesFactory(schemaIdSupplier = AvroAdapterDefault.schemaIdSupplier, schemaRevisionResolver = AvroAdapterDefault.schemaRevisionResolver)

  println(registryClient.listGlobalRules())
  println(registryClient.listArtifactsInGroup("default"))
  println(registryClient.listArtifactsInGroup("default").count)

  val c = GroupAwareRegistryClient(
    client = registryClient,
    schemaIdSupplier = AvroAdapterDefault.schemaIdSupplier,
    schemaRevisionResolver = AvroAdapterDefault.schemaRevisionResolver
  )
  println(c.findArtifactMetaData("6edc992f-494b-42ea-9665-8061aa6fc930").getOrNull())

}
