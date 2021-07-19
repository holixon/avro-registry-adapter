package io.holixon.avro.adapter.registry.apicurio.type

import io.apicurio.registry.rest.v2.beans.ArtifactMetaData
import io.apicurio.registry.types.ArtifactState
import io.apicurio.registry.types.ArtifactType
import io.holixon.avro.adapter.api.AvroSchemaRevision
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
    /**
     * Create data class from [ArtifactMetaData].
     */
    operator fun invoke(metaData: ArtifactMetaData): ApicurioArtifactMetaData = with(metaData) {
      ApicurioArtifactMetaData(
        globalId = this@with.globalId,
        id = this@with.id,
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

  val revision: AvroSchemaRevision? = properties[ApicurioAvroSchemaRegistry.KEY_REVISION]
  val namespace: String? = properties[ApicurioAvroSchemaRegistry.KEY_NAMESPACE]
}
