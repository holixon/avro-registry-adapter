package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.AvroAdapterApi
import io.holixon.avro.adapter.api.SchemaRevision
import io.holixon.avro.adapter.api.SchemaRevisionResolver
import org.apache.avro.Schema
import java.util.*

class DefaultSchemaRevisionResolver : SchemaRevisionResolver {
  private val propertyBasedResolver = AvroAdapterApi.propertyBasedSchemaRevisionResolver(AvroAdapterDefault.PROPERTY_REVISION)

  override fun apply(schema: Schema): Optional<SchemaRevision> = propertyBasedResolver.apply(schema)

}
