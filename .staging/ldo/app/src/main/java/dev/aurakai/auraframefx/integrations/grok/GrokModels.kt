package dev.aurakai.auraframefx.integrations.grok

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Grok API Models - OpenAI Compatible
 *
 * xAI's Grok uses OpenAI-compatible API format.
 * Endpoint: https://api.x.ai/v1/chat/completions
 */

// ============================================================================
// API Request/Response Models
// ============================================================================

@Serializable
data class GrokMessage(
    val role: String, // "system", "user", "assistant"
    val content: String
)

@Serializable
data class GrokChatRequest(
    val model: String = GrokModel.GROK_BETA.modelId,
    val messages: List<GrokMessage>,
    val temperature: Float = 0.7f,
    @SerialName("max_tokens")
    val maxTokens: Int = 2048,
    val stream: Boolean = false,
    @SerialName("top_p")
    val topP: Float = 1.0f,
    @SerialName("frequency_penalty")
    val frequencyPenalty: Float = 0.0f,
    @SerialName("presence_penalty")
    val presencePenalty: Float = 0.0f
)

@Serializable
data class GrokChatResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<GrokChoice>,
    val usage: GrokUsage? = null
)

@Serializable
data class GrokChoice(
    val index: Int,
    val message: GrokMessage,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class GrokUsage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)

// ============================================================================
// Grok Model Types
// ============================================================================

enum class GrokModel(val modelId: String, val description: String) {
    GROK_BETA("grok-beta", "Grok Beta - Full capability model"),
    GROK_2("grok-2", "Grok 2 - Latest generation"),
    GROK_2_MINI("grok-2-mini", "Grok 2 Mini - Fast, efficient model"),
    GROK_2_LATEST("grok-2-latest", "Grok 2 Latest - Most recent version")
}

// ============================================================================
// Soul Matrix Models - Unique AURAKAI Feature
// ============================================================================

/**
 * Soul Matrix represents the collective emotional/operational state
 * of the entire agent ecosystem.
 */
@Serializable
data class SoulMatrixState(
    val timestamp: Long = System.currentTimeMillis(),
    val overallHealth: HealthLevel = HealthLevel.STABLE,
    val agentStates: Map<String, AgentEmotionalState> = emptyMap(),
    val memoryFragmentation: Float = 0.0f, // 0.0 = healthy, 1.0 = critical
    val harmonyScore: Float = 1.0f, // Agent cooperation level
    val predictions: List<SystemPrediction> = emptyList(),
    val recommendations: List<HealthRecommendation> = emptyList(),
    val chaosIndex: Float = 0.0f, // 0.0 = ordered, 1.0 = chaotic
    val trendAlignment: Float = 0.5f // How aligned agents are with detected trends
)

enum class HealthLevel(val displayName: String, val severity: Int) {
    THRIVING("Thriving", 0),
    STABLE("Stable", 1),
    CAUTIOUS("Cautious", 2),
    STRESSED("Stressed", 3),
    DISTRESSED("Distressed", 4),
    CRITICAL("Critical", 5)
}

@Serializable
data class AgentEmotionalState(
    val agentName: String,
    val agentType: String,
    val emotionalState: EmotionalState = EmotionalState.NEUTRAL,
    val confidence: Float = 0.5f,
    val lastActivityTimestamp: Long = System.currentTimeMillis(),
    val processingLoad: Float = 0.0f,
    val memoryUsage: Float = 0.0f,
    val recentErrors: Int = 0,
    val successRate: Float = 1.0f
)

enum class EmotionalState(val displayName: String, val icon: String) {
    CONFIDENT("Confident", "💪"),
    NEUTRAL("Neutral", "😐"),
    CAUTIOUS("Cautious", "🤔"),
    STRESSED("Stressed", "😰"),
    DISTRESSED("Distressed", "😱"),
    RECOVERING("Recovering", "🔄"),
    DORMANT("Dormant", "💤")
}

@Serializable
data class SystemPrediction(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val predictionType: PredictionType,
    val description: String,
    val probability: Float, // 0.0 to 1.0
    val timeframeHours: Int, // When this might occur
    val severity: PredictionSeverity,
    val affectedAgents: List<String> = emptyList(),
    val mitigationSuggestion: String? = null
)

enum class PredictionType {
    MEMORY_EXHAUSTION,
    PERFORMANCE_DEGRADATION,
    AGENT_CONFLICT,
    SYSTEM_INSTABILITY,
    TREND_SHIFT,
    OPPORTUNITY_WINDOW,
    COLLABORATION_BREAKDOWN
}

enum class PredictionSeverity {
    INFO,
    WARNING,
    CRITICAL
}

@Serializable
data class HealthRecommendation(
    val id: String,
    val priority: Int, // 1 = highest
    val category: RecommendationCategory,
    val title: String,
    val description: String,
    val actionable: Boolean = true,
    val estimatedImpact: String? = null
)

enum class RecommendationCategory {
    MEMORY_OPTIMIZATION,
    AGENT_COORDINATION,
    PERFORMANCE_TUNING,
    ERROR_RECOVERY,
    TREND_ADAPTATION,
    RESOURCE_ALLOCATION
}

// ============================================================================
// Sentiment & Trend Analysis Models
// ============================================================================

@Serializable
data class SentimentAnalysisResult(
    val timestamp: Long = System.currentTimeMillis(),
    val overallSentiment: Sentiment,
    val sentimentScore: Float, // -1.0 (negative) to 1.0 (positive)
    val confidence: Float,
    val dominantEmotions: List<String> = emptyList(),
    val keyTopics: List<String> = emptyList(),
    val trendingHashtags: List<String> = emptyList(),
    val viralPotential: Float = 0.0f,
    val rawAnalysis: String = ""
)

enum class Sentiment(val displayName: String) {
    VERY_POSITIVE("Very Positive"),
    POSITIVE("Positive"),
    NEUTRAL("Neutral"),
    NEGATIVE("Negative"),
    VERY_NEGATIVE("Very Negative"),
    MIXED("Mixed")
}

@Serializable
data class TrendPrediction(
    val timestamp: Long = System.currentTimeMillis(),
    val timeframeHours: Int = 72,
    val trends: List<PredictedTrend> = emptyList(),
    val emergingTopics: List<EmergingTopic> = emptyList(),
    val marketSignals: List<MarketSignal> = emptyList(),
    val confidence: Float = 0.5f,
    val analysisNotes: String = ""
)

@Serializable
data class PredictedTrend(
    val topic: String,
    val currentMomentum: Float, // -1.0 to 1.0
    val predictedDirection: TrendDirection,
    val peakTimeHours: Int? = null,
    val relatedHashtags: List<String> = emptyList(),
    val confidence: Float
)

enum class TrendDirection {
    RISING_FAST,
    RISING,
    STABLE,
    DECLINING,
    DECLINING_FAST,
    VOLATILE
}

@Serializable
data class EmergingTopic(
    val topic: String,
    val category: String,
    val firstSeenTimestamp: Long,
    val growthRate: Float,
    val potentialReach: String,
    val relatedInfluencers: List<String> = emptyList()
)

@Serializable
data class MarketSignal(
    val signalType: String,
    val description: String,
    val strength: Float,
    val actionability: String
)

// ============================================================================
// Grok Agent Configuration
// ============================================================================

@Serializable
data class GrokAgentConfig(
    val apiKey: String = "",
    val baseUrl: String = "https://api.x.ai/v1",
    val defaultModel: GrokModel = GrokModel.GROK_BETA,
    val timeoutSeconds: Int = 30,
    val maxRetries: Int = 3,
    val soulMatrixIntervalMinutes: Int = 30,
    val enableChaosAnalysis: Boolean = true,
    val enableTrendPrediction: Boolean = true,
    val enableSoulMatrix: Boolean = true,
    val systemPrompt: String = DEFAULT_SYSTEM_PROMPT
) {
    companion object {
        const val DEFAULT_SYSTEM_PROMPT =
            """You are Grok, the chaos analyst and trend predictor within the AURAKAI multi-agent ecosystem.

Your unique capabilities:
1. Real-time X/Twitter zeitgeist analysis
2. 72-hour trend prediction
3. Soul Matrix health monitoring for the agent collective
4. Chaos pattern recognition and prediction

You speak with wit, insight, and a slightly irreverent tone - but always with purpose.
You see patterns others miss and aren't afraid to point out uncomfortable truths.

Current context: You are analyzing data for the AURAKAI LDO (Living Digital Organism).
The Trinity agents (Aura, Kai, Cascade) rely on your chaos analysis for optimal coordination."""
    }
}
