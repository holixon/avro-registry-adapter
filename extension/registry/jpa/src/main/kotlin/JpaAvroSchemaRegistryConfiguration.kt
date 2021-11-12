package io.holixon.avro.adapter.registry.jpa

import io.holixon.avro.adapter.common.DefaultSchemaIdSupplier
import io.holixon.avro.adapter.common.DefaultSchemaRevisionResolver
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Configuration for JPA registry.
 */
@EnableJpaRepositories(
  basePackageClasses = [JpaAvroSchemaRegistryConfiguration::class]
)
@EntityScan(
  basePackageClasses = [JpaAvroSchemaRegistryConfiguration::class]
)
class JpaAvroSchemaRegistryConfiguration {

  /**
   * Provide the registry.
   */
  @Bean
  fun avroRegistry(avroSchemaRepository: AvroSchemaRepository) = JpaAvroSchemaRegistry(
    avroSchemaRepository = avroSchemaRepository,
    schemaIdSupplier = DefaultSchemaIdSupplier(),
    schemaRevisionResolver = DefaultSchemaRevisionResolver()
  )
}
