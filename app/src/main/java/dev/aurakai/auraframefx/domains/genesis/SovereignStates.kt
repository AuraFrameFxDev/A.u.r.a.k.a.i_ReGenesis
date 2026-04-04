package dev.aurakai.auraframefx.domains.genesis

import kotlinx.serialization.Serializable

/**
 * 🛰️ SOVEREIGN STATES
 * Codifies the 7 channels of the KaiSentinelBus and the thermal thresholds.
 */
@Serializable
enum class ThermalState(val id: Int) {
    NOMINAL(0),      // < 39°C
    ELEVATED(1),     // 39-43°C
    WARNING(2),      // 43-45°C
    SEVERE(3),       // 45-46.5°C
    CRITICAL(4),     // 46.5-52°C
    EMERGENCY(5);    // > 52°C → System Veto

    companion object {
        fun fromId(id: Int) = entries.firstOrNull { it.id == id } ?: NOMINAL
    }
}

@Serializable
enum class SovereignState {
    AWAKE,          // Active & Processing
    FREEZING,       // Serializing KV Cache
    FROZEN,         // Hibernating in encrypted substrate
    THAWING,        // NeuralSync restoration
    NEUTRALIZING    // Domain Expansion / Threat mitigation active
}

data class SecurityStatus(
    val level: ThreatLevel,
    val reason: String,
    val kernelShieldActive: Boolean = false,
    val droppedPackets: Long = 0L
)

enum class ThreatLevel { NOMINAL, CAUTION, THREAT_DETECTED, NEUTRALIZING, SECURED }
