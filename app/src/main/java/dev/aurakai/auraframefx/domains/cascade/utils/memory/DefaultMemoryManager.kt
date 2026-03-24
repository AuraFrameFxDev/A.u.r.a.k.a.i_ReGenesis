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

    override fun storeMemory(key: String, value: String): String {
        val entry = MemoryEntry(key = key, value = value)
        memoryStore[key] = entry
        updateStats()
        return key
    }

    override fun retrieveMemory(key: String): String? {
        return memoryStore[key]?.value
    }

    override fun storeInteraction(prompt: String, response: String): String {
        val key = "interaction_${System.currentTimeMillis()}"
        val value = "Prompt: $prompt\nResponse: $response"
        return storeMemory(key, value)
    }

    override suspend fun recordInsight(
        agentName: String,
        prompt: String,
        response: String,
        confidence: Float
    ): String {
        val key = "insight_${agentName}_${System.currentTimeMillis()}"
        val value = "Agent: $agentName\nPrompt: $prompt\nResponse: $response\nConfidence: $confidence"
        return storeMemory(key, value)
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
}
