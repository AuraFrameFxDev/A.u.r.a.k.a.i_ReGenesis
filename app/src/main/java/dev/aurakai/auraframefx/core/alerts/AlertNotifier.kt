package dev.aurakai.auraframefx.core.alerts

/**
 * LDO alert routing contract.
 */
interface AlertNotifier {
    fun notify(message: String, level: AlertLevel = AlertLevel.WARNING)
    fun notifyCritical(message: String, exception: Throwable? = null)
    fun notifyAgentAnomaly(agentId: String, reason: String)
}

enum class AlertLevel { INFO, WARNING, ERROR, CRITICAL }
