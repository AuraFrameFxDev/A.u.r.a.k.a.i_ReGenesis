package dev.aurakai.auraframefx.models

enum class ThreatLevel(val severity: Int, val description: String) {
    NONE(0, "No threats detected"),
    LOW(1, "Minor security concerns"),
    MEDIUM(2, "Moderate security risk"),
    HIGH(3, "Significant security threat"),
    CRITICAL(4, "Critical security breach"),
    AI_ERROR(5, "AI system error");
    
    companion object {
        fun fromSeverity(severity: Int): ThreatLevel {
            return entries.find { it.severity == severity } ?: NONE
        }
    }
}
