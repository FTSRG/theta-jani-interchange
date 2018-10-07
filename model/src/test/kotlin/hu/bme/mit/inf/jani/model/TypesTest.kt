package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.annotation.JsonCreator
import hu.bme.mit.inf.jani.model.json.JaniModelMapper
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TypesTest {
    private val objectMapper = JaniModelMapper()

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `serialize types on the top level`(testCase: SerializationTestCase<Type>) {
        testCase.assertSerialized(objectMapper, Type::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedWrappedTypeDataProvider")
    fun `serialize types inside another object`(testCase: SerializationTestCase<Dummy>) {
        testCase.assertSerialized(objectMapper, Dummy::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `deserialize types on the top level`(testCase: SerializationTestCase<Type>) {
        testCase.assertDeserialized(objectMapper, Type::class.java)
    }

    @ParameterizedTest
    @MethodSource("serializedWrappedTypeDataProvider")
    fun `deserialize types on inside another object`(testCase: SerializationTestCase<Dummy>) {
        testCase.assertDeserialized(objectMapper, Dummy::class.java)
    }

    @Suppress("unused")
    fun serializedTopLevelTypeDataProvider() = Stream.of(
            """"bool"""" isJsonFor BoolType,
            """"int"""" isJsonFor IntType,
            """"real"""" isJsonFor RealType,
            """"clock"""" isJsonFor ClockType,
            """"continuous"""" isJsonFor ContinuousType,
            """{"kind":"array","base":"int"}""" isJsonFor ArrayType(IntType),
            """{"kind":"datatype","ref":"struct"}""" isJsonFor DatatypeType("struct"),
            """{"kind":"option","base":"real"}""" isJsonFor OptionType(RealType),
            """{"kind":"array","base":{"kind":"array","base":"bool"}}""" isJsonFor ArrayType(ArrayType(BoolType))
    )!!

    @Suppress("unused")
    fun serializedWrappedTypeDataProvider() = serializedTopLevelTypeDataProvider().map {
        """{"type":${it.json}}""" isJsonFor Dummy(it.data)
    }!!

    data class Dummy @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(val type: Type)
}