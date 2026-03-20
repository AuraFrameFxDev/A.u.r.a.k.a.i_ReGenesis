package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class AgentRequest(
    val type: String,
    val payload: String? = null,
    val context: Map<String, Any>? = null
)

@Serializable
data class AgentResponse(
    val content: String,
    val confidence: Float = 1.0f,
    val agentName: String? = null,
    val error: String? = null,
    val isProcessing: Boolean = false,
    val metadata: Map<String, Any> = emptyMap()
) {
    companion object {
        fun success(content: String, agentName: String, metadata: Map<String, Any> = emptyMap()) =
            AgentResponse(content = content, agentName = agentName, confidence = 1.0f, metadata = metadata)
        
        fun error(message: String) =
            AgentResponse(content = "", error = message, confidence = 0.0f)
            
        fun processing(message: String) =
            AgentResponse(content = message, isProcessing = true)
    }
}

@Serializable
data class AgentMessage(
    val content: String,
    val sender: AgentType,
    val timestamp: Long,
    val confidence: Float
)

@Serializable
data class AiRequest(val query: String)

@Serializable
data class EnhancedInteractionData(
    val content: String,
    val source: String? = null,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class InteractionResponse(
    val content: String,
    val agent: String,
    val confidence: Float,
    val timestamp: String,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class SecurityAnalysis(
    val threatLevel: ThreatLevel,
    val description: String,
    val recommendedActions: List<String>,
    val confidence: Float
)

@Serializable
enum class ThreatLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

@Serializable
enum class ConversationMode {
    TURN_ORDER, FREE_FORM
}

@Serializable
data class HierarchyAgentConfig(
    val name: String,
    val priority: Int,
    val capabilities: Set<String>
)

@Serializable
data class FusionMemory(val id: String, val data: String)

interface ContextAwareAgent {
    fun setContext(context: Map<String, Any>)
}
