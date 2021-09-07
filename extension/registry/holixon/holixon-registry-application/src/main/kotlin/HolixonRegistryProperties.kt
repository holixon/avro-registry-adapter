package io.holixon.avro.adapter.registry.holixon

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * Properties for registry application.
 */
@ConfigurationProperties("avro.registry.holixon")
@ConstructorBinding
data class HolixonRegistryProperties(
  val mode: RegistryMode
)

/**
 * Switches registry mode.
 */
enum class RegistryMode {
  /**
   * Use JPA.
   */
  JPA,

  /**
   * Use Axon Event Sourcing.
   */
  AXON
}
