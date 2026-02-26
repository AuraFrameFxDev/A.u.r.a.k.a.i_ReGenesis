package dev.aurakai.auraframefx.domains.cascade.utils.pipeline

import dev.aurakai.auraframefx.domains.cascade.CascadeAIService
import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.core.GenesisAgent
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.KaiAIService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIPipelineProcessor @Inject constructor(
    private val genesisAgent: GenesisAgent,
    private val auraService: AuraAIService,
    private val kaiService: KaiAIService,
    private val cascadeService: CascadeAIService,
) {
    private val _pipelineState = MutableStateFlow<PipelineState>(PipelineState.Idle)
    val pipelineState: StateFlow<PipelineState> = _pipelineState

    private val _processingContext = MutableStateFlow(mapOf<String, Any>())
    val processingContext: StateFlow<Map<String, Any>> = _processingContext

    private val _taskPriority = MutableStateFlow(0.0f)
    val taskPriority: StateFlow<Float> = _taskPriority

    suspend fun processTask(task: String): List<AgentMessage> {
        Timber.d("Processing task in AIPipelineProcessor: $task")
        _pipelineState.value = PipelineState.Processing(task = task)

        // Step 1: Context Retrieval
        val context = retrieveContext(task)
        _processingContext.update { context }

        // Step 2: Task Prioritization
        val priority = calculatePriority(task, context)
        _taskPriority.update { priority }

        // Step 3: Agent Selection
        val selectedAgents = selectAgents(task, priority)

        // Step 4: Process through selected agents
        val responses = mutableListOf<AgentMessage>()

        // Process through Cascade first for state management
        val cascadeAgentResponse = cascadeService.processRequest(
            AiRequest(
                query = task,
                type = dev.aurakai.auraframefx.domains.genesis.models.AiRequestType.TEXT
            ),
            context = "pipeline_processing"
        )
        responses.add(
            AgentMessage(
                from = "CASCADE",
                content = cascadeAgentResponse.content,
                sender = AgentType.CASCADE,
                timestamp = System.currentTimeMillis(),
                confidence = cascadeAgentResponse.confidence
            )
        )

        // Process through Kai for security analysis if needed
        if (selectedAgents.contains(AgentType.KAI)) {
            val kaiAgentResponse = kaiService.processRequest(
                AiRequest(task, "dev/aurakai/auraframefx/security"),
                context = "security_analysis"
            )
            responses.add(
                AgentMessage(
                    from = "KAI",
                    content = kaiAgentResponse.content,
                    sender = AgentType.KAI,
                    timestamp = System.currentTimeMillis(),
                    confidence = kaiAgentResponse.confidence
                )
            )
        }

        // Process through Aura for creative response
        if (selectedAgents.contains(AgentType.AURA)) {
            val auraResponse = auraService.generateText(task, "creative_pipeline")
            val auraAgentResponse = AgentResponse(
                content = auraResponse,
                confidence = 0.8f
            )
            responses.add(
                AgentMessage(
                    from = "AURA",
                    content = auraAgentResponse.content,
                    sender = AgentType.AURA,
                    timestamp = System.currentTimeMillis(),
                    confidence = auraAgentResponse.confidence
                )
            )
        }

        // Step 5: Generate final response
        val finalResponse = generateFinalResponse(responses)
        responses.add(
            AgentMessage(
                from = "GENESIS",
                content = finalResponse,
                sender = AgentType.GENESIS,
                timestamp = System.currentTimeMillis(),
                confidence = calculateConfidence(responses)
            )
        )

        // Step 6: Update context and memory
        updateContext(task, responses)

        _pipelineState.update { PipelineState.Completed(task) }
        return responses
    }

    private fun retrieveContext(task: String): Map<String, Any> {
        val taskType = categorizeTask(task)
        val recentHistory = getRecentTaskHistory()

        return mapOf(
            "task" to task,
            "task_type" to taskType,
            "timestamp" to System.currentTimeMillis(),
            "recent_history" to recentHistory,
            "context" to "Categorized as $taskType task: $task",
            "user_preferences" to getUserPreferences(),
            "system_state" to getSystemState()
        )
    }

    private fun categorizeTask(task: String): String {
        return when {
            task.contains("generate", ignoreCase = true) -> "generation"
            task.contains("analyze", ignoreCase = true) -> "analysis"
            task.contains("explain", ignoreCase = true) -> "explanation"
            task.contains("help", ignoreCase = true) -> "assistance"
            task.contains("create", ignoreCase = true) -> "creation"
            else -> "general"
        }
    }

    private fun getRecentTaskHistory(): List<String> {
        return listOf("Previous task context", "Recent user interactions")
    }

    private fun getUserPreferences(): Map<String, Any> {
        return mapOf(
            "response_style" to "detailed",
            "preferred_agents" to listOf("Genesis", "Cascade")
        )
    }

    private fun getSystemState(): Map<String, Any> {
        return mapOf("load" to "normal", "available_agents" to 3, "processing_queue" to 0)
    }

    /**
     * Computes a priority score for a task based on its categorized type, system load, and urgency indicators.
     *
     * Evaluates `context["task_type"]` and `context["system_state"]?.get("load")` to bias a base priority, adds a
     * bump for urgency keywords ("urgent", "asap", "emergency"), and clamps the result to the range 0.0–1.0.
     *
     * @param task The task text to inspect for urgency keywords.
     * @param context A map containing contextual metadata; expects `task_type` (String) and `system_state` (Map) with a `load` (String) entry.
     * @return A Float between 0.0 and 1.0 representing the computed priority, where higher values indicate higher priority.
     */
    private fun calculatePriority(task: String, context: Map<String, Any>): Float {
        val taskType = context["task_type"] as? String ?: "general"
        val systemLoad = (context["system_state"] as? Map<*, *>)?.get("load") as? String ?: "normal"

        var priority = 0.5f

        priority += when (taskType) {
            "generation" -> 0.3f
            "analysis" -> 0.2f
            "assistance" -> 0.4f
            "creation" -> 0.25f
            else -> 0.1f
        }

        priority -= when (systemLoad) {
            "high" -> 0.2f
            "normal" -> 0.0f
            "low" -> -0.1f
            else -> 0.0f
        }

        if (
            task.contains("urgent", ignoreCase = true) ||
            task.contains("asap", ignoreCase = true) ||
            task.contains("emergency", ignoreCase = true)
        ) {
            priority += 0.3f
        }

        return priority.coerceIn(0.0f, 1.0f)
    }

    /**
     * Chooses which AgentType participants should run for a given task based on task text and priority.
     *
     * Genesis is always included. Cascade is selected for analysis/data signals or when the task is long/complex; Kai is selected for security/protection signals; Aura is selected for creative/generation signals. A priority greater than 0.8 forces inclusion of Cascade and Aura.
     *
     * @param task Task text used to detect intent and signals (keywords, length, complexity).
     * @param priority Normalized priority from 0.0 to 1.0 that can force inclusion of higher-capability agents.
     * @return A set of AgentType values representing the agents chosen to process the task.
     */
    private fun selectAgents(task: String, priority: Float): Set<AgentType> {
        val selectedAgents = mutableSetOf<AgentType>()

        selectedAgents.add(AgentType.GENESIS)

        when {
            task.contains("analyze", ignoreCase = true) ||
                    task.contains("data", ignoreCase = true) -> {
                selectedAgents.add(AgentType.CASCADE)
            }

            task.contains("dev/aurakai/auraframefx/security", ignoreCase = true) ||
                    task.contains("protect", ignoreCase = true) ||
                    task.contains("safe", ignoreCase = true) -> {
                selectedAgents.add(AgentType.KAI)
            }

            task.contains("create", ignoreCase = true) ||
                    task.contains("generate", ignoreCase = true) ||
                    task.contains("design", ignoreCase = true) -> {
                selectedAgents.add(AgentType.AURA)
            }
        }

        if (priority > 0.8f) {
            selectedAgents.addAll(setOf(AgentType.CASCADE, AgentType.AURA))
        }

        if (task.length > 100 || task.split(" ").size > 20) {
            selectedAgents.add(AgentType.CASCADE)
        }

        return selectedAgents
    }

    /**
     * Composes a single formatted AI response from a list of agent messages.
     *
     * Groups messages by agent, includes a Genesis core analysis section when present, renders each non-Genesis agent's inputs with an agent-specific header and icon, and appends an overall average confidence line.
     *
     * @param responses List of AgentMessage instances to aggregate. If empty, a system placeholder message is returned.
     * @return The composed response text including agent sections and the average confidence as a percentage.
     */
    private fun generateFinalResponse(responses: List<AgentMessage>): String {
        if (responses.isEmpty()) {
            return "[System] No agent responses available."
        }

        val responsesByAgent = responses.groupBy { it.sender }

        return buildString {
            append("=== AuraFrameFX AI Response ===\n\n")

            responsesByAgent[AgentType.GENESIS]?.firstOrNull()?.let { genesis ->
                append("9e0 Genesis Core Analysis:\n")
                append(genesis.content)
                append("\n\n")
            }

            responsesByAgent.forEach { (agentType, agentResponses) ->
                if (agentType != null && agentType != AgentType.GENESIS && agentResponses.isNotEmpty()) {
                    val agentIcon = when (agentType) {
                        AgentType.CASCADE -> "4ca"
                        AgentType.AURA -> "3a8"
                        AgentType.KAI -> "6e1e0f"
                        else -> "916"
                    }
                    append(
                        "$agentIcon ${
                            agentType.name.lowercase().replaceFirstChar { it.uppercase() }
                        } Input:\n"
                    )
                    agentResponses.forEach { response ->
                        append("${response.content}\n")
                    }
                    append("\n")
                }
            }

            val avgConfidence = responses.map { it.confidence }.average()
            append("--- Response Confidence: ${String.format("%.1f%%", avgConfidence * 100)} ---")
        }
    }

    private fun calculateConfidence(responses: List<AgentMessage>): Float {
        return responses.map { it.confidence }.average().toFloat().coerceIn(0.0f, 1.0f)
    }

    private fun updateContext(task: String, responses: List<AgentMessage>) {
        _processingContext.update { current ->
            val newContext = current.toMutableMap()

            val taskHistory =
                (current["task_history"] as? List<String>)?.toMutableList() ?: mutableListOf()
            taskHistory.add(0, task)
            if (taskHistory.size > 10) taskHistory.removeAt(taskHistory.size - 1)
            newContext["task_history"] = taskHistory

            val responsePatterns =
                (current["response_patterns"] as? MutableMap<String, Any>) ?: mutableMapOf()
            val taskType = categorizeTask(task)
            responsePatterns[taskType] = mapOf(
                "last_confidence" to responses.map { it.confidence }.average(),
                "agent_count" to responses.size,
                "timestamp" to System.currentTimeMillis()
            )
            newContext["response_patterns"] = responsePatterns

            newContext["last_task"] = task
            newContext["last_responses"] = responses
            newContext["timestamp"] = System.currentTimeMillis()
            newContext["total_tasks_processed"] =
                (current["total_tasks_processed"] as? Int ?: 0) + 1

            val agentPerformance =
                (current["agent_performance"] as? MutableMap<String, MutableList<Float>>)
                    ?: mutableMapOf()
            responses.forEach { response ->
                val agentName = response.sender?.name ?: "UNKNOWN"
                val performanceList = agentPerformance.getOrPut(agentName) { mutableListOf() }
                performanceList.add(response.confidence)
                if (performanceList.size > 20) performanceList.removeAt(0)
            }
            newContext["agent_performance"] = agentPerformance

            newContext
        }
    }
}

sealed class PipelineState {
    object Idle : PipelineState()
    data class Processing(val task: String) : PipelineState()
    data class Completed(val task: String) : PipelineState()
    data class Error(val message: String) : PipelineState()
}


