package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.annotation.*

interface Expression

interface ConstantValue : Expression

data class IntConstant @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(val value: Int) : ConstantValue

data class RealConstant @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(val value: Double) : ConstantValue

data class BoolConstant @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(val value: Boolean) : ConstantValue

enum class NamedConstant(val value: Double) {
    @JsonProperty("e")
    E(Math.E),

    @JsonProperty("π")
    PI(Math.PI)
}

data class Identifier @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(val name: String) : ConstantValue

@JsonTypeName("ite")
data class Ite(
        @param:JsonProperty("if") @get:JsonProperty("if") val condition: Expression,
        @param:JsonProperty("then") @get:JsonProperty("then") val thenExp: Expression,
        @param:JsonProperty("else") @get:JsonProperty("else") val elseExp: Expression
) : Expression

interface UnaryExpressionLike : Expression {
    val exp: Expression
}

@JsonTypeName("¬")
data class Not @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
        override val exp: Expression
) : UnaryExpressionLike

@JsonTypeName("floor")
data class Floor @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
        override val exp: Expression
) : UnaryExpressionLike

@JsonTypeName("ceil")
data class Ceil @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
        override val exp: Expression
) : UnaryExpressionLike

@JsonTypeName("der")
data class Der @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
        override val exp: Expression
) : UnaryExpressionLike

interface BinaryExpression : Expression {
    val left: Expression
    val right: Expression
}

@JsonTypeName("∨")
data class Or(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("∧")
data class And(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("=")
data class Eq(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("≠")
data class Neq(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("<")
data class Lt(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("+")
data class Add(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("-")
data class Sub(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("*")
data class Mul(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("%")
data class Mod(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("/")
data class Div(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("pow")
data class Pow(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("log")
data class Log(override val left: Expression, override val right: Expression) : BinaryExpression

@JsonTypeName("filter")
data class Filter(
        @param:JsonProperty("fun") @get:JsonProperty("fun") val function: FilterFunction,
        val values: Expression,
        val states: Expression
)

enum class FilterFunction(private val expectedValuesType: Type, val resultType: Type?) {
    @JsonProperty("min")
    MIN(RealType, RealType),

    @JsonProperty("max")
    MAX(RealType, RealType),

    @JsonProperty("sum")
    SUM(RealType, RealType),

    @JsonProperty("avg")
    AVG(RealType, RealType),

    @JsonProperty("count")
    COUNT(BoolType, IntType),

    @JsonProperty("∀")
    FORALL(BoolType, BoolType),

    @JsonProperty("∃")
    EXISTS(BoolType, BoolType),

    @JsonProperty("argmin")
    ARGMIN(RealType, null),

    @JsonProperty("argmax")
    ARGMAX(RealType, null),

    @JsonProperty("values")
    VALUES(RealType, null) {
        override fun acceptsValuesOfType(valuesType: Type): Boolean =
                RealType.isAssignableFrom(valuesType) || BoolType.isAssignableFrom(valuesType)
    };

    open fun acceptsValuesOfType(valuesType: Type): Boolean = expectedValuesType.isAssignableFrom(valuesType)
}

enum class ExtremeValue {
    MIN, MAX
}

enum class ProbabilityKind {
    INITIAL, STEADY_STATE
}

abstract class ProbabilityQuery internal constructor(
        @get:JsonIgnore val kind: ProbabilityKind, @get:JsonIgnore val extremeValue: ExtremeValue,
        override val exp: Expression
): UnaryExpressionLike {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProbabilityQuery) return false

        if (kind != other.kind) return false
        if (extremeValue != other.extremeValue) return false
        if (exp != other.exp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = kind.hashCode()
        result = 31 * result + extremeValue.hashCode()
        result = 31 * result + exp.hashCode()
        return result
    }

    override fun toString(): String = "${javaClass.simpleName}(exp=$exp)"

    companion object {
        @JvmStatic
        fun of(kind: ProbabilityKind, extremeValue: ExtremeValue, exp: Expression) = when (kind) {
            ProbabilityKind.INITIAL -> P.of(extremeValue, exp)
            ProbabilityKind.STEADY_STATE -> S.of(extremeValue, exp)
        }
    }
}

abstract class P internal constructor(
    extremeValue: ExtremeValue, exp: Expression
) : ProbabilityQuery(ProbabilityKind.INITIAL, extremeValue, exp) {
    companion object {
        @JvmStatic
        fun of(extremeValue: ExtremeValue, exp: Expression): P = when(extremeValue) {
            ExtremeValue.MIN -> PMin(exp)
            ExtremeValue.MAX -> PMax(exp)
        }
    }
}

@JsonTypeName("Pmin")
class PMin @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(exp: Expression) : P(ExtremeValue.MIN, exp)

@JsonTypeName("Pmax")
class PMax @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(exp: Expression) : P(ExtremeValue.MAX, exp)

@JsonTypeName("∀")
data class Forall @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
        override val exp: Expression
) : UnaryExpressionLike

@JsonTypeName("∃")
data class Exists @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
        override val exp: Expression
) : UnaryExpressionLike

abstract class E internal constructor(
        @get:JsonIgnore val extremeValue: ExtremeValue, val exp: Expression, val accumulate: RewardAccumulation?,
        val reach: Expression?, val stepInstant: Expression?, val timeInstant: Expression?,
        val rewardInstants: List<RewardInstant>
) : Expression {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is E) return false

        if (extremeValue != other.extremeValue) return false
        if (exp != other.exp) return false
        if (accumulate != other.accumulate) return false
        if (reach != other.reach) return false
        if (stepInstant != other.stepInstant) return false
        if (timeInstant != other.timeInstant) return false
        if (rewardInstants != other.rewardInstants) return false

        return true
    }

    override fun hashCode(): Int {
        var result = extremeValue.hashCode()
        result = 31 * result + exp.hashCode()
        result = 31 * result + (accumulate?.hashCode() ?: 0)
        result = 31 * result + (reach?.hashCode() ?: 0)
        result = 31 * result + (stepInstant?.hashCode() ?: 0)
        result = 31 * result + (timeInstant?.hashCode() ?: 0)
        result = 31 * result + rewardInstants.hashCode()
        return result
    }

    override fun toString(): String =
            "${javaClass.simpleName}(extremeValue=$extremeValue, exp=$exp, accumulate=$accumulate, reach=$reach ," +
                "stepInstant=$stepInstant, timeInstant=$timeInstant, rewardInstants=$rewardInstants)"

    companion object {
        @JvmStatic
        fun of(
                extremeValue: ExtremeValue, exp: Expression, accumulate: RewardAccumulation? = null,
                reach: Expression? = null, stepInstant: Expression? = null, timeInstant: Expression? = null,
                rewardInstants: List<RewardInstant> = emptyList()
        ): E = when (extremeValue) {
            ExtremeValue.MIN -> EMin(exp, accumulate, reach, stepInstant, timeInstant, rewardInstants)
            ExtremeValue.MAX -> EMax(exp, accumulate, reach, stepInstant, timeInstant, rewardInstants)
        }
    }
}

data class RewardInstant(val exp: Expression, val accumulate: RewardAccumulation, val instant: Expression)

enum class RewardAccumulation {
    @JsonProperty("steps")
    STEPS,

    @JsonProperty("time")
    TIME
}

@JsonTypeName("Emin")
class EMin(
        exp: Expression, accumulate: RewardAccumulation? = null, reach: Expression? = null,
        stepInstant: Expression? = null, timeInstant: Expression? = null,
        rewardInstants: List<RewardInstant> = emptyList()
) : E(ExtremeValue.MIN, exp, accumulate, reach, stepInstant, timeInstant, rewardInstants)

@JsonTypeName("Emax")
class EMax(
        exp: Expression, accumulate: RewardAccumulation? = null, reach: Expression? = null,
        stepInstant: Expression? = null, timeInstant: Expression? = null,
        rewardInstants: List<RewardInstant> = emptyList()
) : E(ExtremeValue.MIN, exp, accumulate, reach, stepInstant, timeInstant, rewardInstants)

abstract class S internal constructor(
        extremeValue: ExtremeValue, exp: Expression
) : ProbabilityQuery(ProbabilityKind.INITIAL, extremeValue, exp) {
    companion object {
        @JvmStatic
        fun of(extremeValue: ExtremeValue, exp: Expression): S = when(extremeValue) {
            ExtremeValue.MIN -> SMin(exp)
            ExtremeValue.MAX -> SMax(exp)
        }
    }
}

abstract class TemporalExpression internal constructor(
        val stepBounds: Expression?, val timeBounds: Expression?, val rewardBounds: List<RewardBound>
) : Expression

data class RewardBound(val exp: Expression, val accumulate: RewardAccumulation, val bounds: PropertyInterval)

data class PropertyInterval(
        val lower: Expression?, val lowerExclusive: Boolean?,
        val upper: Expression?, val upperExclusive: Boolean?
)

enum class BinaryTemporalOperator {
    U, W, R
}

abstract class BinaryTemporalExpression internal constructor(
        @get:JsonIgnore val operator: BinaryTemporalOperator, override val left: Expression,
        override val right: Expression, stepBounds: Expression?, timeBounds: Expression?,
        rewardBounds: List<RewardBound>
) : TemporalExpression(stepBounds, timeBounds, rewardBounds),
        BinaryExpression {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BinaryTemporalExpression) return false

        if (operator != other.operator) return false
        if (left != other.left) return false
        if (right != other.right) return false
        if (stepBounds != other.stepBounds) return false
        if (timeBounds != other.timeBounds) return false
        if (rewardBounds != other.rewardBounds) return false

        return true
    }

    override fun hashCode(): Int {
        var result = operator.hashCode()
        result = 31 * result + left.hashCode()
        result = 31 * result + right.hashCode()
        result = 31 * result + (stepBounds?.hashCode() ?: 0)
        result = 31 * result + (timeBounds?.hashCode() ?: 0)
        result = 31 * result + rewardBounds.hashCode()
        return result
    }

    override fun toString(): String = "${javaClass.simpleName}(left=$left, right=$right, stepBounds=$stepBounds, " +
            "timeBounds=$timeBounds, rewardBounds=$rewardBounds)"

    companion object {
        @JvmStatic
        fun of(
                operator: BinaryTemporalOperator, left: Expression, right: Expression, stepBounds: Expression? = null,
                timeBounds: Expression? = null, rewardBounds: List<RewardBound> = emptyList()
        ) = when (operator) {
            BinaryTemporalOperator.U -> U(left, right, stepBounds, timeBounds, rewardBounds)
            BinaryTemporalOperator.W -> W(left, right, stepBounds, timeBounds, rewardBounds)
            BinaryTemporalOperator.R -> R(left, right, stepBounds, timeBounds, rewardBounds)
        }
    }
}

@JsonTypeName("U")
class U(
        left: Expression, right: Expression, stepBounds: Expression? = null, timeBounds: Expression? = null,
        rewardBounds: List<RewardBound> = emptyList()
) : BinaryTemporalExpression(BinaryTemporalOperator.U, left, right, stepBounds, timeBounds, rewardBounds)

@JsonTypeName("W")
class W(
        left: Expression, right: Expression, stepBounds: Expression? = null, timeBounds: Expression? = null,
        rewardBounds: List<RewardBound> = emptyList()
) : BinaryTemporalExpression(BinaryTemporalOperator.U, left, right, stepBounds, timeBounds, rewardBounds)

@JsonTypeName("initial")
object Initial : Expression

@JsonTypeName("deadlock")
object Deadlock : Expression

@JsonTypeName("timelock")
object Timelock : Expression

@JsonTypeName("Smin")
class SMin @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(exp: Expression) : S(ExtremeValue.MIN, exp)

@JsonTypeName("Smax")
class SMax @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(exp: Expression) : S(ExtremeValue.MAX, exp)

// Extension: arrays

@JsonTypeName("aa")
data class ArrayAccess(val exp: Expression, val index: Expression) : Expression

@JsonTypeName("av")
data class ArrayValue @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
        val elements: List<Expression>
) : Expression

@JsonTypeName("ac")
data class ArrayConstructor(
        @param:JsonProperty("var") @get:JsonProperty("var") val varName: String,
        val length: Expression, val exp: Expression
) : Expression

// Extension: datatypes

@JsonTypeName("da")
data class DatatypeMemberAccess(val exp: Expression, val member: String) : Expression

@JsonTypeName("dv")
data class DatatypeValue(val type: String, val values: List<DatatypeMemberValue>) : Expression

data class DatatypeMemberValue(val member: String, val value: Expression)

@JsonTypeName("oa")
data class OptionValueAccess(override val exp: Expression) : UnaryExpressionLike

@JsonTypeName("ov")
data class OptionValue(override val exp: Expression) : UnaryExpressionLike

@JsonTypeName("empty")
object EmptyOption : Expression {
    override fun toString(): String = "EmptyOption"
}

// Extension: derived-expressions

@JsonTypeName("R")
class R(
        left: Expression, right: Expression, stepBounds: Expression? = null, timeBounds: Expression? = null,
        rewardBounds: List<RewardBound> = emptyList()
) : BinaryTemporalExpression(BinaryTemporalOperator.U, left, right, stepBounds, timeBounds, rewardBounds)

enum class UnaryTemporalOperator {
    F, G
}

abstract class UnaryTemporalExpression internal constructor(
        @get:JsonIgnore val operator: UnaryTemporalOperator, override val exp: Expression,
        stepBounds: Expression?, timeBounds: Expression?, rewardBounds: List<RewardBound>
) : TemporalExpression(stepBounds, timeBounds, rewardBounds),
        UnaryExpressionLike {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnaryTemporalExpression) return false

        if (operator != other.operator) return false
        if (exp != other.exp) return false
        if (stepBounds != other.stepBounds) return false
        if (timeBounds != other.timeBounds) return false
        if (rewardBounds != other.rewardBounds) return false

        return true
    }

    override fun hashCode(): Int {
        var result = operator.hashCode()
        result = 31 * result + exp.hashCode()
        result = 31 * result + (stepBounds?.hashCode() ?: 0)
        result = 31 * result + (timeBounds?.hashCode() ?: 0)
        result = 31 * result + rewardBounds.hashCode()
        return result
    }

    override fun toString(): String = "${javaClass.simpleName}(exp=$exp, stepBounds=$stepBounds, " +
            "timeBounds=$timeBounds, rewardBounds=$rewardBounds)"

    companion object {
        @JvmStatic
        fun of(
                operator: UnaryTemporalOperator, exp: Expression, stepBounds: Expression? = null,
                timeBounds: Expression? = null, rewardBounds: List<RewardBound> = emptyList()
        ): UnaryTemporalExpression = when (operator) {
            UnaryTemporalOperator.F -> F(exp, stepBounds, timeBounds, rewardBounds)
            UnaryTemporalOperator.G -> G(exp, stepBounds, timeBounds, rewardBounds)
        }
    }
}

@JsonTypeName("F")
class F(
        exp: Expression, stepBounds: Expression? = null, timeBounds: Expression? = null,
        rewardBounds: List<RewardBound> = emptyList()
) : UnaryTemporalExpression(UnaryTemporalOperator.F, exp, stepBounds, timeBounds, rewardBounds)

@JsonTypeName("G")
class G(
        exp: Expression, stepBounds: Expression? = null, timeBounds: Expression? = null,
        rewardBounds: List<RewardBound> = emptyList()
) : UnaryTemporalExpression(UnaryTemporalOperator.G, exp, stepBounds, timeBounds, rewardBounds)