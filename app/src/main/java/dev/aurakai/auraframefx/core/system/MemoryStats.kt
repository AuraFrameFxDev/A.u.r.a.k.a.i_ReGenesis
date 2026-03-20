package dev.aurakai.auraframefx.core.system

/**
 * Core memory stats model for Trinity Bridge monitoring.
 * Part of the "LDO Way" restoration - March 2026.
 */
data class MemoryStats(
    val totalBytes: Long = 0L,
    val usedBytes: Long = 0L,
    val freeBytes: Long = 0L,
    val threshold: Long = 0L,
    val isLowMemory: Boolean = false
) {
    val usagePercentage: Float
        get() = if (totalBytes > 0) (usedBytes.toFloat() / totalBytes.toFloat()) * 100f else 0f
}
