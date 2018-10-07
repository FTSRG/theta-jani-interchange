package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import hu.bme.mit.inf.jani.model.json.*

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = Expression.OP_PROPERTY_NAME
)
@JsonSubTypes(
        // [ConstantValue], [Identifier], [DistributionSampling] and [Named] are omitted,
        // because they need special handling.
        JsonSubTypes.Type(Ite::class),
        JsonSubTypes.Type(UnaryExpression::class),
        JsonSubTypes.Type(BinaryExpression::class),
        JsonSubTypes.Type(FilterExpression::class),
        JsonSubTypes.Type(Expectation::class),
        JsonSubTypes.Type(StatePredicate::class),
        JsonSubTypes.Type(BinaryPathExpression::class),
        JsonSubTypes.Type(ArrayAccess::class),
        JsonSubTypes.Type(ArrayValue::class),
        JsonSubTypes.Type(ArrayConstructor::class),
        JsonSubTypes.Type(DatatypeMemberAccess::class),
        JsonSubTypes.Type(DatatypeValue::class),
        JsonSubTypes.Type(OptionValueAccess::class),
        JsonSubTypes.Type(OptionValue::class),
        JsonSubTypes.Type(EmptyOption::class),
        JsonSubTypes.Type(UnaryPathExpression::class),
        JsonSubTypes.Type(Call::class),
        JsonSubTypes.Type(Nondet::class)
)
interface Expression {
    companion object {
        const val OP_PROPERTY_NAME = "op"
    }
}

interface LValue : Expression

interface ConstantValue : Expression

data class IntConstant @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(
        @get:JsonValue val value: Int
) : ConstantValue

data class RealConstant @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(
        @get:JsonValue val value: Double
) : ConstantValue

enum class BoolConstant(@get:JsonValue val value: Boolean) : ConstantValue {
    FALSE(false),
    TRUE(true);

    companion object {
        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun fromBoolean(value: Boolean): BoolConstant = if (value) {
            TRUE
        } else {
            FALSE
        }
    }
}

enum class NamedConstant(@get:JsonValue val constantName: String, val value: Double) : ConstantValue {
    E("e", Math.E),
    PI("π", Math.PI)
}

data class Identifier @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(
        @get:JsonValue val name: String
) : LValue

@JsonTypeName("ite")
data class Ite(
        @param:JsonProperty("if") @get:JsonProperty("if") val condition: Expression,
        @param:JsonProperty("then") @get:JsonProperty("then") val thenExp: Expression,
        @param:JsonProperty("else") @get:JsonProperty("else") val elseExp: Expression
) : Expression

interface NamedOpLike {
    @get:JsonValue
    val opName: String
}

abstract class OpRegistry<out T : NamedOpLike>(private val type: String) {
    abstract val namedOps: Iterable<T>

    private val nameToOpMap by lazy {
        namedOps.map { it.opName to it }.toMap()
    }

    fun hasOp(opName: String): Boolean = nameToOpMap.containsKey(opName)

    fun fromOpName(opName: String): T =
            nameToOpMap[opName] ?: throw IllegalArgumentException("Unknown $type operator: $opName")
}

@JsonDeserialize(converter = UnaryOpLikeConverter::class)
interface UnaryOpLike : NamedOpLike {
    fun of(exp: Expression): UnaryExpression = UnaryExpression(this, exp)

    companion object : OpRegistry<UnaryOpLike>("unary") {
        override val namedOps: Iterable<UnaryOpLike>
            get() = arrayOf<Array<out UnaryOpLike>>(
                    UnaryOp.values(), ProbabilityOp.values(), PathQuantifier.values(), SteadyStateOp.values(),
                    DerivedUnaryOp.values(), HyperbolicOp.values(), TrigonometricOp.values()
            ).flatten()
    }
}

enum class UnaryOp(override val opName: String) : UnaryOpLike {
    NOT("¬"),
    FLOOR("floor"),
    CEIL("ceil"),
    DER("der")
}

@JaniJsonMultiOp
data class UnaryExpression(val op: UnaryOpLike, val exp: Expression) : Expression

@JsonDeserialize(converter = BinaryOpLikeConverter::class)
interface BinaryOpLike : NamedOpLike {
    fun of(left: Expression, right: Expression): BinaryExpression = BinaryExpression(this, left, right)

    companion object : OpRegistry<BinaryOpLike>("binary") {
        override val namedOps: Iterable<BinaryOpLike>
            get() = arrayOf<Array<out BinaryOpLike>>(
                    BinaryOp.values(), DerivedBinaryOp.values()
            ).flatten()
    }
}

enum class BinaryOp(override val opName: String) : BinaryOpLike {
    OR("∨"),
    AND("∧"),
    EQ("="),
    NEQ("≠"),
    LT("<"),
    LEQ("≤"),
    ADD("+"),
    SUB("-"),
    MUL("*"),
    MOD("%"),
    DIV("/"),
    POW("pow"),
    LOG("log")
}

@JaniJsonMultiOp
data class BinaryExpression(val op: BinaryOpLike, val left: Expression, val right: Expression) : Expression

@JsonTypeName("filter")
data class FilterExpression(
        @param:JsonProperty("fun") @get:JsonProperty("fun") val function: Filter,
        val values: Expression, val states: Expression
) : Expression

enum class Filter(@get:JsonValue val functionName: String) {
    MIN("min"),
    MAX("max"),
    SUM("sum"),
    AVG("avg"),
    COUNT("count"),
    FORALL("∀"),
    EXISTS("∃"),
    ARGMIN("argmin"),
    ARGMAX("argmax"),
    VALUES("values");

    fun of(values: Expression, states: Expression): FilterExpression = FilterExpression(this, values, states)
}

enum class ExtremeValue {
    MIN,
    MAX
}

enum class ProbabilityOp(val extremeValue: ExtremeValue) : UnaryOpLike {
    MIN(ExtremeValue.MIN),
    MAX(ExtremeValue.MAX);

    override val opName: String = "P${name.toLowerCase()}"
}

enum class PathQuantifier(override val opName: String) : UnaryOpLike {
    FORALL("∀"),
    EXISTS("∃")
}

enum class ExpectationOp(val extremeValue: ExtremeValue) : NamedOpLike {
    MIN(ExtremeValue.MIN),
    MAX(ExtremeValue.MAX);

    override val opName: String = "E${name.toLowerCase()}"

    fun of(
            exp: Expression, accumulate: RewardAccumulation? = null, reach: Expression? = null,
            stepInstant: Expression? = null, timeInstant: Expression? = null,
            rewardInstants: List<RewardInstant> = emptyList()
    ): Expectation = Expectation(
            this, exp, accumulate, reach, stepInstant, timeInstant, rewardInstants
    )
}

@JaniJsonMultiOp
data class Expectation(
        val op: ExpectationOp, val exp: Expression, val accumulate: RewardAccumulation?,
        val reach: Expression?, val stepInstant: Expression?, val timeInstant: Expression?,
        val rewardInstants: List<RewardInstant>
) : Expression

data class RewardInstant(val exp: Expression, val accumulate: RewardAccumulation, val instant: Expression)

enum class RewardAccumulation {
    @JsonProperty("steps")
    STEPS,

    @JsonProperty("time")
    TIME,

    @JsonProperty("exit")
    @JaniExtension(ModelFeature.STATE_EXIT_REWARDS)
    EXIT;
}

enum class SteadyStateOp(val extremeValue: ExtremeValue) : UnaryOpLike {
    MIN(ExtremeValue.MIN),
    MAX(ExtremeValue.MAX);

    override val opName: String = "S${name.toLowerCase()}"
}

interface PathExpression : Expression {
    val stepBounds: PropertyInterval?
    val timeBounds: PropertyInterval?
    val rewardBounds: List<RewardBound>
}

data class RewardBound(val exp: Expression, val accumulate: RewardAccumulation, val bounds: PropertyInterval)

data class PropertyInterval(
        val lower: Expression? = null,
        @get:JsonInclude(JsonInclude.Include.CUSTOM, valueFilter = FalseValueFilter::class)
        val lowerExclusive: Boolean = false,
        val upper: Expression? = null,
        @get:JsonInclude(JsonInclude.Include.CUSTOM, valueFilter = FalseValueFilter::class)
        val upperExclusive: Boolean = false
)

@JsonDeserialize(converter = BinaryPathOpLikeConverter::class)
interface BinaryPathOpLike : NamedOpLike {
    fun of(
            left: Expression, right: Expression, stepBounds: PropertyInterval? = null,
            timeBounds: PropertyInterval? = null, rewardBounds: List<RewardBound> = emptyList()
    ): BinaryPathExpression = BinaryPathExpression(this, left, right, stepBounds, timeBounds, rewardBounds)

    companion object : OpRegistry<BinaryPathOpLike>("binary path") {
        override val namedOps: Iterable<BinaryPathOpLike>
            get() = arrayOf<Array<out BinaryPathOpLike>>(
                    BinaryPathOp.values(), DerivedBinaryPathOp.values()
            ).flatten()
    }
}

enum class BinaryPathOp : BinaryPathOpLike {
    U, W;

    override val opName: String
        get() = name
}

@JaniJsonMultiOp
data class BinaryPathExpression(
        val op: BinaryPathOpLike, val left: Expression, val right: Expression,
        override val stepBounds: PropertyInterval? = null, override val timeBounds: PropertyInterval? = null,
        override val rewardBounds: List<RewardBound> = emptyList()
) : PathExpression

@JaniJsonMultiOp(predicate = StatePredicateConversionPredicate::class)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
// The declaring-class property shows up spuriously at serialization unless it is ignored.
@JsonIgnoreProperties("declaring-class")
@JsonDeserialize(using = StatePredicateDeserializer::class)
enum class StatePredicate(@get:JsonProperty(Expression.OP_PROPERTY_NAME) val predicateName: String) : Expression {
    INITIAL("initial"),
    DEADLOCK("deadlock"),
    TIMELOCK("timelock");

    companion object {
        private val namesToPredicatesMap = values().map { it.predicateName to it }.toMap()

        fun isStatePredicate(predicateName: String): Boolean = namesToPredicatesMap.containsKey(predicateName)

        @JvmStatic
        fun fromPredicateName(@JsonProperty(Expression.OP_PROPERTY_NAME) predicateName: String): StatePredicate =
                namesToPredicatesMap[predicateName]
                        ?: throw IllegalArgumentException("Unknown state predicate: $predicateName")
    }
}

@JsonTypeName("aa")
@JaniExtension(ModelFeature.ARRAYS)
data class ArrayAccess(val exp: Expression, val index: Expression) : LValue

@JsonTypeName("av")
@JaniExtension(ModelFeature.ARRAYS)
data class ArrayValue @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
        @get:JsonInclude(JsonInclude.Include.ALWAYS) val elements: List<Expression>
) : Expression {
    constructor(vararg elements: Expression) : this(elements.toList())
}

@JsonTypeName("ac")
@JaniExtension(ModelFeature.ARRAYS)
data class ArrayConstructor(
        @param:JsonProperty("var") @get:JsonProperty("var") val varName: String,
        val length: Expression, val exp: Expression
) : Expression

@JsonTypeName("da")
@JaniExtension(ModelFeature.DATATYPES)
data class DatatypeMemberAccess(val exp: Expression, val member: String) : LValue

@JsonTypeName("dv")
@JaniExtension(ModelFeature.DATATYPES)
data class DatatypeValue(
        val type: String,
        @get:JsonInclude(JsonInclude.Include.ALWAYS) val values: List<DatatypeMemberValue>
) : Expression

@JaniExtension(ModelFeature.DATATYPES)
data class DatatypeMemberValue(val member: String, val value: Expression)

@JsonTypeName("oa")
@JaniExtension(ModelFeature.DATATYPES)
data class OptionValueAccess(val exp: Expression) : LValue

@JsonTypeName("ov")
@JaniExtension(ModelFeature.DATATYPES)
data class OptionValue(val exp: Expression) : Expression

@JsonTypeName("empty")
@JaniExtension(ModelFeature.DATATYPES)
object EmptyOption : Expression {
    override fun toString(): String = javaClass.simpleName

    // Make sure we always get the same singleton instance upon deserialization.
    @Suppress("unused")
    @JvmStatic
    val instance: EmptyOption
        @JsonCreator get() = EmptyOption
}

@JaniExtension(ModelFeature.DERIVED_OPERATORS)
enum class DerivedUnaryOp : UnaryOpLike {
    ABS, SGN, TRC;

    override val opName: String = name.toLowerCase()
}

@JaniExtension(ModelFeature.DERIVED_OPERATORS)
enum class DerivedBinaryOp(override val opName: String) : BinaryOpLike {
    IMPLIES("⇒"),
    GT(">"),
    GEQ("≥"),
    MIN("min"),
    MAX("max")
}

enum class DerivedBinaryPathOp : BinaryPathOpLike {
    R;

    override val opName: String
        get() = name
}

@JaniExtension(ModelFeature.DERIVED_OPERATORS)
enum class UnaryPathOp {
    F, G;

    fun of(
            exp: Expression, stepBounds: PropertyInterval? = null, timeBounds: PropertyInterval? = null,
            rewardBounds: List<RewardBound> = emptyList()
    ): UnaryPathExpression = UnaryPathExpression(this, exp, stepBounds, timeBounds, rewardBounds)
}

@JaniJsonMultiOp
@JaniExtension(ModelFeature.DERIVED_OPERATORS)
data class UnaryPathExpression(
        val op: UnaryPathOp, val exp: Expression, override val stepBounds: PropertyInterval? = null,
        override val timeBounds: PropertyInterval? = null, override val rewardBounds: List<RewardBound> = emptyList()
) : PathExpression

@JsonTypeName("call")
@JaniExtension(ModelFeature.FUNCTIONS)
data class Call(
        val function: String,
        @get:JsonInclude(JsonInclude.Include.ALWAYS) val args: List<Expression>
) : Expression

@JaniExtension(ModelFeature.HYPERBOLIC_FUNCTIONS)
enum class HyperbolicOp : UnaryOpLike {
    SINH, COSH, TANH, COTH, SECH, CSCH, ASINH, ACOSH, ATANH, ACOTH, ASECH, ACSCH;

    override val opName: String = name.toLowerCase()
}

@JaniExtension(ModelFeature.NAMED_EXPRESSIONS)
data class Named(val name: String, val exp: Expression) : Expression {
    companion object {
        const val NAME_PROPERTY_NAME = "name"
    }
}

@JsonTypeName("nondet")
@JaniExtension(ModelFeature.NONDET_SELECTION)
data class Nondet(
        @param:JsonProperty("var") @get:JsonProperty("var") val varName: String,
        val exp: Expression
) : Expression

@JaniExtension(ModelFeature.HYPERBOLIC_FUNCTIONS)
enum class TrigonometricOp : UnaryOpLike {
    SIN, COS, TAN, COT, SEC, CSC, ASIN, ACOS, ATAN, ACOT, ASEC, ACSC;

    override val opName: String = name.toLowerCase()
}