package io.holixon.avro.adapter.registry.apicurio.type

import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaWithId
import io.holixon.avro.adapter.registry.apicurio.type.ApicurioTypeFixtures.toDataClass
import io.holixon.avro.lib.test.schema.SampleEventV4712
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.util.*

internal class ApicurioSchemaDataTest {

  private val now = Date()

  @Test
  fun `create from metadata`() {
    val schema = SampleEventV4712.schema
    val schemaInfo = schema.avroSchemaWithId
    val metaData = ApicurioTypeFixtures.artifactMetaData(schemaInfo)

    val schemaData = ApicurioSchemaData(metaData = ApicurioArtifactMetaData(metaData), schema = schema)

    assertThat(schemaData.schema).isEqualTo(schema)
  }

  @Test
  fun `fails when metaData is not initialized`() {
    val schema = SampleEventV4712.schema
    val schemaInfo = schema.avroSchemaWithId
    val metaData = ApicurioTypeFixtures.artifactMetaData(schemaInfo).toDataClass().copy(properties = emptyMap())

    assertThatThrownBy { ApicurioSchemaData(metaData = metaData, schema = schema) }
      .isInstanceOf(IllegalArgumentException::class.java)
  }
}
