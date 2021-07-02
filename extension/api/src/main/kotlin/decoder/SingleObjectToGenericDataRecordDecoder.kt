package io.holixon.avro.adapter.api.decoder

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import org.apache.avro.generic.GenericData

/**
 * Decodes a [org.apache.avro.generic.GenericData.Record] from given [AvroSingleObjectEncoded] bytes.
 */
fun interface SingleObjectToGenericDataRecordDecoder {

  /**
   * @param bytes the encoded single object
   * @return record with decoded bytes content
   */
  fun decode(bytes: AvroSingleObjectEncoded): GenericData.Record

}
