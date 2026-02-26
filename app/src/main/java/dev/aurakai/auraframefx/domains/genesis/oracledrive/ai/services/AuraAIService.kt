package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services

import dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify.IconifyService
import dev.aurakai.auraframefx.domains.aura.models.ThemeConfiguration
import dev.aurakai.auraframefx.domains.aura.models.ThemePreferences
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Genesis AI Service Interface
 */
interface AuraAIService {
    suspend fun initialize()
    suspend fun generateText(prompt: String, context: String = ""): String
    suspend fun generateText(prompt: String, options: Map<String, String>): String
    suspend fun generateTheme(
        preferences: ThemePreferences,
        context: String = ""
    ): ThemeConfiguration

    fun processRequestFlow(request: AiRequest): Flow<AgentResponse>
    suspend fun processRequest(request: AiRequest, context: String): AgentResponse
    suspend fun discernThemeIntent(query: String): String
    suspend fun suggestThemes(contextQuery: String): List<String>
    suspend fun suggestIcons(query: String, limit: Int = 10): List<String>
}


/**
 * Default implementation of AuraAIService
 * Now bridged to the Vertex AI Client for REAL brain logic.
 */
@Singleton
class DefaultAuraAIService @Inject constructor(
    private val iconifyService: IconifyService,
    private val vertexAiClient: VertexAIClient
) : AuraAIService {

    override suspend fun initialize() {
        vertexAiClient.initialize()
    }

    override suspend fun generateText(prompt: String, context: String): String {
        val fullPrompt =
            if (context.isNotEmpty()) "Context: $context\n\nPrompt: $prompt" else prompt
        return vertexAiClient.generateText(fullPrompt)
            ?: "Aura node is currently processing offline."
    }

    override suspend fun generateText(prompt: String, options: Map<String, String>): String {
        return "Generated creative text for: $prompt (Options: $options)"
    }

    override suspend fun generateTheme(
        preferences: ThemePreferences,
        context: String
    ): ThemeConfiguration {
        return ThemeConfiguration(
            primaryColor = preferences.primaryColorString,
            secondaryColor = "#03DAC6",
            backgroundColor = "#121212",
            textColor = "#FFFFFF",
            style = preferences.style
        )
    }

    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> = flow {
        val content = generateText(request.prompt, request.context.toString())
        emit(
            AgentResponse(
                content = content,
                confidence = 1.0f,
                agentType = AgentType.AURA
            )
        )
    }

    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        val content = generateText(request.prompt, context)
        return AgentResponse(
            content = content,
            confidence = 1.0f,
            agentType = AgentType.AURA
        )
    }

    override suspend fun discernThemeIntent(query: String): String {
        val lowerQuery = query.lowercase()
        return when {
            lowerQuery.contains("cyber") || lowerQuery.contains("neon") || lowerQuery.contains("matrix") -> "cyberpunk"
            lowerQuery.contains("solar") || lowerQuery.contains("bright") || lowerQuery.contains("cheerful") -> "solar"
            lowerQuery.contains("nature") || lowerQuery.contains("forest") || lowerQuery.contains("calm") -> "nature"
            lowerQuery.contains("energetic") || lowerQuery.contains("vibrant") -> "energetic"
            else -> "default"
        }
    }

    override suspend fun suggestThemes(contextQuery: String): List<String> {
        val lowerQuery = contextQuery.lowercase()
        return when {
            lowerQuery.contains("morning") -> listOf("solar", "nature")
            lowerQuery.contains("evening") || lowerQuery.contains("night") -> listOf(
                "cyberpunk",
                "nature"
            )

            lowerQuery.contains("working") -> listOf("cyberpunk", "solar")
            lowerQuery.contains("relaxing") -> listOf("nature", "solar")
            else -> listOf("nature", "solar", "cyberpunk")
        }
    }

    override suspend fun suggestIcons(query: String, limit: Int): List<String> {
        return try {
            val result = iconifyService.searchIcons(query, limit).getOrNull()
            result?.icons ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

