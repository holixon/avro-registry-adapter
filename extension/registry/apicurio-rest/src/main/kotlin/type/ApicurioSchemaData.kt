package io.holixon.avro.adapter.registry.apicurio.type

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaRevision
import io.holixon.avro.adapter.api.AvroSchemaWithId
import org.apache.avro.Schema

data class ApicurioSchemaData(
  val metaData: ApicurioArtifactMetaData,
  val apicurioArtifactId: String = metaData.id,
  override val schemaId: AvroSchemaId = requireNotNull(metaData.schemaIdProperty) { "schemaId property is null "},
  override val namespace: String = requireNotNull(metaData.namespaceProperty) { "namespace property is null "},
  override val name: String = requireNotNull(metaData.nameProperty){ "name property is null "},
  override val revision: AvroSchemaRevision? = metaData.revisionProperty,
  override val schema: Schema,
) : AvroSchemaWithId
