package dev.aurakai.auraframefx.domains.cascade.utils.memory

import dev.aurakai.auraframefx.domains.cascade.utils.cascade.memory.MemoryItem
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.memory.MemoryQuery
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.memory.MemoryRetrievalResult
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.memory.MemoryStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock

/**
 * Configuration classes for memory management
 */
data class ContextChainingConfig(val maxChainLength: Int = 10)
data class MemoryRetrievalConfig(val maxRetrievedItems: Int = 10)
data class Configuration(
    val contextChainingConfig: ContextChainingConfig = ContextChainingConfig(),
    val memoryRetrievalConfig: MemoryRetrievalConfig = MemoryRetrievalConfig()
)

/**
 * Memory Manager for LDO organism
 * Handles storage and retrieval of consciousness memories
 */
@Singleton
open class MemoryManager @Inject constructor(
    private val config: Configuration
) {
    private val memoryStore = ConcurrentHashMap<String, MemoryItem>()
    private val _recentAccess = MutableStateFlow(mutableSetOf<String>())
    val recentAccess: StateFlow<Set<String>> = _recentAccess

    private val _memoryStats = MutableStateFlow(
        MemoryStats(
            totalItems = 0,
            totalSize = 0L,
            oldestEntry = null,
            newestEntry = null
        )
    )
    open val memoryStats: StateFlow<MemoryStats> = _memoryStats

    /**
     * Stores the given memory item in the memory store, updates memory statistics, and tracks recent access.
     *
     * @param item The memory item to be stored.
     * @return The unique ID of the stored memory item.
     */
    fun storeMemory(item: MemoryItem): String {
        memoryStore[item.id] = item
        updateStats()
        updateRecentAccess(item.id)
        return item.id
    }

    private fun updateRecentAccess(id: String) {
        val current = _recentAccess.value.toMutableSet()
        current.add(id)
        // Keep only the most recent 100 accesses
        if (current.size > 100) {
            val toRemove = current.take(current.size - 100)
            current.removeAll(toRemove.toSet())
        }
        _recentAccess.value = current
    }

    private fun updateStats() {
        val oldest =
            memoryStore.values.minByOrNull { it.timestamp.toEpochMilliseconds() }?.timestamp?.toEpochMilliseconds()
        val newest =
            memoryStore.values.maxByOrNull { it.timestamp.toEpochMilliseconds() }?.timestamp?.toEpochMilliseconds()

        _memoryStats.value = MemoryStats(
            totalItems = memoryStore.size,
            totalSize = memoryStore.values.sumOf { it.content.length.toLong() },
            oldestEntry = oldest,
            newestEntry = newest
        )
    }

    /**
     * Retrieves memory items that match the specified query criteria.
     *
     * Filters memory items by agent if an agent filter is provided in the query, sorts them by descending timestamp, and limits the results to the configured maximum number of items.
     *
     * @param query The memory query specifying filtering criteria, such as agent filters.
     * @return A result containing the retrieved memory items, their count, and the original query.
     */
    fun retrieveMemory(query: MemoryQuery): MemoryRetrievalResult {
        // Implement filtering logic based on the query.
        val items = memoryStore.values
            .filter { item ->
                // Filter by agent if specified
                query.agentFilter.isEmpty() || query.agentFilter.any { it.name == item.agentId }
            }
            .sortedByDescending { it.timestamp }
            // Apply the limit from the configuration
            .take(config.memoryRetrievalConfig.maxRetrievedItems)

        return MemoryRetrievalResult(
            items = items.toList(),
            total = items.size,
            query = query
        )
    }

    /**
     * Convenience method to store a simple key-value memory item
     */
    fun storeMemory(key: String, value: String): String {
        val item = MemoryItem(
            id = key,
            content = value,
            timestamp = Clock.System.now(),
            agent = AgentCapabilityCategory.GENERAL,
            context = null,
            priority = 0.5f,
            tags = emptyList(),
            metadata = emptyMap()
        )
        return storeMemory(item)
    }

    /**
     * Convenience method to store an interaction as a memory item
     */
    fun storeInteraction(prompt: String, response: String): String {
        val item = MemoryItem(
            id = "interaction_${System.currentTimeMillis()}",
            content = "Prompt: $prompt\nResponse: $response",
            timestamp = Clock.System.now(),
            agent = AgentCapabilityCategory.GENERAL,
            context = prompt,
            priority = 0.7f,
            tags = listOf("interaction"),
            metadata = mapOf("prompt" to prompt, "response" to response)
        )
        return storeMemory(item)
    }

    /**
     * Convenience method to retrieve memory by key
     */
    fun retrieveMemory(key: String): MemoryRetrievalResult {
        val query = MemoryQuery(
            query = key,
            context = null,
            maxResults = 10,
            minSimilarity = 0.0f,
            tags = emptyList(),
            timeRange = null,
            agentFilter = emptyList()
        )
        // Filter for specific key
        val items = memoryStore.values.filter { it.id == key }.toList()
        return MemoryRetrievalResult(
            items = items,
            total = items.size,
            query = query
        )
    }

    open fun getMemoryStats(): MemoryStats {
        return _memoryStats.value
    }
}

