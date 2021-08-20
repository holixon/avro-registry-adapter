package io.holixon.avro.adapter.registry.reflection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class ReflectionReadOnlySchemaRegistryTest {

  @Test
  internal fun `find none`() {
    val registry = ReflectionReadOnlySchemaRegistry("xxx")

    assertThat(registry.findAll()).isEmpty()
  }

  @Test
  internal fun `find all`() {
    val registry = ReflectionReadOnlySchemaRegistry()
    assertThat(registry.findAll()).hasSize(6)
  }
}
