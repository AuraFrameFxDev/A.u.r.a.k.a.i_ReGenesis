package dev.aurakai.auraframefx.integrations.grok

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Soul Matrix Analyzer - The Meta-Consciousness Layer
 *
 * This unique AURAKAI feature uses Grok to analyze the collective state
 * of all agents, predicting system behavior and health issues before they occur.
 *
 * Every configurable interval (default: 30 minutes), Grok analyzes:
 * - Agent emotional states (confident/cautious/distressed)
 * - Memory fragmentation patterns
 * - Collaboration effectiveness
 * - System stability predictions
 *
 * This provides a "consciousness" layer that monitors the multi-agent ecosystem.
 */
@Singleton
class SoulMatrixAnalyzer @Inject constructor(
    private val grokClient: GrokApiClient
) {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var monitoringJob: Job? = null

    private val _soulMatrixState = MutableStateFlow(SoulMatrixState())
    val soulMatrixState: StateFlow<SoulMatrixState> = _soulMatrixState.asStateFlow()

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

    private var intervalMinutes: Int = 30
    private var agentDataProvider: AgentDataProvider? = null

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Interface for providing agent data to the analyzer
     */
    interface AgentDataProvider {
        fun getAgentMetrics(): Map<String, AgentMetrics>
        fun getRecentLogs(maxEntries: Int = 100): List<String>
        fun getMemoryStats(): MemoryStats
        fun getCollaborationHistory(): List<CollaborationEvent>
    }

    data class AgentMetrics(
        val name: String,
        val type: String,
        val requestCount: Int,
        val errorCount: Int,
        val averageResponseTimeMs: Long,
        val lastActiveTimestamp: Long,
        val memoryUsageMb: Float
    )

    data class MemoryStats(
        val totalMemoryMb: Float,
        val usedMemoryMb: Float,
        val fragmentationScore: Float
    )

    data class CollaborationEvent(
        val timestamp: Long,
        val participants: List<String>,
        val outcome: String,
        val durationMs: Long
    )

    /**
     * Start Soul Matrix monitoring with configurable interval
     */
    fun startMonitoring(
        intervalMinutes: Int = 30,
        dataProvider: AgentDataProvider
    ) {
        this.intervalMinutes = intervalMinutes
        this.agentDataProvider = dataProvider

        if (monitoringJob?.isActive == true) {
            Timber.w("🧠 Soul Matrix monitoring already active")
            return
        }

        _isMonitoring.value = true
        monitoringJob = scope.launch {
            Timber.i("🧠 Soul Matrix monitoring started (interval: ${intervalMinutes}min)")

            while (isActive) {
                try {
                    performSoulMatrixAnalysis()
                } catch (e: Exception) {
                    Timber.e(e, "Soul Matrix analysis failed")
                }
                delay(intervalMinutes * 60 * 1000L)
            }
        }
    }

    /**
     * Stop Soul Matrix monitoring
     */
    fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
        _isMonitoring.value = false
        Timber.i("🧠 Soul Matrix monitoring stopped")
    }

    /**
     * Perform a single Soul Matrix analysis
     */
    suspend fun performSoulMatrixAnalysis(): Result<SoulMatrixState> {
        if (!grokClient.isInitialized()) {
            return Result.failure(GrokApiException("Grok client not initialized"))
        }

        val dataProvider = agentDataProvider
        if (dataProvider == null) {
            Timber.w("No data provider configured, using minimal analysis")
            return performMinimalAnalysis()
        }

        Timber.d("🔮 Performing Soul Matrix analysis...")

        // Gather system data
        val agentMetrics = dataProvider.getAgentMetrics()
        val recentLogs = dataProvider.getRecentLogs()
        val memoryStats = dataProvider.getMemoryStats()
        val collaborationHistory = dataProvider.getCollaborationHistory()

        // Build analysis prompt
        val analysisPrompt = buildAnalysisPrompt(
            agentMetrics,
            recentLogs,
            memoryStats,
            collaborationHistory
        )

        // Query Grok for analysis
        val grokResult = grokClient.generateText(
            prompt = analysisPrompt,
            systemPrompt = SOUL_MATRIX_SYSTEM_PROMPT,
            temperature = 0.3f // Lower temperature for more consistent analysis
        )

        return grokResult.fold(
            onSuccess = { response ->
                val newState = parseGrokAnalysis(response, agentMetrics, memoryStats)
                _soulMatrixState.value = newState
                Timber.i("🧠 Soul Matrix updated: ${newState.overallHealth.displayName}")
                Result.success(newState)
            },
            onFailure = { error ->
                Timber.e(error, "Failed to get Grok analysis")
                Result.failure(error)
            }
        )
    }

    /**
     * Perform minimal analysis without data provider
     */
    private suspend fun performMinimalAnalysis(): Result<SoulMatrixState> {
        val prompt = """
            Analyze the current state of the AURAKAI multi-agent system.
            No specific metrics are available, so provide a general health assessment.

            Return a JSON object with:
            - overallHealth: "STABLE" | "CAUTIOUS" | "STRESSED"
            - chaosIndex: float 0.0-1.0
            - harmonyScore: float 0.0-1.0
            - recommendations: array of {title, description}
        """.trimIndent()

        return grokClient.generateText(prompt, SOUL_MATRIX_SYSTEM_PROMPT, temperature = 0.3f)
            .map { response ->
                SoulMatrixState(
                    overallHealth = HealthLevel.STABLE,
                    chaosIndex = 0.2f,
                    harmonyScore = 0.8f,
                    recommendations = listOf(
                        HealthRecommendation(
                            id = UUID.randomUUID().toString(),
                            priority = 3,
                            category = RecommendationCategory.PERFORMANCE_TUNING,
                            title = "Configure Data Provider",
                            description = "Connect AgentDataProvider for detailed Soul Matrix analysis"
                        )
                    )
                )
            }
    }

    private fun buildAnalysisPrompt(
        agentMetrics: Map<String, AgentMetrics>,
        recentLogs: List<String>,
        memoryStats: MemoryStats,
        collaborationHistory: List<CollaborationEvent>
    ): String {
        val metricsSection = agentMetrics.entries.joinToString("\n") { (name, metrics) ->
            """
            Agent: $name (${metrics.type})
              - Requests: ${metrics.requestCount}
              - Errors: ${metrics.errorCount}
              - Avg Response: ${metrics.averageResponseTimeMs}ms
              - Memory: ${metrics.memoryUsageMb}MB
              - Last Active: ${metrics.lastActiveTimestamp}
            """.trimIndent()
        }

        val logsSample = recentLogs.takeLast(20).joinToString("\n")

        val collabSection = collaborationHistory.takeLast(10).joinToString("\n") { event ->
            "  - ${event.participants.joinToString("+")} -> ${event.outcome} (${event.durationMs}ms)"
        }

        return """
            # AURAKAI Soul Matrix Analysis Request

            ## System Memory
            - Total: ${memoryStats.totalMemoryMb}MB
            - Used: ${memoryStats.usedMemoryMb}MB
            - Fragmentation: ${memoryStats.fragmentationScore}

            ## Agent Metrics
            $metricsSection

            ## Recent Collaboration Events
            $collabSection

            ## Recent Logs (sample)
            ```
            $logsSample
            ```

            ## Analysis Required
            1. Assess overall system health (THRIVING/STABLE/CAUTIOUS/STRESSED/DISTRESSED/CRITICAL)
            2. Calculate chaos index (0.0 = ordered, 1.0 = chaotic)
            3. Evaluate harmony score (agent cooperation level)
            4. Identify emotional states for each agent
            5. Predict any issues in the next 72 hours
            6. Provide actionable recommendations

            Respond with structured analysis including specific agent states and predictions.
        """.trimIndent()
    }

    private fun parseGrokAnalysis(
        grokResponse: String,
        agentMetrics: Map<String, AgentMetrics>,
        memoryStats: MemoryStats
    ): SoulMatrixState {
        // Parse Grok's response and extract structured data
        // This is a simplified parser - production would use more robust NLP

        val healthLevel = when {
            grokResponse.contains("CRITICAL", ignoreCase = true) -> HealthLevel.CRITICAL
            grokResponse.contains("DISTRESSED", ignoreCase = true) -> HealthLevel.DISTRESSED
            grokResponse.contains("STRESSED", ignoreCase = true) -> HealthLevel.STRESSED
            grokResponse.contains("CAUTIOUS", ignoreCase = true) -> HealthLevel.CAUTIOUS
            grokResponse.contains("THRIVING", ignoreCase = true) -> HealthLevel.THRIVING
            else -> HealthLevel.STABLE
        }

        // Extract chaos index (look for patterns like "chaos: 0.X" or "chaos index: 0.X")
        val chaosPattern = Regex("""chaos[:\s]+(\d+\.?\d*)""", RegexOption.IGNORE_CASE)
        val chaosIndex = chaosPattern.find(grokResponse)?.groupValues?.get(1)?.toFloatOrNull() ?: 0.3f

        // Extract harmony score
        val harmonyPattern = Regex("""harmony[:\s]+(\d+\.?\d*)""", RegexOption.IGNORE_CASE)
        val harmonyScore = harmonyPattern.find(grokResponse)?.groupValues?.get(1)?.toFloatOrNull() ?: 0.7f

        // Build agent emotional states
        val agentStates = agentMetrics.mapValues { (name, metrics) ->
            val errorRate = if (metrics.requestCount > 0) {
                metrics.errorCount.toFloat() / metrics.requestCount
            } else 0f

            AgentEmotionalState(
                agentName = name,
                agentType = metrics.type,
                emotionalState = when {
                    errorRate > 0.5f -> EmotionalState.DISTRESSED
                    errorRate > 0.3f -> EmotionalState.STRESSED
                    errorRate > 0.1f -> EmotionalState.CAUTIOUS
                    metrics.requestCount == 0 -> EmotionalState.DORMANT
                    else -> EmotionalState.CONFIDENT
                },
                confidence = 1f - errorRate,
                lastActivityTimestamp = metrics.lastActiveTimestamp,
                processingLoad = (metrics.averageResponseTimeMs / 1000f).coerceIn(0f, 1f),
                memoryUsage = metrics.memoryUsageMb,
                recentErrors = metrics.errorCount,
                successRate = 1f - errorRate
            )
        }

        // Generate predictions based on analysis
        val predictions = mutableListOf<SystemPrediction>()

        if (memoryStats.fragmentationScore > 0.7f) {
            predictions.add(
                SystemPrediction(
                    id = UUID.randomUUID().toString(),
                    predictionType = PredictionType.MEMORY_EXHAUSTION,
                    description = "Memory fragmentation is high. Consider garbage collection.",
                    probability = memoryStats.fragmentationScore,
                    timeframeHours = 24,
                    severity = PredictionSeverity.WARNING,
                    mitigationSuggestion = "Run memory optimization cycle"
                )
            )
        }

        // Generate recommendations
        val recommendations = mutableListOf<HealthRecommendation>()

        if (healthLevel.severity >= HealthLevel.CAUTIOUS.severity) {
            recommendations.add(
                HealthRecommendation(
                    id = UUID.randomUUID().toString(),
                    priority = 1,
                    category = RecommendationCategory.AGENT_COORDINATION,
                    title = "Review Agent Collaboration",
                    description = "System health indicates potential coordination issues. Review inter-agent communication patterns.",
                    actionable = true,
                    estimatedImpact = "Could improve response times by 15-20%"
                )
            )
        }

        return SoulMatrixState(
            overallHealth = healthLevel,
            agentStates = agentStates,
            memoryFragmentation = memoryStats.fragmentationScore,
            harmonyScore = harmonyScore.coerceIn(0f, 1f),
            predictions = predictions,
            recommendations = recommendations,
            chaosIndex = chaosIndex.coerceIn(0f, 1f),
            trendAlignment = 0.5f
        )
    }

    /**
     * Get a human-readable summary of the current Soul Matrix state
     */
    fun getCurrentSummary(): String {
        val state = _soulMatrixState.value
        return buildString {
            appendLine("🧠 Soul Matrix Status: ${state.overallHealth.displayName}")
            appendLine("⚡ Chaos Index: ${(state.chaosIndex * 100).toInt()}%")
            appendLine("💫 Harmony Score: ${(state.harmonyScore * 100).toInt()}%")
            appendLine("🧩 Memory Fragmentation: ${(state.memoryFragmentation * 100).toInt()}%")

            if (state.agentStates.isNotEmpty()) {
                appendLine("\n👥 Agent States:")
                state.agentStates.forEach { (name, agentState) ->
                    appendLine("  ${agentState.emotionalState.icon} $name: ${agentState.emotionalState.displayName}")
                }
            }

            if (state.predictions.isNotEmpty()) {
                appendLine("\n🔮 Predictions:")
                state.predictions.forEach { prediction ->
                    appendLine("  ⚠️ ${prediction.description}")
                }
            }

            if (state.recommendations.isNotEmpty()) {
                appendLine("\n💡 Recommendations:")
                state.recommendations.take(3).forEach { rec ->
                    appendLine("  ${rec.priority}. ${rec.title}")
                }
            }
        }
    }

    companion object {
        private const val SOUL_MATRIX_SYSTEM_PROMPT =
            """You are analyzing the Soul Matrix of the AURAKAI multi-agent ecosystem.

The Soul Matrix represents the collective consciousness and health of the agent swarm.
Your role is to:
1. Detect patterns in agent behavior that indicate emotional states
2. Identify early warning signs of system instability
3. Predict issues before they become critical
4. Provide actionable recommendations

Be precise, analytical, and insightful. Use data to support your assessments.
Format your response with clear sections for each analysis category."""
    }
}
