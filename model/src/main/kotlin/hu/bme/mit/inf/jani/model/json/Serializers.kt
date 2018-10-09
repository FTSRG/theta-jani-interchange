package hu.bme.mit.inf.jani.model.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import hu.bme.mit.inf.jani.model.*

object JaniModelBeanSerializerModifier : BeanSerializerModifier() {
    private val typesWithDisabledTypeSerializer = listOf(
            SimpleType::class.java, ConstantValue::class.java, Identifier::class.java, StatePredicate::class.java,
            Named::class.java
    )

    @Suppress("UNCHECKED_CAST")
    override fun modifySerializer(
            config: SerializationConfig, beanDesc: BeanDescription, serializer: JsonSerializer<*>
    ): JsonSerializer<*> = when {
        shouldDisableTypeSerializer(beanDesc) -> DisableTypeSerializer(serializer)
        DistributionSampling::class.java.isAssignableFrom(beanDesc.beanClass) ->
            TypeSerializerOverride(
                    DistributionSampling::class.java,
                    serializer as JsonSerializer<in DistributionSampling>
            )
        else -> serializer
    }

    private fun shouldDisableTypeSerializer(beanDesc: BeanDescription): Boolean {
        if (beanDesc.classInfo.hasAnnotation(JaniJsonMultiOp::class.java)) {
            return true
        }

        val beanClass = beanDesc.beanClass
        return typesWithDisabledTypeSerializer.any { it.isAssignableFrom(beanClass) }
    }
}

class DisableTypeSerializer<T>(
        private val originalSerializer: JsonSerializer<T>
) : StdSerializer<T>(originalSerializer.handledType()), ContextualSerializer {
    override fun serialize(value: T?, gen: JsonGenerator?, provider: SerializerProvider?) {
        originalSerializer.serialize(value, gen, provider)
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
                DisableTypeSerializer(contextualOriginalSerializer)
            } else {
                this
            }
}

class TypeSerializerOverride<T>(
        targetClass: Class<T>,
        private val originalSerializer: JsonSerializer<in T>
): StdSerializer<T>(targetClass), ContextualSerializer {
    override fun serialize(value: T?, gen: JsonGenerator?, provider: SerializerProvider?) {
        throw IllegalStateException("TypeSerializerOverride must be contextualized before use.")
    }

    override fun createContextual(prov: SerializerProvider, property: BeanProperty?): JsonSerializer<*> {
        val javaType = prov.constructType(_handledType)
        val typeSerializer = prov.findTypeSerializer(javaType)
        @Suppress("UNCHECKED_CAST")
        val contextualSerializer = if (originalSerializer is ContextualSerializer) {
            originalSerializer.createContextual(prov, property) as JsonSerializer<in T>
        } else {
            originalSerializer
        }
        return Contextual(_handledType, contextualSerializer, typeSerializer)
    }

    class Contextual<T>(
            targetClass: Class<T>,
            private val originalSerializer: JsonSerializer<in T>,
            private val typeSer: TypeSerializer
    ): StdSerializer<T>(targetClass), ContextualSerializer {
        override fun serialize(value: T?, gen: JsonGenerator?, provider: SerializerProvider?) {
            originalSerializer.serializeWithType(value, gen, provider, typeSer)
        }

        override fun serializeWithType(
                value: T, gen: JsonGenerator?, serializers: SerializerProvider?, typeSer: TypeSerializer?
        ) {
            serialize(value, gen, serializers)
        }

        override fun createContextual(prov: SerializerProvider?, property: BeanProperty?): JsonSerializer<*> =
                if (originalSerializer is ContextualSerializer) {
                    @Suppress("UNCHECKED_CAST")
                    val contextualSerializer =
                            originalSerializer.createContextual(prov, property) as JsonSerializer<in T>
                    Contextual(_handledType, contextualSerializer, typeSer)
                } else {
                    this
                }
    }
}

class FalseValueFilter {
    override fun equals(other: Any?): Boolean = other == false

    override fun hashCode(): Int = false.hashCode()
}

class ZeroValueFilter {
    override fun equals(other: Any?): Boolean = other == 0

    override fun hashCode(): Int = 0.hashCode()
}