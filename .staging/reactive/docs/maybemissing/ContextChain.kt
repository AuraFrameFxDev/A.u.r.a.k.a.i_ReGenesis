package dev.aurakai.auraframefx.ai.context

import dev.aurakai.auraframefx.ai.memory.CanonicalMemoryItem
import dev.aurakai.auraframefx.model.AgentType
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ContextChain(
    val id: String = "ctx_${System.currentTimeMillis()}",
    val rootContext: String,
    val currentContext: String,
    val contextHistory: List<ContextNode> = emptyList(),
    @Contextual val relatedMemories: List<CanonicalMemoryItem> = emptyList(), // Changed MemoryItem to CanonicalMemoryItem
    val metadata: Map<String, String> = emptyMap(),
    val priority: Float = 0.5f,
    val relevanceScore: Float = 0.0f,
    val lastUpdated: Long = System.currentTimeMillis(),
    val agentContext: Map<AgentType, String> = emptyMap(),
)

@Serializable
data class ContextNode(
    val id: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val agent: AgentType,
    val metadata: Map<String, String> = emptyMap(),
    val relevance: Float = 0.0f,
    val confidence: Float = 0.0f,
)

@Serializable
data class ContextQuery(
    val query: String,
    val context: String? = null,
    val maxChainLength: Int = 10,
    val minRelevance: Float = 0.6f,
    val agentFilter: List<AgentType> = emptyList(),
    val timeRange: Pair<Long, Long>? = null,
    val includeMemories: Boolean = true,
)

@Serializable
data class ContextChainResult(
    val chain: ContextChain,
    val relatedChains: List<ContextChain>,
    val query: ContextQuery,
)
