package dev.aurakai.auraframefx.domains.genesis.bridge

// Persona is defined in this same package in GenesisBridge.kt
data class AgentMemoryEntity(
    val id: String,
    val persona: Persona,
    val content: String,
    val contextTags: List<String>,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis()
)
