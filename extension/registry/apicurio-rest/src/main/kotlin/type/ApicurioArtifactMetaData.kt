package io.holixon.avro.adapter.registry.apicurio.type

import io.apicurio.registry.rest.v2.beans.ArtifactMetaData
import io.apicurio.registry.types.ArtifactState
import io.apicurio.registry.types.ArtifactType
import io.holixon.avro.adapter.api.AvroSchemaRevision
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.registry.apicurio.ApicurioAvroSchemaRegistry
import java.util.*

/**
 * Typesafe wrapper for [ArtifactMetaData].
 */
data class ApicurioArtifactMetaData(
  val globalId: Long? = null,
  val id: String,
  val version: String? = null,
  val name: String? = null,
  val description: String? = null,
  val type: ArtifactType? = null,
  /**
   * Describes the state of an artifact or artifact version.
   * The following states are possible:
   *
   * * ENABLED
   * * DISABLED
   * * DEPRECATED
   */
  val state: ArtifactState? = null,
  val createdBy: String? = null,
  val createdOn: Date? = null,
  val modifiedBy: String? = null,
  val modifiedOn: Date? = null,
  val labels: List<String?> = emptyList(),
  /**
   * User-defined name-value pairs. Name and value must be strings.
   */
  val properties: Map<String, String> = emptyMap(),
  /**
   * An ID of a single artifact group.
   */
  val groupId: String? = null,
  val contentId: Long? = null
) {
  companion object {
    //fun factory(schemaIdSupplier: SchemaIdSupplier, )

    /**
     * Create data class from [ArtifactMetaData].
     */
    operator fun invoke(metaData: ArtifactMetaData): ApicurioArtifactMetaData = with(metaData) {
      ApicurioArtifactMetaData(
        id = this@with.id,
        globalId = this@with.globalId,
        version = this@with.version,
        name = this@with.name,
        description = this@with.description,
        type = this@with.type,
        state = this@with.state,
        createdBy = this@with.createdBy,
        createdOn = this@with.createdOn,
        modifiedBy = this@with.modifiedBy,
        modifiedOn = this@with.modifiedOn,
        labels = this@with.labels ?: emptyList(),
        properties = this@with.properties ?: emptyMap(),
        groupId = this@with.groupId,
        contentId = this@with.contentId
      )
    }
  }

  val revision: AvroSchemaRevision? by lazy {
    properties[ApicurioAvroSchemaRegistry.KEY_REVISION]
  }

  val namespace: String? by lazy { properties[ApicurioAvroSchemaRegistry.KEY_NAMESPACE] }

  fun toMetaData() = ArtifactMetaData().apply {
    id = this@ApicurioArtifactMetaData.id
    globalId = this@ApicurioArtifactMetaData.globalId
    version = this@ApicurioArtifactMetaData.version
    name = this@ApicurioArtifactMetaData.name
    description = this@ApicurioArtifactMetaData.description
    type = this@ApicurioArtifactMetaData.type
    state = this@ApicurioArtifactMetaData.state
    createdBy = this@ApicurioArtifactMetaData.createdBy
    createdOn = this@ApicurioArtifactMetaData.createdOn

    modifiedBy = this@ApicurioArtifactMetaData.modifiedBy
    modifiedOn = this@ApicurioArtifactMetaData.modifiedOn

    labels = this@ApicurioArtifactMetaData.labels ?: emptyList()
    properties = this@ApicurioArtifactMetaData.properties ?: emptyMap()
    groupId = this@ApicurioArtifactMetaData.groupId
    contentId = this@ApicurioArtifactMetaData.contentId
  }
}
