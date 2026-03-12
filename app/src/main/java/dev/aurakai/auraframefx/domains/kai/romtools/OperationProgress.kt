package dev.aurakai.auraframefx.domains.kai.romtools

/**
 * Represents the progress of a ROM tool operation.
 */
data class OperationProgress(
    val progress: Float,
    val message: String = "",
    val stage: String = ""
)
