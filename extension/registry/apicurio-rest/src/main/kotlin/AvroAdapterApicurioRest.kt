package io.holixon.avro.adapter.registry.apicurio

import io.apicurio.registry.rest.client.RegistryClient
import io.apicurio.registry.rest.client.RegistryClientFactory
import io.holixon.avro.adapter.api.AvroSchemaWithId
import org.apache.avro.Schema

/**
 *
 * REST endpoint (version 1.3.x): `http://$host:port/api`
 * REST endpoint (version 2.0.x): `http://$host:port/api/registry/v2`
 *
 */
object AvroAdapterApicurioRest {
  object PropertyKey {
    const val SCHEMA_ID = "schemaId"
    const val NAME = "name"
    const val NAMESPACE = "namespace"
    const val CANONICAL_NAME = "canonicalName"
    const val REVISION = "revision"
  }

  const val DEFAULT_GROUP = "default"

  @JvmOverloads
  @JvmStatic
  fun registryApiUrl(host: String, port: Int, https: Boolean = false) = "http${if (https) "s" else ""}://$host:$port/apis/registry/v2"

  @JvmOverloads
  @JvmStatic
  fun registryRestClient(host: String, port: Int, https: Boolean = false): RegistryClient =
    registryRestClient(registryApiUrl(host, port, https))

  @JvmStatic
  fun registryRestClient(apiUrl: String): RegistryClient = RegistryClientFactory.create(apiUrl)

  fun Schema.description(): String? = this.doc

  internal fun AvroSchemaWithId.properties(): Map<String, String? /* = kotlin.String? */> = mapOf(
    PropertyKey.SCHEMA_ID to schemaId,
    PropertyKey.NAMESPACE to namespace,
    PropertyKey.NAME to name,
    PropertyKey.REVISION to revision,
    PropertyKey.CANONICAL_NAME to canonicalName
  )
}

typealias GroupId = String
typealias ArtifactId = String
typealias ArtifactName = String
typealias Version = String
