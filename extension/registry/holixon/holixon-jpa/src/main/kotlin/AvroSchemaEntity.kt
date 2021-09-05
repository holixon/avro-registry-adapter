package io.holixon.avro.adapter.registry.jpa

import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import org.apache.avro.Schema
import javax.persistence.*

/**
 * Entity to store the schema in a RDBMS.
 */
@Entity
@Table(name = "HLX_AVRO_SCHEMA")
class AvroSchemaEntity(
  @Column(name = "SCHEMA_ID")
  @Id
  var schemaId: String,
  @Column(name = "NAME")
  var name: String,
  @Column(name = "NAMESPACE")
  var namespace: String,
  @Column(name = "REVISION", nullable = true)
  var revision: String?,
  @Column(name = "DESCRIPTION", nullable = true)
  var description: String?,
  @Column(name = "SCHEMA")
  @Lob
  var schema: String
) {

  override fun toString(): String = "Schema[schemaId:$schemaId, revision:$revision]"

  /**
   * Creates the DTO out of entity.
   */
  fun toDto() = AvroSchemaWithIdData(
    namespace = this.namespace,
    name = this.name,
    revision = this.revision,
    schemaId = this.schemaId,
    schema = Schema.Parser().parse(this.schema)
  )

}
