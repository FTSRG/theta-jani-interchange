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