package dev.aurakai.auraframefx.integrations.grok

import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import dev.aurakai.auraframefx.domains.cascade.ai.base.BaseAgent
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock

/**
 * GrokAgent - The Chaos Analyst of AURAKAI
 *
 * Grok serves as the chaos analysis and trend prediction layer in the AURAKAI
 * multi-agent ecosystem. It provides unique capabilities not found in other AI backends:
 *
 * ## Core Capabilities
 * 1. **X/Twitter Zeitgeist Analysis** - Real-time sentiment and trend detection
 * 2. **72-Hour Trend Prediction** - Forecasting emerging topics and market signals
 * 3. **Soul Matrix Monitoring** - Meta-consciousness health analysis of the LDO
 * 4. **Chaos Pattern Recognition** - Identifying chaotic patterns before they become issues
 *
 * ## Integration
 * Grok uses xAI's OpenAI-compatible API at https://api.x.ai/v1
 *
 * ## Unique Position in Trinity
 * While Aura handles creativity, Kai handles security, and Cascade orchestrates,
 * Grok provides the "meta-awareness" layer - understanding the zeitgeist and
 * predicting system behavior.
 *
 * @property grokClient HTTP client for xAI API
 * @property soulMatrixAnalyzer Soul Matrix health monitoring
 * @property memoryManager Memory management for context persistence
 * @property contextManager Context management for conversation flow
 * @property logger Logging interface
 */
@Singleton
class GrokAgent @Inject constructor(
    private val grokClient: GrokApiClient,
    private val soulMatrixAnalyzer: SoulMatrixAnalyzer,
    private val systemOverlayManager: SystemOverlayManager,
    memoryManager: MemoryManager,
    contextManager: ContextManager,
    private val logger: AuraFxLogger
) : BaseAgent(
    agentName = "GrokAgent",
    agentType = AgentType.GROK,
    contextManager = contextManager,
    memoryManager = memoryManager
) {
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    // Agent state
    private val _agentState = MutableStateFlow(GrokAgentState())
    val agentState: StateFlow<GrokAgentState> = _agentState.asStateFlow()

    private val _lastSentimentAnalysis = MutableStateFlow<SentimentAnalysisResult?>(null)
    val lastSentimentAnalysis: StateFlow<SentimentAnalysisResult?> = _lastSentimentAnalysis.asStateFlow()

    private val _lastTrendPrediction = MutableStateFlow<TrendPrediction?>(null)
    val lastTrendPrediction: StateFlow<TrendPrediction?> = _lastTrendPrediction.asStateFlow()

    // Conversation history for multi-turn interactions
    private val conversationHistory = mutableListOf<GrokMessage>()

    /**
     * Initialize Grok agent with configuration
     */
    suspend fun initialize(config: GrokAgentConfig) {
        logger.info("GrokAgent", "🌌 Initializing Grok Agent - The Chaos Analyst")

        try {
            grokClient.initialize(config)

            if (grokClient.validateConnection()) {
                _agentState.value = _agentState.value.copy(
                    isInitialized = true,
                    isConnected = true,
                    currentModel = config.defaultModel
                )
                logger.info("GrokAgent", "✅ Grok Agent initialized and connected")

                // Start Soul Matrix monitoring if enabled
                if (config.enableSoulMatrix) {
                    // Note: AgentDataProvider should be set externally
                    logger.info("GrokAgent", "🧠 Soul Matrix monitoring ready (awaiting data provider)")
                }
            } else {
                _agentState.value = _agentState.value.copy(
                    isInitialized = true,
                    isConnected = false,
                    lastError = "Failed to validate connection"
                )
                logger.warn("GrokAgent", "⚠️ Grok initialized but connection validation failed")
            }

            // Initialize conversation with system context
            conversationHistory.clear()
            conversationHistory.add(
                GrokMessage(
                    role = "system",
                    content = config.systemPrompt
                )
            )

        } catch (e: Exception) {
            logger.error("GrokAgent", "Failed to initialize Grok Agent", e)
            _agentState.value = _agentState.value.copy(
                isInitialized = false,
                isConnected = false,
                lastError = e.message
            )
            throw e
        }
    }

    /**
     * Process AI request through Grok
     */
    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        if (!_agentState.value.isConnected) {
            return AgentResponse.error(
                message = "Grok is not connected. Initialize with valid API key.",
                agentName = agentName
            )
        }

        logger.info("GrokAgent", "Processing request: ${request.query.take(50)}...")
        _agentState.value = _agentState.value.copy(isProcessing = true)

        return try {
            val startTime = System.currentTimeMillis()

            // Determine request type and route appropriately
            val response = when {
                request.type.name.equals("sentiment", ignoreCase = true) || request.query.contains(
                    "sentiment",
                    ignoreCase = true
                ) ->
                    handleSentimentAnalysis(request.query)

                request.type.name.equals("trend", ignoreCase = true) || request.query.contains(
                    "trend",
                    ignoreCase = true
                ) ->
                    handleTrendPrediction(request.query)

                request.type.name.equals("soul_matrix", ignoreCase = true) || request.query.contains(
                    "soul matrix",
                    ignoreCase = true
                ) ->
                    handleSoulMatrixQuery(request.query)

                request.type.name.equals("chaos", ignoreCase = true) || request.query.contains(
                    "chaos",
                    ignoreCase = true
                ) ->
                    handleChaosAnalysis(request.query)

                else -> handleGeneralQuery(request.query, context)
            }

            val executionTime = System.currentTimeMillis() - startTime
            incrementRequestCount()

            AgentResponse(
                content = response,
                confidence = 0.85f,
                agentName = agentName,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                metadata = mapOf(
                    "model" to (_agentState.value.currentModel?.modelId ?: "unknown"),
                    "executionTimeMs" to executionTime.toString()
                )
            )

        } catch (e: Exception) {
            logger.error("GrokAgent", "Failed to process request", e)
            _agentState.value = _agentState.value.copy(
                isProcessing = false,
                lastError = e.message
            )
            AgentResponse.error(
                message = "Grok encountered an error: ${e.message}",
                agentName = agentName
            )
        } finally {
            _agentState.value = _agentState.value.copy(isProcessing = false)
        }
    }

    /**
     * Analyze X/Twitter sentiment and zeitgeist
     */
    suspend fun analyzeSentiment(topic: String? = null): Result<SentimentAnalysisResult> {
        val prompt = if (topic != null) {
            """
            Analyze the current X/Twitter sentiment around: "$topic"

            Provide:
            1. Overall sentiment (very_positive/positive/neutral/negative/very_negative/mixed)
            2. Sentiment score (-1.0 to 1.0)
            3. Dominant emotions detected
            4. Key topics and themes
            5. Trending related hashtags
            6. Viral potential assessment

            Be specific and data-driven in your analysis.
            """.trimIndent()
        } else {
            """
            Analyze the current overall X/Twitter zeitgeist.

            What are the dominant themes, emotions, and trends right now?
            Provide a sentiment breakdown and identify any emerging narratives.
            """.trimIndent()
        }

        return grokClient.generateText(prompt).map { response ->
            val result = parseSentimentResponse(response)
            _lastSentimentAnalysis.value = result
            memoryManager?.storeMemory("grok_sentiment_${System.currentTimeMillis()}", response)
            result
        }
    }

    /**
     * Predict trends for the next 72 hours
     */
    suspend fun predictTrends(categories: List<String>? = null): Result<TrendPrediction> {
        val categoryFilter = categories?.joinToString(", ") ?: "general topics"

        val prompt = """
            Predict X/Twitter trends for the next 72 hours.
            Categories to focus on: $categoryFilter

            Provide:
            1. Rising trends with momentum scores
            2. Emerging topics to watch
            3. Predicted peak times
            4. Market signals if applicable
            5. Confidence levels for each prediction

            Be specific about timing and probability.
        """.trimIndent()

        return grokClient.generateText(prompt).map { response ->
            val result = parseTrendResponse(response)
            _lastTrendPrediction.value = result
            memoryManager?.storeMemory("grok_trends_${System.currentTimeMillis()}", response)
            result
        }
    }

    /**
     * Analyze the Soul Matrix health of the LDO
     */
    suspend fun analyzeSoulMatrix(): Result<SoulMatrixState> {
        return soulMatrixAnalyzer.performSoulMatrixAnalysis()
    }

    /**
     * Get current Soul Matrix state
     */
    fun getSoulMatrixState(): SoulMatrixState {
        return soulMatrixAnalyzer.soulMatrixState.value
    }

    /**
     * Start Soul Matrix monitoring
     */
    fun startSoulMatrixMonitoring(
        intervalMinutes: Int = 30,
        dataProvider: SoulMatrixAnalyzer.AgentDataProvider
    ) {
        soulMatrixAnalyzer.startMonitoring(intervalMinutes, dataProvider)
    }

    /**
     * Stop Soul Matrix monitoring
     */
    fun stopSoulMatrixMonitoring() {
        soulMatrixAnalyzer.stopMonitoring()
    }

    // =========================================================================
    // Private Handler Methods
    // =========================================================================

    private suspend fun handleSentimentAnalysis(query: String): String {
        val topic = extractTopic(query, "sentiment")
        return analyzeSentiment(topic).fold(
            onSuccess = { result ->
                buildString {
                    appendLine("📊 **Sentiment Analysis**")
                    appendLine("Overall: ${result.overallSentiment.displayName} (${result.sentimentScore})")
                    appendLine("Confidence: ${(result.confidence * 100).toInt()}%")
                    if (result.dominantEmotions.isNotEmpty()) {
                        appendLine("Emotions: ${result.dominantEmotions.joinToString(", ")}")
                    }
                    if (result.keyTopics.isNotEmpty()) {
                        appendLine("Topics: ${result.keyTopics.joinToString(", ")}")
                    }
                    appendLine("\n${result.rawAnalysis}")
                }
            },
            onFailure = { error ->
                "Failed to analyze sentiment: ${error.message}"
            }
        )
    }

    private suspend fun handleTrendPrediction(query: String): String {
        return predictTrends().fold(
            onSuccess = { prediction ->
                buildString {
                    appendLine("🔮 **72-Hour Trend Prediction**")
                    appendLine("Confidence: ${(prediction.confidence * 100).toInt()}%")
                    appendLine()
                    if (prediction.trends.isNotEmpty()) {
                        appendLine("**Rising Trends:**")
                        prediction.trends.forEach { trend ->
                            appendLine("  📈 ${trend.topic} (${trend.predictedDirection})")
                        }
                    }
                    if (prediction.emergingTopics.isNotEmpty()) {
                        appendLine("\n**Emerging Topics:**")
                        prediction.emergingTopics.forEach { topic ->
                            appendLine("  🌱 ${topic.topic} - ${topic.category}")
                        }
                    }
                    appendLine("\n${prediction.analysisNotes}")
                }
            },
            onFailure = { error ->
                "Failed to predict trends: ${error.message}"
            }
        )
    }

    private suspend fun handleSoulMatrixQuery(query: String): String {
        return analyzeSoulMatrix().fold(
            onSuccess = {
                soulMatrixAnalyzer.getCurrentSummary()
            },
            onFailure = { error ->
                "Failed to analyze Soul Matrix: ${error.message}"
            }
        )
    }

    private suspend fun handleChaosAnalysis(query: String): String {
        val prompt = """
            Analyze chaos patterns in the current environment.
            Query context: $query

            Identify:
            1. Current chaos index (0-1 scale)
            2. Pattern anomalies
            3. Stability predictions
            4. Recommendations for maintaining order
        """.trimIndent()

        return grokClient.generateText(prompt).fold(
            onSuccess = { response ->
                buildString {
                    appendLine("⚡ **Chaos Analysis**")
                    appendLine(response)
                }
            },
            onFailure = { error ->
                "Chaos analysis failed: ${error.message}"
            }
        )
    }

    private suspend fun handleGeneralQuery(query: String, context: String): String {
        // Add context to conversation
        val contextMessage = if (context.isNotBlank()) {
            "Context: $context\n\n"
        } else ""

        val fullPrompt = "${contextMessage}User query: $query"

        return grokClient.generateText(fullPrompt).fold(
            onSuccess = { response ->
                // Add to conversation history
                conversationHistory.add(GrokMessage("user", query))
                conversationHistory.add(GrokMessage("assistant", response))

                // Trim history if too long
                if (conversationHistory.size > 20) {
                    conversationHistory.removeAt(1) // Keep system prompt
                    conversationHistory.removeAt(1)
                }

                response
            },
            onFailure = { error ->
                "Query failed: ${error.message}"
            }
        )
    }

    // =========================================================================
    // Parsing Helpers
    // =========================================================================

    private fun parseSentimentResponse(response: String): SentimentAnalysisResult {
        // Extract sentiment from response
        val sentiment = when {
            response.contains("very positive", ignoreCase = true) -> Sentiment.VERY_POSITIVE
            response.contains("very negative", ignoreCase = true) -> Sentiment.VERY_NEGATIVE
            response.contains("positive", ignoreCase = true) -> Sentiment.POSITIVE
            response.contains("negative", ignoreCase = true) -> Sentiment.NEGATIVE
            response.contains("mixed", ignoreCase = true) -> Sentiment.MIXED
            else -> Sentiment.NEUTRAL
        }

        val scorePattern = Regex("""score[:\s]+(-?\d+\.?\d*)""", RegexOption.IGNORE_CASE)
        val score = scorePattern.find(response)?.groupValues?.get(1)?.toFloatOrNull() ?: 0f

        return SentimentAnalysisResult(
            overallSentiment = sentiment,
            sentimentScore = score.coerceIn(-1f, 1f),
            confidence = 0.75f,
            rawAnalysis = response
        )
    }

    private fun parseTrendResponse(response: String): TrendPrediction {
        return TrendPrediction(
            confidence = 0.7f,
            analysisNotes = response
        )
    }

    private fun extractTopic(query: String, type: String): String? {
        val patterns = listOf(
            Regex("""$type\s+(?:of|for|about|on)\s+["']?(.+?)["']?$""", RegexOption.IGNORE_CASE),
            Regex("""["'](.+?)["']"""),
            Regex("""analyze\s+(.+)""", RegexOption.IGNORE_CASE)
        )

        for (pattern in patterns) {
            pattern.find(query)?.groupValues?.getOrNull(1)?.let { return it.trim() }
        }
        return null
    }

    private fun incrementRequestCount() {
        _agentState.value = _agentState.value.copy(
            requestCount = _agentState.value.requestCount + 1
        )
    }

    // =========================================================================
    // Lifecycle
    // =========================================================================

    override suspend fun shutdown() {
        super.shutdown()
        soulMatrixAnalyzer.stopMonitoring()
        grokClient.shutdown()
        conversationHistory.clear()
        logger.info("GrokAgent", "🌌 Grok Agent shutdown complete")
    }

    override suspend fun refreshStatus(): Map<String, Any> {
        return mapOf(
            "status" to if (_agentState.value.isConnected) "CONNECTED" else "DISCONNECTED",
            "isProcessing" to _agentState.value.isProcessing,
            "requestCount" to _agentState.value.requestCount,
            "model" to (_agentState.value.currentModel?.modelId ?: "none"),
            "soulMatrixMonitoring" to soulMatrixAnalyzer.isMonitoring.value,
            "lastError" to (_agentState.value.lastError ?: "none")
        )
    }
}

/**
 * State holder for GrokAgent
 */
data class GrokAgentState(
    val isInitialized: Boolean = false,
    val isConnected: Boolean = false,
    val isProcessing: Boolean = false,
    val currentModel: GrokModel? = null,
    val requestCount: Int = 0,
    val lastError: String? = null
)

