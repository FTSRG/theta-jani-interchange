package hu.bme.mit.inf.theta.interchange.jani.interaction

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonValue
import hu.bme.mit.inf.theta.interchange.jani.model.utils.upperSnakeToLowerKebabCase

interface Message

interface ClientToServer : Message

data class Authenticate(
    val janiVersions: Set<Int>,
    val extensions: Set<InteractionExtension> = emptySet(),
    val login: String? = null,
    val password: String? = null
) : ClientToServer

interface ServerToClient : Message

data class Metadata(
    val name: String,
    val version: Version,
    val author: String? = null,
    val description: String? = null,
    val url: String? = null
)

data class Version(val major: Int, val minor: Int, val revision: Int)

@JsonTypeName("capabilities")
data class Capabilities(
    val janiVersion: Int,
    val extensions: Set<InteractionExtension> = emptySet(),
    val metadata: Metadata,
    val parameters: List<ParameterDefinition>,
    val roles: Set<Role>
)

data class ParameterDefinition(val name: String)

enum class Role {
    ANALYSE, TRANSFORM;

    @get:JsonValue
    val featureName: String = name.upperSnakeToLowerKebabCase()
}