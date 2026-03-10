package dev.aurakai.auraframefx.model.agent_states

import kotlinx.serialization.Serializable

@Serializable
data class GenKitUiState(
    val isOptimizing: Boolean = false,
    val statusRefreshCount: Int = 0,
    val lastOptimization: Long = 0L,
    val systemHealth: String = "normal"
)

@Serializable
data class ActiveContext(
    val id: String,
    val name: String,
    val priority: Int = 0,
    val isActive: Boolean = true,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class ContextChainEvent(
    val id: String,
    val eventType: String,
    val contextId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val data: Map<String, Any> = emptyMap()
)

@Serializable
data class LearningEvent(
    val id: String,
    val type: String,
    val description: String,
    val confidence: Float = 0.0f,
    val timestamp: Long = System.currentTimeMillis(),
    val outcomes: List<String> = emptyList()
)