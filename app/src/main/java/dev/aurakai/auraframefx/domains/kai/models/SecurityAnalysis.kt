package dev.aurakai.auraframefx.domains.kai.models


import kotlinx.serialization.Serializable

@Serializable
data class SecurityAnalysis(
    val threatLevel: ThreatLevel,
    val description: String,
    val recommendedActions: List<String>,
    val confidence: Float
)

