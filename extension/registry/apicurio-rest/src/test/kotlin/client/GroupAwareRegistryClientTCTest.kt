package io.holixon.avro.adapter.registry.apicurio.client

import io.apicurio.registry.rest.v2.beans.SearchedArtifact
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.DEFAULT_GROUP
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRestHelper.ApicurioRegistryTestContainer
import io.holixon.avro.lib.test.schema.SampleEventV4711
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@TestMethodOrder(OrderAnnotation::class)
internal class GroupAwareRegistryClientTCTest {
  companion object : KLogging() {

    @Container
    @JvmStatic
    val CONTAINER = ApicurioRegistryTestContainer()
  }

  private val registryClient by lazy {
    CONTAINER.restClient()
  }

  private val client = GroupAwareRegistryClient(client = registryClient, group = DEFAULT_GROUP)

  @Test
  @Order(1)
  internal fun `findSchemaById fails because schema not found`() {
    assertThat(client.findSchemaById("xxx").isSuccess).isFalse
  }

  @Test
  @Order(2)
  internal fun `findAllArtifacts is empty because no schemas registered`() {
    val result: Result<List<SearchedArtifact>> = client.findAllArtifacts()
    assertThat(result.isSuccess).isTrue
    assertThat(result.getOrThrow()).isEmpty()
  }

  @Test
  @Order(3)
  internal fun `findArtifactMetaData fails because schema not found`() {
    assertThat(client.findArtifactMetaData("xxx").isSuccess).isFalse
  }

  @Test
  @Order(4)
  internal fun registerSchema() {
    val result = client.registerSchema(SampleEventV4711.schema, SampleEventV4711.schemaData.schemaId, SampleEventV4711.schemaData.revision)
    assertThat(result.isSuccess).isTrue
    val metaData = result.getOrThrow()

    assertThat(metaData.id).isEqualTo(SampleEventV4711.schemaData.schemaId)
    assertThat(metaData.name).isEqualTo(SampleEventV4711.schemaData.name)
    assertThat(metaData.description).isEqualTo(SampleEventV4711.schema.doc)

    assertThat(metaData.properties[ApicurioAvroSchemaRegistry.KEY_NAME]).isEqualTo(SampleEventV4711.schemaData.name)
    assertThat(metaData.properties[ApicurioAvroSchemaRegistry.KEY_NAMESPACE]).isEqualTo(SampleEventV4711.schemaData.namespace)
    assertThat(metaData.properties[ApicurioAvroSchemaRegistry.KEY_CANONICAL_NAME]).isEqualTo(SampleEventV4711.schema.fullName)
    assertThat(metaData.properties[ApicurioAvroSchemaRegistry.KEY_REVISION]).isEqualTo(SampleEventV4711.schemaData.revision)
  }

  @Test
  @Order(5)
  internal fun `findSchemaById success after register (4)`() {
    val result = client.findSchemaById(SampleEventV4711.schemaData.schemaId)

    assertThat(result.isSuccess).isTrue
    assertThat(result.getOrThrow()).isEqualTo(SampleEventV4711.schema)
  }

  @Test
  @Order(6)
  internal fun `findAllArtifacts contains sampleEvent after register (4)`() {
    val result: Result<List<SearchedArtifact>> = client.findAllArtifacts()
    assertThat(result.isSuccess).isTrue
    assertThat(result.getOrThrow()).hasSize(1)
  }

  @Test
  @Order(7)
  internal fun `findArtifactMetaData is success after register (4)`() {
    assertThat(client.findArtifactMetaData(SampleEventV4711.schemaData.schemaId).isSuccess).isTrue
  }
}
