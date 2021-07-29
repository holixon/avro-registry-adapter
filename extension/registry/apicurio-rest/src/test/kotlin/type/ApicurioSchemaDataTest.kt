package io.holixon.avro.adapter.registry.apicurio.type

import io.apicurio.registry.rest.v2.beans.ArtifactMetaData
import io.apicurio.registry.types.ArtifactState
import io.apicurio.registry.types.ArtifactType
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaInfo
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaWithId
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.PropertyKey
import io.holixon.avro.lib.test.schema.SampleEventV4712
import org.assertj.core.api.Assertions

import org.junit.jupiter.api.Test
import java.util.*

internal class ApicurioSchemaDataTest {

  private val now = Date()

  @Test
  internal fun `create from metadata`() {
    val schema = SampleEventV4712.schema
    val schemaInfo = schema.avroSchemaWithId
    val metaData = ArtifactMetaData().apply {
      id = UUID.randomUUID().toString()
      name = SampleEventV4712.schemaData.name
      createdOn = now
      modifiedOn = now
      version = "1"
      type = ArtifactType.AVRO
      globalId = 1
      state = ArtifactState.ENABLED
      labels = emptyList()
      groupId = null
      contentId = 1
      properties = mapOf(
        PropertyKey.SCHEMA_ID to schemaInfo.schemaId,
        PropertyKey.NAMESPACE to schemaInfo.namespace,
        PropertyKey.NAME to schemaInfo.name,
        PropertyKey.REVISION to schemaInfo.revision,
        PropertyKey.CANONICAL_NAME to schemaInfo.canonicalName
      )
    }

    val schemaData = ApicurioSchemaData(metaData = ApicurioArtifactMetaData(metaData), schema = schema)

    Assertions.assertThat(schemaData.schema).isEqualTo(schema)
  }
}
