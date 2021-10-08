package io.holixon.avro.adapter.registry.holixon

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaInfo
import io.holixon.avro.adapter.api.AvroSchemaRegistry
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.adapter.registry.ApiClient
import io.holixon.avro.adapter.registry.holixon.model.AvroSchemaWithIdDto
import org.apache.avro.Schema
import java.util.*

/**
 * Adapter to connect to remote Holixon Schema Registry.
 */
@Suppress("LeakingThis")
open class HolixonRemoteSchemaRegistry(
  private val basePath: String
) : AvroSchemaRegistry {
  /**
   * Feign client used for access of the remote REST registry.
   */
  private val schemaApi: SchemaApi by lazy { buildClient(basePath) }

  /**
   * Builds and configures the feign client.
   */
  open fun buildClient(path: String): SchemaApi {
    return ApiClient().apply {
      this.basePath = path
    }.buildClient(SchemaApi::class.java)
  }

  override fun register(schema: Schema): AvroSchemaWithId {
    return schemaApi.registerSchema(schema).toAvroSchemaWithId()
  }

  override fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> {
    return Optional.ofNullable(schemaApi.findById(schemaId)).map { it.toAvroSchemaWithId() }
  }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> {
    return Optional.ofNullable(schemaApi.findByInfo(info.namespace, info.name, info.revision)).map { it.toAvroSchemaWithId() }
  }

  override fun findAllByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId> {
    return schemaApi.findAllByCanonicalName(namespace, name).map { it.toAvroSchemaWithId() }
  }

  override fun findAll(): List<AvroSchemaWithId> {
    return schemaApi.findAll().map { it.toAvroSchemaWithId() }
  }

  /**
   * Converts REST DTO to API object.
   */
  private fun AvroSchemaWithIdDto.toAvroSchemaWithId(): AvroSchemaWithId =
    AvroSchemaWithIdData(
      schemaId = this.schemaId,
      schema = Schema.Parser().parse(this.content),
      revision = this.revision,
      namespace = this.namespace,
      name = this.name
    )
}
