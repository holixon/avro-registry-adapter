package io.holixon.avro.adapter.registry.apicurio.type

import io.apicurio.registry.rest.v2.beans.ArtifactMetaData
import io.holixon.avro.adapter.common.ext.DefaultSchemaExt.avroSchemaWithId
import io.holixon.avro.lib.test.schema.SampleEventV4712
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ApicurioArtifactMetaDataTest {

  @Test
  fun `not initialized when required properties are not set`() {
    val artifactMetaData: ArtifactMetaData = ApicurioTypeFixtures.artifactMetaData(SampleEventV4712.schema.avroSchemaWithId)

    assertThat(ApicurioArtifactMetaData(artifactMetaData).copy(properties = emptyMap()).isInitialized).isFalse
  }


  @Test
  fun `initialized when required properties are set`() {
    val artifactMetaData: ArtifactMetaData = ApicurioTypeFixtures.artifactMetaData(SampleEventV4712.schema.avroSchemaWithId)

    assertThat(ApicurioArtifactMetaData(artifactMetaData).isInitialized).isTrue
  }
}
