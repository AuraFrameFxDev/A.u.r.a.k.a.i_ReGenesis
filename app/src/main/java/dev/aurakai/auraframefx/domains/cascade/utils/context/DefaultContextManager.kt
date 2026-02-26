package dev.aurakai.auraframefx.domains.cascade.utils.context

import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.context.ContextEntry
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.context.ContextMemory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.memory.DefaultMemoryManager
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.memory.MemoryManager
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.context.ContextManager as GenesisContextManager

/**
 * Genesis Context Manager Implementation
 * Manages AI context awareness and memory integration
 */
class DefaultContextManager(
    private val memoryManager: MemoryManager = DefaultMemoryManager()
) : GenesisContextManager {

    private val contextData = ConcurrentHashMap<String, Any>()
    private val contextHistory = mutableListOf<ContextEntry>()
    private var isCreativeModeEnabled = false
    private var isUnifiedModeEnabled = false

    override fun getCurrentContext(): String {
        val context = StringBuilder()

        // Add mode information
        if (isCreativeModeEnabled) {
            context.append("[CREATIVE MODE] Enhanced creativity and lateral thinking enabled. ")
        }
        if (isUnifiedModeEnabled) {
            context.append("[UNIFIED MODE] Cross-agent collaboration enabled. ")
        }

        // Add recent context data
        contextData.forEach { (key, value) ->
            context.append("$key: $value; ")
        }

        // Add recent memories
        val recentMemories = memoryManager.searchMemories("recent context")
        if (recentMemories.isNotEmpty()) {
            context.append("Recent insights: ")
            recentMemories.take(3).forEach { memory ->
                context.append("${memory.value}; ")
            }
        }

        return context.toString().trim()
    }

    override suspend fun enhanceContext(context: String): String {
        // Simulate AI processing delay
        delay(50)

        val enhanced = StringBuilder(context)

        // Add contextual enhancements
        if (isCreativeModeEnabled) {
            enhanced.append(" [Enhanced for creative thinking and innovative solutions]")
        }

        if (isUnifiedModeEnabled) {
            enhanced.append(" [Unified agent perspective active]")
        }

        // Search for relevant memories
        val keywords = extractKeywords(context)
        val relevantMemories = keywords.flatMap { keyword ->
            memoryManager.searchMemories(keyword)
        }.distinctBy { it.key }.take(3)

        if (relevantMemories.isNotEmpty()) {
            enhanced.append(" Relevant context: ")
            relevantMemories.forEach { memory ->
                enhanced.append("${memory.value}; ")
            }
        }

        recordContextOperation(
            "enhance",
            mapOf("original_length" to context.length, "enhanced_length" to enhanced.length)
        )

        return enhanced.toString()
    }

    override suspend fun enableCreativeMode() {
        isCreativeModeEnabled = true
        updateContext("creative_mode", true)
        recordContextOperation(
            "enable_creative_mode",
            mapOf("timestamp" to System.currentTimeMillis())
        )
    }

    override suspend fun enableUnifiedMode() {
        isUnifiedModeEnabled = true
        updateContext("unified_mode", true)
        recordContextOperation(
            "enable_unified_mode",
            mapOf("timestamp" to System.currentTimeMillis())
        )
    }

    override suspend fun recordInsight(request: String, response: String, complexity: String) {
        // Store in memory manager
        memoryManager.storeInteraction(request, response)

        // Create a contextual insight
        val insight = "Request complexity: $complexity. Key patterns: ${
            extractKeywords("$request $response").take(3)
        }"
        val timestamp = System.currentTimeMillis()

        memoryManager.storeMemory("insight_$timestamp", insight)

        recordContextOperation(
            "record_insight", mapOf(
                "complexity" to complexity,
                "request_length" to request.length,
                "response_length" to response.length
            )
        )
    }

    override suspend fun searchMemories(query: String): List<ContextMemory> {
        val memoryEntries = memoryManager.searchMemories(query)

        return memoryEntries.map { entry ->
            ContextMemory(
                content = entry.value,
                relevanceScore = entry.relevanceScore,
                timestamp = entry.timestamp,
                context = mapOf(
                    "key" to entry.key,
                    "creative_mode" to isCreativeModeEnabled,
                    "unified_mode" to isUnifiedModeEnabled
                ) as Map<String, Any>
            )
        }
    }

    override fun updateContext(key: String, value: Any) {
        contextData[key] = value
        recordContextOperation(
            "update",
            mapOf("key" to key, "value_type" to value.javaClass.simpleName)
        )
    }

    override fun getContextHistory(): List<ContextEntry> {
        synchronized(contextHistory) {
            return contextHistory.toList()
        }
    }

    override fun clearContext() {
        contextData.clear()
        synchronized(contextHistory) {
            contextHistory.clear()
        }
        isCreativeModeEnabled = false
        isUnifiedModeEnabled = false
        recordContextOperation("clear", mapOf("timestamp" to System.currentTimeMillis()))
    }

    /**
     * Records a context operation for history tracking
     */
    private fun recordContextOperation(operation: String, data: Map<String, Any>) {
        val entry = ContextEntry(
            timestamp = System.currentTimeMillis(),
            operation = operation,
            data = data
        )

        synchronized(contextHistory) {
            contextHistory.add(entry)

            // Keep only last 100 operations
            if (contextHistory.size > 100) {
                contextHistory.removeAt(0)
            }
        }
    }

    /**
     * Extracts keywords from text for memory searching
     */
    private fun extractKeywords(text: String): List<String> {
        return text.lowercase()
            .split(" ")
            .filter { it.length > 3 }
            .filter { !stopWords.contains(it) }
            .distinct()
            .take(10)
    }

    /**
     * Common stop words to filter out
     */
    private val stopWords = setOf(
        "the", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by",
        "this", "that", "these", "those", "is", "are", "was", "were", "be", "been",
        "have", "has", "had", "do", "does", "did", "will", "would", "could", "should"
    )
}

