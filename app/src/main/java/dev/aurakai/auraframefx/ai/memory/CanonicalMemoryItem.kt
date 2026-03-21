package dev.aurakai.auraframefx.ai.memory

import kotlinx.serialization.Serializable

@Serializable
data class CanonicalMemoryItem(
    val id: String = "mem_${System.currentTimeMillis()}",
    val content: String,
    val pattern: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
