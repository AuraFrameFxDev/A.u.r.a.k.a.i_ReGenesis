package dev.aurakai.auraframefx.utils

import kotlinx.serialization.json.Json

object JsonUtils {
    internal val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    fun <T> toJson(obj: T, serializer: kotlinx.serialization.KSerializer<T>): String? {
        return try {
            Json.encodeToString(serializer, obj)
        } catch (e: Exception) {
            // Log the exception
            null
        }
    }

    fun <T> fromJson(jsonString: String, serializer: kotlinx.serialization.KSerializer<T>): T? {
        return try {
            Json.decodeFromString(serializer, jsonString)
        } catch (e: Exception) {
            // Log the exception
            null
        }
    }
}
