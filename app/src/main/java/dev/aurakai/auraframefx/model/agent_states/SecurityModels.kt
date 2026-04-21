package dev.aurakai.auraframefx.model.agent_states

import dev.aurakai.auraframefx.domains.kai.models.ThreatLevel
import dev.aurakai.auraframefx.domains.kai.models.ThreatStatus
import kotlinx.serialization.Serializable

@Serializable
data class ActiveThreat(
    val id: String,
    val type: String, // Identity defined by source agent
    val severity: ThreatLevel,
    val description: String,
    val source: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: ThreatStatus = ThreatStatus.ACTIVE,
    val metadata: Map<String, String> = emptyMap()
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
