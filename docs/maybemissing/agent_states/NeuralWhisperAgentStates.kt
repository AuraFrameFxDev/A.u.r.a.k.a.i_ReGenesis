package dev.aurakai.auraframefx.model.agent_states

// TODO: Define actual properties for these states/events.

@Suppress("unused") // Reserved for NeuralWhisperAgent implementation
data class ActiveContext(
    // Renamed from ActiveContexts (singular)
    val contextId: String,
    val description: String? = null,
    val relatedData: Map<String, String> = emptyMap(),
    // Add other relevant active context properties
)

@Suppress("unused") // Reserved for NeuralWhisperAgent implementation
// ContextChain could be a list of context snapshots or events
data class ContextChainEvent(
    val eventId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val contextSnapshot: String? = null, // e.g., JSON representation of a context state
    // Add other relevant chain event properties
)

@Suppress("unused") // Reserved for NeuralWhisperAgent implementation
data class LearningEvent(
    val eventId: String,
    val description: String,
    val outcome: String? = null, // e.g., "positive_reinforcement", "correction"
    val dataLearned: Map<String, String> = emptyMap(),
    // Add other relevant learning event properties
)
