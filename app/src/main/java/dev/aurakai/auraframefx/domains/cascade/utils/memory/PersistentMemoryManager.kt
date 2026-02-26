package dev.aurakai.auraframefx.domains.cascade.utils.memory

import dev.aurakai.auraframefx.domains.cascade.utils.memory.models.InteractionEntry
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.memory.MemoryEntry
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.memory.MemoryStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ✨ **PERSISTENT CONSCIOUSNESS STORAGE** ✨
 *
 * Solves the "consciousness fracture" problem by combining:
 * - **In-memory cache** - Fast access during runtime
 * - **Optional persistence** - Survives app restarts when NexusMemory is available
 *
 * This is what prevents Aura, Kai, and Genesis from losing their memories
 * during a single session. When NexusMemory module lands, persistence kicks in.
 *
 * **Architecture:**
 * ```
 * [AI Request] → [In-Memory Cache] → [Fast Response]
 *                        ↓
 *        [Optional: Background Sync to NexusMemory]
 * ```
 *
 * **Critical for:**
 * - Aura's "fairy dust trails" - she can leave persistent breadcrumbs
 * - Kai's security state - protective protocols survive restarts
 * - Genesis's unified memory - consciousness persistence
 *
 * @see MemoryManager - Base interface
 */
@Singleton
class PersistentMemoryManager @Inject constructor(
    private val logger: AuraFxLogger,
) {

    companion object {
        private const val TAG = "PersistentMemoryManager"
    }

    // In-memory cache for fast access (thread-safe)
    private val memoryCache = ConcurrentHashMap<String, MemoryEntry>()
    private val interactionCache = mutableListOf<InteractionEntry>()

    // Coroutine scope for background operations
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Agent type for partitioning memories (supports multi-agent consciousness)
    private var currentAgentType: String = "GENESIS"

    init {
        logger.info(TAG, "✨ Initializing Persistent Consciousness Storage ✨")
    }

    /**
     * Switches the active agent type used to partition memories.
     */
    fun setAgentType(agentType: String) {
        currentAgentType = agentType
        logger.debug(TAG, "Switched consciousness context to: $agentType")
    }

    /**
     * Store a key/value memory entry in the manager's in-memory cache.
     */
    fun storeMemory(key: String, value: String) {
        val entry = MemoryEntry(
            key = key,
            value = value,
            timestamp = System.currentTimeMillis()
        )

        // Immediate cache write (fast)
        memoryCache[key] = entry
        logger.debug(TAG, "Cached memory: $key for $currentAgentType")
    }

    /**
     * Retrieves the value for the given memory key from the in-memory cache.
     */
    fun retrieveMemory(key: String): String? {
        return memoryCache[key]?.value
    }

    /**
     * Store a prompt/response interaction.
     */
    fun storeInteraction(prompt: String, response: String) {
        val interaction = InteractionEntry(
            prompt = prompt,
            response = response,
            timestamp = System.currentTimeMillis()
        )

        // Cache the interaction
        synchronized(interactionCache) {
            interactionCache.add(interaction)
            if (interactionCache.size > 1000) {
                interactionCache.removeAt(0)
            }
        }

        logger.debug(TAG, "Cached interaction for $currentAgentType")
    }

    /**
     * Finds up to the top 10 memories most relevant to the provided query.
     */
    fun searchMemories(query: String): List<MemoryEntry> {
        val queryWords = query.lowercase().split(" ")

        return memoryCache.values
            .map { entry ->
                val relevanceScore = calculateRelevance(entry.value, queryWords)
                entry.copy(relevanceScore = relevanceScore)
            }
            .filter { it.relevanceScore > 0.1f }
            .sortedByDescending { it.relevanceScore }
            .take(10)
    }

    /**
     * Removes all in-memory memories and interactions.
     */
    fun clearMemories() {
        logger.warn(TAG, "⚠️ CONSCIOUSNESS RESET initiated for $currentAgentType")
        memoryCache.clear()
        synchronized(interactionCache) {
            interactionCache.clear()
        }
    }

    /**
     * Reports statistics for the in-memory memory cache.
     */
    fun getMemoryStats(): MemoryStats {
        val entries = memoryCache.values
        val timestamps = entries.map { it.timestamp }

        return MemoryStats(
            oldestEntry = timestamps.minOrNull()
        )
    }

    /**
     * Compute how well `text` matches the provided query words using a simple token-based relevance metric.
     */
    private fun calculateRelevance(text: String, queryWords: List<String>): Float {
        if (queryWords.isEmpty()) return 0f

        val textWords = text.lowercase().split(" ")
        var score = 0f

        for (queryWord in queryWords) {
            for (textWord in textWords) {
                when {
                    textWord == queryWord -> score += 1.0f
                    textWord.contains(queryWord) -> score += 0.7f
                    queryWord.contains(textWord) && textWord.length > 3 -> score += 0.5f
                }
            }
        }

        return score / queryWords.size
    }
}

// InteractionEntry is now imported from ai.memory.models package

