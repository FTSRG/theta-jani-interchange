package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
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
            SerializedData(""""bool"""", SimpleType.BOOL),
            SerializedData(""""int"""", SimpleType.INT),
            SerializedData(""""real"""", SimpleType.REAL),
            SerializedData(""""clock"""", SimpleType.CLOCK),
            SerializedData(""""continuous"""", SimpleType.CONTINUOUS),
            SerializedData("""{"kind":"array","base":"int"}""", ArrayType(SimpleType.INT)),
            SerializedData("""{"kind":"datatype","ref":"struct"}""", DatatypeType("struct")),
            SerializedData("""{"kind":"option","base":"real"}""", OptionType(SimpleType.REAL)),
            SerializedData(
                    """{"kind":"array","base":{"kind":"array","base":"bool"}}""",
                    ArrayType(ArrayType(SimpleType.BOOL))
            )
    )!!

    @Test
    fun fooTest() {
        val obj = objectMapper.readValue("""{"name":"aaa","other":"bbb"}""", IgnoreTest::class.java)
        assertEquals("aaa", obj.name)
    }

    @Suppress("unused")
    fun serializedWrappedTypeDataProvider() = serializedTopLevelTypeDataProvider().map {
        SerializedData("""{"type":${it.json}}""", Dummy(it.data))
    }!!

    data class SerializedData<out T>(val json: String, val data: T)

    data class Dummy @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(val type: Type)

    @JsonIgnoreProperties(value = ["other"])
    data class IgnoreTest @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(val name: String)
}