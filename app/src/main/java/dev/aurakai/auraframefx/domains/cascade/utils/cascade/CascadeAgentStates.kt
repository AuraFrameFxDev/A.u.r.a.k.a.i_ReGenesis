package dev.aurakai.auraframefx.domains.cascade.utils.cascade

/**
 * Data models for CascadeAgent vision and processing states.
 *
 * These models track the Cascade AI's visual perception and task processing status.
 */

@Suppress("unused") // Reserved for CascadeAgent implementation
data class VisionState(
    val lastObservation: String? = null,
    val objectsDetected: List<String> = emptyList(),
    val confidence: Float = 0.0f,
    val timestamp: Long = System.currentTimeMillis(),
    val sceneDescription: String? = null,
    val detectedFaces: Int = 0,
    val detectedText: List<String> = emptyList(),
    val frameAnalysisComplete: Boolean = false
)

@Suppress("unused") // Reserved for CascadeAgent implementation
data class ProcessingState(
    val currentStep: String? = null,
    val progressPercentage: Float = 0.0f,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val startTime: Long? = null,
    val estimatedTimeRemaining: Long? = null,
    val taskId: String? = null,
    val priority: Int = 0,
    val requiresCollaboration: Boolean = false,
    val isProcessing: Boolean = false,
    val currentAgent: String? = null,
    val requestId: String? = null
)
