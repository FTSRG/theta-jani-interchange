package hu.bme.mit.inf.jani.model

data class Model(
        val name: String,
        val metadata: Metadata? = null,
        val type: ModelType,
        val features: List<ModelFeature> = emptyList(),
        @get:JaniExtension(ModelFeature.DATATYPES)  val datatypes: List<DatatypeDefinition> = emptyList(),
        @get:JaniExtension(ModelFeature.FUNCTIONS) val functions: List<FunctionDefinition> = emptyList()
)

data class Metadata(
        val version: String? = null,
        val author: String? = null,
        val description: String? = null,
        val doi: String? = null,
        val url: String? = null
)

enum class ModelType {
    LTS,
    DTMC,
    CTMC,
    MDP,
    CTMPD,
    MA,
    TA,
    PTA,
    STA,
    HA,
    PHA,
    SHA
}

data class FunctionDefinition(
        val name: String, val type: Type, val parameters: List<FunctionParameter>, val body: Expression
)

data class FunctionParameter(val name: String, val type: Type)