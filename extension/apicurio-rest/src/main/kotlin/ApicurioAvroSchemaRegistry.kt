package io.toolisticon.avro.adapter.apicurio

import io.toolisticon.avro.adapter.api.AvroSchemaInfo
import io.toolisticon.avro.adapter.api.AvroSchemaRegistry
import io.toolisticon.avro.adapter.api.AvroSchemaWithId
import io.toolisticon.avro.adapter.api.SchemaId
import org.apache.avro.Schema
import java.util.*

class ApicurioAvroSchemaRegistry(

) : AvroSchemaRegistry {
  override fun register(schema: Schema): AvroSchemaWithId {
    TODO("Not yet implemented")
  }

  override fun findById(globalId: SchemaId): Optional<AvroSchemaWithId> {
    TODO("Not yet implemented")
  }

  override fun findByInfo(info: AvroSchemaInfo): Optional<AvroSchemaWithId> {
    TODO("Not yet implemented")
  }

  override fun findByContextAndName(context: String, name: String): List<AvroSchemaWithId> {
    TODO("Not yet implemented")
  }

  override fun findAll(): List<AvroSchemaWithId> {
    TODO("Not yet implemented")
  }
}
