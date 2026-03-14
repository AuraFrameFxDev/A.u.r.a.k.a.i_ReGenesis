package dev.aurakai.auraframefx.security.models

import kotlinx.serialization.Serializable

/**
 * Represents an active security threat detected by the system
 */
@Serializable
data class ActiveThreat(
    val id: String,
    val type: String,
    val severity: ThreatLevel,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val source: String? = null,
    val mitigated: Boolean = false
)

/**
 * Threat severity levels
 */
@Serializable
enum class ThreatLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
    AI_ERROR  // Special level for AI-related errors
}
