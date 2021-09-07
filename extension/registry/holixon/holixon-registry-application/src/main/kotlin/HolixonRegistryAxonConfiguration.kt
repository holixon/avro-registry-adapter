package io.holixon.avro.adapter.registry.holixon

import mu.KLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

/**
 * Configuration for Axon.
 */
@Configuration
@ConditionalOnProperty(name = ["avro.registry.holixon.mode"], havingValue = "axon", matchIfMissing = true)
@ComponentScan("io.holixon.avro.adapter.registry.axon")
class HolixonRegistryAxonConfiguration {

  companion object : KLogging()

  /**
   * Show that current configuration is used.
   */
  @PostConstruct
  fun activated() {
    logger.info { "HLX-REGISTRY: Activated Axon mode" }
  }
}
