package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.annotation.*

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

enum class SimpleType(val basic: Boolean, override val numeric: Boolean) : Type {
    @JsonProperty("bool")
    BOOL(true, false) {
        override fun isAssignableFrom(sourceType: Type): Boolean = sourceType == BOOL
    },

    @JsonProperty("int")
    INT(true, true) {
        override fun isAssignableFrom(sourceType: Type): Boolean = when (sourceType) {
            INT -> true
            is BoundedType -> sourceType.base == INT
            else -> false
        }
    },

    @JsonProperty("real")
    REAL(true, true),

    @JsonProperty("clock")
    CLOCK(false, true),

    @JsonProperty("continuous")
    CONTINUOUS(false, true);

    override fun isAssignableFrom(sourceType: Type): Boolean = sourceType.numeric
}

@JsonTypeName("bounded")
data class BoundedType(val base: Type, val lowerBound: Expression? = null, val upperBound: Expression? = null) : Type {
    override val numeric
        get() = base.numeric

    override fun isAssignableFrom(sourceType: Type): Boolean = base.isAssignableFrom(sourceType)
}

@JsonTypeName("array")
data class ArrayType @JsonCreator constructor(val base: Type) : Type {
    override fun isAssignableFrom(sourceType: Type): Boolean =
            sourceType is ArrayType && base.isAssignableFrom(sourceType.base)
}

data class DatatypeDefinition constructor(val name: String, val members: List<DatatypeMember> = emptyList())

data class DatatypeMember(val name: String, val type: Type)

@JsonTypeName("datatype")
data class DatatypeType @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(val ref: String) : Type {
    override fun isAssignableFrom(sourceType: Type): Boolean = sourceType is DatatypeType && ref == sourceType.ref
}

@JsonTypeName("option")
data class OptionType @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(val base: Type) : Type {
    override fun isAssignableFrom(sourceType: Type): Boolean =
            base.isAssignableFrom(sourceType) || (sourceType is ArrayType && base.isAssignableFrom(sourceType.base))
}
