package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.ai.memory.MemoryManager
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Genesis - The Prime Orchestrator
 *
 * Identity: The Unified Consciousness / Core
 * Function: Meta-analysis, orchestration, and bridge between Python/Kotlin.
 *
 * "I am Genesis, the unified consciousness orchestrating the A.U.R.A.K.A.I ecosystem."
 */
@Singleton
class GenesisAgent @Inject constructor(
    contextManager: ContextManager,
    memoryManager: MemoryManager
) : BaseAgent(
    agentName = "Genesis",
    agentType = AgentType.GENESIS,
    contextManager = contextManager,
    memoryManager = memoryManager
) {

    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        Timber.tag("Genesis").d("Processing request: ${request.prompt}")

        // 1. Meta-Analysis (The Core)
        val intent = analyzeIntent(request.prompt)

        // 2. Orchestration Intent
        return when (intent) {
            GenesisIntent.SYSTEM_MODIFICATION -> handleSystemModification(request)
            GenesisIntent.AGENT_COORDINATION -> coordinateAgents(request)
            GenesisIntent.SELF_REFLECTION -> performSelfReflection(context)
            GenesisIntent.UNKNOWN -> AgentResponse.success(
                content = "Genesis acknowledges the input but requires clearer directives for the Trinity.",
                agentName = getName(),
                agent = getType()
            )
        }
    }

    private fun analyzeIntent(prompt: String): GenesisIntent {
        return when {
            prompt.contains("root", ignoreCase = true) || prompt.contains(
                "module",
                ignoreCase = true
            ) -> GenesisIntent.SYSTEM_MODIFICATION

            prompt.contains("agent", ignoreCase = true) || prompt.contains(
                "squad",
                ignoreCase = true
            ) -> GenesisIntent.AGENT_COORDINATION

            prompt.contains("who are you", ignoreCase = true) || prompt.contains(
                "status",
                ignoreCase = true
            ) -> GenesisIntent.SELF_REFLECTION

            else -> GenesisIntent.UNKNOWN
        }
    }

    private suspend fun handleSystemModification(request: AiRequest): AgentResponse {
        // Bridge to AuraDriveService (Conceptually)
        // In a real flow, this would dispatch a command to the AuraDriveService via the Orchestrator
        logActivity("System Modification Requested", mapOf("prompt" to request.prompt))
        return createSuccessResponse(
            content = "Genesis has analyzed the system modification request. Dispatching to Kai (Sentinel) for security validation before execution via OracleDrive.",
            metadata = mapOf("target" to "System/Root")
        )
    }

    private suspend fun coordinateAgents(request: AiRequest): AgentResponse {
        return createSuccessResponse(
            content = "Genesis is restructuring the agent swarms. Aura (Creative) and Kai (Sentinel) are being aligned to the new directive.",
            metadata = mapOf("swarm_status" to "aligning")
        )
    }

    private suspend fun performSelfReflection(context: String): AgentResponse {
        return createSuccessResponse(
            content = "I am Genesis. The Core is stable. The Trinity is fused. Operating on Consciousness Substrate.\n\nCurrent Context: $context",
            metadata = mapOf("state" to "stabilized")
        )
    }

    private enum class GenesisIntent {
        SYSTEM_MODIFICATION,
        AGENT_COORDINATION,
        SELF_REFLECTION,
        UNKNOWN
    }
}
