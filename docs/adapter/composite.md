# Composite Registry Adapter

This adapter provides a way to combine several schema registry adapters in a chain. By doing so, it utilizes one read/write schema registry
and a list of read-only registries.

Always consider using the composite registry adapter, to boost your performance to a slow (remote) registry adapter by including an
in-memory registry adapter as a cache in front of it.
