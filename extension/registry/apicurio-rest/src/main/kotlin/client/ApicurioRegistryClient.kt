package io.holixon.avro.adapter.registry.apicurio.client

import io.apicurio.registry.rest.client.RegistryClient
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.SchemaRevisionResolver
import io.holixon.avro.adapter.common.AvroAdapterDefault

/**
 * Client abstraction for [RegistryClient] that combines the features of [CreateOrUpdateArtifact] and [SearchApicurioArtifact].
 */
interface ApicurioRegistryClient : CreateOrUpdateArtifact, SearchApicurioArtifact

/**
 * Default implementation of [ApicurioRegistryClient].
 */
open class DefaultApicurioRegistryClient(
  private val createOrUpdate: CreateOrUpdateArtifact,
  private val searchApicurioArtifact: SearchApicurioArtifact
) : ApicurioRegistryClient, SearchApicurioArtifact by searchApicurioArtifact, CreateOrUpdateArtifact by createOrUpdate {

  constructor(
    registryClient: RegistryClient,
    schemaIdSupplier: SchemaIdSupplier,
    schemaRevisionResolver: SchemaRevisionResolver = AvroAdapterDefault.schemaRevisionResolver
  ) : this(
    createOrUpdate = DefaultCreateOrUpdateArtifact(registryClient, schemaIdSupplier),
    searchApicurioArtifact = DefaultSearchApicurioArtifact(registryClient, schemaRevisionResolver)
  )
}
