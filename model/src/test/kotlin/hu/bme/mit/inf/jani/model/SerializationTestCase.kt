package hu.bme.mit.inf.jani.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals

data class SerializationTestCase<out T>(val json: String, val data: T) {
    fun assertSerialized(objectMapper: ObjectMapper, javaClass: Class<in T>) {
        val serializedJson = objectMapper.writerFor(javaClass).writeValueAsString(data)
        assertEquals(json, serializedJson)
    }

    fun assertDeserialized(objectMapper: ObjectMapper, javaClass: Class<in T>) {
        val deserializedData = objectMapper.readValue(json, javaClass)
        assertEquals(data, deserializedData)
    }

    override fun toString(): String = "$json <~> $data"
}

infix fun <T> String.isJsonFor(data: T): SerializationTestCase<T> = SerializationTestCase(this, data)