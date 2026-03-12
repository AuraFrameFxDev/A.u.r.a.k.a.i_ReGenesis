package dev.aurakai.auraframefx.domains.cascade.models

data class TranscriptSegment(
    val text: String,
    val speaker: String,
    val timestamp: Long,
    val confidence: Float,
    val isFinal: Boolean
)
