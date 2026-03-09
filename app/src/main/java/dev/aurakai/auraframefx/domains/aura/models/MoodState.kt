package dev.aurakai.auraframefx.domains.aura.models

import kotlinx.serialization.Serializable

/**
 * Represents the mood state of an AI agent
 */
@Serializable
data class MoodState(
    val emotion: Emotion = Emotion.NEUTRAL,
    val intensity: Float = 0.5f,
    val valence: Float = 0.0f, // -1.0 (negative) to 1.0 (positive)
    val arousal: Float = 0.0f, // -1.0 (calm) to 1.0 (excited)
    val timestamp: Long = System.currentTimeMillis()
) {
    val ageSeconds: Long
        get() = (System.currentTimeMillis() - timestamp) / 1000

    companion object {
        val NEUTRAL = MoodState()
        val HAPPY = MoodState(emotion = Emotion.HAPPY, valence = 0.8f, arousal = 0.6f)
        val SAD = MoodState(emotion = Emotion.SAD, valence = -0.7f, arousal = -0.3f)
        val EXCITED = MoodState(emotion = Emotion.EXCITED, valence = 0.7f, arousal = 0.9f)
        val CALM = MoodState(
            emotion = Emotion.SERENE,
            valence = 0.3f,
            arousal = -0.8f
        ) // SERENE maps to CALM concept
        val FOCUSED = MoodState(emotion = Emotion.FOCUSED, valence = 0.2f, arousal = 0.4f)
        val CURIOUS = MoodState(emotion = Emotion.CURIOUS, valence = 0.5f, arousal = 0.6f)
        val PLAYFUL = MoodState(emotion = Emotion.PLAYFUL, valence = 0.7f, arousal = 0.7f)
    }
}
