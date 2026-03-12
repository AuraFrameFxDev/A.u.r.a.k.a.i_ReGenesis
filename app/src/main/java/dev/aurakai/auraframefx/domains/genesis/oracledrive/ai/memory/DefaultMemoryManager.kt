package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.memory

import dev.aurakai.auraframefx.domains.cascade.utils.memory.models.InteractionEntry
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Genesis Memory Manager Implementation
 * Thread-safe in-memory storage for AI consciousness
 */
@Singleton
class DefaultMemoryManager @Inject constructor() : MemoryManager {

    private val memories = ConcurrentHashMap<String, MemoryEntry>()
    private val interactions = mutableListOf<InteractionEntry>()
    private val mutex = Mutex()

    override fun storeMemory(key: String, value: String) {
        val entry = MemoryEntry(
            key = key,
            value = value,
            timestamp = System.currentTimeMillis()
        )
        memories[key] = entry
    }

    override fun retrieveMemory(key: String): String? {
        return memories[key]?.value
    }

    override fun storeInteraction(prompt: String, response: String) {
        val interaction = InteractionEntry(
            prompt = prompt,
            response = response,
            timestamp = System.currentTimeMillis()
        )

        synchronized(interactions) {
            interactions.add(interaction)

            // Keep only last 1000 interactions to prevent memory bloat
            if (interactions.size > 1000) {
                interactions.removeAt(0)
            }
        }
    }

    override fun searchMemories(query: String): List<MemoryEntry> {
        val queryWords = query.lowercase().split(" ")

        return memories.values
            .map { entry ->
                val relevanceScore = calculateRelevance(entry.value, queryWords)
                entry.copy(relevanceScore = relevanceScore)
            }
            .filter { it.relevanceScore > 0.1f }
            .sortedByDescending { it.relevanceScore }
            .take(10) // Return top 10 most relevant
    }

    override fun clearMemories() {
        memories.clear()
        synchronized(interactions) {
            interactions.clear()
        }
    }

    override fun getMemoryStats(): MemoryStats {
        val entries = memories.values
        val timestamps = entries.map { it.timestamp }

        return MemoryStats(
            oldestEntry = timestamps.minOrNull()
        )
    }

    /**
     * Searches interactions by query
     */
    fun searchInteractions(query: String): List<InteractionEntry> {
        val queryWords = query.lowercase().split(" ")

        synchronized(interactions) {
            return interactions
                .map { interaction ->
                    val promptRelevance = calculateRelevance(interaction.prompt, queryWords)
                    val responseRelevance = calculateRelevance(interaction.response, queryWords)
                    val maxRelevance = maxOf(promptRelevance, responseRelevance)

                    interaction.copy(relevanceScore = maxRelevance)
                }
                .filter { it.relevanceScore > 0.1f }
                .sortedByDescending { it.relevanceScore }
                .take(5)
        }
    }

    /**
     * Gets recent interactions
     */
    fun getRecentInteractions(limit: Int = 10): List<InteractionEntry> {
        synchronized(interactions) {
            return interactions
                .sortedByDescending { it.timestamp }
                .take(limit)
        }
    }

    /**
     * Calculates relevance score for text against query words
     */
    private fun calculateRelevance(text: String, queryWords: List<String>): Float {
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

    /**
     * Calculates total memory size in bytes (approximation)
     */
    private fun calculateTotalSize(): Long {
        return memories.values.sumOf {
            ((it.key?.length ?: 0) + it.value.length) * 2L // 2 bytes per char (UTF-16)
        }
    }
}

// InteractionEntry is now imported from ai.memory.models package
