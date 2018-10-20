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
class LValuesTest {
    private val objectMapper = JaniModelMapper()

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `serialize LValues`(testCase: SerializationTestCase<LValue>) {
        testCase.assertSerialized(objectMapper, LValue::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `deserialize LValues`(testCase: SerializationTestCase<LValue>) {
        testCase.assertDeserialized(objectMapper, LValue::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `serialize LValues as expressions`(testCase: SerializationTestCase<LValue>) {
        testCase.assertSerialized(objectMapper, Expression::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `deserialize LValues as expressions`(testCase: SerializationTestCase<LValue>) {
        testCase.assertDeserialized(objectMapper, Expression::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `serialize LValues as property expressions`(testCase: SerializationTestCase<LValue>) {
        testCase.assertSerialized(objectMapper, PropertyExpression::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `deserialize LValues as property expressions`(testCase: SerializationTestCase<LValue>) {
        testCase.assertDeserialized(objectMapper, PropertyExpression::class.java)
    }

    @Suppress("unused")
    fun serializedTopLevelTypeDataProvider() = Stream.of(
        "\"foo\"" isJsonFor Identifier("foo"),

        """{"op":"aa","exp":"a","index":0}""" isJsonFor ArrayAccess(Identifier("a"), IntConstant(0)),

        """{"op":"da","exp":"a","member":"b"}""" isJsonFor DatatypeMemberAccess(Identifier("a"), "b"),

        """{"op":"oa","exp":"a"}""" isJsonFor OptionValueAccess(Identifier("a"))
    )!!
}