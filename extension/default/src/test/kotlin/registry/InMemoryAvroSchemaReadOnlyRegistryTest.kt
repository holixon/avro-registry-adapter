package io.holixon.avro.adapter.common.registry

import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaInfo
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaWithId
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaReadOnlyRegistry.Companion.createWithSchemas
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4712
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class InMemoryAvroSchemaReadOnlyRegistryTest {

  private val registry = createWithSchemas(
    SampleEventV4711.schema,
    SampleEventV4712.schema
  )

  @Test
  internal fun `empty registry`() {
    assertThat(createWithSchemas().findAll()).isEmpty()
  }

  @Test
  internal fun `has been registered correctly`() {
    println(registry.findAll())
  }

  @Test
  internal fun `findById sample4711`() {
    val schema = registry.findById(SampleEventV4711.schemaData.schemaId).orElseThrow()
    assertThat(schema.schema).isEqualTo(SampleEventV4711.schema)
  }

  @Test
  internal fun `findByInfo sample4712 by schema with id`() {
    val info: AvroSchemaWithId = SampleEventV4712.schema.avroSchemaWithId
    val schema: AvroSchemaWithId = registry.findByInfo(info).orElseThrow()

    assertThat(schema).isEqualTo(info)
  }

  @Test
  internal fun `findByInfo sample4712 by schema info`() {
    val info = SampleEventV4712.schema.avroSchemaInfo
    assertThat(info.canonicalName).isEqualTo("test.fixture.SampleEvent")
    assertThat(info.revision).isEqualTo("4712")

    val found: AvroSchemaWithId = registry.findByInfo(info).orElseThrow()

    assertThat(found.canonicalName).isEqualTo(info.canonicalName)
    assertThat(found.revision).isEqualTo(info.revision)
  }

  @Test
  internal fun `findAll contains 4711 and 4712`() {
    val all = registry.findAll()

    assertThat(all).hasSize(2)
    assertThat(all.map { it.schemaId }).containsExactlyInAnyOrder(
      SampleEventV4711.schemaData.schemaId,
      SampleEventV4712.schemaData.schemaId
    )
  }

  @Test
  internal fun `findAllByCanonicalName  contains 4711 and 4712`() {
    val byNames = registry.findAllByCanonicalName("test.fixture", "SampleEvent")

    assertThat(byNames).hasSize(2)
    assertThat(byNames.map { it.revision }).containsExactlyInAnyOrder("4711", "4712")
  }
}
