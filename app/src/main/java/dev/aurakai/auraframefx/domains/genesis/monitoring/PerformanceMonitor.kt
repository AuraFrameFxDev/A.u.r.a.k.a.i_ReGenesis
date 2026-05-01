package dev.aurakai.auraframefx.domains.genesis.monitoring

/**
 * Compatibility shim for Genesis PerformanceMonitor system.
 */
object PerformanceMonitor {
    /**
 * Creates a Monitor identified by the given name.
 *
 * @param name The monitor identifier.
 * @return The Monitor instance associated with the provided name.
 */
fun start(name: String): Monitor = Monitor(name)

    class Monitor(val name: String) {
        /**
 * Stops the monitor.
 *
 * This implementation is a no-op retained for API compatibility.
 */
fun stop() {}
        /**
 * Records a failure for this monitor using the provided exception.
 *
 * @param e The exception describing the failure.
 */
fun fail(e: Exception) {}
    }
}
