package io.holixon.avro.adapter.registry.apicurio.type

import io.apicurio.registry.rest.v2.beans.SearchedVersion
import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaName
import io.holixon.avro.adapter.api.AvroSchemaRevision
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaId
import io.holixon.avro.adapter.registry.apicurio.ArtifactId
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest
import io.holixon.avro.adapter.registry.apicurio.GroupId
import io.holixon.avro.adapter.registry.apicurio.Version
import org.apache.avro.Schema

interface ApicurioSearchedArtifact {

  val globalId: Long
  val contentId: Long
  val groupId: GroupId
  val artifactId: ArtifactId
  val name: AvroSchemaName
  val version: Version
  val description: String?
  val properties: Map<String, String>
  val labels: List<String>
  val avroSchemaId: AvroSchemaId?
}

interface ApicurioSearchedSchema : ApicurioSearchedArtifact {
  override val avroSchemaId: AvroSchemaId
  val revision: AvroSchemaRevision?
  val schema: Schema
}

data class ApicurioSearchedArtifactData(
  override val globalId: Long,
  override val contentId: Long,
  override val groupId: GroupId,
  override val artifactId: ArtifactId,
  override val name: AvroSchemaName,
  override val version: Version,
  override val description: String?,
  override val properties: Map<String, String>,
  override val labels: List<String>
) : ApicurioSearchedArtifact {
  override val avroSchemaId: AvroSchemaId? by lazy {
    properties[AvroAdapterApicurioRest.PropertyKey.SCHEMA_ID]
  }
}

data class ApicurioSearchedSchemaData(
  override val globalId: Long,
  override val contentId: Long,
  override val groupId: GroupId,
  override val artifactId: ArtifactId,
  override val name: AvroSchemaName,
  override val version: Version,
  override val description: String?,
  override val properties: Map<String, String>,
  override val labels: List<String>,
  override val schema: Schema,
  override val avroSchemaId: AvroSchemaId,
  override val revision: AvroSchemaRevision? = null
) : ApicurioSearchedSchema

fun ApicurioSearchedArtifact.addSchema(
  schema: Schema,
  schemaId: AvroSchemaId = schema.avroSchemaId,
  revision: AvroSchemaRevision? = null/* = kotlin.String? */
) = ApicurioSearchedSchemaData(
  globalId = globalId,
  contentId = contentId,
  groupId = groupId,
  artifactId = artifactId,
  name = name,
  version = version,
  description = description,
  properties = properties,
  labels = labels,
  schema = schema,
  avroSchemaId = schemaId,
  revision = revision
)

fun ApicurioSearchedSchema.avroSchemaWithId() = AvroSchemaWithIdData(
  schema = schema,
  schemaId = avroSchemaId,
  name = name,
  namespace = groupId,
  revision = revision
)

fun apicurioSearchedArtifact(
  groupId: GroupId /* = kotlin.String */,
  artifactId: ArtifactId /* = kotlin.String */,
  version: SearchedVersion
) = ApicurioSearchedArtifactData(
  globalId = version.globalId,
  contentId = version.contentId,
  groupId = groupId,
  artifactId = artifactId,
  name = version.name,
  version = version.version,
  description = version.description,
  properties = version.properties ?: emptyMap(),
  labels = version.labels ?: emptyList()
)

