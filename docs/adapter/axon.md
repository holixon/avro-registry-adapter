# Axon Framework Registry Adapter

The Axon Registry Adapter uses Axon-Framework implementing the Registry Adapter API. By doing so, it 
provides a schema aggregate, which uses Event Sourcing for persistence. If Axon Server is already in use,
no other event store is required, but if not yet configured, any other event store will do as well (Mongo, JPA, JDBC). 

This implementation is motivated by the scenario of use of Avro Serializer for Axon event serialization.
By doing so, the registry doesn't need any further persistent implementation besides the one already provided 
by the Axon Server.
