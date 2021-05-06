package io.holixon.avro.lib.test

import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificData
import org.junit.jupiter.api.Test

internal class AvroAdapterTestLibTest {

  @Test
  internal fun name() {
    println(AvroAdapterTestLib.sampleSchema4711)
    println(AvroAdapterTestLib.sampleSchema4712)
  }

  @Test
  internal fun `generic record from schema`() {
    val schema4712 = AvroAdapterTestLib.sampleSchema4712.schema

    val record4712: GenericRecord = GenericData.Record(schema4712).apply {
      put("value", "foo")
      put("anotherValue", "bar")
    }

    val sd4712 = SpecificData.get().deepCopy(schema4712, record4712)
    println("sd: $sd4712")


//    name": "value",
//    "type": {
//      "type": "string",
//      "avro.java.string": "String"
//    }
//  },
//  {
//    "name": "anotherValue",
//    "type": {
//    "type": "string",
//    "avro.java.string": "String"
//  }
  }
}
