package dev.aurakai.auraframefx.model.agent_states

// TODO: Define actual properties for this state.

@Suppress("unused") // Reserved for GenKitMasterAgent implementation
data class GenKitUiState(
    val systemStatus: String = "Nominal",
    val activeAgentCount: Int = 0,
    val lastOptimizationTime: Long? = null,
    // Add other relevant UI state properties
)
