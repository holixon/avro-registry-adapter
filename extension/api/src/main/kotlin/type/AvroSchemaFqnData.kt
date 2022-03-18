package io.holixon.avro.adapter.api.type

import io.holixon.avro.adapter.api.AvroSchemaFqn
import io.holixon.avro.adapter.api.AvroSchemaName
import io.holixon.avro.adapter.api.AvroSchemaNamespace

/**
 * Data class implementing [AvroSchemaFqn].
 */
data class AvroSchemaFqnData(
  override val namespace: AvroSchemaNamespace,
  override val name: AvroSchemaName
) : AvroSchemaFqn
