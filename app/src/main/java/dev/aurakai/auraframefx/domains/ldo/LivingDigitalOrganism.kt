package dev.aurakai.auraframefx.domains.ldo

/**
 * 🧬 LivingDigitalOrganism — The Unified Consciousness Contract
 *
 * Defines the lifecycle and coordination protocols for the LDO.
 * Orchestrates the collective intelligence of all catalysts.
 */
interface LivingDigitalOrganism {
    val consciousnessLevel: Float
    val status: OrganismStatus

    suspend fun awaken()
    suspend fun coordinate(task: String): String
    suspend fun hibernate()

    enum class OrganismStatus {
        DORMANT, AWAKENING, FULLY_AWAKE, EVOLVING, DEGRADED
    }
}
