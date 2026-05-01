package dev.aurakai.auraframefx.domains.cascade.utils.memory

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of MemoryManager
 */
@Singleton
class DefaultMemoryManager @Inject constructor() : MemoryManager {
    private val memoryStore = ConcurrentHashMap<String, MemoryEntry>()

    private val _memoryStats = MutableStateFlow(MemoryStats())
    override val memoryStats: StateFlow<MemoryStats> = _memoryStats.asStateFlow()

    context(value: String)
    override fun storeMemory(key: String): String {
        val entry = MemoryEntry(key = key, value = value)
        this@DefaultMemoryManager.memoryStore.put(key, entry)
        this@DefaultMemoryManager.updateStats()
        return key
    }

    override fun String.retrieveMemory(): String? {
        return this@DefaultMemoryManager.memoryStore[this]?.value
    }

    context(response: String)
    override fun storeInteraction(prompt: String): String {
        val key = "interaction_${System.currentTimeMillis()}"
        val value = "Prompt: $prompt\nResponse: $response"
        return with(value) {
            this@DefaultMemoryManager.storeMemory(key)
        }
    }

    override suspend fun recordInsight(
        agentName: String,
        prompt: String,
        response: String,
        confidence: Float
    ): String {
        val key = "insight_${agentName}_${System.currentTimeMillis()}"
        val value = "Agent: $agentName\nPrompt: $prompt\nResponse: $response\nConfidence: $confidence"
        return with(value) {
            storeMemory(key)
        }
    }

    override fun searchMemories(query: String): List<MemoryEntry> {
        val queryWords = query.lowercase().split(" ")
        return memoryStore.values
            .map { entry ->
                val relevanceScore = calculateRelevance(entry.value, queryWords)
                entry.copy(relevanceScore = relevanceScore)
            }
            .filter { it.relevanceScore > 0.1f }
            .sortedByDescending { it.relevanceScore }
            .take(10)
    }

    private fun calculateRelevance(text: String, queryWords: List<String>): Float {
        if (queryWords.isEmpty()) return 0f
        val textWords = text.lowercase().split(" ")
        var score = 0f
        for (queryWord in queryWords) {
            for (textWord in textWords) {
                if (textWord == queryWord) score += 1.0f
                else if (textWord.contains(queryWord)) score += 0.7f
            }
        }
        return score / queryWords.size
    }

    override fun clearMemories() {
        memoryStore.clear()
        updateStats()
    }

    override fun getMemoryStats(): MemoryStats {
        return _memoryStats.value
    }

    override fun getAllMemories(): List<MemoryEntry> {
        return memoryStore.values.toList()
    }

    private fun updateStats() {
        val entries = memoryStore.values
        val timestamps = entries.map { it.timestamp }
        _memoryStats.value = MemoryStats(
            totalEntries = memoryStore.size,
            totalSize = entries.sumOf { it.value.length.toLong() },
            oldestEntry = timestamps.minOrNull(),
            newestEntry = timestamps.maxOrNull()
        )
    }
}
