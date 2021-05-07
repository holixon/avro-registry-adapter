# avro-adapter

[![Build Status](https://github.com/holixon/avro-adapter/workflows/Development%20branches/badge.svg)](https://github.com/holixon/avro-adapter/actions)
[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-RED.svg)](https://holisticon.de/)

[comment]: <> ([![Maven Central]&#40;https://maven-badges.herokuapp.com/maven-central/io.toolisticon.avro/avro-adapter-bom/badge.svg&#41;]&#40;https://maven-badges.herokuapp.com/maven-central/io.toolisticon.avro/avro-adapter-bom&#41;)


Convenient support for working with avro serialization on the JVM

# Modules

## `avro-adapter-bom`

Bill of material that lists all modules for convenient dependency definition.

## `avro-adapter-api`

Defines core interfaces and helpers to simplify and generalize working with apache avro.
These interfaces do not rely on the default serialization of the avro lib, so they can be implemented against various
registries and serialization strategies.

## `avro-adapter-default`

Implements `avro-adapter-api` following the default avro lib serialization and deserialization specifications.

## `avro-adapter-apicurio-rest`

Implements a [AvroSchemaRegistry](./extension/api/src/main/kotlin/AvroSchemaRegistry.kt) that uses the [apicurio-registry-client](https://github.com/Apicurio/apicurio-registry/tree/master/client) to connect to the apicurio registry via REST.
