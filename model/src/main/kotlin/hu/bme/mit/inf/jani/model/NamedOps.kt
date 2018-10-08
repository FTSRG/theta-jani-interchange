package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import hu.bme.mit.inf.jani.model.json.BinaryOpLikeConverter
import hu.bme.mit.inf.jani.model.json.BinaryPathOpLikeConverter
import hu.bme.mit.inf.jani.model.json.UnaryOpLikeConverter
import hu.bme.mit.inf.jani.model.json.UnaryPropertyOpLikeConverter

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
                    UnaryOp.values(), DerivedUnaryOp.values(), HyperbolicOp.values(), TrigonometricOp.values()
            ).flatten()
    }
}

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

@JsonDeserialize(converter = UnaryPropertyOpLikeConverter::class)
interface UnaryPropertyOpLike : NamedOpLike {
    fun of(exp: Expression): UnaryPropertyExpression = UnaryPropertyExpression(this, exp)

    companion object : OpRegistry<UnaryPropertyOpLike>("unary property") {
        override val namedOps: Iterable<UnaryPropertyOpLike>
            get() = arrayOf<Array<out UnaryPropertyOpLike>>(
                    ProbabilityOp.values(), PathQuantifier.values(), SteadyStateOp.values()
            ).flatten()
    }
}

@JsonDeserialize(converter = BinaryPathOpLikeConverter::class)
interface BinaryPathOpLike : NamedOpLike {
    fun of(
            left: PropertyExpression, right: PropertyExpression, stepBounds: PropertyInterval? = null,
            timeBounds: PropertyInterval? = null, rewardBounds: List<RewardBound> = emptyList()
    ): BinaryPathExpression = BinaryPathExpression(this, left, right, stepBounds, timeBounds, rewardBounds)

    companion object : OpRegistry<BinaryPathOpLike>("binary path") {
        override val namedOps: Iterable<BinaryPathOpLike>
            get() = arrayOf<Array<out BinaryPathOpLike>>(
                    BinaryPathOp.values(), DerivedBinaryPathOp.values()
            ).flatten()
    }
}