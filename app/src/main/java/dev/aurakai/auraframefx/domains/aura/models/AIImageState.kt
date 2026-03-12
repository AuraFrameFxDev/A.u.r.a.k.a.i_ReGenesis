package dev.aurakai.auraframefx.domains.aura.models

/**
 * Represents the state of an AI image generation operation.
 *
 * States:
 * - Idle: No operation in progress
 * - Loading: Image generation in progress
 * - Success: Image generated successfully with result
 * - Error: Image generation failed with error message
 */
sealed class AIImageState {
    /**
     * Initial state - no image generation in progress.
     */
    data object Idle : AIImageState()

    /**
     * Image generation is currently in progress.
     */
    data object Loading : AIImageState()

    /**
     * Image generation completed successfully.
     *
     * @param image The generated image as a Bitmap, or URI string if stored externally
     */
    data class Success(val image: Any?) : AIImageState()

    /**
     * Image generation failed with an error.
     *
     * @param message The error message describing what went wrong
     */
    data class Error(val message: String) : AIImageState()
}
