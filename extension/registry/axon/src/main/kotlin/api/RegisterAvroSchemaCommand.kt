package io.holixon.avro.adapter.registry.axon.api

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaRevision
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.common.DefaultSchemaIdSupplier
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaId
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaRevision
import org.apache.avro.Schema
import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Registers the schema.
 */
data class RegisterAvroSchemaCommand(
  @TargetAggregateIdentifier
  val schemaId: AvroSchemaId,
  val namespace: String,
  val name: String,
  val revision: AvroSchemaRevision?,
  val schema: String,
) {
  companion object {
    fun create(schema:Schema) = RegisterAvroSchemaCommand(
      schemaId = schema.avroSchemaId,
      namespace = schema.namespace,
      name = schema.name,
      revision = schema.avroSchemaRevision,
      schema = schema.toString()
    )
  }
}
