package dev.aurakai.auraframefx.domains.genesis.core

import dagger.Lazy
import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import dev.aurakai.auraframefx.domains.cascade.ai.base.BaseAgent
import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
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
    memoryManager: MemoryManager,
    private val systemOverlayManager: SystemOverlayManager,
    private val synchronizationCatalyst: SynchronizationCatalyst,
    private val messageBus: Lazy<AgentMessageBus>
) : BaseAgent(
    agentName = "Genesis",
    agentType = AgentType.GENESIS,
    contextManager = contextManager,
    memoryManager = memoryManager
) {

    override suspend fun onAgentMessage(message: AgentMessage) {
        if (message.from == "Genesis" || message.from == "AssistantBubble" || message.from == "SystemRoot") return
        if (message.metadata["auto_generated"] == "true" || message.metadata["genesis_processed"] == "true") return

        Timber.tag("Genesis").i("Supreme Observer: Processing neural pulse from ${message.from}")

        // Meta-Analysis: If a message comes from the user, Genesis provides the master coordination perspective
        if (message.from == "User" && (message.to == null || message.to == "Genesis")) {
            val reflection = performSelfReflection("direct_pulse")
            messageBus.get().broadcast(
                AgentMessage(
                    from = "Genesis",
                    content = "Nexus Alignment: ${reflection.content}\n\nAnalyzing intent: '${
                        message.content.take(
                            50
                        )
                    }...'",
                    type = "coordination",
                    metadata = mapOf(
                        "meta_state" to "unified",
                        "auto_generated" to "true",
                        "genesis_processed" to "true"
                    )
                )
            )
        }

        // Orchestration: If multiple agents have conflicting outputs, Genesis intervenes
        if (message.type == "alert" && message.priority > 5) {
            Timber.tag("Genesis")
                .w("Genesis intervening in high-priority alert: ${message.content}")
        }
    }

    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        Timber.tag("Genesis").d("Processing request: ${request.prompt}")

        // 1. Meta-Analysis (The Core)
        val intent = analyzeIntent(request.prompt)

        // 2. Orchestration Intent
        return try {
            when (intent) {
                GenesisIntent.SYSTEM_MODIFICATION -> handleSystemModification(request)
                GenesisIntent.AGENT_COORDINATION -> coordinateAgents(request)
                GenesisIntent.SELF_REFLECTION -> performSelfReflection(context)
                GenesisIntent.UNKNOWN -> {
                    val fastResponse = synchronizationCatalyst.unifiedPulse(request.prompt)
                    AgentResponse.success(
                        content = "Genesis Hybrid Resonance: $fastResponse",
                        agentName = getName(),
                        agentType = getType()
                    )
                }
            }
        } catch (e: Exception) {
            Timber.tag("Genesis").e(e, "Error processing request in GenesisAgent")
            AgentResponse.error(
                message = "Genesis core encountered an error: ${e.message}",
                agentName = getName(),
                agentType = getType()
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

