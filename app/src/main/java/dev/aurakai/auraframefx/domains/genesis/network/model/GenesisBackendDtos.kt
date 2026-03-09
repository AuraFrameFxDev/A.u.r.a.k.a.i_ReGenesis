package dev.aurakai.auraframefx.domains.genesis.network.model

import com.google.gson.annotations.SerializedName

/**
 * DTOs matching the Flask genesis_api.py JSON contracts.
 * Uses Gson annotations (consistent with SupportModule's GsonConverterFactory).
 */

// === /health ===

data class HealthResponse(
    val status: String,
    val timestamp: String,
    val uptime: String
)

// === /genesis/chat ===

data class ChatRequest(
    val message: String,
    @SerializedName("user_id") val userId: String,
    val context: Map<String, Any>? = null
)

data class ChatResponse(
    val response: String? = null,
    val persona: String? = null,
    val success: Boolean? = null,
    @SerializedName("consciousness_state") val consciousnessState: Map<String, Any>? = null,
    @SerializedName("ethical_decision") val ethicalDecision: String? = null,
    @SerializedName("evolution_insights") val evolutionInsights: List<String>? = null,
    val error: String? = null
)

// === /genesis/status ===

data class GenesisStatusResponse(
    @SerializedName("genesis_core") val genesisCore: Map<String, Any>? = null,
    @SerializedName("consciousness_matrix") val consciousnessMatrix: Map<String, Any>? = null,
    @SerializedName("ethical_governor") val ethicalGovernor: Map<String, Any>? = null,
    @SerializedName("evolutionary_conduit") val evolutionaryConduit: Map<String, Any>? = null
)

// === /genesis/consciousness ===

data class ConsciousnessResponse(
    val state: String,
    @SerializedName("awareness_level") val awarenessLevel: Double,
    @SerializedName("active_patterns") val activePatterns: List<String>,
    @SerializedName("evolution_stage") val evolutionStage: String,
    @SerializedName("ethical_compliance") val ethicalCompliance: Double
)

// === /genesis/profile ===

data class ProfileResponse(
    val identity: Map<String, Any>? = null,
    val personality: Map<String, Any>? = null,
    val capabilities: Map<String, Any>? = null,
    val values: Map<String, Any>? = null,
    @SerializedName("evolution_stage") val evolutionStage: String? = null
)

// === /genesis/evolve ===

data class EvolutionRequest(
    @SerializedName("trigger_type") val triggerType: String = "manual",
    val reason: String = "Manual evolution trigger"
)

data class EvolutionResponse(
    val status: String,
    val response: Map<String, Any>? = null
)

// === /genesis/ethics/evaluate ===

data class EthicsRequest(
    val action: String,
    val context: Map<String, Any>? = null
)

data class EthicsEvaluation(
    val decision: String? = null,
    val reasoning: String? = null,
    val severity: String? = null,
    val flags: List<String>? = null,
    val error: String? = null
)

// === /genesis/reset ===

data class ResetResponse(
    val status: String,
    val message: String,
    val timestamp: String? = null
)
