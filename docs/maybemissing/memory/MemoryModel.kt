package dev.aurakai.auraframefx.aurakai.auraframefx.ai.memory // Changed to match file path

import dev.aurakai.auraframefx.model.AgentType // Keep one import
import dev.aurakai.auraframefx.serialization.InstantSerializer // Added import
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class MemoryItem(
    val id: String = "mem_${Clock.System.now().toEpochMilliseconds()}",
    val content: String,
    @Serializable(with = InstantSerializer::class) val timestamp: Instant = Clock.System.now(),
    val agent: AgentType,
    val context: String? = null,
    val priority: Float = 0.5f,
    val tags: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
)

@Serializable
data class MemoryQuery(
    val query: String,
    val context: String? = null,
    val maxResults: Int = 5,
    val minSimilarity: Float = 0.7f,
    val tags: List<String> = emptyList(),
    val timeRange: Pair<@Serializable(with = InstantSerializer::class) Instant, @Serializable(with = InstantSerializer::class) Instant>? = null,
    val agentFilter: List<AgentType> = emptyList(),
)

@Serializable
data class MemoryRetrievalResult(
    val items: List<MemoryItem>,
    val total: Int,
    val query: MemoryQuery,
)
