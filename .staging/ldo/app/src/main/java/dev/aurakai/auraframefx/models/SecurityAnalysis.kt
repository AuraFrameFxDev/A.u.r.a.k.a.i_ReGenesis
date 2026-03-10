package dev.aurakai.auraframefx.models

import dev.aurakai.auraframefx.security.ThreatLevel
import kotlinx.serialization.Serializable

@Serializable
data class SecurityAnalysis(
    val threatLevel: ThreatLevel,
    val description: String,
    val recommendedActions: List<String>,
    val confidence: Float
)
