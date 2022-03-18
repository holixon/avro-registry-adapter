# Reflective Registry Adapter

A reflective registry adapter is used to find all schemas available in the classes on the classpath
and initialize an in-memory read-only with them. By doing so, the scan is executed only once, since the
classpath is assumed unchanged during the runtime of the application. 

This adapter is helpful in scenarios where generated Avro Java classes are present on the classpath
and all the underlying schemas should be added to a registry.
