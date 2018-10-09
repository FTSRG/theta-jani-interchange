package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import hu.bme.mit.inf.jani.model.json.ZeroValueFilter

data class Automaton(
        override val name: String,
        val variables: List<VariableDeclaration> = emptyList(),
        val restrictInitial: CommentedExpression? = null,
        @JsonInclude(JsonInclude.Include.ALWAYS) val locations: List<Location>,
        @JsonInclude(JsonInclude.Include.ALWAYS) val initialLocations: List<String>,
        @JsonInclude(JsonInclude.Include.ALWAYS) val edges: List<Edge>,
        override val comment: String? = null,
        @get:JaniExtension(ModelFeature.FUNCTIONS) val functions: List<FunctionDefinition> = emptyList()
) : NamedElement, CommentedElement

data class Location(
        override val name: String,
        val timeProgress: CommentedExpression? = null,
        val transientValues: List<TransientValue> = emptyList(),
        override val comment: String? = null
) : NamedElement, CommentedElement

data class TransientValue(
        @param:JsonProperty("ref") @get:JsonProperty("ref") val reference: LValue,
        val value: Expression,
        override val comment: String? = null
) : CommentedElement

data class Edge(
        val location: String,
        val action: String? = null,
        val rate: CommentedExpression? = null,
        val guard: CommentedExpression? = null,
        @JsonInclude(JsonInclude.Include.ALWAYS) val destinations: List<Destination>,
        override val comment: String? = null
) : CommentedElement

data class Destination(
        val location: String,
        val probability: CommentedExpression? = null,
        val assignments: List<Assignment> = emptyList(),
        override val comment: String? = null
) : CommentedElement

data class Assignment(
        @param:JsonProperty("ref") @get:JsonProperty("ref") val reference: LValue,
        val value: Expression,
        @JsonInclude(JsonInclude.Include.CUSTOM, valueFilter = ZeroValueFilter::class) val index: Int = 0,
        override val comment: String? = null
) : CommentedElement