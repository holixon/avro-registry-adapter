package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import io.holixon.avro.adapter.api.IsAvroSingleObjectEncodedPredicate
import java.nio.ByteBuffer

class DefaultIsAvroSingleObjectEncodedPredicate : IsAvroSingleObjectEncodedPredicate {

  override fun test(buffer: ByteBuffer): Boolean {
    TODO("Not yet implemented")
  }

  fun test(bytes: AvroSingleObjectEncoded): Boolean = TODO()
}
