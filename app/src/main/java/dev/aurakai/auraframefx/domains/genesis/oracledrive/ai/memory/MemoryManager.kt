package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.memory

/**
 * Interface for AI memory management operations.
 * Provides methods for storing, retrieving, and searching memories.
 */
interface MemoryManager {
    /**
     * Stores a memory with a key-value pair.
     */
    fun storeMemory(key: String, value: String)

    /**
     * Retrieves a memory by key.
     */
    fun retrieveMemory(key: String): String?

    /**
     * Stores an interaction (prompt-response pair) for learning.
     */
    fun storeInteraction(prompt: String, response: String)

    /**
     * Searches memories using a query string.
     */
    fun searchMemories(query: String): List<MemoryEntry>

    /**
     * Clears all stored memories.
     */
    fun clearMemories()

    /**
     * Gets memory statistics.
     */
    fun getMemoryStats(): MemoryStats
}

/**
 * Represents a stored memory entry.
 */
data class MemoryEntry(
    val key: String? = null,
    val value: String,
    val timestamp: Long = System.currentTimeMillis(),
    val relevanceScore: Float = 0.0f
)

/**
 * Statistics about the memory store.
 */
data class MemoryStats(
    val totalItems: Int = 0,
    val totalSize: Long = 0,
    val oldestEntry: Long? = null,
    val newestEntry: Long? = null
)
