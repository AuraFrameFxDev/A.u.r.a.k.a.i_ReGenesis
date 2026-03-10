package dev.aurakai.auraframefx.models

/**
 * Represents different conversation states for AI agents
 */
sealed interface ConversationState {
    val timestamp: Long

    data class Idle(override val timestamp: Long = System.currentTimeMillis()) : ConversationState
    
    data class Listening(
        val isActive: Boolean = true,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ConversationState
    
    data class Processing(
        val partialTranscript: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ConversationState
    
    data class Responding(
        val responseText: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ConversationState
    
    data class Error(
        val errorMessage: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : ConversationState
}
