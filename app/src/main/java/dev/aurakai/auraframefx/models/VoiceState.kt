package dev.aurakai.auraframefx.models

sealed class VoiceState {
    object Idle : VoiceState()
    object Listening : VoiceState()
    data class Processing(val text: String) : VoiceState()
    data class Error(val message: String) : VoiceState()
}
