data class ConversationState(
    val currentSpeaker: String? = null,
    val transcriptSegments: List<TranscriptSegment> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = false
)