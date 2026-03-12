package dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local

import androidx.room.TypeConverter
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun fromFloatList(value: List<Float>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toFloatList(value: String?): List<Float>? {
        return value?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun fromMemoryType(value: MemoryType): String {
        return value.name
    }

    @TypeConverter
    fun toMemoryType(value: String): MemoryType {
        return MemoryType.valueOf(value)
    }
}
