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

import hu.bme.mit.inf.theta.interchange.jani.model.json.JaniModelMapper
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TypesTest {
    private val objectMapper = JaniModelMapper()

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `serialize types`(testCase: SerializationTestCase<Type>) {
        testCase.assertSerialized(objectMapper, Type::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `deserialize types`(testCase: SerializationTestCase<Type>) {
        testCase.assertDeserialized(objectMapper, Type::class.java)
    }

    @Suppress("unused")
    fun serializedTopLevelTypeDataProvider() = Stream.of(
            "\"bool\"" isJsonFor BoolType,
            "\"int\"" isJsonFor IntType,
            "\"real\"" isJsonFor RealType,
            "\"clock\"" isJsonFor ClockType,
            "\"continuous\"" isJsonFor ContinuousType,
            """{"kind":"bounded","base":"int"}""" isJsonFor BoundedType(IntType),
            """{"kind":"bounded","base":"real"}""" isJsonFor BoundedType(RealType),
            """{"kind":"bounded","base":"int","lower-bound":1}""" isJsonFor
                    BoundedType(IntType, lowerBound = IntConstant(1)),
            """{"kind":"bounded","base":"int","upper-bound":2}""" isJsonFor
                    BoundedType(IntType, upperBound = IntConstant(2)),
            """{"kind":"bounded","base":"int","lower-bound":1,"upper-bound":2}""" isJsonFor
                    BoundedType(IntType, lowerBound = IntConstant(1), upperBound = IntConstant(2)),
            """{"kind":"array","base":"int"}""" isJsonFor ArrayType(IntType),
            """{"kind":"datatype","ref":"struct"}""" isJsonFor DatatypeType("struct"),
            """{"kind":"option","base":"real"}""" isJsonFor OptionType(RealType),
            """{"kind":"array","base":{"kind":"array","base":"bool"}}""" isJsonFor ArrayType(ArrayType(BoolType))
    )!!
}