package io.holixon.avro.adapter.registry.axon

import io.holixon.avro.adapter.api.*
import io.holixon.avro.adapter.registry.axon.api.*
import org.apache.avro.Schema
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.messaging.responsetypes.ResponseTypes.instanceOf
import org.axonframework.queryhandling.QueryGateway
import org.reactivestreams.Publisher
import java.util.*
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
      instanceOf(returnClazz),
      instanceOf(returnClazz)
    )

    val result: Result<AvroSchemaWithId> = query.initialResult()
      .concatWith { query.updates() }
      .map { success(it) }
      .onErrorResume { e -> Publisher { failure<AvroSchemaWithId>(e) } }
      .shareNext()
      .block()!!

    return result.getOrThrow()
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
