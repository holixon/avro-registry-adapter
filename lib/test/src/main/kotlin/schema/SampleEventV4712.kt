package io.holixon.avro.lib.test.schema

object SampleEventV4712 : TestSchemaDataProvider{
  const val SCHEMA = """
    {
      "type": "record",
      "name": "SampleEvent",
      "namespace": "test.fixture",
      "fields": [
        {
          "name": "value",
          "type": {
            "type": "string",
            "avro.java.string": "String"
          }
        },
        {
          "name": "anotherValue",
          "type": {
            "type": "string",
            "avro.java.string": "String"
          }
        }
      ],
      "__comment": "used for testing only!",
      "revision": "4712"
    }
  """

  override fun schemaData() = parseData(SCHEMA)
}
