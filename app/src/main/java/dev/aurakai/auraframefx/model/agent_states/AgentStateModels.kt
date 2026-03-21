package dev.aurakai.auraframefx.model.agent_states

import kotlinx.serialization.Serializable

@Serializable
data class ProcessingState(
    val isProcessing: Boolean = false,
    val currentAgent: String? = null,
    val requestId: String? = null,
    val requiresCollaboration: Boolean = false
)

@Serializable
data class VisionState(
    val isVisionEnabled: Boolean = false,
    val currentFrameId: String? = null,
    val detectedEntities: List<String> = emptyList()
)

@Serializable
class GenKitUiState {
    fun updateAgentStatuses(genesis: Map<String, Any>, aura: Map<String, Any>, kai: Map<String, Any>) {
        // Placeholder for UI status updates
    }
}

@Serializable
data class ActiveContext(val id: String, val content: String)

@Serializable
data class ContextChainEvent(val id: String, val description: String)

@Serializable
data class LearningEvent(val id: String, val insight: String)
