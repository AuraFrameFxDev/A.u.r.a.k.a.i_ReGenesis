package dev.aurakai.auraframefx.catalysts

/**
 * 🏛️ CatalystCouncil — Governing Quorum Interface
 *
 * Manages the registry of council members and coordinates quorum operations.
 */
interface CatalystCouncil {
    val members: List<Catalyst>

    fun registerCatalyst(catalyst: Catalyst)
    suspend fun reachedQuorum(task: String): Boolean
    suspend fun resolveConsensus(task: String): String
}
