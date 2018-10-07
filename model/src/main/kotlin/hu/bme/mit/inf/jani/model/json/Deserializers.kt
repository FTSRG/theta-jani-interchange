package hu.bme.mit.inf.jani.model.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.util.JsonParserSequence
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.util.TokenBuffer
import hu.bme.mit.inf.jani.model.*
import kotlin.reflect.full.createInstance

object JaniModelBeanDeserializerModifier : BeanDeserializerModifier() {
    @Suppress("UNCHECKED_CAST")
    override fun modifyDeserializer(
            config: DeserializationConfig, beanDesc: BeanDescription, deserializer: JsonDeserializer<*>
    ): JsonDeserializer<*> = when (beanDesc.beanClass) {
        Type::class.java -> TypeDeserializer(deserializer as JsonDeserializer<out Type>)
        // We must not use @JsonDeserialize(using = ExpressionDeserializer::class),
        // because it would be inherited to child classes and cause [StackOverflowError].
        Expression::class.java -> ExpressionDeserializer()
        else -> deserializer
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <T> JsonDeserializer<out T>.contextualize(
        ctxt: DeserializationContext, property: BeanProperty?
): JsonDeserializer<out T> = when (this) {
    is ContextualDeserializer -> createContextual(ctxt, property) as JsonDeserializer<out T>
    else -> this
}

@Suppress("UNCHECKED_CAST")
internal fun <T> DeserializationContext.findContextualValueDeserializer(
        javaClass: Class<out T>, beanProperty: BeanProperty?
): JsonDeserializer<out T> {
    val javaType = constructType(javaClass)
    return findContextualValueDeserializer(javaType, beanProperty) as JsonDeserializer<out T>
}

internal inline fun <reified T> DeserializationContext.findContextualValueDeserializer(beanProperty: BeanProperty?)
        : JsonDeserializer<out T> = findContextualValueDeserializer(T::class.java, beanProperty)

class TypeDeserializer(private val originalDeserializer: JsonDeserializer<out Type>) :
        StdDeserializer<Type>(Type::class.java), ContextualDeserializer {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Type? {
        throw IllegalStateException("TypeDeserializer must be contextualized before use")
    }

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> =
            Contextual(
                    originalDeserializer.contextualize(ctxt, property),
                    ctxt.findContextualValueDeserializer(property)
            )

    private class Contextual(
            private val originalDeserializer: JsonDeserializer<out Type>,
            private val simpleTypeDeserializer: JsonDeserializer<out SimpleType>
    ) : StdDeserializer<Type>(Type::class.java), ContextualDeserializer {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Type? =
                originalDeserializer.deserialize(p, ctxt)

        override fun deserializeWithType(
                p: JsonParser, ctxt: DeserializationContext?,
                typeDeserializer: com.fasterxml.jackson.databind.jsontype.TypeDeserializer?
        ): Any? = when (p.currentToken) {
            JsonToken.VALUE_STRING -> simpleTypeDeserializer.deserialize(p, ctxt)
            else -> originalDeserializer.deserializeWithType(p, ctxt, typeDeserializer)
        }

        override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> =
                if (originalDeserializer is ContextualDeserializer
                        || simpleTypeDeserializer is ContextualDeserializer) {
                    Contextual(
                            originalDeserializer.contextualize(ctxt, property),
                            simpleTypeDeserializer.contextualize(ctxt, property)
                    )
                } else {
                    this
                }
    }
}

class BasicNumericTypeDeserializer : StdDeserializer<BasicNumericType>(BasicNumericType::class.java),
        ContextualDeserializer {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): BasicNumericType? {
        throw IllegalStateException("BasicNumericType must be contextualized before use")
    }

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> =
            Contextual(ctxt.findContextualValueDeserializer(property))

    private class Contextual(val simpleTypeDeserializer: JsonDeserializer<out SimpleType>):
            StdDeserializer<BasicNumericType>(BasicNumericType::class.java), ContextualDeserializer {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): BasicNumericType? =
                checkType(simpleTypeDeserializer.deserialize(p, ctxt))

        override fun deserializeWithType(
                p: JsonParser?, ctxt: DeserializationContext?,
                typeDeserializer: com.fasterxml.jackson.databind.jsontype.TypeDeserializer?
        ): Any? = deserialize(p, ctxt)

        override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> =
                if (simpleTypeDeserializer is ContextualDeserializer) {
                    Contextual(simpleTypeDeserializer.contextualize(ctxt, property))
                } else {
                    this
                }

        private fun checkType(type: Any?): BasicNumericType? = when (type) {
            null -> null
            is BasicNumericType -> type
            else -> throw IllegalArgumentException("Expected IntType or RealType, got $type instead")
        }
    }
}

class ExpressionDeserializer : StdDeserializer<Expression>(Expression::class.java), ContextualDeserializer {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Expression? {
        throw IllegalStateException("ExpressionDeserializer must be contextualized before use")
    }

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> =
            Contextual(ExpressionDeserializers(ctxt, property))

    private class Contextual(private val deserializers: ExpressionDeserializers) :
            StdDeserializer<Expression>(Expression::class.java), ContextualDeserializer {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Expression? = when (p.currentToken) {
            JsonToken.VALUE_FALSE, JsonToken.VALUE_TRUE -> deserializers.boolConstantDeserializer.deserialize(p, ctxt)
            JsonToken.VALUE_NUMBER_INT -> deserializers.intConstantDeserializer.deserialize(p, ctxt)
            JsonToken.VALUE_NUMBER_FLOAT -> deserializers.realConstantDeserializer.deserialize(p, ctxt)
            JsonToken.VALUE_STRING -> deserializeString(p, ctxt)
            JsonToken.VALUE_NULL -> {
                p.nextToken()
                null
            }
            JsonToken.START_OBJECT, JsonToken.FIELD_NAME -> deserializeObject(p, ctxt)
            else -> {
                ctxt.reportWrongTokenException(this, JsonToken.START_OBJECT, "Malformed Expression")
                null
            }
        }

        private fun deserializeString(p: JsonParser, ctxt: DeserializationContext): Expression? =
                if (deserializers.isNamedConstant(p.text)) {
                    deserializers.namedConstantDeserializer.deserialize(p, ctxt)
                } else {
                    deserializers.identifierDeserializer.deserialize(p, ctxt)
                }

        private fun deserializeObject(p: JsonParser, ctxt: DeserializationContext): Expression? {
            var t = p.currentToken
            if (t == JsonToken.START_OBJECT) {
                t = p.nextToken()
            }
            var tokenBuffer: TokenBuffer? = null
            while (t == JsonToken.FIELD_NAME) {
                val fieldName = p.currentName
                p.nextToken()
                when (fieldName) {
                    Expression.OP_PROPERTY_NAME -> return deserializeOp(p, ctxt, tokenBuffer)
                    DistributionSampling.DISTRIBUTION_PROPERTY_NAME ->
                        return deserializeDistributionSampling(p, ctxt, tokenBuffer)
                    Named.NAME_PROPERTY_NAME -> return deserializeNamed(p, ctxt, tokenBuffer)
                    else -> {
                        if (tokenBuffer == null) {
                            tokenBuffer = TokenBuffer(p)
                        }
                        tokenBuffer.writeFieldName(fieldName)
                        tokenBuffer.copyCurrentStructure(p)
                    }
                }
                t = p.nextToken()
            }
            throw IllegalStateException("Malformed expression")
        }

        private fun deserializeOp(p: JsonParser, ctxt: DeserializationContext, tb: TokenBuffer?): Expression? {
            var tokenBuffer = tb
            val opName = p.text
            val (deserializer, explicitOpProperty) = deserializers.findSubtypeDeserializer(opName)
            if (explicitOpProperty) {
                if (tokenBuffer == null) {
                    tokenBuffer = TokenBuffer(p)
                }
                tokenBuffer.writeFieldName(Expression.OP_PROPERTY_NAME)
                tokenBuffer.writeString(opName)
            }
            val p2 = if (tokenBuffer == null) {
                p
            } else {
                p.clearCurrentToken()
                JsonParserSequence.createFlattened(false, tokenBuffer.asParser(p), p)
            }
            p2.nextToken()
            return deserializer.deserialize(p2, ctxt)
        }

        private fun deserializeNamed(p: JsonParser, ctxt: DeserializationContext, tb: TokenBuffer?): Expression? =
                deserializeWithField(p, ctxt, tb, Named.NAME_PROPERTY_NAME, deserializers.namedDeserializer)

        private fun deserializeDistributionSampling(
                p: JsonParser, ctxt: DeserializationContext, tb: TokenBuffer?
        ): Expression? = deserializeWithField(
                p, ctxt, tb, DistributionSampling.DISTRIBUTION_PROPERTY_NAME,
                deserializers.distributionSamplingDeserializer
        )

        private fun deserializeWithField(
                p: JsonParser, ctxt: DeserializationContext, tb: TokenBuffer?, fieldName: String,
                deserializer: JsonDeserializer<out Expression>
        ): Expression? {
            val tokenBuffer = tb ?: TokenBuffer(p)
            tokenBuffer.writeFieldName(fieldName)
            tokenBuffer.writeString(p.text)
            p.clearCurrentToken()
            val p2 = JsonParserSequence.createFlattened(false, tokenBuffer.asParser(p), p)
            p2.nextToken()
            return deserializer.deserialize(p2, ctxt)
        }

        override fun deserializeWithType(
                p: JsonParser, ctxt: DeserializationContext,
                typeDeserializer: com.fasterxml.jackson.databind.jsontype.TypeDeserializer?
        ): Any? = deserialize(p, ctxt)

        override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> =
                Contextual(deserializers.contextualize(ctxt, property))
    }

}

class ExpressionSubtypeDescription(ctxt: DeserializationContext) {
    private val namedConstantPredicate = ConversionPredicate.forBean(
            ctxt.config.introspectForCreation(ctxt.constructType(NamedConstant::class.java)), ctxt.config
    )
    private val opToSubtypeMap: MutableMap<String, Class<*>> = HashMap()
    private val predicatedSubtypes: MutableList<PredicatedSubtype> = ArrayList()

    init {
        // No contextualization support for now.
        val config = ctxt.config
        val annotationIntrospector = config.annotationIntrospector
        val beanDesc = config.introspectClassAnnotations(Expression::class.java)

        for (subtype in annotationIntrospector.findSubtypes(beanDesc.classInfo)) {
            val subtypeDesc = config.introspectClassAnnotations(subtype.type)
            if (subtypeDesc.classInfo.hasAnnotation(JaniJsonMultiOp::class.java)) {
                val conversionPredicate = findConversionPredicate(subtypeDesc, config)
                predicatedSubtypes += PredicatedSubtype(conversionPredicate, subtype.type)
            } else {
                val name = subtype.name ?: annotationIntrospector.findTypeName(subtypeDesc.classInfo)
                opToSubtypeMap[name] = subtype.type
            }
        }
    }

    fun isNamedConstant(identifier: String): Boolean = namedConstantPredicate.canConvert(identifier)

    fun getSubtypeInfo(opName: String): SubtypeInfo {
        val singleOpSubtypeClass = opToSubtypeMap[opName]
        if (singleOpSubtypeClass != null) {
            return SubtypeInfo(singleOpSubtypeClass, false)
        }

        val predicatedSubtypeClass = predicatedSubtypes.first { it.predicate.canConvert(opName) }.subtypeClass
        return SubtypeInfo(predicatedSubtypeClass, true)
    }

    private fun findConversionPredicate(beanDesc: BeanDescription, config: DeserializationConfig): ConversionPredicate {
        val annotation = beanDesc.classInfo.getAnnotation(JaniJsonMultiOp::class.java)
        val predicateClass = annotation.predicate

        return if (predicateClass == ConversionPredicate.None::class) {
            // Create conversion predicate according to "op" property.

            // First re-introspect, now taking properties into account.
            val fullBeanDesc = config.introspectForCreation<BeanDescription>(beanDesc.type)
            val opProperty = fullBeanDesc.findProperties().firstOrNull { it.name == Expression.OP_PROPERTY_NAME }
                    ?: throw IllegalArgumentException("${beanDesc.classInfo.name} has no " +
                            "${Expression.OP_PROPERTY_NAME} property")
            val propertyTypeDesc = config.introspect<BeanDescription>(opProperty.primaryType)

            ConversionPredicate.forBean(propertyTypeDesc, config)
        } else {
            predicateClass.createInstance()
        }
    }

    private data class PredicatedSubtype(val predicate: ConversionPredicate, val subtypeClass: Class<*>)

    data class SubtypeInfo(val subtypeClass: Class<*>, val explicitOpProperty: Boolean)
}

class ExpressionDeserializers private constructor(
        private val subtypeDescription: ExpressionSubtypeDescription,
        private val ctxt: DeserializationContext,
        private val beanProperty: BeanProperty?,
        private val deserializersMap: MutableMap<Class<*>, JsonDeserializer<out Expression>> = HashMap()
) {
    init {
        for (specialSubtype in ExpressionDeserializers.subtypesWithSpecialHandling) {
            if (!deserializersMap.containsKey(specialSubtype)) {
                deserializersMap[specialSubtype] = ctxt.findContextualValueDeserializer(specialSubtype, beanProperty)
            }
        }

        // Deserialize DistributionSampling subtypes according to their distribution field, not their op field.
        if (!deserializersMap.containsKey(DistributionSampling::class.java)) {
            val distributionSamplingType = ctxt.constructType(DistributionSampling::class.java)
            @Suppress("UNCHECKED_CAST")
            val distributionSamplingDeserializer = ctxt.findContextualValueDeserializer(
                    distributionSamplingType, null
            ) as JsonDeserializer<out DistributionSampling>
            val distributionSamplingTypeDeserializer = ctxt.config.findTypeDeserializer(distributionSamplingType)
            deserializersMap[DistributionSampling::class.java] = TypeDeserializerOverride(
                    DistributionSampling::class.java, distributionSamplingDeserializer,
                    distributionSamplingTypeDeserializer
            )
        }
    }

    constructor(ctxt: DeserializationContext, beanProperty: BeanProperty?) : this(
            ExpressionSubtypeDescription(ctxt), ctxt, beanProperty
    )

    val boolConstantDeserializer: JsonDeserializer<out Expression>
        get() = deserializersMap[BoolConstant::class.java]!!

    val intConstantDeserializer: JsonDeserializer<out Expression>
        get() = deserializersMap[IntConstant::class.java]!!

    val realConstantDeserializer: JsonDeserializer<out Expression>
        get() = deserializersMap[RealConstant::class.java]!!

    val namedConstantDeserializer: JsonDeserializer<out Expression>
        get() = deserializersMap[NamedConstant::class.java]!!

    val identifierDeserializer: JsonDeserializer<out Expression>
        get() = deserializersMap[Identifier::class.java]!!

    val distributionSamplingDeserializer: JsonDeserializer<out Expression>
        get() = deserializersMap[DistributionSampling::class.java]!!

    val namedDeserializer: JsonDeserializer<out Expression>
        get() = deserializersMap[Named::class.java]!!

    fun isNamedConstant(identifier: String): Boolean = subtypeDescription.isNamedConstant(identifier)

    fun findSubtypeDeserializer(opName: String): SubtypeDeserializer {
        val (subtype, explicitOpProperty) = subtypeDescription.getSubtypeInfo(opName)
        val deserializer = deserializersMap.computeIfAbsent(subtype) {
            @Suppress("UNCHECKED_CAST")
            ctxt.findContextualValueDeserializer(it as Class<out Expression>, beanProperty)
        }
        return SubtypeDeserializer(deserializer, explicitOpProperty)
    }

    fun contextualize(ctxt: DeserializationContext, beanProperty: BeanProperty?): ExpressionDeserializers {
        val nonContextualDeserializersMap = HashMap<Class<*>, JsonDeserializer<out Expression>>()
        deserializersMap.filterTo(nonContextualDeserializersMap) { it.value !is ContextualDeserializer }
        return ExpressionDeserializers(
                subtypeDescription, ctxt, beanProperty, nonContextualDeserializersMap
        )
    }

    companion object {
        val subtypesWithSpecialHandling: List<Class<out Expression>> = listOf(
                BoolConstant::class.java, IntConstant::class.java, RealConstant::class.java, NamedConstant::class.java,
                Identifier::class.java, Named::class.java
        )
    }

    data class SubtypeDeserializer(val deserializer: JsonDeserializer<out Expression>, val explicitOpProperty: Boolean)
}

class TypeDeserializerOverride<T>(
        targetClass: Class<out T>,
        private val deserializer: JsonDeserializer<out T>,
        private val typeDeserializer: com.fasterxml.jackson.databind.jsontype.TypeDeserializer
) : StdDeserializer<T>(targetClass) {
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): T? =
            deserializer.deserializeWithType(p, ctxt, typeDeserializer) as T?

    override fun deserializeWithType(
            p: JsonParser?, ctxt: DeserializationContext?,
            typeDeserializer: com.fasterxml.jackson.databind.jsontype.TypeDeserializer?
    ): Any? = deserialize(p, ctxt)
}

class StatePredicateDeserializer : StdDeserializer<StatePredicate>(StatePredicate::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): StatePredicate? {
        if (p.currentToken == JsonToken.START_OBJECT) {
            p.nextToken()
        }
        if (p.currentToken != JsonToken.FIELD_NAME || p.currentName != Expression.OP_PROPERTY_NAME) {
            ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, "Expected FIELD_NAME" +
                    "\"${Expression.OP_PROPERTY_NAME}\"")
            return null
        }
        p.nextToken()
        if (p.currentToken != JsonToken.VALUE_STRING) {
            ctxt.reportWrongTokenException(this, JsonToken.VALUE_STRING, "Expected VALUE_STRING StatePredicate name")
            return null
        }
        val statePredicate = StatePredicate.fromPredicateName(p.text)
        p.nextToken()
        if (p.currentToken != JsonToken.END_OBJECT) {
            ctxt.reportWrongTokenException(this, JsonToken.VALUE_STRING, "Expected END_OBJECT")
            return null
        }
        return statePredicate
    }

}