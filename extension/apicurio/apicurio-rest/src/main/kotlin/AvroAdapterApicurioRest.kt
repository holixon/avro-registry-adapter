package io.holixon.avro.adapter.apicurio

import org.apache.avro.Schema

/**
 *
 * REST endpoint (version 1.3.x): `http://$host:port/api`
 *
 */
object AvroAdapterApicurioRest {

  fun Schema.description() : String? = this.doc

}
