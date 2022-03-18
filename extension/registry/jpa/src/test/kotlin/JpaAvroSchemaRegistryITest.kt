package io.holixon.avro.adapter.registry.jpa

import io.holixon.avro.lib.test.schema.SampleEventV4711
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [JpaAvroSchemaRegistryConfiguration::class])
internal class JpaAvroSchemaRegistryITest {

  @Autowired
  private lateinit var registry: JpaAvroSchemaRegistry

  @Test
  fun `register schema and find`() {
    assertThat(registry.findAll()).isEmpty()

    val registered = registry.register(SampleEventV4711.schema)

    assertThat(registry.findById(SampleEventV4711.schemaData.schemaId)).isNotEmpty
    val list = registry.findAllByCanonicalName(SampleEventV4711.schema.namespace, SampleEventV4711.schema.name)
    assertThat(list).hasSize(1)
    assertThat(list[0].schemaId).isEqualTo(registered.schemaId)
  }
}
