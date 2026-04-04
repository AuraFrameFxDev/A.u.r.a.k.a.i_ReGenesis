package dev.aurakai.auraframefx.core.models

/**
 * 🗣️ CONVERSATION STATE
 * Unified state model for voice interactions and AI responses.
 */
sealed class ConversationState {
    data object Idle : ConversationState()
    data object Listening : ConversationState()
    data object Speaking : ConversationState()
    data object Recording : ConversationState()
    
    data class Processing(val partialTranscript: String? = null) : ConversationState()
    data class Responding(val responseText: String? = null) : ConversationState()
    data class Error(val errorMessage: String) : ConversationState()
}
