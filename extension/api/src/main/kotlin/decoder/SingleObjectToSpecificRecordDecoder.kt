package io.holixon.avro.adapter.api.decoder

import io.holixon.avro.adapter.api.AvroSingleObjectEncoded
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificRecordBase

/**
 * Decodes an instance of [org.apache.avro.specific.SpecificRecordBase] from given [AvroSingleObjectEncoded] bytes.
 */
interface SingleObjectToSpecificRecordDecoder {

  /**
   * @param bytes the encoded single object
   * @return specific record instance with decoded bytes content
   */
  fun <T : SpecificRecordBase> decode(bytes: AvroSingleObjectEncoded): T

}
