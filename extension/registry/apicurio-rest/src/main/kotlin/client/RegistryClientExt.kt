package io.holixon.avro.adapter.registry.apicurio.client

import io.apicurio.registry.rest.client.RegistryClient
import io.apicurio.registry.rest.v2.beans.SearchedArtifact
import io.apicurio.registry.rest.v2.beans.SortBy
import io.apicurio.registry.rest.v2.beans.SortOrder
import io.apicurio.registry.types.ArtifactType
import io.holixon.avro.adapter.api.AvroSchemaFqn
import io.holixon.avro.adapter.api.type.AvroSchemaFqnData
import io.holixon.avro.adapter.registry.apicurio.GroupId

/**
 * Extension functions for the [RegistryClient].
 */
object RegistryClientExt {

  fun RegistryClient.exists(fqn: AvroSchemaFqn): Result<Boolean> = this.runCatching {
    listArtifactsInGroup(fqn.namespace).artifacts
      .map(SearchedArtifact::getName)
      .map { AvroSchemaFqnData(namespace = fqn.namespace, name = it) }
      .toSet()
  }.map { res -> res.any { fqn.name == it.name } }


  fun SearchedArtifact.isAvro() = ArtifactType.AVRO == this.type

  /**
   * Redirecting to [RegistryClient.searchArtifacts] using named parameters and default values (`null`).
   */
  fun RegistryClient.queryArtifacts(
    group: GroupId? = null,
    name: String? = null,
    description: String? = null,
    labels: List<String>? = null,
    properties: List<String>? = null,
    globalId: Long? = null,
    contentId: Long? = null,
    orderBy: SortBy? = null,
    order: SortOrder? = null,
    offset: Int? = null,
    limit: Int? = null
  ): Result<List<SearchedArtifact>> = this.runCatching {
    searchArtifacts(
      group, name, description,
      labels, properties,
      globalId, contentId,
      orderBy, order,
      offset, limit
    ).artifacts.filter { it.isAvro() }
  }
}
