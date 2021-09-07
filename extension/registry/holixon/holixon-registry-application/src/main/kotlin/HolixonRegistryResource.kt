package io.holixon.avro.adapter.registry.holixon

import com.fasterxml.jackson.databind.ObjectMapper
import io.holixon.avro.adapter.api.AvroSchemaRegistry
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.type.AvroSchemaInfoData
import io.holixon.avro.adapter.registry.remote.rest.api.SchemaApi
import io.holixon.avro.adapter.registry.remote.rest.model.AvroSchemaWithIdDto
import mu.KLogging
import org.apache.avro.Schema
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest controller.
 */
@RestController
@RequestMapping("/public/api")
class HolixonRegistryResource(
  val service: AvroSchemaRegistry,
  val objectMapper: ObjectMapper,
) : SchemaApi {

  companion object : KLogging()

  override fun findAll(): ResponseEntity<List<AvroSchemaWithIdDto>> {
    return ok(service.findAll().map { it.toDto() })
  }

  override fun findAllByCanonicalName(namespace: String, name: String): ResponseEntity<List<AvroSchemaWithIdDto>> {
    return ok(service.findAllByCanonicalName(namespace, name).map { it.toDto() })
  }

  override fun findById(id: String): ResponseEntity<AvroSchemaWithIdDto> {
    return service.findById(id).map { ok(it.toDto()) }.orElse(notFound().build())
  }

  override fun findByInfo(namespace: String, name: String, revision: String?): ResponseEntity<AvroSchemaWithIdDto> {
    return service.findByInfo(
      AvroSchemaInfoData(namespace = namespace, name = name, revision = revision)
    ).map { ok(it.toDto()) }.orElse(notFound().build())
  }

  override fun registerSchema(schema: Any): ResponseEntity<AvroSchemaWithIdDto> {
    val json = objectMapper.writeValueAsString(schema)
    val schemaWithId = service.register(Schema.Parser().parse(json))
    logger.info { "Registered schema: $schemaWithId" }
    return ok(schemaWithId.toDto())
  }

  /**
   * Converter to DTO defined in the API.
   */
  private fun AvroSchemaWithId.toDto() = AvroSchemaWithIdDto(
    namespace = this.namespace,
    name = this.name,
    schemaId = this.schemaId,
    content = this.schema.toString(),
    revision = this.revision
  )
}
