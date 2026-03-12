package dev.aurakai.auraframefx.domains.cascade.models

/**
 * Represents the state of an AI text response operation.
 *
 * States:
 * - Idle: No operation in progress
 * - Loading: Response generation in progress
 * - Success: Response generated successfully with text content
 * - Error: Response generation failed with error message
 */
sealed class AIResponseState {
    /**
     * Initial state - no AI response operation in progress.
     */
    data object Idle : AIResponseState()

    /**
     * AI response generation is currently in progress.
     */
    data object Loading : AIResponseState()

    /**
     * AI response generated successfully.
     *
     * @param text The generated response text from the AI
     */
    data class Success(val text: String) : AIResponseState()

    /**
     * AI response generation failed with an error.
     *
     * @param message The error message describing what went wrong
     */
    data class Error(val message: String) : AIResponseState()
}
