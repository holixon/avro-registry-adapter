package io.holixon.avro.adapter.common.ext

import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization

/**
 * Extension functions to retrieve schema fingerprint.
 */
object SchemaExt {

  val Schema.fingerprint get() = SchemaNormalization.parsingFingerprint64(this)

}
