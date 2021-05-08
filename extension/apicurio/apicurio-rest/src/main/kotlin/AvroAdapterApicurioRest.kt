package io.holixon.avro.adapter.apicurio

import io.apicurio.registry.rest.client.RegistryClientFactory
import org.apache.avro.Schema

/**
 *
 * REST endpoint (version 1.3.x): `http://$host:port/api`
 * REST endpoint (version 2.0.x): `http://$host:port/api/registry/v2`
 *
 */
object AvroAdapterApicurioRest {

  @JvmOverloads
  fun registryApiUrl(host: String, port: Int, https: Boolean = false) = "http${if (https) "s" else ""}://$host:$port/apis/registry/v2"

  @JvmOverloads
  fun registryRestClient(host: String, port: Int, https: Boolean = false) = registryRestClient(registryApiUrl(host, port, https))

  fun registryRestClient(apiUrl : String) = RegistryClientFactory.create(apiUrl)

  fun Schema.description(): String? = this.doc

}
