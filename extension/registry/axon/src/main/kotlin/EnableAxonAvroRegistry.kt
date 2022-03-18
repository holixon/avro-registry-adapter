package io.holixon.avro.adapter.registry.axon

import org.springframework.context.annotation.Import

@MustBeDocumented
@Import(AxonAvroRegistryConfiguration::class)
annotation class EnableAxonAvroRegistry
