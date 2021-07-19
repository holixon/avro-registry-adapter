package io.holixon.avro.adapter.registry.apicurio

import io.holixon.avro.adapter.api.type.AvroSchemaInfoData
import io.holixon.avro.adapter.common.AvroAdapterDefault
import io.holixon.avro.adapter.registry.apicurio.AvroAdapterApicurioRestHelper.ApicurioRegistryTestContainer
import io.holixon.avro.lib.test.AvroAdapterTestLib
import io.holixon.avro.lib.test.schema.SampleEventV4711
import mu.KLogging
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@TestMethodOrder(OrderAnnotation::class)
internal class ApicurioAvroSchemaRegistryTCTest {
  companion object : KLogging() {

    @Container
    @JvmStatic
    val CONTAINER = ApicurioRegistryTestContainer()
  }

  private val schemaRegistry by lazy {
    CONTAINER.schemaRegistry()
  }

  @Test
  @Order(1)
  internal fun `findById is empty`() {
    assertThat(schemaRegistry.findById("xxx")).isEmpty
  }

  @Test
  @Order(2)
  internal fun `findByInfo is empty`() {
    assertThat(
      schemaRegistry.findByInfo(
        AvroSchemaInfoData(
          namespace = "foo",
          name = "bar",
          revision = "1"
        )
      )
    ).isEmpty
  }

  @Test
  @Order(3)
  internal fun `findAllByCanonicalName is empty`() {
    assertThat(schemaRegistry.findAllByCanonicalName("bar", "foo")).isEmpty()
  }

  @Test
  @Order(4)
  internal fun `findAll is empty`() {
    assertThat(schemaRegistry.findAll()).isEmpty()
  }


  @Test
  internal fun `find by id`() {
    val schema: Schema = SampleEventV4711.schema
    val fingerprint = AvroAdapterDefault.schemaIdSupplier.apply(schema)

    val created = schemaRegistry.register(schema)
    logger.info { "created: $created" }

    val found = schemaRegistry.findById(fingerprint).orElseThrow()

    assertThat(found.schemaId).isEqualTo(fingerprint)
    assertThat(found.schema).isEqualTo(schema)
    assertThat(found.revision).isEqualTo("4711")
  }

  @Test
  internal fun `find by info`() {
    val schema: Schema = AvroAdapterTestLib.schemaSampleEvent4711
    val fingerprint = AvroAdapterDefault.schemaIdSupplier.apply(schema)

    val created = schemaRegistry.register(schema)
    logger.info { "created: $created" }

    val found = schemaRegistry.findByInfo(
      AvroSchemaInfoData(
        namespace = schema.namespace,
        name = schema.name,
        revision = "4711"
      )
    )

    assertThat(found).isNotEmpty
    assertThat(found.get().schemaId).isEqualTo(fingerprint)

    assertThat(
      schemaRegistry.findByInfo(
        AvroSchemaInfoData(
          namespace = schema.namespace,
          name = schema.name,
          revision = "4712"
        )
      )
    ).isEmpty
  }

  @Test
  internal fun `find by context and name`() {
    val schema = SampleEventV4711.schema
    val fingerprint = SampleEventV4711.schemaData.fingerPrint.toString()

    val created = schemaRegistry.register(schema)
    logger.info { "created: $created" }

    val found = schemaRegistry.findAllByCanonicalName(
      namespace = schema.namespace,
      name = schema.name
    )

    assertThat(found).hasSize(1)
    assertThat(found.first().schemaId).isEqualTo(fingerprint)
  }
}
