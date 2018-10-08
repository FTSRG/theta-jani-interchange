package hu.bme.mit.inf.jani.model

import hu.bme.mit.inf.jani.model.json.JaniModelMapper
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