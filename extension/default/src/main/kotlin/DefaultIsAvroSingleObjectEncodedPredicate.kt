package io.toolisticon.avro.adapter.common

import io.toolisticon.avro.adapter.api.AvroSingleObjectEncoded
import io.toolisticon.avro.adapter.api.IsAvroSingleObjectEncodedPredicate
import java.nio.ByteBuffer

class DefaultIsAvroSingleObjectEncodedPredicate : IsAvroSingleObjectEncodedPredicate {

  override fun test(buffer: ByteBuffer): Boolean {
    TODO("Not yet implemented")
  }

  fun test(bytes: AvroSingleObjectEncoded): Boolean = TODO()
}
