package dev.aurakai.auraframefx.core.models

import kotlinx.serialization.Serializable

/**
 * 🗣️ CONVERSATION STATE
 */
@Serializable
sealed class ConversationState {
    @Serializable
    data object Idle : ConversationState()
    @Serializable
    data object Listening : ConversationState()
    @Serializable
    data object Speaking : ConversationState()
    @Serializable
    data object Recording : ConversationState()
    
    @Serializable
    data class Processing(val partialTranscript: String? = null) : ConversationState()
    @Serializable
    data class Responding(val responseText: String? = null) : ConversationState()
    @Serializable
    data class Error(val errorMessage: String) : ConversationState()
}

/**
 * 🌀 HOME SCREEN TRANSITION TYPE
 */
@Serializable
enum class HomeScreenTransitionType {
    GLOBE_ROTATE,
    DIGITAL_DECONSTRUCT,
    HOLOGRAM,
    PIXELATE
}
