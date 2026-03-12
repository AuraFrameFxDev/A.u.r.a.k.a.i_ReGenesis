package dev.aurakai.auraframefx.domains.cascade.models

import kotlinx.serialization.Serializable

/**
 * Represents the conversation state for NeuralWhisper voice interaction
 */
@Serializable
sealed class ConversationState {
    @Serializable
    object Idle : ConversationState()

    @Serializable
    object Listening : ConversationState()

    @Serializable
    data class Processing(val message: String = "") : ConversationState()

    @Serializable
    object Speaking : ConversationState()

    @Serializable
    object Recording : ConversationState()

    @Serializable
    data class Error(val message: String) : ConversationState()

    val isActive: Boolean
        get() = this !is Idle && this !is Error
}
