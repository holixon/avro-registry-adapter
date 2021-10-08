package io.holixon.avro.adapter.registry.jpa

import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Avro schema Spring-Data repository.
 */
interface AvroSchemaRepository : CrudRepository<AvroSchemaEntity, String> {

  /**
   * Finds a schema by namespace, name and optional revision.
   */
  fun findByNamespaceAndNameAndRevision(namespace: String, name: String, revision: String?): Optional<AvroSchemaEntity>

  /**
   * Finds all schemas by namespace and name.
   */
  fun findAllByNamespaceAndName(namespace: String, name: String): List<AvroSchemaEntity>

}
