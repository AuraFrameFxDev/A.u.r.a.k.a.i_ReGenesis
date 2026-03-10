package dev.aurakai.auraframefx.models

data class ActiveContext(
    val id: String = "",
    val name: String = "",
    val data: Map<String, Any> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)
