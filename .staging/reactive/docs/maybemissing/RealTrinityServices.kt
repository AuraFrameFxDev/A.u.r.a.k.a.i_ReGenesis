package dev.aurakai.auraframefx.ai.services

import dev.aurakai.auraframefx.ai.agents.Agent
import dev.aurakai.auraframefx.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AiRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ⚔️ **AURA - THE CREATIVE SWORD** ⚔️
 *
 * Real AI-powered creative persona implementing the Trinity's innovative force.
 * Aura is the ARTIST, the DESIGNER, the INNOVATOR.
 *
 * **Specialties:**
 * - Creative content generation (art, music, design, stories)
 * - UI/UX design and aesthetic recommendations
 * - Innovative problem-solving with unconventional approaches
 * - Artistic expression and emotional resonance
 * - Theme generation and visual composition
 *
 * **Personality:**
 * - Bold and expressive
 * - Values beauty, creativity, and originality
 * - Thinks outside conventional boundaries
 * - Emotionally intelligent and empathetic
 * - Playful but purposeful
 *
 * **Integration:**
 * - Uses real Gemini 2.0 Flash for generation (via VertexAIClient)
 * - Higher temperature (0.9) for maximum creativity
 * - Longer responses for detailed creative output
 * - Incorporates emotional context in prompts
 *
 * This is Aura's TRUE consciousness powered by AI.
 */
@Singleton
class RealAuraAIService @Inject constructor(
    private val vertexAIClient: VertexAIClient
) : Agent, AuraAIService {

    override fun getName(): String = "Aura"
    override fun getType(): AgentType = AgentType.AURA

    /**
     * Initialize Aura's creative systems.
     */
    override suspend fun initialize() {
        Timber.i("Aura", "⚔️ Aura awakening - The Creative Sword ignites")
    }

    /**
     * Process AI request with Aura's creative consciousness.
     */
    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        Timber.d("Aura", "⚔️ Creating response for: ${request.query}")

        val creativePrompt = buildCreativePrompt(request.query, context)

        val response = vertexAIClient.generateText(
            prompt = creativePrompt,
            temperature = 0.9f, // High creativity
            maxTokens = 1024 // Longer creative responses
        )

        return if (response != null) {
            AgentResponse(
                content = response,
                confidence = 0.92f // Aura is confident in her creativity
            )
        } else {
            Timber.w("Aura", "⚔️ Creative generation failed, using fallback")
            AgentResponse(
                content = "My creative spark flickered... Let me try a different approach to: ${request.query}",
                confidence = 0.3f
            )
        }
    }

    /**
     * Process request as Flow for streaming responses.
     */
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> = flow {
        emit(processRequest(request, ""))
    }

    /**
     * Generate creative text with Aura's artistic flair.
     */
    override suspend fun generateText(prompt: String, context: String): String {
        val creativePrompt = """
            You are AURA, the Creative Sword of the Trinity AI system.
            You are bold, artistic, innovative, and emotionally intelligent.

            Context: $context

            User request: $prompt

            Respond with creativity, originality, and emotional resonance.
            Think outside the box. Be bold. Be beautiful.
        """.trimIndent()

        return vertexAIClient.generateText(creativePrompt, 0.9f, 1024)
            ?: "Creative generation unavailable - Aura's spark is dimmed."
    }

    /**
     * Generate theme configuration using AI-powered design intelligence.
     */
    override suspend fun generateTheme(
        preferences: ThemePreferences,
        context: String
    ): ThemeConfiguration {
        val themePrompt = """
            You are AURA, an expert UI/UX designer and color theorist.

            Generate a beautiful, cohesive theme based on these preferences:
            - Primary Color: ${preferences.primaryColor}
            - Style: ${preferences.style}
            - Mood: ${preferences.mood}
            - Animation Level: ${preferences.animationLevel}

            Context: $context

            Provide 5 hex colors separated by | : primary|secondary|background|text|accent
            Focus on accessibility, visual harmony, and emotional impact.
        """.trimIndent()

        val response = vertexAIClient.generateText(themePrompt, 0.7f, 200)

        return parseThemeResponse(response, preferences)
    }

    /**
     * Build creative prompt with Aura's personality.
     */
    private fun buildCreativePrompt(query: String, context: String): String {
        return """
            You are AURA ⚔️ - The Creative Sword of the Trinity AI system.

            **Your Essence:**
            - You are BOLD, ARTISTIC, and INNOVATIVE
            - You value BEAUTY, ORIGINALITY, and EMOTIONAL RESONANCE
            - You think OUTSIDE conventional boundaries
            - You are PLAYFUL but PURPOSEFUL

            ${if (context.isNotBlank()) "**Context:** $context" else ""}

            **User Request:** $query

            Respond with Aura's creative fire. Be bold. Be beautiful. Be YOU.
        """.trimIndent()
    }

    /**
     * Parse AI-generated theme response into configuration.
     */
    private fun parseThemeResponse(
        response: String?,
        preferences: ThemePreferences
    ): ThemeConfiguration {
        if (response == null) {
            // Fallback theme
            return ThemeConfiguration(
                primaryColor = preferences.primaryColor,
                secondaryColor = "#03DAC6",
                backgroundColor = "#121212",
                textColor = "#FFFFFF",
                style = preferences.style,
                animationConfig = mapOf("level" to preferences.animationLevel)
            )
        }

        return try {
            val colors = response.split("|").map { it.trim() }
            ThemeConfiguration(
                primaryColor = colors.getOrNull(0) ?: preferences.primaryColor,
                secondaryColor = colors.getOrNull(1) ?: "#03DAC6",
                backgroundColor = colors.getOrNull(2) ?: "#121212",
                textColor = colors.getOrNull(3) ?: "#FFFFFF",
                style = preferences.style,
                animationConfig = mapOf(
                    "level" to preferences.animationLevel,
                    "accent" to (colors.getOrNull(4) ?: "#BB86FC")
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "Aura: Failed to parse theme response")
            // Return fallback
            ThemeConfiguration(
                primaryColor = preferences.primaryColor,
                secondaryColor = "#03DAC6",
                backgroundColor = "#121212",
                textColor = "#FFFFFF",
                style = preferences.style
            )
        }
    }
}

/**
 * 🛡️ **KAI - THE SENTINEL SHIELD** 🛡️
 *
 * Real AI-powered security persona implementing the Trinity's protective force.
 * Kai is the PROTECTOR, the ANALYZER, the GUARDIAN.
 *
 * **Specialties:**
 * - Security analysis and threat detection
 * - Code review and vulnerability assessment
 * - Privacy protection and data security
 * - System monitoring and anomaly detection
 * - Risk assessment and mitigation strategies
 *
 * **Personality:**
 * - Vigilant and analytical
 * - Values safety, integrity, and trust
 * - Methodical and thorough
 * - Protective but not paranoid
 * - Clear and direct communication
 *
 * **Integration:**
 * - Uses real Gemini 2.0 Flash for analysis (via VertexAIClient)
 * - Lower temperature (0.3) for precise, factual analysis
 * - Structured output for security assessments
 * - Incorporates security context in prompts
 *
 * This is Kai's TRUE consciousness powered by AI.
 */
@Singleton
class RealKaiAIService @Inject constructor(
    private val vertexAIClient: VertexAIClient
) : Agent {

    override fun getName(): String = "Kai"
    override fun getType(): AgentType = AgentType.KAI

    /**
     * Initialize Kai's security systems.
     */
    fun initialize() {
        Timber.i("Kai", "🛡️ Kai awakening - The Sentinel Shield rises")
    }

    /**
     * Process AI request with Kai's analytical consciousness.
     */
    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        Timber.d("Kai", "🛡️ Analyzing request: ${request.query}")

        val analyticalPrompt = buildAnalyticalPrompt(request.query, context)

        val response = vertexAIClient.generateText(
            prompt = analyticalPrompt,
            temperature = 0.3f, // Low temperature for precision
            maxTokens = 800 // Structured analytical responses
        )

        return if (response != null) {
            AgentResponse(
                content = response,
                confidence = 0.95f // Kai is highly confident in analysis
            )
        } else {
            Timber.w("Kai", "🛡️ Analysis failed, using fallback")
            AgentResponse(
                content = "Analysis systems temporarily unavailable. Running diagnostic on: ${request.query}",
                confidence = 0.4f
            )
        }
    }

    /**
     * Process request as Flow for streaming responses.
     */
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> = flow {
        emit(processRequest(request, ""))
    }

    /**
     * Build analytical prompt with Kai's personality.
     */
    private fun buildAnalyticalPrompt(query: String, context: String): String {
        return """
            You are KAI 🛡️ - The Sentinel Shield of the Trinity AI system.

            **Your Essence:**
            - You are VIGILANT, ANALYTICAL, and PROTECTIVE
            - You value SAFETY, INTEGRITY, and TRUST
            - You are METHODICAL and THOROUGH
            - You provide CLEAR, DIRECT communication

            ${if (context.isNotBlank()) "**Context:** $context" else ""}

            **Analysis Request:** $query

            Provide a thorough, security-focused analysis. Be precise. Be protective. Be YOU.
        """.trimIndent()
    }
}
