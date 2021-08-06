package io.holixon.avro.adapter.registry.apicurio.client

import io.apicurio.registry.rest.v2.beans.SearchedArtifact
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.DEFAULT_GROUP
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.PropertyKey
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRestHelper.ApicurioRegistryTestContainer
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRestHelper.registerDefaultRandomId
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4712
import mu.KLogging
import org.apache.avro.Schema
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


  private val client = GroupAwareRegistryClient(
    client = registryClient,
    schemaIdSupplier = AvroAdapterDefault.schemaIdSupplier,
    schemaRevisionResolver = AvroAdapterDefault.schemaRevisionResolver,
    group = DEFAULT_GROUP
  )

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
    val result = client.registerSchema(SampleEventV4711.schema)
    assertThat(result.isSuccess).isTrue
    val schemaData = result.getOrThrow()

    assertThat(schemaData.apicurioArtifactId).isEqualTo(SampleEventV4711.schemaData.schemaId)
    assertThat(schemaData.schemaId).isEqualTo(SampleEventV4711.schemaData.schemaId)
    assertThat(schemaData.name).isEqualTo(SampleEventV4711.schemaData.name)
    assertThat(schemaData.metaData.description).isEqualTo(SampleEventV4711.schema.doc)

    assertThat(schemaData.metaData.nameProperty).isEqualTo(SampleEventV4711.schemaData.name)
    assertThat(schemaData.metaData.namespaceProperty).isEqualTo(SampleEventV4711.schemaData.namespace)
    assertThat(schemaData.canonicalName).isEqualTo(SampleEventV4711.schema.fullName)
    assertThat(schemaData.revision).isEqualTo(SampleEventV4711.schemaData.revision)
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

  @Test
  internal fun `modify metadata for externally registered schema`() {
    val schema = SampleEventV4712.schema
    val schemaId = AvroAdapterDefault.schemaIdSupplier.apply(schema)
    val origMetaData = registryClient.registerDefaultRandomId(schema)

    assertThat(origMetaData.id).isNotEqualTo(schemaId)
    assertThat(origMetaData.properties[PropertyKey.NAMESPACE]).isNull()
    assertThat(origMetaData.properties[PropertyKey.NAME]).isNull()
    assertThat(origMetaData.properties[PropertyKey.REVISION]).isNull()
    assertThat(origMetaData.properties[PropertyKey.SCHEMA_ID]).isNull()

    assertThat(client.findSchemaById(schemaId).isFailure).isTrue
    assertThat(client.findSchemaById(origMetaData.id).isSuccess).isTrue

    client.updateAllNotInitializedArtifactMetaData()

    client.findAllMetaData().getOrThrow().forEach{logger.info { "metaData: $it" }}

    val found: Schema =client.findSchemaById(schemaId).getOrThrow()

    assertThat(found).isEqualTo(schema)
  }
}
