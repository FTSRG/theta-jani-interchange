package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import hu.bme.mit.inf.jani.model.json.*

typealias PropertyExpression = Expression

@JsonTypeName("filter")
data class FilterExpression(
        @param:JsonProperty("fun") @get:JsonProperty("fun") val function: Filter,
        val values: PropertyExpression, val states: PropertyExpression
) : PropertyExpression

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

    fun of(values: PropertyExpression, states: PropertyExpression): FilterExpression =
            FilterExpression(this, values, states)
}

@JaniJsonMultiOp
data class UnaryPropertyExpression(val op: UnaryPropertyOpLike, val exp: PropertyExpression) : PropertyExpression

enum class ExtremeValue {
    MIN,
    MAX
}

enum class ProbabilityOp(val extremeValue: ExtremeValue) : UnaryPropertyOpLike {
    MIN(ExtremeValue.MIN),
    MAX(ExtremeValue.MAX);

    override val opName: String = "P${name.toLowerCase()}"
}

enum class PathQuantifier(override val opName: String) : UnaryPropertyOpLike {
    FORALL("∀"),
    EXISTS("∃")
}

enum class ExpectationOp(val extremeValue: ExtremeValue) : NamedOpLike {
    MIN(ExtremeValue.MIN),
    MAX(ExtremeValue.MAX);

    override val opName: String = "E${name.toLowerCase()}"

    fun of(
            exp: Expression, accumulate: Set<RewardAccumulation> = emptySet(), reach: PropertyExpression? = null,
            stepInstant: Expression? = null, timeInstant: Expression? = null,
            rewardInstants: List<RewardInstant> = emptyList()
    ): Expectation = Expectation(
            this, exp, accumulate, reach, stepInstant, timeInstant, rewardInstants
    )
}

@JaniJsonMultiOp
data class Expectation(
        val op: ExpectationOp, val exp: Expression, val accumulate: Set<RewardAccumulation>,
        val reach: PropertyExpression?, val stepInstant: Expression?, val timeInstant: Expression?,
        val rewardInstants: List<RewardInstant>
) : PropertyExpression

data class RewardInstant(
        val exp: Expression, val accumulate: Set<RewardAccumulation>, val instant: Expression
)

enum class RewardAccumulation {
    @JsonProperty("steps")
    STEPS,

    @JsonProperty("time")
    TIME,

    @JsonProperty("exit")
    @JaniExtension(ModelFeature.STATE_EXIT_REWARDS)
    EXIT;
}

enum class SteadyStateOp(val extremeValue: ExtremeValue) : UnaryPropertyOpLike {
    MIN(ExtremeValue.MIN),
    MAX(ExtremeValue.MAX);

    override val opName: String = "S${name.toLowerCase()}"
}

interface PathExpression : PropertyExpression {
    val stepBounds: PropertyInterval?
    val timeBounds: PropertyInterval?
    val rewardBounds: List<RewardBound>
}

data class RewardBound(val exp: Expression, val accumulate: Set<RewardAccumulation>, val bounds: PropertyInterval)

data class PropertyInterval(
        val lower: Expression? = null,
        @get:JsonInclude(JsonInclude.Include.CUSTOM, valueFilter = FalseValueFilter::class)
        val lowerExclusive: Boolean = false,
        val upper: Expression? = null,
        @get:JsonInclude(JsonInclude.Include.CUSTOM, valueFilter = FalseValueFilter::class)
        val upperExclusive: Boolean = false
)

enum class BinaryPathOp : BinaryPathOpLike {
    U, W;

    override val opName: String
        get() = name
}

@JaniJsonMultiOp
data class BinaryPathExpression(
        val op: BinaryPathOpLike, val left: PropertyExpression, val right: PropertyExpression,
        override val stepBounds: PropertyInterval? = null, override val timeBounds: PropertyInterval? = null,
        override val rewardBounds: List<RewardBound> = emptyList()
) : PathExpression

@JaniJsonMultiOp(predicate = StatePredicateConversionPredicate::class)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
// The declaring-class property shows up spuriously at serialization unless it is ignored.
@JsonIgnoreProperties("declaring-class")
@JsonDeserialize(using = StatePredicateDeserializer::class)
enum class StatePredicate(
        @get:JsonProperty(Expression.OP_PROPERTY_NAME) val predicateName: String
) : Expression {
    INITIAL("initial"),
    DEADLOCK("deadlock"),
    TIMELOCK("timelock");

    companion object {
        private val namesToPredicatesMap = values().map { it.predicateName to it }.toMap()

        fun isStatePredicate(predicateName: String): Boolean = namesToPredicatesMap.containsKey(predicateName)

        @JvmStatic
        fun fromPredicateName(
                @JsonProperty(Expression.OP_PROPERTY_NAME) predicateName: String
        ): StatePredicate =
                namesToPredicatesMap[predicateName]
                        ?: throw IllegalArgumentException("Unknown state predicate: $predicateName")
    }
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
            exp: PropertyExpression, stepBounds: PropertyInterval? = null, timeBounds: PropertyInterval? = null,
            rewardBounds: List<RewardBound> = emptyList()
    ): UnaryPathExpression = UnaryPathExpression(this, exp, stepBounds, timeBounds, rewardBounds)
}

@JaniJsonMultiOp
@JaniExtension(ModelFeature.DERIVED_OPERATORS)
data class UnaryPathExpression(
        val op: UnaryPathOp, val exp: PropertyExpression, override val stepBounds: PropertyInterval? = null,
        override val timeBounds: PropertyInterval? = null, override val rewardBounds: List<RewardBound> = emptyList()
) : PathExpression