package hu.bme.mit.inf.jani.model.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import hu.bme.mit.inf.jani.model.RewardAccumulation
import hu.bme.mit.inf.jani.model.SimpleType

object JaniModelBeanSerializerModifier : BeanSerializerModifier() {
    override fun modifySerializer(
            config: SerializationConfig, beanDesc: BeanDescription, serializer: JsonSerializer<*>
    ): JsonSerializer<*> {
        val beanClass = beanDesc.beanClass
        return when {
            SimpleType::class.java.isAssignableFrom(beanClass) -> OverrideTypeSerializer(serializer, null)
            else -> serializer
        }
    }
}

class OverrideTypeSerializer<T>(
        private val originalSerializer: JsonSerializer<T>,
        private val typeSer: TypeSerializer?
) : StdSerializer<T>(originalSerializer.handledType()), ContextualSerializer {
    override fun serialize(value: T?, gen: JsonGenerator?, provider: SerializerProvider?) {
        if (typeSer == null) {
            originalSerializer.serialize(value, gen, provider)
        } else {
            originalSerializer.serializeWithType(value, gen, provider, typeSer)
        }
    }

    override fun serializeWithType(
            value: T, gen: JsonGenerator?, serializers: SerializerProvider?,
            typeSer: com.fasterxml.jackson.databind.jsontype.TypeSerializer?
    ) {
        serialize(value, gen, serializers)
    }

    override fun createContextual(prov: SerializerProvider?, property: BeanProperty?): JsonSerializer<*> =
            if (originalSerializer is ContextualSerializer) {
                val contextualOriginalSerializer = originalSerializer.createContextual(prov, property)
                OverrideTypeSerializer(contextualOriginalSerializer, typeSer)
            } else {
                this
            }
}