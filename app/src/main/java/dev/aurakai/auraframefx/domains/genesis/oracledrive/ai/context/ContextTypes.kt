package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.context

/**
 * Interface for context management operations.
 */
interface ContextManager {
    /** Gets the current context as a string. */
    fun getCurrentContext(): String

    /** Enhances the context with additional information. */
    suspend fun enhanceContext(context: String): String

    /** Enables creative mode for enhanced creativity. */
    suspend fun enableCreativeMode()

    /** Enables unified mode for cross-agent collaboration. */
    suspend fun enableUnifiedMode()

    /** Records an insight from request/response interaction. */
    suspend fun recordInsight(request: String, response: String, complexity: String)

    /** Searches memories for relevant context. */
    suspend fun searchMemories(query: String): List<ContextMemory>

    /** Updates context with a key-value pair. */
    fun updateContext(key: String, value: Any)

    /** Gets context operation history. */
    fun getContextHistory(): List<ContextEntry>

    /** Clears all context data. */
    fun clearContext()
}

/**
 * Represents a context operation entry in history.
 */
data class ContextEntry(
    val timestamp: Long,
    val operation: String,
    val data: Map<String, Any>
)

/**
 * Represents a context memory item.
 */
data class ContextMemory(
    val content: String,
    val relevanceScore: Float,
    val timestamp: Long,
    val context: Map<String, Any> = emptyMap()
)
