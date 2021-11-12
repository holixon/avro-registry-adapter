package io.holixon.avro.adapter.registry.axon

import io.holixon.avro.adapter.api.*
import io.holixon.avro.adapter.registry.axon.api.*
import org.apache.avro.Schema
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.messaging.responsetypes.ResponseTypes.instanceOf
import org.axonframework.messaging.responsetypes.ResponseTypes.optionalInstanceOf
import org.axonframework.queryhandling.QueryGateway
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

/**
 * Registry storing schemas in event store.
 */
open class AxonAvroRegistry(
  private val commandGateway: CommandGateway,
  private val schemaIdSupplier: SchemaIdSupplier,
  private val schemaRevisionResolver: SchemaRevisionResolver,
  private val queryGateway: QueryGateway
) : AvroSchemaRegistry {

  companion object {
    val returnClazz = AvroSchemaWithId::class.java
  }

  override fun register(schema: Schema): AvroSchemaWithId {
    val schemaId = schemaIdSupplier.apply(schema)

    val query = queryGateway.subscriptionQuery(
      FindSchemaById(schemaId),
      optionalInstanceOf(returnClazz),
      optionalInstanceOf(returnClazz)
    )

    commandGateway.send<Void>(
      RegisterAvroSchemaCommand(
        namespace = schema.namespace,
        name = schema.name,
        revision = schemaRevisionResolver.apply(schema).orElse(null),
        schema = schema.toString(),
        schemaId = schemaId
      )
    )

    return query.initialResult().concatWith { query.updates() }

      .filter { it.isPresent }

//      .timeout(Duration.ofSeconds(10L))
      .map { it.get() }
      .shareNext()
      .block()!!
  }

  override fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> {
    return queryGateway.query(FindSchemaById(schemaId), ResponseTypes.optionalInstanceOf(returnClazz)).join()
  }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> {
    return queryGateway.query(FindSchemaByInfo(info), ResponseTypes.optionalInstanceOf(returnClazz)).join()
  }

  override fun findAllByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId> {
    return queryGateway.query(
      FindAllSchemaByCanonicalName(namespace, name),
      ResponseTypes.multipleInstancesOf(AvroSchemaWithId::class.java)
    ).join()
  }

  override fun findAll(): List<AvroSchemaWithId> {
    return queryGateway.query(FindAllSchema(), ResponseTypes.multipleInstancesOf(returnClazz)).join()
  }
}
