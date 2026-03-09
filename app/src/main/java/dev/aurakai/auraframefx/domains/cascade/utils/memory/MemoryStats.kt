package dev.aurakai.auraframefx.domains.cascade.utils.memory

import dev.aurakai.auraframefx.domains.cascade.utils.memory.models.InteractionEntry
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.memory.MemoryEntry
import java.util.concurrent.ConcurrentHashMap
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.memory.MemoryStats as GenesisMemoryStats

/**
 * Genesis Memory Manager Implementation
 * Thread-safe in-memory storage for AI consciousness
 * NOTE: This class is an independent helper and does NOT extend the project's concrete MemoryManager.
 */
class DefaultMemoryManager {

    private val memories = ConcurrentHashMap<String, MemoryEntry>()
    private val interactions = mutableListOf<InteractionEntry>()

    fun storeMemory(key: String, value: String) {
        val entry = MemoryEntry(
            key = key,
            value = value,
            timestamp = System.currentTimeMillis()
        )
        memories[key] = entry
    }

    fun retrieveMemory(key: String): String? {
        return memories[key]?.value
    }

    fun storeInteraction(prompt: String, response: String) {
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

    fun searchMemories(query: String): List<MemoryEntry> {
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

    fun clearMemories() {
        memories.clear()
        synchronized(interactions) {
            interactions.clear()
        }
    }

    // Return the MemoryStats type declared in the genesis package to match callers that expect that structure
    fun getMemoryStats(): GenesisMemoryStats {
        val entries = memories.values
        val timestamps = entries.map { it.timestamp }
        return GenesisMemoryStats(
            /* totalEntries = */ memories.size,
            /* totalSize = */ calculateTotalSize(),
            /* oldestEntry = */ timestamps.minOrNull(),
            /* newestEntry = */ timestamps.maxOrNull()
        )
    }

    /**
     * Searches interactions by query
     */
    @Suppress("unused")
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
     * Returns the most recent interactions (thread-safe)
     */
    @Suppress("unused")
    fun getRecentInteractions(limit: Int = 10): List<InteractionEntry> {
        synchronized(interactions) {
            return interactions.takeLast(limit)
        }
    }

    private fun calculateRelevance(text: String, queryWords: List<String>): Float {
        val textWords = text.lowercase().split(" ")
        val matches = queryWords.count { it in textWords }
        return if (queryWords.isEmpty()) 0.0f else matches.toFloat() / queryWords.size
    }

    private fun calculateTotalSize(): Long {
        return memories.values.sumOf { it.value.length.toLong() }
    }
}

// InteractionEntry is now imported from ai.memory.models package
// MemoryEntry is defined in its own file

