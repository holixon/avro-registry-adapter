package io.holixon.avro.adapter.apicurio

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

  fun Schema.description(): String? = this.doc

}
