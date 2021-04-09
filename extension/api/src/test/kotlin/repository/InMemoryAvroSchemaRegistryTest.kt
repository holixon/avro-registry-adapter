package io.holixon.avro.adapter.api.repository

import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper.schemaIdSupplier
import io.holixon.avro.adapter.api.AvroAdapterApiTestHelper.schemaRevisionResolver
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class InMemoryAvroSchemaRegistryTest {

  private val registry = InMemoryAvroSchemaRegistry(
    schemaIdSupplier = schemaIdSupplier,
    schemaRevisionResolver = schemaRevisionResolver
  )

  private val schema = Schema.Parser().parse(AvroAdapterTestLib.loadArvoResource("test.fixture.SampleEvent-v4711"))

  private val schemaId = schemaIdSupplier.apply(schema)

  @BeforeEach
  internal fun setUp() {
    assertThat(registry.findAll()).isEmpty()
  }

  @Test
  internal fun `register returns schemaWithId`() {
    val registered = registry.register(schema)

    assertThat(registry.findAll()).hasSize(1)
    assertThat(registered.schemaId).isEqualTo(schemaId)
    assertThat(registered.schema).isEqualTo(schema)
    assertThat(registered.canonicalName).isEqualTo("test.fixture.SampleEvent")
    assertThat(registered.revision).isEqualTo("4711")
  }

  @Test
  internal fun `find by context and name`() {
    assertThat(registry.findByContextAndName("test.fixture", "SampleEvent")).isEmpty()
    registry.register(schema)

    assertThat(registry.findByContextAndName("test.fixture", "SampleEvent")).hasSize(1)
  }

  @Test
  internal fun `can register schema and find by id`() {
    registry.register(schema)
    assertThat(registry.findAll()).isNotEmpty

    val found = registry.findById(schemaId).orElseThrow()

    assertThat(found.schemaId).isEqualTo(schemaId)
    assertThat(found.schema).isEqualTo(schema)
    assertThat(found.revision).isEqualTo("4711")
  }

}
