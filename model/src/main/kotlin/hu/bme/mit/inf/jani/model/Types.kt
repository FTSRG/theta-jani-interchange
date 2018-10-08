package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes(
        // [SimpleType] is deliberately not included, we handle it in [json.TypeDeserializer] separately.
        JsonSubTypes.Type(BoundedType::class),
        JsonSubTypes.Type(ArrayType::class),
        JsonSubTypes.Type(DatatypeType::class),
        JsonSubTypes.Type(OptionType::class)
)
interface Type {
    val numeric: Boolean
        @JsonIgnore get() = false

    fun isAssignableFrom(sourceType: Type): Boolean
}

sealed class SimpleType(@get:JsonValue val name: String, override val numeric: Boolean) : Type {
    override fun toString(): String = javaClass.simpleName

    companion object {
        // Lazy to delay initialization until the [SimpleType] class was initialized and its instances were constructed.
        private val simpleTypeByNameMap by lazy {
            val simpleTypes = listOf(BoolType, IntType, RealType, ClockType, ContinuousType)
            simpleTypes.map { it.name to it }.toMap()
        }

        @JvmStatic
        @JsonCreator
        fun fromName(name: String): SimpleType =
                simpleTypeByNameMap[name] ?: throw IllegalArgumentException("Unknown SimpleType: $name")
    }
}

sealed class BasicType(name: String, numeric: Boolean) : SimpleType(name, numeric)

object BoolType : BasicType("bool", false) {
    override fun isAssignableFrom(sourceType: Type): Boolean = sourceType == BoolType
}

sealed class BasicNumericType(name: String) : BasicType(name, true)

object IntType : BasicNumericType("int") {
    override fun isAssignableFrom(sourceType: Type): Boolean = when (sourceType) {
        IntType -> true
        is BoundedType -> sourceType.base == IntType
        else -> false
    }
}

object RealType : BasicNumericType("real") {
    override fun isAssignableFrom(sourceType: Type): Boolean = sourceType.numeric
}

object ClockType : SimpleType("clock", true) {
    override fun isAssignableFrom(sourceType: Type): Boolean = sourceType.numeric
}

object ContinuousType : SimpleType("continuous", true) {
    override fun isAssignableFrom(sourceType: Type): Boolean = sourceType.numeric
}

@JsonTypeName("bounded")
data class BoundedType(
        val base: BasicNumericType, val lowerBound: Expression? = null, val upperBound: Expression? = null
) : Type {
    override val numeric
        get() = base.numeric

    override fun isAssignableFrom(sourceType: Type): Boolean = base.isAssignableFrom(sourceType)
}

@JsonTypeName("array")
@JaniExtension(ModelFeature.ARRAYS)
data class ArrayType @JsonCreator constructor(val base: Type) : Type {
    override fun isAssignableFrom(sourceType: Type): Boolean =
            sourceType is ArrayType && base.isAssignableFrom(sourceType.base)
}

@JaniExtension(ModelFeature.DATATYPES)
data class DatatypeDefinition constructor(val name: String, val members: List<DatatypeMember> = emptyList())

@JaniExtension(ModelFeature.DATATYPES)
data class DatatypeMember(val name: String, val type: Type)

@JsonTypeName("datatype")
@JaniExtension(ModelFeature.DATATYPES)
data class DatatypeType @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(val ref: String) : Type {
    override fun isAssignableFrom(sourceType: Type): Boolean = sourceType is DatatypeType && ref == sourceType.ref
}

@JsonTypeName("option")
@JaniExtension(ModelFeature.DATATYPES)
data class OptionType @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(val base: Type) : Type {
    override fun isAssignableFrom(sourceType: Type): Boolean =
            base.isAssignableFrom(sourceType) || (sourceType is ArrayType && base.isAssignableFrom(sourceType.base))
}
