package io.holixon.avro.adapter.registry.axon.api

import org.axonframework.modelling.command.TargetAggregateIdentifier

/**
 * Registers the schema.
 */
data class RegisterAvroSchemaCommand(
  @TargetAggregateIdentifier
  val schemaId: String,
  val namespace: String,
  val name: String,
  val revision: String?,
  val schema: String,
)
