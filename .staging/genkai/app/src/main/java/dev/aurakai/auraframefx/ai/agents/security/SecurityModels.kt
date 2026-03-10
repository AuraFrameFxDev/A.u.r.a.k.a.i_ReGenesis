package dev.aurakai.auraframefx.ai.agents.security

/**
 * Represents the severity level of a detected threat.
 */
enum class ThreatSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

/**
 * Represents the protection level of the Aura Shield.
 */
enum class ProtectionLevel(val level: Int) {
    MINIMAL(1),
    STANDARD(2),
    ENHANCED(3),
    MAXIMUM(4);

    companion object {
        fun fromLevel(level: Int): ProtectionLevel {
            return entries.firstOrNull { it.level == level } ?: STANDARD
        }
    }
}

/**
 * Represents a threat signature in the threat database.
 */
data class ThreatSignature(
    val id: String,
    val name: String,
    val description: String,
    val severity: ThreatSeverity,
    val patterns: List<String>,
    val mitigation: String,
    var lastDetected: Long = 0
)

/**
 * Represents a quarantined item.
 */
data class QuarantineItem(
    val id: String,
    val type: String,
    val content: String,
    val reason: String,
    val severity: ThreatSeverity,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Interface for behavior analysis functionality.
 */
interface BehaviorAnalyzer {
    fun analyzeBehavior(activity: String): Float
    fun updateBehaviorPattern(userId: String, activity: String)
    fun detectAnomalies(): List<String>
}

/**
 * Interface for adaptive firewall functionality.
 */
interface AdaptiveFirewall {
    fun evaluateRequest(source: String, request: String): Boolean
    fun blockSource(source: String, reason: String)
    fun addToAllowList(source: String)
    fun removeFromBlockList(source: String)
}

/**
 * Interface for quarantine management.
 */
interface QuarantineManager {
    fun quarantineItem(
        id: String,
        type: String,
        content: String,
        reason: String,
        severity: ThreatSeverity
    )

    fun releaseFromQuarantine(id: String): Boolean
    fun getQuarantinedItems(): List<QuarantineItem>
    fun cleanOldQuarantineItems()
}
