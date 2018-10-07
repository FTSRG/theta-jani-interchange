package hu.bme.mit.inf.jani.model.json

import com.fasterxml.jackson.annotation.JacksonAnnotation
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@JacksonAnnotation
annotation class JaniJsonMultiOp(val predicate: KClass<out ConversionPredicate> = ConversionPredicate.None::class)