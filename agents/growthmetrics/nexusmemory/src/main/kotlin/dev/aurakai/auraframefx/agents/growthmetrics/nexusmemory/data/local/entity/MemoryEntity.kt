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
    val embedding: List<Float>? = null, // Gemini Embedding 2 MRL vector (768/1536/3072 dims)
    val embeddingDimensions: Int = 1536, // MRL dimension used when embedding was generated
    val modalityTag: String = "text", // "text" | "image" | "audio" | "text+image" | "text+audio" | "multimodal"
    val relatedMemoryIds: List<Long> = emptyList(), // Adjacency list for graph-like structure
    val isEncrypted: Boolean = false
)

enum class MemoryType {
    CONVERSATION,
    OBSERVATION,
    REFLECTION,
    FACT,
    EMOTION,
    VALENCE // Multimodal valence-tagged memory (Gemini Embedding 2)
}
