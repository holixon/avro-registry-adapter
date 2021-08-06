package io.holixon.avro.adapter.api

import io.holixon.avro.adapter.api.type.AvroSchemaWithIdData
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4712
import io.holixon.avro.lib.test.schema.SampleEventV4713
import io.holixon.avro.lib.test.schema.TestSchemaDataProvider
import org.apache.avro.Schema
import org.apache.avro.SchemaNormalization
import java.util.*

object AvroAdapterApiTestHelper {

  val sampleEvent4711 = avroSchemaWithId(SampleEventV4711)
  val sampleEvent4712 = avroSchemaWithId(SampleEventV4712)
  val sampleEvent4713 = avroSchemaWithId(SampleEventV4713)

  val sampleEventsSchemaResolver = InMemoryAvroSchemaResolver(listOf(sampleEvent4711, sampleEvent4712, sampleEvent4713))

  fun avroSchemaWithId(schema: Schema): AvroSchemaWithIdData {
    return AvroSchemaWithIdData(
      schemaId = SchemaNormalization.parsingFingerprint64(schema).toString(),
      schema = schema,
      revision = AvroAdapterApi.propertyBasedSchemaRevisionResolver("revision").apply(schema).orElse(null)
    )
  }

  fun avroSchemaWithId(testData: TestSchemaDataProvider): AvroSchemaWithIdData = AvroSchemaWithIdData(
    schemaId = testData.schemaData.schemaId,
    schema = testData.schema,
    revision = testData.schemaData.revision
  )

  open class InMemoryAvroSchemaResolver(schemas: List<AvroSchemaWithId>) : AvroSchemaResolver {
    val map: Map<AvroSchemaId /* = kotlin.String */, AvroSchemaWithId> = schemas.associateBy { it.schemaId }

    override fun apply(schemaId: AvroSchemaId): Optional<AvroSchemaWithId> = Optional.ofNullable(map[schemaId])
  }
}
