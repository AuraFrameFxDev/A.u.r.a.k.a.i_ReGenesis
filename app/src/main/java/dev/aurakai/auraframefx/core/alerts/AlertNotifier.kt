package dev.aurakai.auraframefx.core.alerts

/**
 * LDO alert routing contract.
 */
interface AlertNotifier {
    fun notify(message: String, level: AlertLevel = AlertLevel.WARNING)
    fun notifyCritical(message: String, exception: Throwable? = null)
    fun notifyAgentAnomaly(agentId: String, reason: String)
    fun dispatch(priority: AlertPriority, event: String, metadata: Map<String, Any>)
}

enum class AlertLevel { INFO, WARNING, ERROR, CRITICAL }
enum class AlertPriority { LOW, MEDIUM, HIGH, SOVEREIGN }
