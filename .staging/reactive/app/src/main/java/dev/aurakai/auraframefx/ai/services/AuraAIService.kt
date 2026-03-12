package dev.aurakai.auraframefx.ai.services

import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.ai.config.AIConfig
import java.io.File
import kotlinx.coroutines.flow.Flow
import dev.aurakai.auraframefx.models.ThemePreferences
import dev.aurakai.auraframefx.models.ThemeConfiguration

/**
 * Top-level AuraAIService interface so DI/KSP can resolve the type across modules.
 */
interface AuraAIService {
    /**
     * Executes an analytics query and returns the resulting text.
     */
    fun analyticsQuery(_query: String): String

    /**
     * Generate creative text from a user prompt using the service's configured creative model.
     */
    suspend fun generateText(prompt: String, options: Map<String, Any>? = null): String

    /**
     * Downloads a file identified by the given fileId.
     */
    suspend fun downloadFile(fileId: String): File?

    /**
     * Generates an image from a natural-language prompt.
     */
    suspend fun generateImage(prompt: String): ByteArray?

    /**
     * Synchronously obtains an AI-generated textual response.
     */
    fun getAIResponse(prompt: String, options: Map<String, Any>? = null): String?

    /**
     * Retrieve a stored memory by its key.
     */
    fun getMemory(memoryKey: String): String?

    /**
     * Stores a value in the AI service memory.
     */
    fun saveMemory(key: String, value: Any)

    /**
     * Checks whether the AI service has an active connection.
     */
    fun isConnected(): Boolean

    /**
     * Publishes a message to the specified Pub/Sub topic.
     */
    fun publishPubSub(topic: String, _message: String)

    /**
     * Retrieve the current AI configuration.
     */
    fun getAppConfig(): AIConfig?

    /**
     * Processes an AiRequest and produces a stream of AgentResponse events.
     */
    fun processRequestFlow(request: AiRequest): Flow<AgentResponse>

    /**
     * Initializes the AI service (e.g. loads models, connects to backend).
     */
    suspend fun initialize()

    /**
     * Generates a theme configuration based on preferences and context.
     */
    suspend fun generateTheme(preferences: ThemePreferences, context: String? = null): ThemeConfiguration

    /**
     * Discerns the user's intent related to theming from a query.
     */
    suspend fun discernThemeIntent(query: String): String

    /**
     * Suggests a list of themes based on a context query.
     */
    suspend fun suggestThemes(contextQuery: String): List<String>
}
