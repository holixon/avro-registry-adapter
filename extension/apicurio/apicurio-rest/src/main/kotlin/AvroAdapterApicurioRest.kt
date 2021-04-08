package io.toolisticon.avro.adapter.apicurio

import org.apache.avro.Schema


object AvroAdapterApicurioRest {

  fun Schema.description() = this.getObjectProp("__comment") as String?

}
