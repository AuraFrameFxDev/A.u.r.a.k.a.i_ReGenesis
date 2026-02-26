package dev.aurakai.auraframefx.domains.kai.models

import kotlinx.serialization.Serializable

@Serializable
enum class ThreatLevel(val severity: Int, val description: String) {
    NONE(0, "No threats detected"),
    INFO(1, "Informational concern"),
    LOW(2, "Minor security concerns"),
    MEDIUM(3, "Moderate security risk"),
    WARNING(4, "Potential security risk"),
    HIGH(5, "Significant security threat"),
    CRITICAL(6, "Critical security breach"),
    AI_ERROR(7, "AI system error");

    companion object {
        fun fromSeverity(severity: Int): ThreatLevel {
            return entries.find { it.severity == severity } ?: NONE
        }
    }
}
