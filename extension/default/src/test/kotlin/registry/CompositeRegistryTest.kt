package io.holixon.avro.adapter.common.registry

import io.holixon.avro.adapter.api.AvroSchemaReadOnlyRegistry
import io.holixon.avro.adapter.api.AvroSchemaRegistry
import io.holixon.avro.adapter.api.AvroSchemaWithId
import io.holixon.avro.adapter.api.type.AvroSchemaInfoData
import io.holixon.avro.adapter.common.AvroAdapterDefault.toAvroSchemaWithId
import io.holixon.avro.lib.test.schema.SampleEventV4711
import io.holixon.avro.lib.test.schema.SampleEventV4712
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.*

internal class CompositeRegistryTest {

  private val schema4711 = SampleEventV4711.schema.toAvroSchemaWithId()
  private val schema4712 = SampleEventV4712.schema.toAvroSchemaWithId()

  private val fastRoRegistry: AvroSchemaReadOnlyRegistry = mock()
  private val mediumRwRegistry: AvroSchemaRegistry = mock()
  private val slowRwRegistry: AvroSchemaRegistry = mock()

  private val registry: AvroSchemaRegistry = CompositeAvroSchemaRegistry(slowRwRegistry, fastRoRegistry, mediumRwRegistry, slowRwRegistry)

  @Test
  fun `should deliver from the first registry if found`() {
    mockRegistryFindMethods(fastRoRegistry, schema4711)
    mockRegistryFindMethods(mediumRwRegistry, null)
    mockRegistryFindMethods(slowRwRegistry, null)

    callRegistryFindMethodsAndFindResult()

    verifyCallsExecuted(fastRoRegistry, true)
    verifyCallsExecuted(mediumRwRegistry, false)
    verifyCallsExecuted(slowRwRegistry, false)
  }

  @Test
  fun `should deliver from the second registry if not found in the first`() {
    mockRegistryFindMethods(fastRoRegistry, null)
    mockRegistryFindMethods(mediumRwRegistry, schema4711)
    mockRegistryFindMethods(slowRwRegistry, null)

    callRegistryFindMethodsAndFindResult()

    verifyCallsExecuted(fastRoRegistry, true)
    verifyCallsExecuted(mediumRwRegistry, true)
    verifyCallsExecuted(slowRwRegistry, false)
  }

  @Test
  fun `should deliver from the third registry if not found in the first and second`() {
    mockRegistryFindMethods(fastRoRegistry, null)
    mockRegistryFindMethods(mediumRwRegistry, null)
    mockRegistryFindMethods(slowRwRegistry, schema4711)

    callRegistryFindMethodsAndFindResult()

    verifyCallsExecuted(fastRoRegistry, true)
    verifyCallsExecuted(mediumRwRegistry, true)
    verifyCallsExecuted(slowRwRegistry, true)
  }

  @Test
  internal fun `findAllByCanonicalName returns schemas with different revisions from different registries`() {
    mockRegistryFindMethods(mediumRwRegistry, schema4711)
    mockRegistryFindMethods(slowRwRegistry, schema4712)

    val allFQN = registry.findAllByCanonicalName(schema4711.namespace, schema4711.name)
    assertThat(allFQN).hasSize(2)
  }

  @Test
  fun `should ask all registries if not found`() {
    mockRegistryFindMethods(fastRoRegistry, null)
    mockRegistryFindMethods(mediumRwRegistry, null)
    mockRegistryFindMethods(slowRwRegistry, null)

    callRegistryFindMethodsAndDontFindResult()

    verifyCallsExecuted(fastRoRegistry, true)
    verifyCallsExecuted(mediumRwRegistry, true)
    verifyCallsExecuted(slowRwRegistry, true)
  }

  @Test
  fun `should register schema in the designated registry`() {
    whenever(slowRwRegistry.register(any())).thenReturn(schema4711)

    registry.register(schema4711.schema)

    verify(slowRwRegistry).register(schema4711.schema)
    verifyZeroInteractions(mediumRwRegistry)
  }

  @Test
  internal fun `findAll returns combined list`() {
    mockRegistryFindMethods(fastRoRegistry, schema4711)
    mockRegistryFindMethods(mediumRwRegistry, schema4712)
    mockRegistryFindMethods(slowRwRegistry, null)

    val all = registry.findAll()
    assertThat(all).hasSize(2)
    assertThat(all.map { it.schemaId }).containsExactlyInAnyOrder(schema4711.schemaId, schema4712.schemaId)
  }

  /**
   * Stubbing of methods.
   */
  private fun mockRegistryFindMethods(registry: AvroSchemaReadOnlyRegistry, schemaWithId: AvroSchemaWithId?) {
    if (schemaWithId != null) {
      whenever(registry.findById(any())).thenReturn(Optional.of(schemaWithId))
      whenever(registry.findByInfo(any())).thenReturn(Optional.of(schemaWithId))
      whenever(registry.findAll()).thenReturn(listOf(schemaWithId))
      whenever(registry.findAllByCanonicalName(any(), any())).thenReturn(listOf(schemaWithId))
    } else {
      whenever(registry.findById(any())).thenReturn(Optional.empty())
      whenever(registry.findByInfo(any())).thenReturn(Optional.empty())
      whenever(registry.findAll()).thenReturn(listOf())
      whenever(registry.findAllByCanonicalName(any(), any())).thenReturn(listOf())
    }
  }

  /**
   * Verification that the call has been (or not) executed.
   */
  private fun verifyCallsExecuted(registry: AvroSchemaReadOnlyRegistry, executed: Boolean) {
    if (executed) {
      verify(registry).findById(schema4711.schemaId)
      verify(registry).findByInfo(AvroSchemaInfoData(schema4711.namespace, schema4711.name, null))
    } else {
      verifyNoMoreInteractions(registry)
    }
  }

  /**
   * Actual call of the registry methods.
   */
  private fun callRegistryFindMethodsAndFindResult() {
    assertThat(registry.findById(schema4711.schemaId)).isPresent.get().isEqualTo(schema4711)
    assertThat(registry.findByInfo(AvroSchemaInfoData(schema4711.namespace, schema4711.name, null))).isPresent.get().isEqualTo(schema4711)
  }

  /**
   * Actual call of the registry methods.
   */
  private fun callRegistryFindMethodsAndDontFindResult() {
    assertThat(registry.findAll()).isEmpty()
    assertThat(registry.findAllByCanonicalName(schema4711.namespace, schema4711.name)).isEmpty()
    assertThat(registry.findById(schema4711.schemaId)).isEmpty
    assertThat(registry.findByInfo(AvroSchemaInfoData(schema4711.namespace, schema4711.name, null))).isEmpty
  }

}
