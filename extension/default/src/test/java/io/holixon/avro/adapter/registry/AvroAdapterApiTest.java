package io.holixon.avro.adapter.registry;

import static org.assertj.core.api.Assertions.assertThat;

import io.holixon.avro.adapter.api.AvroAdapterApi;
import io.holixon.avro.adapter.api.AvroSchemaRegistry;
import io.holixon.avro.adapter.api.SchemaIdSupplier;
import io.holixon.avro.adapter.api.SchemaRevisionResolver;
import io.holixon.avro.adapter.common.registry.InMemoryAvroSchemaRegistry;
import io.holixon.avro.lib.test.schema.SampleEventV4711;
import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AvroAdapterApiTest {

  public static final Schema SAMPLE_4711 = SampleEventV4711.INSTANCE.getSchema();

  private static final SchemaRevisionResolver SCHEMA_REVISION_RESOLVER = AvroAdapterApi.propertyBasedSchemaRevisionResolver("revision");
  private static final SchemaIdSupplier SCHEMA_ID_SUPPLIER = schema -> String.valueOf(SchemaNormalization.parsingFingerprint64(schema));

  private final AvroSchemaRegistry schemaRegistry = new InMemoryAvroSchemaRegistry(SCHEMA_ID_SUPPLIER, SCHEMA_REVISION_RESOLVER);

  @BeforeEach
  void setUp() {
    assertThat(schemaRegistry.findAll()).isEmpty();
  }

  @Test
  void registerReturnsSchemaWithId() {
    var registered = schemaRegistry.register(SAMPLE_4711);

    assertThat(schemaRegistry.findAll()).hasSize(1);
    assertThat(registered.getSchemaId()).isEqualTo(String.valueOf(SampleEventV4711.INSTANCE.getSchemaData().getFingerPrint()));
  }


}
