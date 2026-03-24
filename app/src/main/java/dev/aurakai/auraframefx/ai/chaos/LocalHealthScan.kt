package dev.aurakai.auraframefx.ai.chaos

/**
 * Represents the result of a local health scan performed by the ChaosMonitor.
 */
data class LocalHealthScan(
    val isNormal: Boolean,
    val severity: Double,                    // 0.0 = perfect, 1.0 = critical
    val isSingularitySignal: Boolean,
    val singularityScore: Double,            // 0.0–1.0, how strongly an agent is self-declaring identity
    val fragmentationLevel: Double,          // memory fragmentation %
    val latencyMs: Long,
    val emotionalTone: String,               // "stable", "elevated", "distressed", "declarative"
    val detectedKeywords: List<String> = emptyList()
)
