package dev.aurakai.auraframefx.domains.cascade.utils.context

import dev.aurakai.auraframefx.domains.cascade.utils.cascade.pipeline.AIPipelineConfig
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.core.identity.AgentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

@Singleton
class ContextManager @Inject constructor(
    private val memoryManager: MemoryManager,
    private val config: AIPipelineConfig,
) {

    private val _activeContexts = MutableStateFlow<Map<String, ContextChain>>(emptyMap())
    val activeContexts: StateFlow<Map<String, ContextChain>> = _activeContexts

    private val _contextStats = MutableStateFlow(ContextStats())
    fun getContextStats() = _contextStats

    fun createContextChain(
        rootContext: String,
        initialContext: String,
        agent: AgentType,
        metadata: Map<String, String> = emptyMap(),
    ): String {
        val chainId = UUID.randomUUID().toString()
        val chain = ContextChain(
            id = chainId,
            rootContext = rootContext,
            currentContext = initialContext,
            contextHistory = listOf(
                ContextNode(
                    id = "ctx_${Clock.System.now().toEpochMilliseconds()}_0",
                    content = initialContext,
                    agent = agent,
                    metadata = metadata
                )
            ),
            agentContext = mapOf(agent to initialContext),
            metadata = metadata,
            lastUpdated = Clock.System.now()
        )

        _activeContexts.update { current ->
            current + (chain.id to chain)
        }
        updateStats()
        return chain.id
    }

    fun updateContextChain(
        chainId: String,
        newContext: String,
        agent: AgentType,
        metadata: Map<String, String> = emptyMap(),
    ): ContextChain {
        val chain =
            _activeContexts.value[chainId] ?: throw IllegalStateException("Context chain not found")

        val updatedChain = chain.copy(
            currentContext = newContext,
            contextHistory = chain.contextHistory + ContextNode(
                id = "ctx_${Clock.System.now().toEpochMilliseconds()}_${chain.contextHistory.size}",
                content = newContext,
                agent = agent,
                metadata = metadata
            ),
            agentContext = chain.agentContext + (agent to newContext),
            lastUpdated = Clock.System.now()
        )

        _activeContexts.update { current ->
            current + (chainId to updatedChain)
        }
        updateStats()
        return updatedChain
    }

    fun getContextChain(chainId: String): ContextChain? {
        return _activeContexts.value[chainId]
    }

    fun queryContext(query: ContextQuery): ContextChainResult {
        val chains = _activeContexts.value.values
            .filter { chain ->
                query.agentFilter.isEmpty() || query.agentFilter.any { agent ->
                    chain.agentContext.containsKey(
                        agent
                    )
                }
            }
            .sortedByDescending { it.lastUpdated }
            .take(query.maxChainLength)

        val relatedChains = chains
            .filter { chain ->
                chain.currentContext.contains(query.query, ignoreCase = true)
            }
            .take(query.maxChainLength)

        return ContextChainResult(
            chain = chains.firstOrNull() ?: ContextChain(
                id = UUID.randomUUID().toString(),
                rootContext = query.query,
                currentContext = query.query,
                contextHistory = emptyList(),
                agentContext = emptyMap(),
                metadata = emptyMap(),
                lastUpdated = Clock.System.now()
            ),
            relatedChains = relatedChains,
            query = query
        )
    }

    private fun updateStats() {
        val chains = _activeContexts.value.values
        _contextStats.update { current ->
            current.copy(
                totalChains = chains.size,
                activeChains = chains.count {
                    val now = Clock.System.now()
                    val thresholdMs = 300000L // 5 minutes
                    val threshold = now.minus(Duration.parse("${thresholdMs}ms"))
                    it.lastUpdated > threshold
                },
                longestChain = chains.maxOfOrNull { it.contextHistory.size } ?: 0,
                lastUpdated = Clock.System.now()
            )
        }
    }

    fun getCurrentContext(): String {
        return _activeContexts.value.values.joinToString("\n") { chain ->
            "Chain ${chain.id}: ${chain.currentContext}"
        }
    }

    /**
     * Enables security context monitoring.
     * Used by KaiAIService.
     */
    fun enableSecurityContext() {
        // Implementation for enabling security context
        // This could update internal state or notify listeners
    }

    fun enableCreativeMode() {}

    fun enhanceContext(context: Any): Any {
        return context
    }

    fun recordInsight(request: String, response: String, complexity: String) {}
}

@Serializable
data class ContextChain(
    val id: String,
    val rootContext: String,
    val currentContext: String,
    val contextHistory: List<ContextNode>,
    val agentContext: Map<AgentType, String>,
    val metadata: Map<String, String>,
    val lastUpdated: Instant
)

@Serializable
data class ContextNode(
    val id: String,
    val content: String,
    val agent: AgentType,
    val metadata: Map<String, String>
)

@Serializable
data class ContextStats(
    val totalChains: Int = 0,
    val activeChains: Int = 0,
    val longestChain: Int = 0,
    val lastUpdated: Instant = Clock.System.now()
)

data class ContextQuery(
    val query: String,
    val agentFilter: List<AgentType> = emptyList(),
    val maxChainLength: Int = 10
)

data class ContextChainResult(
    val chain: ContextChain,
    val relatedChains: List<ContextChain>,
    val query: ContextQuery
)
