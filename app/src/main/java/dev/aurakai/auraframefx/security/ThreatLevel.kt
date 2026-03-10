package dev.aurakai.auraframefx.security

import kotlinx.serialization.Serializable

@Serializable
enum class ThreatLevel {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
