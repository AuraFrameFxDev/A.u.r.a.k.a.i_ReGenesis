package dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "nexus_memories")
@Serializable
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String? = null, // Added for compatibility with PersistentMemoryManager
    val content: String,
    val timestamp: Long,
    val type: MemoryType,
    val tags: List<String> = emptyList(),
    val importance: Float = 0.5f,
    val embedding: List<Float>? = null, // For future vector search
    val relatedMemoryIds: List<Long> = emptyList() // Adjacency list for graph-like structure
)

enum class MemoryType {
    CONVERSATION,
    OBSERVATION,
    REFLECTION,
    FACT,
    EMOTION
}
