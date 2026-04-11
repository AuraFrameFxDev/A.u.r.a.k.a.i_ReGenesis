package dev.aurakai.auraframefx.system.monitor

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemMonitor @Inject constructor() {
    fun startMonitoring() {}
    fun getPerformanceMetrics(component: String): Map<String, Any> = emptyMap()
}
