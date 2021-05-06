package io.holixon.avro.lib.test.schema

import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.lib.test.AvroAdapterTestLib.SCHEMA_CONTEXT
import io.holixon.avro.lib.test.AvroAdapterTestLib.SCHEMA_NAME
import io.holixon.avro.lib.test.AvroAdapterTestLib.loadArvoResource
import java.lang.IllegalStateException

object SampleEventV4711 : TestSchemaDataProvider {
  val schemaJson = loadArvoResource("$SCHEMA_CONTEXT.$SCHEMA_NAME-v4711")

  override fun schemaData(): TestSchemaData = parseData(schemaJson)

}
