package hu.bme.mit.inf.jani.model.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import hu.bme.mit.inf.jani.model.SimpleType
import hu.bme.mit.inf.jani.model.Type

object JaniModelBeanDeserializerModifier : BeanDeserializerModifier() {
    @Suppress("UNCHECKED_CAST")
    override fun modifyDeserializer(
            config: DeserializationConfig, beanDesc: BeanDescription, deserializer: JsonDeserializer<*>
    ): JsonDeserializer<*> = when (beanDesc.beanClass) {
        Type::class.java -> TypeDeserializer(deserializer as JsonDeserializer<out Type>)
        else -> deserializer
    }
}

class SimpleTypeDeserializer : StdDeserializer<SimpleType>(SimpleType::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): SimpleType? {
        if (p.currentToken != JsonToken.VALUE_STRING) {
            ctxt.reportWrongTokenException(handledType(), JsonToken.VALUE_STRING,
                    "Expected VALUE_STRING for SimpleType")
            return null
        }
        val simpleTypeName = p.text
        return SimpleType.fromName(simpleTypeName)
    }
}

class TypeDeserializer(
        private val originalDeserializer: JsonDeserializer<out Type>,
        private val simpleTypeDeserializer: JsonDeserializer<out SimpleType>? = null
) : StdDeserializer<Type>(Type::class.java), ContextualDeserializer {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Type? =
            originalDeserializer.deserialize(p, ctxt)

    override fun deserializeWithType(
            p: JsonParser, ctxt: DeserializationContext?,
            typeDeserializer: com.fasterxml.jackson.databind.jsontype.TypeDeserializer?
    ): Any? = when (p.currentToken) {
        JsonToken.VALUE_STRING -> simpleTypeDeserializer!!.deserialize(p, ctxt)
        else -> originalDeserializer.deserializeWithType(p, ctxt, typeDeserializer)
    }

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> {
        @Suppress("UNCHECKED_CAST")
        val contextualOriginalDeserializer = if (originalDeserializer is ContextualDeserializer) {
            originalDeserializer.createContextual(ctxt, property) as JsonDeserializer<out Type>
        } else {
            originalDeserializer
        }

        val simpleTypeType = ctxt.constructType(SimpleType::class.java)
        @Suppress("UNCHECKED_CAST")
        val contextualSimpleTypeDeserializer =
                ctxt.findContextualValueDeserializer(simpleTypeType, property) as JsonDeserializer<SimpleType>

        return TypeDeserializer(contextualOriginalDeserializer, contextualSimpleTypeDeserializer)
    }
}
