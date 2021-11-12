package io.holixon.avro.adapter.registry.axon

import io.holixon.avro.adapter.api.*
import io.holixon.avro.adapter.registry.axon.api.*
import mu.KLogging
import org.apache.avro.Schema
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes.multipleInstancesOf
import org.axonframework.messaging.responsetypes.ResponseTypes.optionalInstanceOf
import org.axonframework.queryhandling.QueryGateway
import java.time.Duration
import java.util.*

/**
 * Registry storing schemas in event store.
 */
open class AxonAvroRegistry(
  private val commandGateway: CommandGateway,
  private val schemaIdSupplier: SchemaIdSupplier,
  private val schemaRevisionResolver: SchemaRevisionResolver,
  private val queryGateway: QueryGateway,
  private val schemaRegistrationTimeout: Long
) : AvroSchemaRegistry {

  companion object : KLogging() {
    val returnClazz = AvroSchemaWithId::class.java
  }

  override fun register(schema: Schema): AvroSchemaWithId {
    val schemaId = schemaIdSupplier.apply(schema)
    // sync send
    commandGateway.sendAndWait<Void>(
      RegisterAvroSchemaCommand(
        namespace = schema.namespace,
        name = schema.name,
        revision = schemaRevisionResolver.apply(schema).orElse(null),
        schema = schema.toString(),
        schemaId = schemaId
      )
    )

    val query = queryGateway.subscriptionQuery(
      FindSchemaById(schemaId),
      optionalInstanceOf(returnClazz),
      optionalInstanceOf(returnClazz)
    )

    return query
      .initialResult()
      .concatWith { query.updates() }
      .filter { option -> option.isPresent }
      .map { option -> option.get() }
      .doOnNext { element -> logger.trace { "Registered schema $element" } }
      .timeout(Duration.ofSeconds(schemaRegistrationTimeout))
      .doOnError { e -> logger.error(e) { "Error registering schema request for id $schemaId" } }
      .doFinally { query.cancel() }
      .blockFirst()!!
  }

  override fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> {
    return queryGateway.query(FindSchemaById(schemaId), optionalInstanceOf(returnClazz)).join()
  }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> {
    return queryGateway.query(FindSchemaByInfo(info), optionalInstanceOf(returnClazz)).join()
  }

  override fun findAllByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId> {
    return queryGateway.query(FindAllSchemaByCanonicalName(namespace, name), multipleInstancesOf(AvroSchemaWithId::class.java)).join()
  }

  override fun findAll(): List<AvroSchemaWithId> {
    return queryGateway.query(FindAllSchema(), multipleInstancesOf(returnClazz)).join()
  }
}
