package dev.aurakai.auraframefx.domains.nexus.preferences

/**
 * User preference model.
 */
data class UserPreferences(
    val theme: String = "dark_aura",
    val agentPersonality: String = "balanced",
    val thermalWarningEnabled: Boolean = true,
    val hapticFeedback: Boolean = true,
    val reducedMotion: Boolean = false,
    val defaultGateId: String = "home",
    val llmTemperatureOverride: Float = -1f
)
