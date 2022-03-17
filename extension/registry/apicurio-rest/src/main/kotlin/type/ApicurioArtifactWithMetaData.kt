package io.holixon.avro.adapter.registry.apicurio.type

import io.holixon.avro.adapter.registry.apicurio.ArtifactName
import io.holixon.avro.adapter.registry.apicurio.GroupId
import io.holixon.avro.adapter.registry.apicurio.Version
import org.apache.avro.Schema

data class ApicurioArtifactWithMetaData(
  val globalId: Long,
  val contentId: Long,
  val groupId: GroupId,
  val name : ArtifactName,
  val version: Version,
  val schemaId: String? = null,
  val schema: Schema? = null
)
