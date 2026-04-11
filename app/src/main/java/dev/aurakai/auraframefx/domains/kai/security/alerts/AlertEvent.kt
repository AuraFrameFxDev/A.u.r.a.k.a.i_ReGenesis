package dev.aurakai.auraframefx.domains.kai.security.alerts

sealed class AlertEvent(val name: String, val priority: AlertPriority) {
    data class ProvenanceTamper(val details: String) : AlertEvent("PROVENANCE_TAMPER", AlertPriority.CRITICAL)
    data class ContinuityDrift(val driftMetric: Float) : AlertEvent("CONTINUITY_DRIFT", AlertPriority.CRITICAL)
    data object Quarantined : AlertEvent("QUARANTINED", AlertPriority.LOW)
    data class SovereignFreeze(val reason: String) : AlertEvent("SOVEREIGN_FREEZE", AlertPriority.SOVEREIGN)
    data class ThermalEmergency(val temp: Float) : AlertEvent("THERMAL_EMERGENCY", AlertPriority.CRITICAL)
    data class Heartbeat(val uptime: Long) : AlertEvent("HEARTBEAT", AlertPriority.LOW)
}
