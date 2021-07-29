package io.holixon.avro.adapter.registry.apicurio.type

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaRevision
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRest
import org.apache.avro.Schema

data class ApicurioSchemaData(
  val metaData: ApicurioArtifactMetaData,
  val apicurioArtifactId: String = metaData.id,
  override val schemaId: AvroSchemaId = requireNotNull(metaData.properties[AvroAdapterApicurioRest.PropertyKey.SCHEMA_ID]),
  override val namespace: String = requireNotNull(metaData.properties[AvroAdapterApicurioRest.PropertyKey.NAMESPACE]),
  override val name: String = requireNotNull(metaData.properties[AvroAdapterApicurioRest.PropertyKey.NAME]),
  override val revision: AvroSchemaRevision? = metaData.properties[AvroAdapterApicurioRest.PropertyKey.NAME],
  override val schema: Schema,
) : AvroSchemaWithId

