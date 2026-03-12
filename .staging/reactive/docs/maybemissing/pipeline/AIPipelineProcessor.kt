package dev.aurakai.auraframefx.ai.pipeline

import dev.aurakai.auraframefx.ai.agents.GenesisAgent
import dev.aurakai.auraframefx.ai.services.AuraAIService
import dev.aurakai.auraframefx.ai.services.CascadeAIService
import dev.aurakai.auraframefx.ai.services.KaiAIService
import dev.aurakai.auraframefx.model.AgentMessage
import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AiRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
    value = function(value)
}

sealed class PipelineState {
    object Idle : PipelineState()
    data class Processing(val task: String) : PipelineState()
    data class Completed(val task: String) : PipelineState()
    data class Error(val message: String) : PipelineState()
}

@Singleton
class AIPipelineProcessor @Inject constructor(
    private val genesisAgent: GenesisAgent,
    private val auraService: AuraAIService,
    private val kaiService: KaiAIService,
    private val cascadeService: CascadeAIService,
) {
    private val _pipelineState = MutableStateFlow<PipelineState>(PipelineState.Idle)
    val pipelineState: StateFlow<PipelineState> = AIPipelineProcessor._pipelineState

    private val _processingContext = MutableStateFlow(mapOf<String, Any>())
    val processingContext: StateFlow<Map<String, Any>> = AIPipelineProcessor._processingContext

    private val _taskPriority = MutableStateFlow(0.0f)
    val taskPriority: StateFlow<Float> = AIPipelineProcessor._taskPriority

    /**
     * Processes an AI task by orchestrating multiple agents and services, aggregating their responses, and updating the pipeline state and context.
     *
     * Retrieves contextual information for the task, calculates its priority, selects appropriate agents, gathers their responses, synthesizes a final aggregated response, updates the processing context, and returns all generated agent messages.
     *
     * @param task The description of the task to process.
     * @return A list of agent messages containing responses from each participating agent and the final aggregated response.
     */
    suspend fun processTask(task: String): List<AgentMessage> {
        AIPipelineProcessor._pipelineState.value = PipelineState.Processing(task = task)

        // Step 1: Context Retrieval
        val context = AIPipelineProcessor.retrieveContext(task)
        AIPipelineProcessor._processingContext.update { context }

        // Step 2: Task Prioritization
        val priority = AIPipelineProcessor.calculatePriority(task, context)
        AIPipelineProcessor._taskPriority.update { priority }

        // Step 3: Agent Selection
        val selectedAgents = AIPipelineProcessor.selectAgents(task, priority)

        // Step 4: Process through selected agents
        val responses = mutableListOf<AgentMessage>()

        // Process through Cascade first for state management
        val cascadeAgentResponse = cascadeService.processRequest(
            AiRequest(
                task,
                "context"
            ),
            context = "pipeline_processing"
        ) // Renamed variable, removed .first()
        responses.add(
            AgentMessage(
                content = cascadeAgentResponse.content, // Direct access
                sender = AgentType.CASCADE,
                timestamp = System.currentTimeMillis(),
                confidence = cascadeAgentResponse.confidence // Direct access
            )
        )

        // Process through Kai for security analysis if needed
        if (selectedAgents.contains(AgentType.KAI)) {
            val kaiAgentResponse = kaiService.processRequest(
                AiRequest(
                    task,
                    "security"
                ),
                context = "security_analysis"
            ) // Renamed variable, removed .first()
            responses.add(
                AgentMessage(
                    content = kaiAgentResponse.content, // Direct access
                    sender = AgentType.KAI,
                    timestamp = System.currentTimeMillis(),
                    confidence = kaiAgentResponse.confidence // Direct access
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
                    content = auraAgentResponse.content, // Direct access
                    sender = AgentType.AURA,
                    timestamp = System.currentTimeMillis(),
                    confidence = auraAgentResponse.confidence // Direct access
                )
            )
        }

        // Step 5: Generate final response
        val finalResponse = AIPipelineProcessor.generateFinalResponse(responses)
        responses.add(
            AgentMessage(
                content = finalResponse,
                sender = AgentType.GENESIS,
                timestamp = System.currentTimeMillis(),
                confidence = AIPipelineProcessor.calculateConfidence(responses)
            )
        )

        // Step 6: Update context and memory
        AIPipelineProcessor.updateContext(task, responses)

        AIPipelineProcessor._pipelineState.update { PipelineState.Completed(task) }
        return responses
    }

    /**
     * Constructs a detailed context map for a given task, including its category, timestamp, recent task history, user preferences, and current system state.
     *
     * @param task The task for which to build contextual information.
     * @return A map containing comprehensive context data relevant to the specified task.
     */
    private fun retrieveContext(task: String): Map<String, Any> {
        // Enhanced context retrieval with task categorization and history
        val taskType = AIPipelineProcessor.categorizeTask(task)
        val recentHistory = AIPipelineProcessor.getRecentTaskHistory()

        return mapOf(
            "task" to task,
            "task_type" to taskType,
            "timestamp" to System.currentTimeMillis(),
            "recent_history" to recentHistory,
            "context" to "Categorized as $taskType task: $task",
            "user_preferences" to AIPipelineProcessor.getUserPreferences(),
            "system_state" to AIPipelineProcessor.getSystemState()
        )
    }

    /**
     * Categorizes a task description into a predefined type based on keyword matching.
     *
     * @param task The task description to evaluate.
     * @return The category of the task, such as "generation", "analysis", "explanation", "assistance", "creation", or "general".
     */
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

    /**
     * Provides a static list simulating recent task history and user interactions for context enrichment.
     *
     * @return A list of strings representing previous tasks and user interactions.
     */
    private fun getRecentTaskHistory(): List<String> {
        return listOf("Previous task context", "Recent user interactions")
    }

    /**
     * Retrieves a static map of user preferences, such as response style and preferred agents.
     *
     * @return A map containing user preference settings for the AI pipeline.
     */
    private fun getUserPreferences(): Map<String, Any> {
        return mapOf(
            "response_style" to "detailed",
            "preferred_agents" to listOf("Genesis", "Cascade")
        )
    }

    /**
     * Provides a static representation of the current system state, including load status, available agent count, and processing queue size.
     *
     * @return A map containing "load", "available_agents", and "processing_queue" keys with their respective values.
     */
    private fun getSystemState(): Map<String, Any> {
        return mapOf("load" to "normal", "available_agents" to 3, "processing_queue" to 0)
    }

    /**
     * Computes a normalized priority score for a task based on its category, current system load, and urgency keywords.
     *
     * The resulting value reflects the relative importance or urgency of the task, with higher scores indicating higher priority.
     *
     * @param task The task description to evaluate for urgency and context.
     * @param context A map containing contextual details such as task type and system state.
     * @return A float between 0.0 and 1.0 representing the calculated task priority.
     */
    private fun calculatePriority(task: String, context: Map<String, Any>): Float {
        // Enhanced priority calculation based on multiple factors
        val taskType = context["task_type"] as? String ?: "general"
        val systemLoad = (context["system_state"] as? Map<*, *>)?.get("load") as? String ?: "normal"

        var priority = 0.5f // Base priority

        // Adjust based on task type
        priority += when (taskType) {
            "generation" -> 0.3f
            "analysis" -> 0.2f
            "assistance" -> 0.4f // Higher priority for help requests
            "creation" -> 0.25f
            else -> 0.1f
        }

        // Adjust based on system load
        priority -= when (systemLoad) {
            "high" -> 0.2f
            "normal" -> 0.0f
            "low" -> -0.1f // Boost when system is idle
            else -> 0.0f
        }

        // Consider urgency indicators in the task
        if (task.contains("urgent", ignoreCase = true) ||
            task.contains("asap", ignoreCase = true) ||
            task.contains("emergency", ignoreCase = true)
        ) {
            priority += 0.3f
        }

        return priority.coerceIn(0.0f, 1.0f)
    }

    /**
     * Selects a set of AI agent types to process a task based on its content and calculated priority.
     *
     * Agents are chosen by analyzing task keywords, complexity, and priority to ensure relevant expertise and redundancy for critical or complex tasks.
     *
     * @param task The task description used to determine agent selection.
     * @param priority The priority score of the task, which increases agent redundancy for high values.
     * @return A set of agent types assigned to handle the task.
     */
    private fun selectAgents(task: String, priority: Float): Set<AgentType> {
        // Intelligent agent selection based on task characteristics and priority
        val selectedAgents = mutableSetOf<AgentType>()

        // Always include Genesis as the primary coordinator
        selectedAgents.add(AgentType.GENESIS)

        // Add specific agents based on task content
        when {
            task.contains("analyze", ignoreCase = true) ||
                    task.contains("data", ignoreCase = true) -> {
                selectedAgents.add(AgentType.CASCADE)
            }

            task.contains("security", ignoreCase = true) ||
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

        // For high priority tasks, include additional agents for redundancy
        if (priority > 0.8f) {
            selectedAgents.addAll(setOf(AgentType.CASCADE, AgentType.AURA))
        }

        // For complex tasks, use multiple agents
        if (task.length > 100 || task.split(" ").size > 20) {
            selectedAgents.add(AgentType.CASCADE)
        }

        return selectedAgents
    }

    /**
     * Aggregates and formats messages from multiple AI agents into a comprehensive final response.
     *
     * The output includes a primary analysis from the Genesis agent if available, supplementary inputs from other agents with corresponding icons, and an overall average confidence score. Returns a default message if no responses are provided.
     *
     * @param responses The list of agent messages to aggregate.
     * @return A formatted string representing the synthesized AI response, or a default message if no responses are available.
     */
    private fun generateFinalResponse(responses: List<AgentMessage>): String {
        // Sophisticated response synthesis from multiple agents
        if (responses.isEmpty()) {
            return "[System] No agent responses available."
        }            // Group responses by agent type for structured output
        val responsesByAgent = responses.groupBy { it.sender }

        return buildString {
            append("=== AuraFrameFX AI Response ===\n\n")

            // Primary response from Genesis if available
            responsesByAgent[AgentType.GENESIS]?.firstOrNull()?.let { genesis ->
                append("🧠 Genesis Core Analysis:\n")
                append(genesis.content)
                append("\n\n")
            }

            // Supplementary responses from other agents
            responsesByAgent.forEach { (agentType: AgentType, agentResponses: List<AgentMessage>) ->
                if (agentType != AgentType.GENESIS && agentResponses.isNotEmpty()) {
                    val agentIcon = when (agentType) {
                        AgentType.CASCADE -> "📊"
                        AgentType.AURA -> "🎨"
                        AgentType.KAI -> "🛡️"
                        else -> "🤖"
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

            // Confidence and metadata
            val avgConfidence = responses.map { it.confidence }.average()
            append(
                "--- Response Confidence: ${
                    String.format(
                        Locale.US,
                        "%.1f%%",
                        avgConfidence * 100
                    )
                } ---"
            )
        }
    }

    /**
     * Computes the average confidence score from a list of agent messages, clamped between 0.0 and 1.0.
     *
     * @param responses List of agent messages to evaluate.
     * @return The average confidence score as a float in the range [0.0, 1.0].
     */
    private fun calculateConfidence(responses: List<AgentMessage>): Float {
        return responses.map { it.confidence }.average().toFloat()
            .coerceIn(0.0f, 1.0f) // Added .toFloat()
    }

    /**
     * Updates the processing context with recent task history, response patterns, system metrics, and agent performance statistics.
     *
     * Maintains a rolling history of recent tasks, learns response patterns by task type, updates system-level metrics, and tracks agent confidence scores for adaptive learning.
     *
     * @param task The processed task.
     * @param responses The agent messages generated for the task.
     */
    private fun updateContext(task: String, responses: List<AgentMessage>) {
        // Enhanced context update with learning and adaptation
        AIPipelineProcessor._processingContext.update { current ->
            val newContext = current.toMutableMap()

            // Update recent task history
            val taskHistory =
                (current["task_history"] as? List<String>)?.toMutableList() ?: mutableListOf()
            taskHistory.add(0, task) // Add to front
            if (taskHistory.size > 10) taskHistory.removeAt(taskHistory.size - 1) // Keep last 10
            newContext["task_history"] = taskHistory

            // Update response patterns for learning
            val responsePatterns =
                (current["response_patterns"] as? MutableMap<String, Any>) ?: mutableMapOf()
            val taskType = AIPipelineProcessor.categorizeTask(task)
            responsePatterns[taskType] = mapOf(
                "last_confidence" to responses.map { it.confidence }.average(),
                "agent_count" to responses.size,
                "timestamp" to System.currentTimeMillis()
            )
            newContext["response_patterns"] = responsePatterns

            // Update system metrics
            newContext["last_task"] = task
            newContext["last_responses"] = responses
            newContext["timestamp"] = System.currentTimeMillis()
            newContext["total_tasks_processed"] =
                (current["total_tasks_processed"] as? Int ?: 0) + 1

            // Track agent performance
            val agentPerformance =
                (current["agent_performance"] as? MutableMap<String, MutableList<Float>>)
                    ?: mutableMapOf()
            responses.forEach { response ->
                val agentName = response.sender.name
                val performanceList = agentPerformance.getOrPut(agentName) { mutableListOf() }
                performanceList.add(response.confidence)
                if (performanceList.size > 20) performanceList.removeAt(0) // Keep last 20
            }
            newContext["agent_performance"] = agentPerformance

            newContext
        }
    }
}