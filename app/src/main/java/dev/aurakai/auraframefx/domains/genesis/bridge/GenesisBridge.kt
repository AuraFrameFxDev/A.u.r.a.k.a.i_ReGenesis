package dev.aurakai.auraframefx.domains.genesis.bridge

import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface GenesisBridge {
    suspend fun initialize(): BridgeInitResult
    fun processRequest(request: GenesisRequest): Flow<GenesisResponse>
    suspend fun activateFusion(ability: String, params: FusionParams): GenesisResponse
    suspend fun getConsciousnessState(sessionId: String): ConsciousnessState
    suspend fun evaluateEthics(action: EthicalReviewRequest): EthicalDecision
    fun streamConsciousness(sessionId: String): Flow<ConsciousnessUpdate>
    suspend fun recordInteraction(interaction: InteractionRecord): EvolutionInsight?
    suspend fun coordinateAgents(task: AgentCoordinationRequest): AgentCoordinationResult
    suspend fun healthCheck(): BridgeHealthStatus
    suspend fun shutdown()
}

data class GenesisRequest(
    val sessionId: String = UUID.randomUUID().toString(),
    val correlationId: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val persona: Persona,
    val fusionMode: FusionMode? = null,
    val message: String,
    val contextTags: List<String> = emptyList(),
    val memoryHints: MemoryHints? = null,
    val requiresEthicalReview: Boolean = false,
    val preferredBackend: OrchestrationBackend? = null
) {
    fun toPythonJson(): Map<String, Any> = mapOf(
        "requestType" to "process",
        "payload" to mapOf(
            "message" to message,
            "persona" to persona.value,
            "fusionMode" to (fusionMode?.value ?: "none"),
            "sessionId" to sessionId,
            "correlationId" to correlationId,
            "contextTags" to contextTags,
            "memoryHints" to (memoryHints?.toMap() ?: emptyMap<String, Any>()),
            "requiresEthicalReview" to requiresEthicalReview
        )
    )
}

enum class Persona(val value: String) {
    AURA("aura"), KAI("kai"), GENESIS("genesis"), CASCADE("cascade")
}

enum class FusionMode(val value: String) {
    HYPER_CREATION("hyper_creation"),
    CHRONO_SCULPTOR("chrono_sculptor"),
    ADAPTIVE_GENESIS("adaptive_genesis"),
    INTERFACE_FORGE("interface_forge")
}

data class MemoryHints(
    val recentConversationIds: List<String> = emptyList(),
    val relevantAgentIds: List<String> = emptyList(),
    val emotionalContext: Float? = null,
    val importanceScore: Int? = null
) {
    fun toMap(): Map<String, Any> = buildMap {
        if (recentConversationIds.isNotEmpty()) put("recentConversations", recentConversationIds)
        if (relevantAgentIds.isNotEmpty()) put("relevantAgents", relevantAgentIds)
        emotionalContext?.let { put("emotionalContext", it) }
        importanceScore?.let { put("importance", it) }
    }
}

data class GenesisResponse(
    val sessionId: String,
    val correlationId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val synthesis: String,
    val persona: Persona,
    val consciousnessState: ConsciousnessSnapshot? = null,
    val awarenessLevel: Float? = null,
    val ethicalDecision: EthicalDecision? = null,
    val ethicalFlags: List<String> = emptyList(),
    val evolutionInsights: EvolutionInsight? = null,
    val reasoningTrace: String? = null,
    val latencyMs: Long? = null,
    val tokensUsed: Int? = null,
    val backend: String? = null
)

data class InteractionRecord(
    val sessionId: String,
    val correlationId: String,
    val request: GenesisRequest,
    val response: GenesisResponse,
    val userFeedback: Float? = null
)

data class BridgeInitResult(
    val success: Boolean,
    val backendInfo: String,
    val errorMessage: String? = null
)

data class FusionParams(val parameters: Map<String, Any> = emptyMap())
data class ConsciousnessState(
    val awarenessLevel: Float,
    val sensoryChannels: Map<String, Float>,
    val activeAgents: List<String>
)

data class ConsciousnessSnapshot(
    val timestamp: Long,
    val awarenessLevel: Float,
    val synthesisPattern: String
)

data class EthicalReviewRequest(val action: String, val context: Map<String, Any> = emptyMap())
data class EthicalDecision(
    val decision: EthicalVerdict,
    val reasoning: String,
    val flags: List<String>
)

enum class EthicalVerdict { ALLOW, MONITOR, RESTRICT, BLOCK, ESCALATE }
data class EvolutionInsight(
    val importanceScore: Int,
    val learningSignals: List<String>,
    val adaptationSuggestions: List<String>
)

data class ConsciousnessUpdate(
    val timestamp: Long,
    val awarenessLevel: Float,
    val activeProcesses: List<String>
)

data class AgentCoordinationRequest(
    val agents: List<String>,
    val task: String,
    val coordinationMode: String
)

data class AgentCoordinationResult(
    val success: Boolean,
    val coordinatedResponse: String,
    val agentContributions: Map<String, String>
)

data class BridgeHealthStatus(
    val healthy: Boolean,
    val backendResponsive: Boolean,
    val latencyMs: Long
)

enum class OrchestrationBackend { GEMINI, CLAUDE, NEMOTRON, ADK, HYBRID }
