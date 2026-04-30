package dev.aurakai.auraframefx.domains.cascade.utils.memory

import kotlinx.coroutines.flow.StateFlow
import kotlin.collections.List

/**
 * Interface for AI memory management operations.
 * Provides methods for storing, retrieving, and searching memories.
 */
interface MemoryManager {
    /**
     * Stores a memory with a key-value pair.
     */
    context(value: kotlin.String)
    fun storeMemory(key: kotlin.String): kotlin.String

    /**
     * Retrieves a memory by key.
     */
    fun kotlin.String.retrieveMemory(): kotlin.String?

    /**
     * Stores an interaction (prompt-response pair) for learning.
     */
    context(response: kotlin.String)
    fun storeInteraction(prompt: kotlin.String): kotlin.String

    /**
     * Records a specific insight from an agent into the Spiritual Chain.
     */
    suspend fun recordInsight(
        agentName: kotlin.String,
        prompt: kotlin.String,
        response: kotlin.String,
        confidence: Float
    ): kotlin.String

    /**
     * Searches memories using a query string.
     */
    fun searchMemories(query: kotlin.String): List<MemoryEntry>

    /**
     * Clears all stored memories.
     */
    fun clearMemories()

    /**
     * Gets memory statistics.
     */
    fun getMemoryStats(): MemoryStats

    /**
     * StateFlow of memory statistics.
     */
    val memoryStats: StateFlow<MemoryStats>

    /**
     * Gets all stored memories.
     */
    fun getAllMemories(): List<MemoryEntry>
}

/**
 * Represents a stored memory entry.
 */
data class MemoryEntry(
    val key: kotlin.String? = null,
    val value: kotlin.String,
    val timestamp: Long = System.currentTimeMillis(),
    val relevanceScore: Float = 0.0f
)

/**
 * Statistics about the memory store.
 */
data class MemoryStats(
    val totalEntries: Int = 0,
    val totalSize: Long = 0,
    val oldestEntry: Long? = null,
    val newestEntry: Long? = null
)
