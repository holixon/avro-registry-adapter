package io.holixon.avro.adapter.registry.axon.command

import holixon.registry.event.AvroSchemaRegisteredEvent
import io.holixon.avro.adapter.registry.axon.api.RegisterAvroSchemaCommand
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

/**
 * Represents a schema.
 */
@Aggregate
class AvroSchemaAggregate() {

  @AggregateIdentifier
  private lateinit var schemaId: String

  /**
   * Creates a new schema.
   */
  fun create(command: RegisterAvroSchemaCommand) = AvroSchemaAggregate().apply {
    AggregateLifecycle.apply(
      AvroSchemaRegisteredEvent(
        command.schemaId,
        command.namespace,
        command.name,
        command.revision,
        command.schema
      )
    )
  }

  /**
   * ES Handler to setup aggregate identifier.
   */
  @EventSourcingHandler
  fun on(event: AvroSchemaRegisteredEvent) {
    if (!this::schemaId.isInitialized) {
      this.schemaId = event.schemaId
    }
  }
}
