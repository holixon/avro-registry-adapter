package io.holixon.avro.adapter.api

import org.apache.avro.Schema

/**
 * The unique id of a schema artifact, published to a repo.
 */
typealias AvroSchemaId = String

/**
 * The version of a schema.
 */
typealias AvroSchemaRevision = String

/**
 * Message encoded as [Single Object](https://avro.apache.org/docs/current/spec.html#single_object_encoding) ByteArray.
 */
typealias AvroSingleObjectEncoded = ByteArray

/**
 * The encoded message. This is only the payload data,
 * so no marker header and encoded schemaId are present.
 */
typealias AvroSingleObjectPayload = ByteArray

/**
 * For Json conversion, just to mark a string as json.
 */
typealias JsonString = String

/**
 * Wrapper type for [AvroSchemaId] and the encoded message [AvroSingleObjectPayload].
 */
interface AvroPayloadAndSchemaId {
  val schemaId: AvroSchemaId
  val payload: AvroSingleObjectPayload
}

/**
 * Wrapper type containing the [AvroSingleObjectPayload], the [Schema] and the artifacts [AvroSchemaId].
 */
interface AvroPayloadAndSchema {
  val schema: AvroSchemaWithId
  val payload: AvroSingleObjectPayload
}

/**
 * The schema info provides the relevant identifiers for a schema:
 *
 * * context (aka namespace)
 * * name
 * * revision
 *
 */
interface AvroSchemaInfo {

  companion object {
    /**
     * Default separator used in canonical name.
     */
    const val NAME_SEPARATOR = "."

    fun canonicalName(namespace: String, name: String) = "$namespace$NAME_SEPARATOR$name"

    /**
     * Compare field by field, instance equals does not work when comparing [AvroSchemaInfo] with [AvroSchemaWithId].
     */
    fun AvroSchemaInfo.equalsByFields(other: AvroSchemaInfo) =
      this.namespace == other.namespace && this.name == other.name && this.revision == other.revision
  }

  /**
   * Schema namespace.
   */
  val namespace: String

  /**
   * Schema name.
   */
  val name: String

  /**
   * Optional revision.
   */
  val revision: AvroSchemaRevision?

  /**
   * Canonical schema revision.
   */
  val canonicalName: String
    get() = canonicalName(namespace, name)
}

/**
 * Tuple wrapping the schema and its id.
 */
interface AvroSchemaWithId : AvroSchemaInfo {
  /**
   * Id of the schema.
   */
  val schemaId: AvroSchemaId

  /**
   * Avro schema.
   */
  val schema: Schema
}

// FIXME: implement meta
// interface AvroSchemaMeta {
//  val name: String?
//
//  val description: String?
//
//  val labels: List<String>?
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
// }
