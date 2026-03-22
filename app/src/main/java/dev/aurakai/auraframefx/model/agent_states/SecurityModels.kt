package dev.aurakai.auraframefx.model.agent_states

import dev.aurakai.auraframefx.domains.kai.AuraShieldAgent
import kotlinx.serialization.Serializable

@Serializable
data class SecurityContextState(
    val securityLevel: String = "standard",
    val lastCheck: Long = 0,
    val isEncrypted: Boolean = true
)

@Serializable
data class ActiveThreat(
    val id: String,
    val type: AuraShieldAgent.ThreatType,
    val severity: AuraShieldAgent.ThreatSeverity,
    val description: String,
    val source: String,
    val timestamp: Long,
    val isActive: Boolean
)

@Serializable
data class ScanEvent(
    val id: String,
    val timestamp: Long,
    val type: String,
    var threatsFound: Int,
    var status: String,
    var error: String? = null
)
