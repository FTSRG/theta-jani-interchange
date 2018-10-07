package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.lang.annotation.Inherited

enum class ModelFeature(@get:JsonValue val featureName: String) {
    /**
     * Support for array types.
     */
    ARRAYS("arrays"),

    /**
     * Support for complex datatypes.
     */
    DATATYPES("datatypes"),

    /**
     * Support for some derived operators in expressions.
     */
    DERIVED_OPERATORS("derived-operators"),

    /**
     * Support for priorities on edges.
     */
    EDGE_PRIORITIES("edge-priorities"),

    /**
     * Support for priorities on edges.
     */
    FUNCTIONS("functions"),

    /**
     * Support for hyperbolic functions.
     */
    HYPERBOLIC_FUNCTIONS("hyperbolic-functions"),

    /**
     * Support for named subexpressions.
     */
    NAMED_EXPRESSIONS("named-expressions"),

    /**
     * Support for nondeterministic selection in expressions.
     */
    NONDET_SELECTION("nondet-selection"),

    /**
     * Support for accumulating rewards when leaving a state.
     */
    STATE_EXIT_REWARDS("state-exit-rewards"),

    /**
     * Support for multi-objective tradeoff properties.
     */
    TRADEOFF_PROPERTIES("tradeoff-properties"),

    /**
     * Support for trigonometric functions.
     */
    TRIGONOMETRIC_FUNCTIONS("trigonometric-functions")
}

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@MustBeDocumented
annotation class JaniExtension(val modelFeature: ModelFeature)