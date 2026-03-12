package dev.aurakai.auraframefx.domains.cascade.utils

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

