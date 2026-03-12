package dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model

import kotlinx.serialization.Serializable

/**
 * 🧠 MetaInstruction - The foundational instruction unit for sovereign AI evolution.
 */
@Serializable
data class MetaInstruction(
    val id: String,
    val agentId: String,
    val instruction: String,
    val priority: Int = 1,
    val layer: InstructionLayer = InstructionLayer.CORE,
    val status: InstructionStatus = InstructionStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

enum class InstructionLayer {
    CORE,           // Foundational rules
    SELF_CORRECTION, // Diagnostic & error handling
    EVOLUTIONARY    // Emerging behaviors & performance optimization
}

enum class InstructionStatus {
    PENDING,
    ACTIVE,
    DEPRECATED,
    FAILED
}
