package hu.bme.mit.inf.jani.model.json

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.introspect.AnnotatedClass
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.module.SimpleModule

object JaniModelModule : SimpleModule("jani-model") {
    init {
        setSerializerModifier(JaniModelBeanSerializerModifier)
        setDeserializerModifier(JaniModelBeanDeserializerModifier)
    }

    override fun setupModule(context: SetupContext) {
        super.setupModule(context)
        context.insertAnnotationIntrospector(JaniAnnotationIntrospector)
    }

    /**
     * Every JANI property is named using the kebab-case convention.
     * We use an annotation introspector to enforce this without overriding the naming strategy globally.
     */
    internal object JaniAnnotationIntrospector : NopAnnotationIntrospector() {
        private const val packageNamePrefix: String = "hu.bme.mit.inf.jani."

        override fun findNamingStrategy(ac: AnnotatedClass): Any? =
            if (ac.type.rawClass.name.startsWith(packageNamePrefix)) {
                PropertyNamingStrategy.KEBAB_CASE
            } else {
                null
            }
    }
}