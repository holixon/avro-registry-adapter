package io.holixon.avro.adapter.api

import org.apache.avro.Schema
import java.util.*

/**
 * The Schema Registry is responsible for storing and retrieving arvo schema files.
 */
interface AvroSchemaRegistry {

  /**
   * Stores a new [Schema] (version) in the repository.
   */
  fun register(schema: Schema): AvroSchemaWithId

  /**
   * Finds a stored [Schema] based on its unique [SchemaId] (e.g. its fingerprint).
   */
  fun findById(schemaId: SchemaId): Optional<AvroSchemaWithId>

  /**
   * Finds a stored [Schema] based on its derived info.
   */
  fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId>

  /**
   * Finds all stored [Schema]s based on its namespace and name (e.g. FQN).
   */
  fun findByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId>

  /**
   * Simply lists all stored [Schema]s.
   */
  fun findAll(): List<AvroSchemaWithId>
}
