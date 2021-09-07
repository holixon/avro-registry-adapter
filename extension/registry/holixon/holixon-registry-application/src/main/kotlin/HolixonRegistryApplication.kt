package io.holixon.avro.adapter.registry.holixon

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import springfox.documentation.oas.annotations.EnableOpenApi

/**
 * Starts the application.
 */
fun main(vararg args: String) {
  runApplication<HolixonRegistryApplication>(*args)
}

/**
 * Main application class.
 * Supports two modes, see [HolixonRegistryProperties].
 */
@SpringBootApplication
@EnableOpenApi
@EnableConfigurationProperties(HolixonRegistryProperties::class)
class HolixonRegistryApplication {

  /**
   * Jackson object mapper.
   */
  @Bean
  fun myObjectMapper() = jacksonObjectMapper()
}
