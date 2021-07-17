package io.holixon.avro.adapter.api.cache

import io.holixon.avro.adapter.api.AvroSchemaId
import io.holixon.avro.adapter.api.AvroSchemaInfo
import io.holixon.avro.adapter.api.AvroSchemaWithId
import org.apache.avro.Schema
import org.apache.avro.message.SchemaStore
import java.util.*
import javax.cache.Cache

class AvroSchemaRegistryCache(
  val byId : Cache<AvroSchemaId, AvroSchemaWithId>
) {

//  fun findById(schemaId: AvroSchemaId): Optional<AvroSchemaWithId>
//
//  fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId>
//
//  fun findAllByCanonicalName(namespace: String, name: String): List<AvroSchemaWithId>

//  fun findAll(): List<AvroSchemaWithId>

}
