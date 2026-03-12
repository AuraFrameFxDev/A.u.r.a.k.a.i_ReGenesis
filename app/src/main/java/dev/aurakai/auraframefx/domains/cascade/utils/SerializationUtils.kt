package dev.aurakai.auraframefx.domains.cascade.utils

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * Extension function to convert a Map to a Kotlin Serialization JsonObject.
 */
fun Map<String, Any?>.toKotlinJsonObject(): JsonObject {
    return buildJsonObject {
        forEach { (key, value) ->
            put(key, value.toJsonElement())
        }
    }
}

/**
 * Extension function to convert any object to a JsonElement.
 * Handles primitives, Maps, Lists, and nulls. Fallback to toString() for others.
 */
fun Any?.toJsonElement(): JsonElement {
    return when (this) {
        null -> JsonNull
        is Boolean -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is Map<*, *> -> {
            @Suppress("UNCHECKED_CAST")
            (this as Map<String, Any?>).toKotlinJsonObject()
        }

        is List<*> -> JsonArray(this.map { it.toJsonElement() })
        else -> JsonPrimitive(this.toString())
    }
}

