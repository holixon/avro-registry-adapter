package io.holixon.avro.adapter.registry.jpa

import io.holixon.avro.adapter.api.*
import mu.KLogging
import org.apache.avro.Schema
import java.util.*

/**
 * Registry implementation using Spring Data JPA for persistence of schemas.
 */
open class JpaAvroSchemaRegistry(
  private val avroSchemaRepository: AvroSchemaRepository,
  private val schemaIdSupplier: SchemaIdSupplier,
  private val schemaRevisionResolver: SchemaRevisionResolver
) : AvroSchemaRegistry {
  companion object : KLogging()

  override fun register(schema: Schema): AvroSchemaWithId =
    avroSchemaRepository.save(
      AvroSchemaEntity(
        name = schema.name,
        namespace = schema.namespace,
        schemaId = schemaIdSupplier.apply(schema),
        revision = schemaRevisionResolver.apply(schema).orElse(null),
        description = schema.doc,
        schema = schema.toString()
      )
    ).toDto()

  override fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> =
    avroSchemaRepository.findById(schemaId).map { it.toDto() }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> =
    avroSchemaRepository.findByNamespaceAndNameAndRevision(
      namespace = info.namespace,
      name = info.name,
      revision = info.revision
    ).map { it.toDto() }

  override fun findAllByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId> =
    avroSchemaRepository.findAllByNamespaceAndName(
      namespace = namespace,
      name = name,
    ).map { it.toDto() }


  override fun findAll(): List<AvroSchemaWithId> = avroSchemaRepository.findAll().map { it.toDto() }
}


