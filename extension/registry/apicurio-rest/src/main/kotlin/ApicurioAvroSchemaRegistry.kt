package io.holixon.avro.adapter.registry.apicurio

import io.apicurio.registry.rest.client.RegistryClient
import io.holixon.avro.adapter.api.*
import io.holixon.avro.adapter.api.ext.FunctionalExt.invoke
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.adapter.registry.apicurio.client.ApicurioRegistryClient
import io.holixon.avro.adapter.registry.apicurio.client.DefaultApicurioRegistryClient
import mu.KLogging
import org.apache.avro.Schema
import java.util.*

/**
 * Implementation of the registry using Apicurio rest client.
 */
open class ApicurioAvroSchemaRegistry(
  private val client: ApicurioRegistryClient,
  private val schemaRevisionResolver: SchemaRevisionResolver
) : AvroSchemaRegistry {
  companion object : KLogging()

  constructor(
    client: RegistryClient,
    schemaIdSupplier: SchemaIdSupplier,
    schemaRevisionResolver: SchemaRevisionResolver
  ) : this(
    client = DefaultApicurioRegistryClient(client, schemaIdSupplier),
    schemaRevisionResolver = schemaRevisionResolver
  )

  override fun register(schema: Schema): AvroSchemaWithId {
    val uploaded = client.upload(schema)
    return AvroSchemaWithIdData(
      schemaId = uploaded.schemaId!!,
      schema = schema,
      revision = schemaRevisionResolver.invoke(schema).orElse(null),
      namespace = uploaded.groupId,
      name = uploaded.name
    )
  }

  override fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> {
//    fun findAndUpdateMetaDataIfNotFound(schemaId: AvroSchemaId, recover: Boolean = true): Result<AvroSchemaWithId> {
//      val result = client.findSchemaById(schemaId)
//        .map {
//          AvroSchemaWithIdData(
//            schemaId = schemaIdSupplier.apply(it),
//            schema = it,
//            revision = schemaRevisionResolver.apply(it).orElse(null)
//          )
//        }
//
//      if (result.isFailure && recover) {
//        client.updateAllNotInitializedArtifactMetaData()
//        return findAndUpdateMetaDataIfNotFound(schemaId, !recover)
//
//    return findAndUpdateMetaDataIfNotFound(schemaId).map { Optional.of(it) }.getOrDefault(Optional.empty())
//  }
//      }

    return client.findSchemaBySchemaId(schemaId)
  }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> = findAllByCanonicalName(info.namespace, info.name)
    .singleOrNull { info.revision == it.revision }
    .let { Optional.ofNullable(it) }

  override fun findAllByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId> =
    client.findAllByGroupIdAndArtifactId(namespace, name)
      .map { client.findSchemaBySearchedArtifact(it) }

  override fun findAll(): List<AvroSchemaWithId> = client.findAll()
    .map { client.findSchemaBySearchedArtifact(it) }
}
