package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.sun.org.apache.xpath.internal.operations.Bool
import hu.bme.mit.inf.jani.model.json.JaniModelModule
import jdk.nashorn.internal.ir.annotations.Ignore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TypesTest {
    private val objectMapper = ObjectMapper().registerModules(KotlinModule(), JaniModelModule)

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `serialize types on the top level`(data: SerializedData<Type>) {
        val json = objectMapper.writerFor(Type::class.java).writeValueAsString(data.data)
        assertEquals(data.json, json)
    }

    @ParameterizedTest
    @MethodSource("serializedWrappedTypeDataProvider")
    fun `serialize types inside another object`(data: SerializedData<Dummy>) {
        val json = objectMapper.writerFor(Dummy::class.java).writeValueAsString(data.data)
        assertEquals(data.json, json)
    }

    @ParameterizedTest
    @MethodSource("serializedTopLevelTypeDataProvider")
    fun `deserialize types on the top level`(data: SerializedData<Type>) {
        val type = objectMapper.readValue(data.json, Type::class.java)
        assertEquals(data.data, type)
    }

    @ParameterizedTest
    @MethodSource("serializedWrappedTypeDataProvider")
    fun `deserialize types on inside another object`(data: SerializedData<Dummy>) {
        val wrapped = objectMapper.readValue(data.json, Dummy::class.java)
        assertEquals(data.data, wrapped)
    }

    @Suppress("unused")
    fun serializedTopLevelTypeDataProvider() = Stream.of(
            SerializedData(""""bool"""", BoolType),
            SerializedData(""""int"""", IntType),
            SerializedData(""""real"""", RealType),
            SerializedData(""""clock"""", ClockType),
            SerializedData(""""continuous"""", ContinuousType),
            SerializedData("""{"kind":"array","base":"int"}""", ArrayType(IntType)),
            SerializedData("""{"kind":"datatype","ref":"struct"}""", DatatypeType("struct")),
            SerializedData("""{"kind":"option","base":"real"}""", OptionType(RealType)),
            SerializedData(
                    """{"kind":"array","base":{"kind":"array","base":"bool"}}""",
                    ArrayType(ArrayType(BoolType))
            )
    )!!

    @Suppress("unused")
    fun serializedWrappedTypeDataProvider() = serializedTopLevelTypeDataProvider().map {
        SerializedData("""{"type":${it.json}}""", Dummy(it.data))
    }!!

    data class SerializedData<out T>(val json: String, val data: T)

    data class Dummy @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(val type: Type)
}