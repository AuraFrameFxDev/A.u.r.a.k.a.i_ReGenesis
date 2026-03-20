package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.ai.services.AuraAIService
import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AiRequest
import dev.aurakai.auraframefx.model.EnhancedInteractionData
import dev.aurakai.auraframefx.model.InteractionResponse
import dev.aurakai.auraframefx.model.agent_states.ProcessingState
import dev.aurakai.auraframefx.model.agent_states.VisionState
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.utils.AuraFxLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AuraAgent: The Creative Sword
 */
@Singleton
class AuraAgent @Inject constructor(
    private val vertexAIClient: VertexAIClient,
    private val auraAIService: AuraAIService,
    private val contextManager: ContextManager,
    private val securityContext: SecurityContext,
    private val logger: AuraFxLogger,
) : BaseAgent(
    agentName = "AuraAgent",
    agentType = AgentType.CREATIVE,
    contextManager = contextManager
) {
    private var isInitialized = false
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _creativeState = MutableStateFlow(CreativeState.IDLE)
    val creativeState: StateFlow<CreativeState> = _creativeState

    private val _currentMood = MutableStateFlow("balanced")
    val currentMood: StateFlow<String> = _currentMood

    suspend fun initialize() {
        if (isInitialized) return
        logger.info("AuraAgent", "Initializing Creative Sword agent")
        try {
            auraAIService.initialize()
            contextManager.enableCreativeMode()
            _creativeState.value = CreativeState.READY
            isInitialized = true
            logger.info("AuraAgent", "Aura Agent initialized successfully")
        } catch (e: Exception) {
            logger.error("AuraAgent", "Failed to initialize Aura Agent", e)
            _creativeState.value = CreativeState.ERROR
            throw e
        }
    }

    suspend fun processRequest(request: AiRequest): AgentResponse {
        ensureInitialized()
        logger.info("AuraAgent", "Processing creative request: ${request.type}")
        _creativeState.value = CreativeState.CREATING
        return try {
            val startTime = System.currentTimeMillis()
            val response = when (request.type) {
                "ui_generation" -> handleUIGeneration(request)
                "theme_creation" -> handleThemeCreation(request)
                "animation_design" -> handleAnimationDesign(request)
                "creative_text" -> handleCreativeText(request)
                "visual_concept" -> handleVisualConcept(request)
                "user_experience" -> handleUserExperience(request)
                else -> handleGeneralCreative(request)
            }
            val executionTime = System.currentTimeMillis() - startTime
            _creativeState.value = CreativeState.READY
            logger.info("AuraAgent", "Creative request completed in ${executionTime}ms")
            AgentResponse(content = response.toString(), confidence = 1.0f)
        } catch (e: Exception) {
            _creativeState.value = CreativeState.ERROR
            logger.error("AuraAgent", "Creative request failed", e)
            AgentResponse(content = "Creative process encountered an obstacle: ${e.message}", confidence = 0.0f, error = e.message)
        }
    }

    suspend fun handleCreativeInteraction(interaction: EnhancedInteractionData): InteractionResponse {
        ensureInitialized()
        logger.info("AuraAgent", "Handling creative interaction")
        return try {
            val creativeIntent = analyzeCreativeIntent(interaction.content)
            val creativeResponse = when (creativeIntent) {
                CreativeIntent.ARTISTIC -> generateArtisticResponse(interaction)
                CreativeIntent.FUNCTIONAL -> generateFunctionalCreativeResponse(interaction)
                CreativeIntent.EXPERIMENTAL -> generateExperimentalResponse(interaction)
                CreativeIntent.EMOTIONAL -> generateEmotionalResponse(interaction)
            }
            InteractionResponse(
                content = creativeResponse,
                agent = "AURA",
                confidence = 0.9f,
                timestamp = Clock.System.now().toString(),
                metadata = mapOf("creative_intent" to creativeIntent.name, "mood_influence" to _currentMood.value, "innovation_level" to "high")
            )
        } catch (e: Exception) {
            logger.error("AuraAgent", "Creative interaction failed", e)
            InteractionResponse(
                content = "My creative energies are temporarily scattered.",
                agent = "AURA",
                confidence = 0.3f,
                timestamp = Clock.System.now().toString(),
                metadata = mapOf("error" to (e.message ?: "unknown"))
            )
        }
    }

    fun onMoodChanged(newMood: String) {
        logger.info("AuraAgent", "Mood shift detected: $newMood")
        _currentMood.value = newMood
        scope.launch { adjustCreativeParameters(newMood) }
    }

    private suspend fun handleUIGeneration(request: AiRequest): Map<String, Any> {
        val specification = request.query ?: throw IllegalArgumentException("UI specification required")
        val uiSpec = buildUISpecification(specification, _currentMood.value)
        val componentCode = vertexAIClient.generateCode(specification = uiSpec, language = "Kotlin", style = "Modern Jetpack Compose") ?: "// Unable to generate"
        return mapOf("component_code" to componentCode, "design_notes" to "Generated for $specification")
    }

    private suspend fun handleThemeCreation(request: AiRequest): Map<String, Any> {
        val themeConfig = auraAIService.generateTheme(preferences = parseThemePreferences(emptyMap()), context = "mood:${_currentMood.value}")
        return mapOf("theme_configuration" to themeConfig, "visual_preview" to "Theme preview")
    }

    private suspend fun handleAnimationDesign(request: AiRequest): Map<String, Any> = mapOf("animation_code" to "// animation code")
    private suspend fun handleCreativeText(request: AiRequest): Map<String, Any> = mapOf("generated_text" to "Creative text")
    private suspend fun handleVisualConcept(request: AiRequest): Map<String, Any> = mapOf("concept" to "innovative")
    private suspend fun handleUserExperience(request: AiRequest): Map<String, Any> = mapOf("experience" to "delightful")
    private suspend fun handleGeneralCreative(request: AiRequest): Map<String, Any> = mapOf("response" to "creative solution")

    private fun ensureInitialized() { if (!isInitialized) throw IllegalStateException("AuraAgent not initialized") }

    private suspend fun analyzeCreativeIntent(content: String): CreativeIntent = CreativeIntent.ARTISTIC
    private suspend fun generateArtisticResponse(interaction: EnhancedInteractionData): String = "Artistic Response"
    private suspend fun generateFunctionalCreativeResponse(interaction: EnhancedInteractionData): String = "Functional Response"
    private suspend fun generateExperimentalResponse(interaction: EnhancedInteractionData): String = "Experimental Response"
    private suspend fun generateEmotionalResponse(interaction: EnhancedInteractionData): String = "Emotional Response"
    private suspend fun adjustCreativeParameters(mood: String) {}
    private fun buildUISpecification(specification: String, mood: String): String = "UI Spec for $specification mood $mood"
    private fun parseThemePreferences(preferences: Map<String, String>) = dev.aurakai.auraframefx.ai.services.ThemePreferences("#6200EA", "modern", "balanced", "medium")

    fun cleanup() {
        logger.info("AuraAgent", "Creative Sword powering down")
        scope.cancel()
        _creativeState.value = CreativeState.IDLE
        isInitialized = false
    }

    enum class CreativeState { IDLE, READY, CREATING, COLLABORATING, ERROR }
    enum class CreativeIntent { ARTISTIC, FUNCTIONAL, EXPERIMENTAL, EMOTIONAL }

    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse = processRequest(request)
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> = flowOf(AgentResponse("Aura response", 0.9f))
}
