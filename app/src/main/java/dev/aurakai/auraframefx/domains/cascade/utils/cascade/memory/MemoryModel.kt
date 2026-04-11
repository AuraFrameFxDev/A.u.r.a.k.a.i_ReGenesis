package dev.aurakai.auraframefx.domains.cascade.utils.cascade.memory

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.helpers.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class MemoryItem(
    val id: String = "mem_${Clock.System.now().toEpochMilliseconds()}",
    val content: String,
    @Serializable(with = InstantSerializer::class) val timestamp: Instant = Clock.System.now(),
    val agent: AgentCapabilityCategory,
    val context: String? = null,
    val priority: Float = 0.5f,
    val tags: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
    val agentId: String = agent.name,
    val sessionId: String = "default",
    val type: String = "memory"
)

@Serializable
data class MemoryQuery(
    val query: String,
    val context: String? = null,
    val maxResults: Int = 5,
    val minSimilarity: Float = 0.7f,
    val tags: List<String> = emptyList(),
    val timeRange: Pair<@Serializable(with = InstantSerializer::class) Instant, @Serializable(with = InstantSerializer::class) Instant>? = null,
    val agentFilter: List<AgentCapabilityCategory> = emptyList(),
)

@Serializable
data class MemoryRetrievalResult(
    val items: List<MemoryItem>,
    val total: Int,
    val query: MemoryQuery,
)
