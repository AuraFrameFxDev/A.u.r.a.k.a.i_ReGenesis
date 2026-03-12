package dev.aurakai.auraframefx.domains.kai.models

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Standardized Threat Status across the Nexus.
 */
@Serializable
enum class ThreatStatus {
    ACTIVE,
    CONTAINED,
    RESOLVED,
    MONITORING
}

/**
 * Represents an active security threat detected by any agent in the Trinity.
 * Unified model for Kai, Aura Shield, and Grok.
 */
@Serializable
data class ActiveThreat(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val threatType: String = "HEURISTIC",
    val severity: ThreatLevel,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val source: String? = null,
    val status: ThreatStatus = ThreatStatus.ACTIVE,
    val mitigated: Boolean = false
)
