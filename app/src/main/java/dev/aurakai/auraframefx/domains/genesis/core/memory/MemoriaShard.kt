package dev.aurakai.auraframefx.domains.genesis.core.memory

import kotlinx.datetime.Instant

/**
 * Represents a memory shard in the Memoria system
 */
data class MemoriaShard(
    val id: String,
    val content: String,
    val category: String = "general",
    val timestamp: Instant? = null,
    val priority: Int = 0,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * External context storage for the memoria system
 */
val _externalContext: MutableList<MemoriaShard> = mutableListOf()

/**
 * Add a shard to the external context
 */
fun add(shard: MemoriaShard) {
    _externalContext.add(shard)
}

/**
 * Get summary of all shards
 */
fun summary(): String {
    return "Total shards: ${_externalContext.size}"
}
