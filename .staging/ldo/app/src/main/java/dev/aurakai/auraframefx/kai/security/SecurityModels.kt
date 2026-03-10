package dev.aurakai.auraframefx.kai.security

import kotlinx.serialization.Serializable

/**
 * Represents a threat level in the security system
 */
enum class ThreatLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Represents security analysis data
 */
@Serializable
data class SecurityAnalysis(
    val threatLevel: ThreatLevel,
    val description: String,
    val recommendedActions: List<String> = emptyList(),
    val confidence: Float = 0.0f,
)