package io.toolisticon.avro.adapter.api

import org.apache.avro.Schema


typealias SchemaId = String
typealias SchemaRevision = String
typealias AvroSingleObjectEncoded = ByteArray


interface AvroPayloadAndSchema {

  val schema: AvroSchemaWithId

  val payload: ByteArray

}

/**
 * The schema info provides the relevant identifiers for a schema:
 *
 * * context (aka namespace)
 * * name
 * * revision
 */
interface AvroSchemaInfo {

  val context: String

  val name: String

  val revision: SchemaRevision?

  val canonicalName : String
    get() = "$context.$name"
}

/**
 * Tuple wrapping the schema and its id.
 */
interface AvroSchemaWithId : AvroSchemaInfo {

  val id: SchemaId

  val schema: Schema

}

interface AvroSchemaMeta {
   val name: String?

   val description: String?

    val labels: List<String>?

    // FIXME: implment meta
//
//  @JsonProperty("createdBy")
//  private val createdBy: String? = null
//
//  @JsonProperty("createdOn")
//  private val createdOn: Long = 0
//
//  @JsonProperty("modifiedBy")
//  private val modifiedBy: String? = null
//
//  @JsonProperty("modifiedOn")
//  private val modifiedOn: Long = 0
//
//  @JsonProperty("id")
//  @JsonPropertyDescription("")
//  private val id: String? = null
//
//  @JsonProperty("version")
//  @JsonPropertyDescription("")
//  private val version: Int? = null
//
//  @JsonProperty("type")
//  @JsonPropertyDescription("")
//  private val type: io.apicurio.registry.types.ArtifactType? = null
//
//  @JsonProperty("globalId")
//  @JsonPropertyDescription("")
//  private val globalId: Long? = null
//
//  @JsonProperty("state")
//  @JsonPropertyDescription("Describes the state of an artifact or artifact version.  The following states\nare possible:\n\n* ENABLED\n* DISABLED\n* DEPRECATED\n")
//  private val state: ArtifactState? = null
//
//  @JsonProperty("properties")
//  @JsonPropertyDescription("A set of name-value properties for an artifact or artifact version.")
//  private val properties: Map<String, String>? = null

}
