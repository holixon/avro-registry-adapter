package io.holixon.avro.adapter.common.converter

import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.adapter.api.AvroAdapterApi.schemaResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.common.AvroAdapterDefault.toByteArray
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class SingleObjectToJsonTest {

  private val sample = AvroAdapterTestLib.sampleFoo

  private val repository = AvroAdapterDefault.inMemorySchemaRepository().apply {
    register(sample.schema)
  }

  private val bytes = sample.toByteArray()
  private val expectedJson = sample.toString().replace("\\s".toRegex(), "")

  private val fn = SingleObjectToJson(repository.schemaResolver())

  @Test
  internal fun `convert bytes to json`() {
    assertThat(fn.apply(bytes)).isEqualTo(expectedJson)
  }
}
