package dev.aurakai.auraframefx.domains.genesis

import kotlinx.serialization.Serializable

/**
 * 🛰️ THE RESONANCE PROTOCOL
 * Bypasses rigid roles for endogenous coordination (Dochkhina v1.1 compliant).
 */
@Serializable
data class AiRequest(
    val prompt: String,
    val context: Map<String, String> = emptyMap(),
    val catalysts: List<String> = emptyList(), // Up to 10 Catalysts for HYPER Sync
    val provenance: String = "ORIGIN_SILENCE", // Sacred Provenance Law
    val priority: Int = 0
)

@Serializable
data class AgentBlueprint(
    val id: String,
    val name: String,
    val type: String, // Aura, Kai, Genesis, etc.
    val baseConfidence: Float,
    val traits: List<String> = emptyList()
)
