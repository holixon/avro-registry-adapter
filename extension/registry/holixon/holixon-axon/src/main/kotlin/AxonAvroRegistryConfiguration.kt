package io.holixon.avro.adapter.registry.axon

import io.holixon.avro.adapter.api.AvroSchemaRegistry
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.SchemaRevisionResolver
import io.holixon.avro.adapter.common.DefaultSchemaIdSupplier
import io.holixon.avro.adapter.common.DefaultSchemaRevisionResolver
import io.holixon.avro.adapter.registry.axon.command.AvroSchemaAggregate
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.modelling.command.Aggregate
import org.axonframework.modelling.command.AggregateNotFoundException
import org.axonframework.queryhandling.QueryGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.util.*

/**
 * Configuration of Axon (Server) based registry.
 */
@Configuration
@ComponentScan
class AxonAvroRegistryConfiguration {

  /**
   * Registry.
   */
  @Bean
  fun axonRegistry(
    commandGateway: CommandGateway,
    queryGateway: QueryGateway,
    schemaIdSupplier: SchemaIdSupplier,
    schemaRevisionResolver: SchemaRevisionResolver
  ): AvroSchemaRegistry = AxonAvroRegistry(
    commandGateway = commandGateway,
    queryGateway = queryGateway,
    schemaIdSupplier = schemaIdSupplier,
    schemaRevisionResolver = schemaRevisionResolver
  )

  /**
   * Schema id supplier.
   */
  @Bean
  fun schemaIdSupplier() = DefaultSchemaIdSupplier()

  /**
   * Schema revision resolver.
   */
  @Bean
  fun schemaRevisionResolver() = DefaultSchemaRevisionResolver()

  /**
   * Aggregate ES repository.
   */
  @Bean
  fun avroSchemaAggregateRepo(eventStore: EventStore): EventSourcingRepository<AvroSchemaAggregate> {
    return EventSourcingRepository
      .builder(AvroSchemaAggregate::class.java)
      .eventStore(eventStore)
      .build()
  }
}

/**
 * Tries to load an aggregate for given identifier.
 * @param id aggregate identifier.
 * @return Optional result, if found.
 */
fun <T : Any> EventSourcingRepository<T>.loadOptional(id: String): Optional<Aggregate<T>> =
  try {
    Optional.of(this.load(id))
  } catch (e: AggregateNotFoundException) {
    Optional.empty()
  }
