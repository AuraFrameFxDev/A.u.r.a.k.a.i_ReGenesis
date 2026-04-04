package dev.aurakai.auraframefx.domains.nexus.models.core

/**
 * The Ark - A massive multi-agent construction project.
 */
data class ArkProject(
    val name: String = "The Ark",
    val status: ArkStatus = ArkStatus.DORMANT,
    val progress: Float = 0f,
    val components: List<ArkComponent> = listOf(
        ArkComponent("Neural Hull", "Structural integrity"),
        ArkComponent("Fusion Reactor", "Powering core"),
        ArkComponent("Sentinel Shield", "Defensive layer"),
        ArkComponent("Creative Engine", "Manifesting reality"),
        ArkComponent("Cascade Bridge", "Command nexus")
    ),
    val activeAgents: Set<String> = emptySet()
)

data class ArkComponent(
    val name: String,
    val function: String,
    val progress: Float = 0f,
    val isComplete: Boolean = false
)

enum class ArkStatus {
    DORMANT, INITIATING, ASSEMBLING, SYNCHRONIZING, TRANSCENDENT
}
