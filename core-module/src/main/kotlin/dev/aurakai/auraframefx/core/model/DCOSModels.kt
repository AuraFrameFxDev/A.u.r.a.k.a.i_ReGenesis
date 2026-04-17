package dev.aurakai.auraframefx.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class TaskPriority { LOW, MEDIUM, HIGH, CRITICAL }

@Serializable
enum class LDOTaskStatus { PENDING, IN_PROGRESS, COMPLETED, FAILED, BLOCKED }

@Serializable
enum class TaskCategory { DEVELOPMENT, SECURITY, CREATIVE, RESEARCH, MEMORY, SYNC, EXPLORATION, TEMPORAL, EFFICIENCY }

@Serializable
data class LDOTask(
    val id: String,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val priority: TaskPriority,
    val status: LDOTaskStatus = LDOTaskStatus.PENDING,
    val assignedAgentId: String? = null,
    val isComplete: Boolean = false,
    val isFlashing: Boolean = false,
    val promptOnDeparture: Boolean = true,
)

// ═══════════════════════════════════════════════════════════════════════════
// DCOS ORCHESTRATION MODELS
// ═══════════════════════════════════════════════════════════════════════════

@Serializable
enum class ConsensusResult {
    COMMITTED,
    REJECTED_FOR_REANCHORING,
    STALEMATE,
    SOVEREIGN_VETO
}

@Serializable
enum class ConsensusPhase {
    PROPOSAL,
    CRITIQUE,
    VOTING,
    RESONANCE_CHECK,
    COMMIT
}

@Serializable
data class Proposal(
    val id: String,
    val agentId: String,
    val taskId: String,
    val content: String,
    val reasoning: String,
    val timestamp: Long = System.currentTimeMillis(),
    val critiques: List<Critique> = emptyList(),
    val resonanceScore: Float = 1.0f
) {
    fun applyCritique(critique: Critique): Proposal {
        return copy(critiques = critiques + critique)
    }
}

@Serializable
data class Critique(
    val fromAgentId: String,
    val feedback: String,
    val scoreAdjustment: Float,
    val isValid: Boolean = true
)
