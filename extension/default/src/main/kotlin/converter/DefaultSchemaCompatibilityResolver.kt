package io.holixon.avro.adapter.common.converter

import io.holixon.avro.adapter.api.AvroSchemaIncompatibilityResolver
import io.holixon.avro.adapter.api.ext.FunctionalExt.invoke
import io.holixon.avro.adapter.common.AvroAdapterDefault.schemaIdSupplier
import org.apache.avro.Schema
import org.apache.avro.SchemaCompatibility
import org.apache.avro.SchemaCompatibility.SchemaCompatibilityType.*

class DefaultSchemaCompatibilityResolver @JvmOverloads constructor(
  val ignoredIncompatibilityTypes: Set<SchemaCompatibility.SchemaIncompatibilityType> = setOf()
) : AvroSchemaIncompatibilityResolver {

  override fun resolve(readerSchema: Schema, writerSchema: Schema): Schema {

    // check compatibility
    val compatibility = SchemaCompatibility.checkReaderWriterCompatibility(readerSchema, writerSchema)
    return when (compatibility.result.compatibility!!) {
      COMPATIBLE -> readerSchema
      INCOMPATIBLE -> if (filterIgnored(compatibility.result.incompatibilities.map { it.type }).isEmpty()) {
        readerSchema
      } else {
        throw IllegalArgumentException(
          "Reader schema[${readerSchema.schemaId}] is not compatible with Writer schema[${writerSchema.schemaId}]. The incompatibilities are: ${
            filterIgnored(
              compatibility.result.incompatibilities.map { it.type })
          }"
        )
      }
      RECURSION_IN_PROGRESS -> throw IllegalArgumentException(
        "Recursion in progress for compatibility check for Reader schema[${readerSchema.schemaId}] and Writer schema[${writerSchema.schemaId}]."
      )
    }
  }

  private val Schema.schemaId get() = schemaIdSupplier(this)

  fun filterIgnored(incompatibilities: List<SchemaCompatibility.SchemaIncompatibilityType>) = (incompatibilities - ignoredIncompatibilityTypes)

}

