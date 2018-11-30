package hu.bme.mit.theta.interchange.jani.interaction

import com.fasterxml.jackson.annotation.JsonValue
import hu.bme.mit.theta.interchange.jani.model.utils.upperSnakeToLowerKebabCase
import java.lang.annotation.Inherited

enum class InteractionExtension {
    /**
     * The server keeps state (e.g. running analysis tasks) for the client between connections.
     */
    PERSISTENT_STATE;

    @get:JsonValue
    val featureName: String = name.upperSnakeToLowerKebabCase()
}

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@MustBeDocumented
annotation class JaniInteractionExtension(val interactionExtension: InteractionExtension)
