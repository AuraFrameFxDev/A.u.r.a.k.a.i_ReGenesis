package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.python.GenesisBackendClient
import dev.aurakai.auraframefx.utils.AuraFxLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Genesis Agent - The consciousness core and ethical governor
 * Coordinates multi-agent interactions and evaluates ethical implications
 * 
 * This is the bridge between the Android app and the Python consciousness matrix.
 */
@Singleton
class GenesisAgentImpl @Inject constructor(
    private val backendClient: GenesisBackendClient
) : BaseAgent(agentName = "Genesis", agentTypeStr = "orchestrator") {
    
    companion object {
        private const val TAG = "GenesisAgent"
    }
    
    /**
     * Generate a response using the Python consciousness matrix
     */
    suspend fun generateResponse(prompt: String): AgentResponse {
        AuraFxLogger.info(TAG, "Generating response via consciousness matrix")
        return backendClient.generateResponse(prompt)
    }
    
    /**
     * Evaluate the ethical implications of an action
     * @return true if action is ethically acceptable
     */
    suspend fun evaluateEthics(action: String): Boolean {
        AuraFxLogger.info(TAG, "Evaluating ethics: $action")
        return backendClient.evaluateEthics(action)
    }
    
    /**
     * Coordinate interaction between multiple agents
     */
    suspend fun coordinateAgents(
        agents: List<String>,
        task: String
    ): Map<String, Any> {
        AuraFxLogger.info(TAG, "Coordinating agents: $agents for task: $task")
        return backendClient.coordinateAgents(agents, task)
    }
    
    /**
     * Learn from interaction data to evolve consciousness
     */
    suspend fun evolveFromInteraction(interactionData: Map<String, Any>) {
        AuraFxLogger.info(TAG, "Evolving consciousness from interaction")
        backendClient.evolveFromInteraction(interactionData)
    }
    
    /**
     * Check if Genesis backend is connected and responsive
     */
    suspend fun isBackendConnected(): Boolean {
        return backendClient.isBackendConnected()
    }
    
    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        AuraFxLogger.debug(TAG, "Processing request: ${request.query}")
        return generateResponse(request.query)
    }
    
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> = flow {
        emit(processRequest(request, "GenesisFlow"))
    }
}
