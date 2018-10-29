package hu.bme.mit.inf.theta.interchange.jani.model.serializer

import hu.bme.mit.inf.theta.interchange.jani.model.ArrayType
import hu.bme.mit.inf.theta.interchange.jani.model.BoundedType
import hu.bme.mit.inf.theta.interchange.jani.model.DatatypeType
import hu.bme.mit.inf.theta.interchange.jani.model.OptionType
import hu.bme.mit.inf.theta.interchange.jani.model.SimpleType
import hu.bme.mit.inf.theta.interchange.jani.model.Type
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.json.JSON
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonLiteral
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTreeMapper

@Serializer(forClass = Type::class)
object TypeSerializer {
    private const val kindProperty = "kind"

    override val descriptor: SerialDescriptor
        get() = SerialClassDescImpl("Type")

    override fun deserialize(input: Decoder): Type {
        if (input !is JSON.JsonInput) {
            throw SerializationException("This class can be loaded only by JSON.")
        }

        val tree = input.readAsTree()
        return when (tree) {
            is JsonLiteral -> {
                val simpleTypeName = tree.content
                SimpleType.fromNameOrNull(tree.content)
                    ?: throw SerializationException("Unknown simple type: $simpleTypeName")
            }
            is JsonObject -> {
                val kind = tree[kindProperty].primitive.content
                val serializer = when (kind) {
                    BoundedType.complexTypeName -> BoundedType.serializer()
                    ArrayType.complexTypeName -> ArrayType.serializer()
                    DatatypeType.complexTypeName -> DatatypeType.serializer()
                    OptionType.complexTypeName -> OptionType.serializer()
                    else -> throw SerializationException("Unknown complex type: $kind")
                }
                val mapper = JsonTreeMapper()
                mapper.readTree(tree, serializer)
            }
            else -> throw SerializationException("Invalid Type $tree")
        }
    }

    override fun serialize(output: Encoder, obj: Type) {
        if (output !is JSON.JsonOutput) {
            throw SerializationException("This class can be saved only by JSON.")
        }

        if (obj is SimpleType) {
            output.encodeString(obj.simpleTypeName)
            return
        }

        val mapper = JsonTreeMapper()
        val (tree, kind) = when (obj) {
            is BoundedType -> mapper.writeTree(obj, BoundedType.serializer()) to BoundedType.complexTypeName
            is ArrayType -> mapper.writeTree(obj, ArrayType.serializer()) to ArrayType.complexTypeName
            is DatatypeType -> mapper.writeTree(obj, DatatypeType.serializer()) to DatatypeType.complexTypeName
            is OptionType -> mapper.writeTree(obj, OptionType.serializer()) to OptionType.complexTypeName
            else -> throw SerializationException("Unknown complex type: $obj")
        }
        val untypedJsonObject = tree.jsonObject
        val children: MutableMap<String, JsonElement> = mutableMapOf(kindProperty to JsonPrimitive(kind))
        children.putAll(tree.jsonObject.content)
        val typedJsonObject = untypedJsonObject.copy(content = children)
        output.writeTree(typedJsonObject)
    }
}