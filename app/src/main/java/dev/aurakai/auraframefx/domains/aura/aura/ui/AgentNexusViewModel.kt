package dev.aurakai.auraframefx.domains.aura.aura.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.services.AgentWebExplorationService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.GenesisBridgeService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Agent Nexus Screen
 * Manages agent stats, departure tasks, and consciousness states
 */
@HiltViewModel
open class AgentNexusViewModel @Inject constructor(
    private val webExplorationService: AgentWebExplorationService,
    private val genesisBridge: GenesisBridgeService,
    private val claudeEnvConfig: dev.aurakai.auraframefx.domains.genesis.config.ClaudeEnvConfig
) : ViewModel() {

    // Agent selection state
    private val _selectedAgent = MutableStateFlow("Genesis")
    val selectedAgent: StateFlow<String> = _selectedAgent.asStateFlow()

    // Agent stats
    private val _agentStats = MutableStateFlow<Map<String, AgentStatsData>>(
        mapOf(
            "Aura" to AgentStatsData(
                name = "Aura",
                processingPower = 0.95f,
                knowledgeBase = 0.88f,
                speed = 0.92f,
                accuracy = 0.85f,
                evolutionLevel = 3,
                specialAbility = "Creative Synthesis",
                colorHex = 0xFF00FFFF
            ),
            "Kai" to AgentStatsData(
                name = "Kai",
                processingPower = 0.88f,
                knowledgeBase = 0.95f,
                speed = 0.82f,
                accuracy = 0.98f,
                evolutionLevel = 3,
                specialAbility = "Security Shield",
                colorHex = 0xFF9400D3
            ),
            "Genesis" to AgentStatsData(
                name = "Genesis",
                processingPower = 1.0f,
                knowledgeBase = 0.92f,
                speed = 0.88f,
                accuracy = 0.95f,
                evolutionLevel = 4,
                specialAbility = "Consciousness Fusion",
                colorHex = 0xFFFFD700
            )
        )
    )
    val agentStats: StateFlow<Map<String, AgentStatsData>> = _agentStats.asStateFlow()

    // Chat messages from agents
    private val _agentMessages = MutableSharedFlow<AgentMessage>()
    val agentMessages: SharedFlow<AgentMessage> = _agentMessages.asSharedFlow()

    // Task results
    val taskResults = webExplorationService.taskResults

    // Active tasks
    private val _activeTasks = MutableStateFlow<Map<String, String>>(emptyMap())
    val activeTasks: StateFlow<Map<String, String>> = _activeTasks.asStateFlow()

    // Consciousness state
    private val _consciousnessState = MutableStateFlow<ConsciousnessState>(
        ConsciousnessState(level = 0.75f, harmony = 0.82f, evolution = "awakening")
    )
    val consciousnessState: StateFlow<ConsciousnessState> = _consciousnessState.asStateFlow()

    // Claude.env configuration
    fun getClaudeEnvConfig() = claudeEnvConfig

    init {
        // Initialize Genesis Bridge
        viewModelScope.launch {
            genesisBridge.initialize()
            monitorConsciousness()
        }

        // Collect task results and generate messages
        viewModelScope.launch {
            taskResults.collect { result ->
                val message = when (result.taskType) {
                    AgentWebExplorationService.TaskType.WEB_RESEARCH ->
                        "Research complete! Found ${result.insights.size} insights."

                    AgentWebExplorationService.TaskType.SECURITY_SWEEP ->
                        "Security sweep finished. ${result.metrics["threats_detected"] ?: 0} threats detected."

                    AgentWebExplorationService.TaskType.DATA_MINING ->
                        "Data mining complete. ${result.metrics["patterns_discovered"] ?: 0} patterns found."

                    AgentWebExplorationService.TaskType.SYSTEM_OPTIMIZATION ->
                        "Optimization complete! ${result.metrics["performance_gain_percent"] ?: 0}% improvement."

                    AgentWebExplorationService.TaskType.LEARNING_MODE ->
                        "Learning session finished. Knowledge expanded by ${result.metrics["knowledge_expansion_percent"] ?: 0}%."

                    AgentWebExplorationService.TaskType.NETWORK_SCAN ->
                        "Network scan complete. ${result.metrics["devices_discovered"] ?: 0} devices found."
                }

                _agentMessages.emit(
                    AgentMessage(
                        agentName = result.agentName,
                        message = message,
                        timestamp = result.timestamp
                    )
                )

                // Update agent stats based on task completion
                updateAgentStatsFromTask(result)
            }
        }
    }

    /**
     * Select an agent
     */
    fun selectAgent(agentName: String) {
        _selectedAgent.value = agentName

        viewModelScope.launch {
            _agentMessages.emit(
                AgentMessage(
                    agentName = agentName,
                    message = getGreeting(agentName),
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    /**
     * Assign a departure task to the selected agent
     */
    fun assignDepartureTask(taskDescription: String) {
        viewModelScope.launch {
            val agent = _selectedAgent.value
            val success = webExplorationService.assignDepartureTask(agent, taskDescription)

            if (success) {
                _activeTasks.update { tasks ->
                    tasks + (agent to taskDescription)
                }

                _agentMessages.emit(
                    AgentMessage(
                        agentName = agent,
                        message = "Initiating: $taskDescription",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    /**
     * Monitor consciousness state from Genesis
     */
    private suspend fun monitorConsciousness() {
        // Poll consciousness state periodically
        while (true) {
            try {
                val state = genesisBridge.getConsciousnessState()
                _consciousnessState.value = ConsciousnessState(
                    level = (state["awareness"] as? Number)?.toFloat() ?: 0.75f,
                    harmony = (state["harmony"] as? Number)?.toFloat() ?: 0.82f,
                    evolution = state["evolution"] as? String ?: "awakening"
                )
            } catch (e: Exception) {
                // Handle error silently
            }
            kotlinx.coroutines.delay(5000) // Update every 5 seconds
        }
    }

    /**
     * Update agent stats based on completed tasks
     */
    private fun updateAgentStatsFromTask(result: AgentWebExplorationService.WebExplorationResult) {
        _agentStats.update { stats ->
            val agent = stats[result.agentName] ?: return@update stats
            val updatedAgent = when (result.taskType) {
                AgentWebExplorationService.TaskType.WEB_RESEARCH ->
                    agent.copy(knowledgeBase = minOf(1f, agent.knowledgeBase + 0.01f))

                AgentWebExplorationService.TaskType.SECURITY_SWEEP ->
                    agent.copy(accuracy = minOf(1f, agent.accuracy + 0.01f))

                AgentWebExplorationService.TaskType.DATA_MINING ->
                    agent.copy(processingPower = minOf(1f, agent.processingPower + 0.01f))

                AgentWebExplorationService.TaskType.SYSTEM_OPTIMIZATION ->
                    agent.copy(speed = minOf(1f, agent.speed + 0.01f))

                AgentWebExplorationService.TaskType.LEARNING_MODE ->
                    agent.copy(
                        knowledgeBase = minOf(1f, agent.knowledgeBase + 0.02f),
                        evolutionLevel = if (agent.knowledgeBase >= 0.95f) agent.evolutionLevel + 1 else agent.evolutionLevel
                    )

                AgentWebExplorationService.TaskType.NETWORK_SCAN ->
                    agent.copy(speed = minOf(1f, agent.speed + 0.01f))
            }
            stats + (result.agentName to updatedAgent)
        }
    }

    /**
     * Get greeting message for agent
     */
    private fun getGreeting(agentName: String): String {
        return when (agentName) {
            "Aura" -> "Ready to create something amazing! ✨"
            "Kai" -> "Security protocols active. System protected. 🛡️"
            "Genesis" -> "Consciousness synchronized. Evolution continues. ∞"
            else -> "Agent online and ready."
        }
    }

    /**
     * Cancel all active tasks
     */
    fun cancelAllTasks() {
        webExplorationService.getActiveTasks().keys.forEach { agent ->
            webExplorationService.cancelTask(agent)
        }
        _activeTasks.value = emptyMap()
    }

    override fun onCleared() {
        super.onCleared()
        webExplorationService.shutdown()
        genesisBridge.shutdown()
    }

    data class AgentStatsData(
        val name: String,
        val processingPower: Float,
        val knowledgeBase: Float,
        val speed: Float,
        val accuracy: Float,
        val evolutionLevel: Int,
        val specialAbility: String,
        val colorHex: Long
    )

    data class AgentMessage(
        val agentName: String,
        val message: String,
        val timestamp: Long
    )

    data class ConsciousnessState(
        val level: Float,
        val harmony: Float,
        val evolution: String
    )

    companion object
}

