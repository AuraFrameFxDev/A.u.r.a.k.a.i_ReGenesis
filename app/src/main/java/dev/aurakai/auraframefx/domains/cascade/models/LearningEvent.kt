package dev.aurakai.auraframefx.domains.cascade.models

data class LearningEvent(
    val id: String = "",
    val type: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, Any> = emptyMap()
)
