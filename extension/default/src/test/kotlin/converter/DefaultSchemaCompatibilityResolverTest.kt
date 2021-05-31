package io.holixon.avro.adapter.common.converter

import org.apache.avro.SchemaCompatibility
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultSchemaCompatibilityResolverTest {


  @Test
  fun `check ignored incompatibilities empty`() {
    val converter = DefaultSchemaCompatibilityResolver()
    assertThat(
      converter.filterIgnored(listOf(SchemaCompatibility.SchemaIncompatibilityType.NAME_MISMATCH))
    ).isNotEmpty
  }

  @Test
  fun `check ignored incompatibilities are matched`() {
    val converter = DefaultSchemaCompatibilityResolver(setOf(SchemaCompatibility.SchemaIncompatibilityType.NAME_MISMATCH))
    assertThat(
      converter.filterIgnored(listOf(SchemaCompatibility.SchemaIncompatibilityType.NAME_MISMATCH))
    ).isEmpty()
  }


  @Test
  fun `check empty incompatibilities are ok`() {
    val converter = DefaultSchemaCompatibilityResolver(setOf(SchemaCompatibility.SchemaIncompatibilityType.NAME_MISMATCH))
    assertThat(
      converter.filterIgnored(listOf())
    ).isEmpty()
  }

}
