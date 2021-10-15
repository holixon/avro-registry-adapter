# In-Memory Registry Adapter

The read/write in-memory implementation of the adapter, backed by a simple hashmap, holding the schema information in memory. The
implementation has no persistence and will lose all schema information on the restart of the JVM.

Consider usage of the in-memory adapter as a cache to other adapter using the [Composite](composite.md) registry adapter. 
