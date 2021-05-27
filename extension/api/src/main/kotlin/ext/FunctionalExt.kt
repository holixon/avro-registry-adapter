package io.holixon.avro.adapter.api.ext

import java.util.function.Function

object FunctionalExt {

  operator fun <T:Any, R:Any> Function<T,R>.invoke(t:T) :R = this.apply(t)

}
