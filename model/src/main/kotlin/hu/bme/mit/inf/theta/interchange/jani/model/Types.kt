/*
 * Copyright 2018 Contributors to the Theta project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hu.bme.mit.inf.theta.interchange.jani.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonValue
import hu.bme.mit.inf.theta.interchange.jani.model.serializer.TypeSerializer
import kotlinx.serialization.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes(
    // [SimpleType] is deliberately not included, we handle it in
    // [hu.bme.mit.inf.theta.interchange.jani.model.json.TypeDeserializer] separately.
    JsonSubTypes.Type(BoundedType::class),
    JsonSubTypes.Type(ArrayType::class),
    JsonSubTypes.Type(DatatypeType::class),
    JsonSubTypes.Type(OptionType::class)
)
interface Type

sealed class SimpleType(@get:JsonValue val simpleTypeName: String) : Type {
    override fun toString(): String = javaClass.simpleName

    companion object {
        // Lazy to delay initialization until the [SimpleType] class was initialized and its instances were constructed.
        private val simpleTypeByNameMap by lazy {
            val simpleTypes = listOf(BoolType, IntType, RealType, ClockType, ContinuousType)
            simpleTypes.map { it.simpleTypeName to it }.toMap()
        }

        @JvmStatic
        @JsonCreator
        fun fromName(name: String): SimpleType = fromNameOrNull(name)
            ?: throw IllegalArgumentException("Unknown SimpleType: $name")

        @JvmStatic
        fun fromNameOrNull(name: String): SimpleType? = simpleTypeByNameMap[name]
    }
}

interface ConstantType : Type

sealed class BasicType(basicTypeName: String) : SimpleType(basicTypeName), ConstantType

object BoolType : BasicType("bool")

sealed class BasicNumericType(name: String) : BasicType(name)

object IntType : BasicNumericType("int")

object RealType : BasicNumericType("real")

object ClockType : SimpleType("clock")

object ContinuousType : SimpleType("continuous")

@JsonTypeName("bounded")
@Serializable
data class BoundedType(
    @Serializable(with = TypeSerializer::class) val base: BasicNumericType,
    val lowerBound: Expression? = null,
    val upperBound: Expression? = null
) : ConstantType {
    companion object {
        const val complexTypeName = "bounded"
    }
}

@JsonTypeName("array")
@JaniExtension(ModelFeature.ARRAYS)
@Serializable
data class ArrayType @JsonCreator constructor(@Serializable(with = TypeSerializer::class) val base: Type) : Type {
    companion object {
        const val complexTypeName = "array"
    }
}

@JsonTypeName("datatype")
@JaniExtension(ModelFeature.DATATYPES)
@Serializable
data class DatatypeType @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(val ref: String) : Type {
    companion object {
        const val complexTypeName = "datatype"
    }
}

@JsonTypeName("option")
@JaniExtension(ModelFeature.DATATYPES)
@Serializable
data class OptionType @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
    @Serializable(with = TypeSerializer::class) val base: Type
) : Type {
    companion object {
        const val complexTypeName = "option"
    }
}
