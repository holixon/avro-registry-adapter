package io.toolisticon.avro.adapter.common

import io.toolisticon.avro.adapter.api.AvroAdapterApi
import io.toolisticon.avro.adapter.api.SchemaRevision
import io.toolisticon.avro.adapter.api.SchemaRevisionResolver
import org.apache.avro.Schema
import java.util.*

class DefaultSchemaRevisionResolver : SchemaRevisionResolver {
  private val propertyBasedResolver = AvroAdapterApi.propertyBasedSchemaRevisionResolver(AvroAdapterDefault.PROPERTY_REVISION)

  override fun apply(schema: Schema): Optional<SchemaRevision> = propertyBasedResolver.apply(schema)
}
