package io.holixon.avro.adapter.registry.apicurio.type

import io.apicurio.registry.rest.v2.beans.ArtifactMetaData
import io.apicurio.registry.types.ArtifactState
import io.apicurio.registry.types.ArtifactType
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaWithId
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest.properties
import io.holixon.avro.lib.test.schema.SampleEventV4712
import java.util.*

object ApicurioTypeFixtures {
  val now = Date()

  val schema4712 = SampleEventV4712.schema
  val schemaInfo4712 = schema4712.avroSchemaWithId

  fun artifactMetaData(avroSchemaWithId: AvroSchemaWithId, artifactId: String = UUID.randomUUID().toString()) = ArtifactMetaData().apply {
    id = artifactId
    name = avroSchemaWithId.name
    createdOn = now
    modifiedOn = now
    version = "1"
    type = ArtifactType.AVRO
    globalId = 1
    state = ArtifactState.ENABLED
    labels = emptyList()
    groupId = null
    contentId = 1
    properties = avroSchemaWithId.properties()
  }

}
