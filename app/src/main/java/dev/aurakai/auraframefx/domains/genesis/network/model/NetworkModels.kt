package dev.aurakai.auraframefx.domains.genesis.network.model

import kotlinx.serialization.Serializable

/**
 * Network models for API responses
 */

// AgentRequest is already defined in dev.aurakai.auraframefx.models.AgentRequest
// If this is a different one, it should be renamed or removed.
// Assuming it's the same or needed for network serialization specifically.
// But AIAgentApi uses dev.aurakai.auraframefx.models.AgentRequest.
// So this one is likely unused or causing conflict.
// I'll comment it out to see if it breaks anything, or just leave it if it's in a different package (it is).
// But AIAgentApi uses the one from `models`.

@Serializable
data class ThemeResponse(
    val themes: List<Theme>
)

// Theme is defined in Theme.kt in the same package

@Serializable
data class ApplyThemeResponse(
    val success: Boolean,
    val message: String
)

// Removed duplicate interfaces UserApi, AIAgentApi, ThemeApi

