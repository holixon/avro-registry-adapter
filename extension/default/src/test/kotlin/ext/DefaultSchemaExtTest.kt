package io.holixon.avro.adapter.common.ext

import io.holixon.avro.adapter.api.type.AvroSchemaInfoData
import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaId
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaInfo
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaRevision
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaWithId
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.createGenericRecord
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.fingerprint
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4712
import org.apache.avro.SchemaNormalization
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultSchemaExtTest {

  private val schema = SampleEventV4711.schema
  private val fingerprint = SchemaNormalization.parsingFingerprint64(schema)

  @Test
  fun `get fingerprint`() {
    assertThat(schema.fingerprint).isEqualTo(fingerprint)
  }

  @Test
  fun `get avroSchemaId`() {
    assertThat(schema.avroSchemaId).isEqualTo("$fingerprint")
  }

  @Test
  fun `get avroSchemaRevision`() {
    assertThat(schema.avroSchemaRevision).isEqualTo("4711")
  }

  @Test
  fun `get avroSchemaWithId`() {
    assertThat(schema.avroSchemaWithId).isEqualTo(
      AvroSchemaWithIdData(
        schemaId = "$fingerprint",
        schema = schema,
        revision = "4711"
      )
    )
  }

  @Test
  fun `get avroSchemaWithId 4712`() {
    val s4712 = SampleEventV4712.schema

    assertThat(s4712.avroSchemaWithId).isEqualTo(
      AvroSchemaWithIdData(
        schemaId = s4712.avroSchemaId,
        schema = s4712,
        revision = "4712"
      )
    )
  }

  @Test
  fun `get avroSchemaInfo`() {
    assertThat(schema.avroSchemaInfo).isEqualTo(
      AvroSchemaInfoData(
        namespace = "test.fixture",
        name = "SampleEvent",
        revision = "4711"
      )
    )
  }

  @Test
  fun `create generic record`() {
    val record: GenericData.Record = schema.createGenericRecord {
      put("value", "foo")
    }

    assertThat(record["value"]).isEqualTo("foo")
    assertThat(record.schema).isEqualTo(schema)
  }
}
