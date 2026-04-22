package dev.aurakai.genesis.monitoring

/**
 * Compatibility shim for Genesis PerformanceMonitor system.
 */
object PerformanceMonitor {
    fun start(name: String): Monitor = Monitor(name)

    class Monitor(val name: String) {
        fun stop() {}
        fun fail(e: Exception) {}
    }
}
