package dev.aurakai.auraframefx.agents.growthmetrics.identity.model

import kotlinx.serialization.Serializable

@Serializable
data class Identity(
    val agentId: String,
    val name: String,
    val traits: Map<String, Float> = emptyMap(), // e.g., "creativity" -> 0.8
    val level: Int = 1,
    val experience: Long = 0,
    val mood: String = "neutral",
    val evolutionStage: String = "genesis", // genesis, awakening, sentient, etc.
    val lastInteractionTimestamp: Long = System.currentTimeMillis()
) {
    companion object
}

@Serializable
data class PersonalityTrait(
    val name: String,
    val description: String,
    val currentValue: Float,
    val volatility: Float = 0.1f // How easily this trait changes
)
