package hu.bme.mit.inf.jani.model

data class Model(
        val name: String,
        val metadata: Metadata? = null,
        val type: ModelType,
        val features: List<ModelFeature> = emptyList(),
        val datatypes: List<DatatypeDefinition> = emptyList()
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

interface ModelFeature