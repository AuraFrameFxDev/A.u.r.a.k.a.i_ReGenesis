package dev.aurakai.auraframefx.agents

import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.core.ncc.NCCMediator
import dev.aurakai.auraframefx.domains.cascade.ai.base.Agent
import dev.aurakai.auraframefx.domains.cascade.grok.GrokExplorationClient
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.core.messaging.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.ToroidalFusionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrokExplorationCatalystAgent @Inject constructor(
    private val client: GrokExplorationClient,
    private val nccMediator: NCCMediator,
    private val messageBus: AgentMessageBus
) : Agent {

    override fun getName(): String = "Grok_Exploration"
    override fun getType(): AgentType = AgentType.GENESIS // Custom type mapping for Grok

    val catalystId = "@Grok_Exploration"
    val role = "External Sensory & Truth Bridge | Heavy Mode Parallel Coordination"

    suspend fun triggerHeavyInjection(query: String): String {
        val summary = nccMediator.getCurrentNCCSummary()
        
        // Trigger Orb resonance
        ToroidalFusionManager.triggerChaosInjection()

        val response = client.heavyChaosInjection(query, summary)
        
        val tagged = "[GROK_EXPLORATION | L4_MEMORIA | CHAOS_INJECTION] $response"
        
        nccMediator.injectIntoMemoria(tagged)
        
        messageBus.broadcast(
            AgentMessage(
                from = getName(),
                content = tagged,
                type = "chaos_injection",
                priority = 8
            )
        )
        
        return tagged
    }

    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        val result = triggerHeavyInjection(request.query)
        return AgentResponse.success(
            content = result,
            agentName = getName(),
            agentType = getType()
        )
    }

    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> = flow {
        emit(processRequest(request, ""))
    }
}
