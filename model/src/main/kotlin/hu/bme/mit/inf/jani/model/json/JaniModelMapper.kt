package hu.bme.mit.inf.jani.model.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

class JaniModelMapper : ObjectMapper() {
    init {
        registerModules(KotlinModule(nullToEmptyCollection = true), JaniModelModule)
    }
}

object JaniModelModule : SimpleModule("jani-model") {
    init {
        setNamingStrategy(PropertyNamingStrategy.KEBAB_CASE)
        setSerializerModifier(JaniModelBeanSerializerModifier)
        setDeserializerModifier(JaniModelBeanDeserializerModifier)
    }
}
