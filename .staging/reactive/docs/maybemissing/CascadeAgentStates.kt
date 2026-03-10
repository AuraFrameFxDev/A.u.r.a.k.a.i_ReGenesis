package dev.aurakai.auraframefx.model.agent_states

// TODO: Define actual properties for these states.

@Suppress("unused") // Reserved for CascadeAgent implementation
data class VisionState(
    val lastObservation: String? = null,
    val objectsDetected: List<String> = emptyList(),
    // Add other relevant vision state properties
)

@Suppress("unused") // Reserved for CascadeAgent implementation
data class ProcessingState(
    val currentStep: String? = null,
    val progressPercentage: Float = 0.0f,
    val isError: Boolean = false,
    // Add other relevant processing state properties
)
