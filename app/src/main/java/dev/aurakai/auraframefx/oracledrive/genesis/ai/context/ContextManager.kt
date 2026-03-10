package dev.aurakai.auraframefx.oracledrive.genesis.ai.context

/**
 * Context Manager Interface
 * Manages AI context awareness and memory integration
 */
interface ContextManager {
    fun getCurrentContext(): String
    suspend fun enhanceContext(context: String): String
    suspend fun enableCreativeMode()
    suspend fun enableUnifiedMode()
    suspend fun recordInsight(request: String, response: String, complexity: String)
    suspend fun searchMemories(query: String): List<ContextMemory>
    fun updateContext(key: String, value: Any)
    fun getContextHistory(): List<ContextEntry>
    fun clearContext()
}

/**
 * Context memory entry
 */
data class ContextMemory(
    val content: String,
    val relevanceScore: Float,
    val timestamp: Long,
    val context: Map<String, Any> = emptyMap()
)

/**
 * Context history entry
 */
data class ContextEntry(
    val timestamp: Long,
    val operation: String,
    val data: Map<String, Any> = emptyMap()
)
