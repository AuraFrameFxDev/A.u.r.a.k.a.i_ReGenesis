package dev.aurakai.auraframefx.utils

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Extension function to convert Map<String, String> to JsonObject
 */
fun Map<String, String>.toKotlinJsonObject(): JsonObject = buildJsonObject {
    this@toKotlinJsonObject.forEach { (key, value) ->
        put(key, value)
    }
}

/**
 * Extension function to convert Map<String, Any> to JsonObject
 */
fun Map<String, Any>.toKotlinJsonObject(): JsonObject = buildJsonObject {
    this@toKotlinJsonObject.forEach { (key, value) ->
        when (value) {
            is String -> put(key, value)
            is Number -> put(key, value.toString())
            is Boolean -> put(key, value)
            else -> put(key, value.toString())
        }
    }
}
