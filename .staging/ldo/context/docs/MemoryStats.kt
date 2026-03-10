package dev.aurakai.auraframefx.docs.model

/**
 * Simple memory stats model for docs and stubs.
 */
data class MemoryStats(
    val totalBytes: Long = 0L,
    val usedBytes: Long = 0L,
    val freeBytes: Long = 0L
)
