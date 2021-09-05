package io.holixon.avro.adapter.registry.holixon

import io.holixon.avro.adapter.registry.jpa.JpaAvroSchemaRegistryConfiguration
import mu.KLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import javax.annotation.PostConstruct

/**
 * Configuration for JPA.
 */
@Configuration
@ConditionalOnProperty(name = ["avro.registry.holixon.mode"], havingValue = "jpa")
@Import(JpaAvroSchemaRegistryConfiguration::class)
class HolixonRegistryJpaConfiguration {

  companion object : KLogging()

  @PostConstruct
  fun activated() {
    logger.info { "HLX-REGISTRY: Activated JPA mode" }
  }
}
