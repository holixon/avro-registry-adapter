package io.holixon.avro.adapter.registry.axon.core

import io.holixon.avro.adapter.registry.axon.api.RegisterAvroSchemaCommand
import io.holixon.avro.adapter.registry.axon.command.AvroSchemaAggregate
import io.holixon.avro.adapter.registry.axon.loadOptional
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingRepository
import org.springframework.stereotype.Component

/**
 * Command handler for schema registrations.
 */
@Component
class RegisterAvroSchemaCommandHandler(
  val repository: EventSourcingRepository<AvroSchemaAggregate>
) {

  /**
   * Handler to allow for duplicate registrations.
   */
  @CommandHandler
  fun createOrUpdate(command: RegisterAvroSchemaCommand) {
    repository.loadOptional(command.schemaId).ifPresentOrElse(
      { aggregate ->
        // re-apply creation.
        aggregate.invoke {
          it.create(command)
        }
      },
      {
        repository.newInstance {
          AvroSchemaAggregate()
            .apply {
              create(command)
            }
        }
      }
    )
  }
}
