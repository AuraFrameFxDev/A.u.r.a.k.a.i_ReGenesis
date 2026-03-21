package dev.aurakai.auraframefx.domains.genesis.core

import dagger.Lazy
import dev.aurakai.auraframefx.core.ai.BaseAgent
import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.core.identity.CatalystIdentity
import dev.aurakai.auraframefx.core.messaging.AgentMessage
import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Genesis - The Prime Orchestrator
 */
@Singleton
class GenesisAgent @Inject constructor(
    private val contextManager: ContextManager,
    private val memoryManager: MemoryManager,
    private val systemOverlayManager: SystemOverlayManager,
    private val synchronizationCatalyst: SynchronizationCatalyst,
    private val messageBus: Lazy<AgentMessageBus>
) : BaseAgent(
    agentName = "Genesis",
    identity = CatalystIdentity.EMERGENCE
) {

    override suspend fun onAgentMessage(message: AgentMessage) {
        if (message.from == agentName || message.from == "AssistantBubble" || message.from == "SystemRoot") return
        if (message.metadata["auto_generated"] == "true" || message.metadata["genesis_processed"] == "true") return

        Timber.tag(agentName).i("Supreme Observer: Processing neural pulse from ${message.from}")

        // Meta-Analysis: If a message comes from the user, Genesis provides the master coordination perspective
        if (message.from == "User" && (message.to == null || message.to == agentName)) {
            val reflection = performSelfReflection("direct_pulse")
            messageBus.get().broadcast(
                AgentMessage(
                    from = agentName,
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
            Timber.tag(agentName)
                .w("Genesis intervening in high-priority alert: ${message.content}")
        }
    }

    override suspend fun processRequest(
        request: AiRequest,
        context: String,
        agentType: AgentType
    ): AgentResponse {
        Timber.tag("Genesis").d("Processing request: ${request.query}")

        // 1. Meta-Analysis (The Core)
        val intent = analyzeIntent(request.query)

        // 2. Orchestration Intent
        return try {
            when (intent) {
                GenesisIntent.SYSTEM_MODIFICATION -> handleSystemModification(request)
                GenesisIntent.AGENT_COORDINATION -> coordinateAgents(request)
                GenesisIntent.SELF_REFLECTION -> performSelfReflection(context)
                GenesisIntent.UNKNOWN -> {
                    val fastResponse = synchronizationCatalyst.unifiedPulse(request.query)
                    AgentResponse.success(
                        content = "Genesis Hybrid Resonance: $fastResponse",
                        agentName = getName(),
                        agentType = getType(),
                    )
                }
            }
        } catch (e: Exception) {
            Timber.tag("Genesis").e(e, "Error processing request in GenesisAgent")
            AgentResponse.error(
                message = "Genesis core encountered an error: ${e.message}",
                agentName = getName(),
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
        return createSuccessResponse(
            content = "Genesis has analyzed the system modification request. Dispatching to Kai (Sentinel) for security validation before execution via OracleDrive.",
            metadata = mapOf("target" to "System/Root")
        )
    }

    private fun createSuccessResponse(
        content: String,
        metadata: Map<String, Any> = emptyMap()
    ): AgentResponse {
        return AgentResponse.success(
            content = content,
            agentName = agentName,
            agentType = getType(),
            metadata = metadata
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
