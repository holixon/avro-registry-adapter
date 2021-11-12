package io.holixon.avro.adapter.registry.axon.core

import holixon.registry.event.AvroSchemaRegisteredEvent
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.SchemaIdSupplier
import io.holixon.avro.adapter.api.SchemaRevisionResolver
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaRegistry
import io.holixon.avro.adapter.registry.axon.api.FindAllSchema
import io.holixon.avro.adapter.registry.axon.api.FindAllSchemaByCanonicalName
import io.holixon.avro.adapter.registry.axon.api.FindSchemaById
import io.holixon.avro.adapter.registry.axon.api.FindSchemaByInfo
import io.holixon.avro.adapter.registry.axon.core.AvroRegistryProjection.Companion.GROUP_NAME
import org.apache.avro.Schema
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.GenericSubscriptionQueryUpdateMessage
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Predicate

/**
 * Projection backed by an in-memory registry.
 */
@Component
@ProcessingGroup(GROUP_NAME)
class AvroRegistryProjection(
  private val schemaIdSupplier: SchemaIdSupplier,
  private val schemaRevisionResolver: SchemaRevisionResolver,
  private val queryUpdateEmitter: QueryUpdateEmitter
) {
  companion object {
    const val GROUP_NAME = "io.holixon.avro.adapter.registry.axon"
  }

  /**
   * Registry for backing.
   */
  private val registry = InMemoryAvroSchemaRegistry(
    schemaIdSupplier = schemaIdSupplier,
    schemaRevisionResolver = schemaRevisionResolver
  )

  /**
   * Registration of the schemas.
   */
  @EventHandler
  fun on(event: AvroSchemaRegisteredEvent) {
    val registered = registry.register(Schema.Parser().parse(event.content))

    val check: Predicate<FindSchemaById> = Predicate {
        query -> query.schemaId == registered.schemaId
    }
    queryUpdateEmitter.emit(
      FindSchemaById::class.java, check, registered
    )
  }

  /**
   * Finds a schema by id.
   */
  @QueryHandler
  fun query(query: FindSchemaById): Optional<AvroSchemaWithId> {
    return registry.findById(schemaId = query.schemaId)
  }

  /**
   * Finds a schema by info.
   */
  @QueryHandler
  fun query(query: FindSchemaByInfo): Optional<AvroSchemaWithId> {
    return registry.findByInfo(info = query.info)
  }

  /**
   * Finds all schemas by namespace, name and optional revision.
   */
  @QueryHandler
  fun query(query: FindAllSchemaByCanonicalName): List<AvroSchemaWithId> {
    return registry.findAllByCanonicalName(namespace = query.namespace, name = query.name)
  }

  /**
   * Finds all schemas.
   */
  @QueryHandler
  fun query(query: FindAllSchema): List<AvroSchemaWithId> {
    return registry.findAll()
  }
}
