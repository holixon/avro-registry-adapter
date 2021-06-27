package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.AvroAdapterDefault.toByteArray
import io.holixon.avro.lib.test.AvroAdapterTestLib
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class DefaultSingleObjectToJsonConverterTest {

  private val sample = AvroAdapterTestLib.sampleFoo

  private val registry = AvroAdapterDefault.inMemorySchemaRegistry().apply {
    register(sample.schema)
  }

  private val bytes = sample.toByteArray()
  private val expectedJson = sample.toString().replace("\\s".toRegex(), "")

  private val fn = DefaultSingleObjectToJsonConverter(registry.schemaResolver())

  @Test
  internal fun `convert bytes to json`() {
    assertThat(fn.convert(bytes)).isEqualTo(expectedJson)
  }
}
