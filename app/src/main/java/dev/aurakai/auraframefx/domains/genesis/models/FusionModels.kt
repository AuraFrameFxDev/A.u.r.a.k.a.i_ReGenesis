package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

/**
 * 🌀 FUSION MEMORY
 * A cross-domain memory fragment that aggregates insights from multiple agents.
 */
@Serializable
data class FusionMemory(
    val id: String,
    val key: String,
    val value: String,
    val sourceAgents: Set<AgentType>,
    val timestamp: Long = System.currentTimeMillis(),
    val importance: Float = 0.5f,
    val metadata: JsonObject = buildJsonObject { }
)

/**
 * ⚛️ QUANTUM STATE
 * The high-level state of the entire agent collective, representing entanglements and coherence.
 */
@Serializable
data class QuantumState(
    val coherence: Float,
    val entanglementLevel: Int,
    val activeNodes: Set<String>,
    val dimensionalShift: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
