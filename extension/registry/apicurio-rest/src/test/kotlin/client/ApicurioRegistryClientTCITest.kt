package io.holixon.avro.adapter.registry.apicurio.client

import io.holixon.avro.adapter.api.ext.SchemaExt.avroSchemaFqn
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRestHelper
import io.holixon.avro.adapter.registry.apicurio.type.ApicurioArtifactWithMetaData
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4712
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
internal class ApicurioRegistryClientTCITest {
  companion object : KLogging() {

    @Container
    @JvmStatic
    private val CONTAINER = AvroAdapterApicurioRestHelper.ApicurioRegistryTestContainer()
  }

  private val sampleEventFqn = SampleEventV4711.schema.avroSchemaFqn()

  private val registryClient by lazy {
    CONTAINER.restClient()
  }

  private val createOrUpdateArtifact = DefaultCreateOrUpdateArtifact(registryClient, AvroAdapterDefault.schemaIdSupplier)
  private val searchArtifact = DefaultSearchApicurioArtifact(registryClient, AvroAdapterDefault.schemaRevisionResolver)
  private val client = DefaultApicurioRegistryClient(createOrUpdateArtifact, searchArtifact)

  @BeforeEach
  fun setUp() {
    searchArtifact.findDistinctGroupIds().forEach {
      registryClient.deleteArtifactsInGroup(it)
    }

    assertThat(client.findAll()).isEmpty()
  }

  @Test
  fun `newly registered schema can be found`() {
    val created: ApicurioArtifactWithMetaData = client.upload(SampleEventV4711.schema)

    assertThat(searchArtifact.findAllByArtifactFqn(sampleEventFqn)).hasSize(1)
    assertThat(created.version).isEqualTo("1")
    assertThat(created.schemaId).isEqualTo(SampleEventV4711.schemaData.schemaId)
  }

  @Test
  fun `update existing fqn`() {
    val created: ApicurioArtifactWithMetaData = client.upload(SampleEventV4711.schema)
    assertThat(created.version).isEqualTo("1")

    val updated: ApicurioArtifactWithMetaData = createOrUpdateArtifact
      .upload(SampleEventV4712.schema)

    assertThat(searchArtifact.findAllByArtifactFqn(fqn = sampleEventFqn)).hasSize(2)
    assertThat(updated.version).isEqualTo("2")
    assertThat(updated.schemaId).isEqualTo(SampleEventV4712.schemaData.schemaId)
  }
}
