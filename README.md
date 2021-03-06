# avro-registry-adapter

[![incubating](https://img.shields.io/badge/lifecycle-INCUBATING-orange.svg)](https://github.com/holisticon#open-source-lifecycle)
[![Build Status](https://github.com/holixon/avro-registry-adapter/workflows/Development%20branches/badge.svg)](https://github.com/holixon/avro-registry-adapter/actions)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1f099f2971ed401ea1d8d55a7183a00c)](https://www.codacy.com/gh/holixon/avro-registry-adapter/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=holixon/avro-registry-adapter&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/holixon/avro-registry-adapter/branch/develop/graph/badge.svg?token=bjT1hlfnH4)](https://codecov.io/gh/holixon/avro-registry-adapter)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.holixon.avro/avro-registry-adapter-bom/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.holixon.avro/avro-registry-adapter-bom)
[![sponsored](https://img.shields.io/badge/sponsoredBy-Holisticon-RED.svg)](https://holisticon.de/)

Convenient support for working with Apache Avro serialization on the JVM

## Modules

### `avro-registry-adapter-bom`

Bill of material that lists all modules for convenient dependency definition.

### `avro-registry-adapter-api`

Defines core interfaces and helpers to simplify and generalize working with apache avro.
These interfaces do not rely on the default serialization of the avro lib, so they can be implemented against various
registries and serialization strategies.

### `avro-registry-adapter-default`

Implements `avro-registry-adapter-api` following the default avro lib serialization and deserialization specifications.

### `avro-registry-adapter-apicurio-rest`

Implements a [AvroSchemaRegistry](./extension/api/src/main/kotlin/AvroSchemaRegistry.kt) that uses the [apicurio-registry-client](https://github.com/Apicurio/apicurio-registry/tree/master/client) to connect to the apicurio registry via REST.
