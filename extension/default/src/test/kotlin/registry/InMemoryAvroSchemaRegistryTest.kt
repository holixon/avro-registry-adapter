package io.holixon.avro.adapter.common.registry

import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaId
import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4712
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class InMemoryAvroSchemaRegistryTest {

  private val registry = AvroAdapterDefault.inMemorySchemaRegistry()

  private val schema = Schema.Parser().parse(AvroAdapterTestLib.loadArvoResource("test.fixture.SampleEvent-v4711"))

  private val schemaId = schema.avroSchemaId

  @BeforeEach
  internal fun setUp() {
    assertThat(registry.findAll()).isEmpty()
  }

  @Test
  fun `register returns schemaWithId`() {
    val r1 = registry.register(schema)
    val r2 = registry.register(SampleEventV4712.schema)

    assertThat(registry.findAll()).hasSize(2)
    assertThat(r1.schemaId).isEqualTo(schemaId)
    assertThat(r1.schema).isEqualTo(schema)
    assertThat(r1.canonicalName).isEqualTo("test.fixture.SampleEvent")
    assertThat(r1.revision).isEqualTo("4711")

    assertThat(r2.revision).isEqualTo("4712")
  }

  @Test
  fun `find by context and name`() {
    assertThat(registry.findAllByCanonicalName("test.fixture", "SampleEvent")).isEmpty()
    registry.register(schema)

    assertThat(registry.findAllByCanonicalName("test.fixture", "SampleEvent")).hasSize(1)
  }

  @Test
  fun `can register schema and find by id`() {
    registry.register(schema)
    assertThat(registry.findAll()).isNotEmpty

    val found = registry.findById(schemaId).orElseThrow()

    assertThat(found.schemaId).isEqualTo(schemaId)
    assertThat(found.schema).isEqualTo(schema)
    assertThat(found.revision).isEqualTo("4711")
  }

  @Test
  fun `can transform to read only registry`() {
    registry.register(schema)
    val readOnly = registry.toReadOnly()

    assertThat(readOnly.findById(schemaId).orElseThrow().schemaId).isEqualTo(schemaId)
  }

  @Test
  fun `register different revisions of sampleEvent and find`() {
    registry.close()
    assertThat(registry.findAll()).isEmpty()
    assertThat(registry.register(SampleEventV4711.schema).revision).isEqualTo("4711")
    assertThat(registry.register(SampleEventV4712.schema).revision).isEqualTo("4712")


    assertThat(registry.findAll()).hasSize(2)
    assertThat(registry.findAll().map { it.revision }).containsExactlyInAnyOrder("4711", "4712")
  }
}
