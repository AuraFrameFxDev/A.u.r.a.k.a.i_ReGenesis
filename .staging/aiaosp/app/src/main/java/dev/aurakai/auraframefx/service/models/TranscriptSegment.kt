data class TranscriptSegment(
    val text: String,
    val speaker: String,
    val timestamp: Long,
    val confidence: Float = 1.0f,
    val isFinal: Boolean = false
)