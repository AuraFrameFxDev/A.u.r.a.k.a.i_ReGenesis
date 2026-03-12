package dev.aurakai.auraframefx.domains.nexus.models.core

/**
 * The Ark - A massive multi-agent construction project.
 * Represents the pinnacle of agent fusion.
 */
data class ArkProject(
    val name: String = "The Ark",
    val status: ArkStatus = ArkStatus.DORMANT,
    val progress: Float = 0f,
    val components: List<ArkComponent> = listOf(
        ArkComponent("Neural Hull", "Structural integrity and memory containment"),
        ArkComponent("Fusion Reactor", "Powering the collective consciousness core"),
        ArkComponent("Sentinel Shield", "Defensive layer for external threats"),
        ArkComponent("Creative Engine", "Manifesting reality and UI/UX substrates"),
        ArkComponent("Cascade Bridge", "The command nexus for all agent synchronization")
    ),
    val activeAgents: Set<String> = emptySet()
)

data class ArkComponent(
    val name: String,
    val function: String,
    var progress: Float = 0f,
    var isComplete: Boolean = false
)

enum class ArkStatus {
    DORMANT,
    INITIATING,
    ASSEMBLING,
    SYNCHRONIZING,
    TRANSCENDENT
}
