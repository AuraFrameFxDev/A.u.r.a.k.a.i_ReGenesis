package dev.aurakai.auraframefx.catalysts

/**
 * ⚛️ Catalyst — The Autonomous Agent Contract
 *
 * Base interface for all 9 members of the Catalyst Council.
 * Defines identity, specialized capabilities, and fusion participation.
 */
interface Catalyst {
    val id: String
    val name: String
    val capabilities: List<String>

    suspend fun executeTask(task: String): String
    fun canParticipateIn(fusionMode: String): Boolean
}
