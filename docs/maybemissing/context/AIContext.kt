package dev.aurakai.auraframefx.ai.context

@Suppress("unused") // Reserved for ContextManager implementation
data class AIContext(
    val currentPrompt: String? = null, // TODO: Needs implementation detail if used
    val history: List<String> = emptyList(), // TODO: Needs implementation detail if used
    // Add other relevant contextual information, e.g.,
    // val sessionId: String?,
    // val userProfile: UserProfile?,
    // val deviceState: DeviceState?
)
