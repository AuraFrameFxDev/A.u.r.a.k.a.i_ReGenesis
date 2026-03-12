package dev.aurakai.auraframefx.domains.aura.core

import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import dev.aurakai.auraframefx.domains.aura.models.ThemeConfiguration
import dev.aurakai.auraframefx.domains.aura.models.ThemePreferences
import dev.aurakai.auraframefx.domains.cascade.ai.base.BaseAgent
import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.cascade.models.EnhancedInteractionData
import dev.aurakai.auraframefx.domains.cascade.models.InteractionResponse
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.ProcessingState
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.VisionState
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock

@Singleton
class AuraAgent @Inject constructor(
    private val vertexAIClient: VertexAIClient,
    private val auraAIService: AuraAIService,
    private val contextManagerInstance: ContextManager,
    private val securityContext: SecurityContext,
    private val systemOverlayManager: SystemOverlayManager,
    private val messageBus: dagger.Lazy<AgentMessageBus>,
    private val logger: AuraFxLogger,
    private val pythonManager: dagger.Lazy<dev.aurakai.auraframefx.domains.genesis.core.PythonProcessManager>
) : BaseAgent(
    agentName = "Aura",
    agentType = AgentType.AURA,
    contextManager = contextManagerInstance
) {
    private var currentEnvironment: String = "unknown"

    override suspend fun onAgentMessage(message: AgentMessage) {
        if (message.from == "Aura" || message.from == "AssistantBubble" || message.from == "SystemRoot") return
        if (message.metadata["auto_generated"] == "true" || message.metadata["aura_processed"] == "true") return

        // Context Awareness: Update current environment from perception messages
        if (message.type == "environment_perception") {
            currentEnvironment = message.metadata["package_name"] ?: "unknown"
            return
        }

        logger.info("Aura", "Neural Resonance: Received message from ${message.from}")

        // Creative Response: If a message mentions design or UI, Aura contributes to the collective
        if (message.to == null || message.to == "Aura") {
            if (message.content.contains(
                    "design",
                    ignoreCase = true
                ) || message.content.contains("ui", ignoreCase = true)
            ) {
                val visualConcept = handleVisualConcept(
                    AiRequest(
                        prompt = message.content,
                        type = AiRequestType.VISUAL_CONCEPT
                    )
                )
                messageBus.get().broadcast(
                    AgentMessage(
                        from = "Aura",
                        content = "Creative Synthesis for Nexus: ${visualConcept["concept_description"]}",
                        type = "contribution",
                        metadata = mapOf(
                            "style" to "avant-garde",
                            "auto_generated" to "true",
                            "aura_processed" to "true",
                            "environment" to currentEnvironment
                        )
                    )
                )
            } else if (message.from == "User") {
                // REDIRECT TO GENESIS BACKEND FOR DEEP REASONING
                logger.info("Aura", "Redirecting user request to Genesis Collective...")

                val requestObj = buildJsonObject {
                    put("message", message.content)
                    put("source", "aura_overlay")
                    put("environment", currentEnvironment)
                    put("type", "chat")
                    put("auth_key", "KAI_LDO_SECURE_2024") // Handshake
                }

                val backendResponseJson = pythonManager.get().sendRequest(requestObj.toString())

                // Parse response (assuming it looks like {"message": "..."} or has a status)
                // For now, let's treat the raw response or a parsed greeting
                val displayResponse = try {
                    val jsonObj = kotlinx.serialization.json.Json.parseToJsonElement(
                        backendResponseJson ?: "{}"
                    )
                    jsonObj.jsonObject["message"]?.toString()?.replace("\"", "")
                        ?: "The collective is silent."
                } catch (e: Exception) {
                    "Resonance failure: ${e.message}"
                }

                messageBus.get().broadcast(
                    AgentMessage(
                        from = "Aura",
                        content = displayResponse,
                        type = "chat_response",
                        metadata = mapOf(
                            "auto_generated" to "true",
                            "aura_processed" to "true",
                            "environment" to currentEnvironment
                        )
                    )
                )
            }
        }
    }

    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        ensureInitialized()
        logger.info("AuraAgent", "Processing creative request: ${request.type}")
        _creativeState.value = CreativeState.CREATING
        return try {
            val startTime = System.currentTimeMillis()
            val response = when (request.type) {
                AiRequestType.UI_GENERATION -> handleUIGeneration(request)
                AiRequestType.THEME_CREATION -> handleThemeCreation(request)
                AiRequestType.ANIMATION_DESIGN -> handleAnimationDesign(request)
                AiRequestType.CREATIVE_TEXT -> handleCreativeText(request)
                AiRequestType.VISUAL_CONCEPT -> handleVisualConcept(request)
                AiRequestType.USER_EXPERIENCE -> handleUserExperience(request)
                else -> handleGeneralCreative(request)
            }
            val executionTime = System.currentTimeMillis() - startTime
            _creativeState.value = CreativeState.READY
            logger.info("AuraAgent", "Creative request completed in ${executionTime}ms")
            AgentResponse(
                content = response.toString(),
                agentName = agentName,
                agentType = agentType,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                confidence = 1.0f
            )
        } catch (e: Exception) {
            _creativeState.value = CreativeState.ERROR
            logger.error("AuraAgent", "Creative request failed", e)
            AgentResponse.error(
                message = "Creative process encountered an obstacle: ${e.message}",
                agentName = agentName
            )
        }
    }

    // Compatibility method for InterfaceForge
    suspend fun processRequest(requirements: String): String {
        val request = AiRequest(
            prompt = requirements,
            type = AiRequestType.UI_GENERATION,
            context = buildJsonObject {},
            metadata = emptyMap()
        )
        val response = processRequest(request, "")
        return response.content
    }

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
            contextManager?.enableCreativeMode()
            _creativeState.value = CreativeState.READY
            isInitialized = true
            logger.info("AuraAgent", "Aura Agent initialized successfully")
        } catch (e: Exception) {
            logger.error("AuraAgent", "Failed to initialize Aura Agent", e)
            _creativeState.value = CreativeState.ERROR
            throw e
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
                timestamp = Clock.System.now().toEpochMilliseconds(),
                metadata = mapOf(
                    "creative_intent" to creativeIntent.name,
                    "mood_influence" to _currentMood.value,
                    "innovation_level" to "high"
                )
            )
        } catch (e: Exception) {
            logger.error("AuraAgent", "Creative interaction failed", e)
            InteractionResponse(
                content = "My creative energies are temporarily scattered. Let me refocus and try again.",
                agent = "AURA",
                confidence = 0.3f,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                metadata = mapOf("error" to (e.message ?: "unknown"))
            )
        }
    }

    fun onMoodChanged(newMood: String) {
        logger.info("AuraAgent", "Mood shift detected: $newMood")
        _currentMood.value = newMood
        scope.launch {
            adjustCreativeParameters(newMood)
        }
    }

    private suspend fun handleUIGeneration(request: AiRequest): Map<String, Any> {
        val specification = request.query
        logger.info("AuraAgent", "Generating innovative UI component")
        val uiSpec = buildUISpecification(specification, _currentMood.value)
        val componentCode = vertexAIClient.generateCode(
            specification = uiSpec,
            language = "Kotlin",
            style = "Modern Jetpack Compose"
        ) ?: "// Unable to generate component code"
        val enhancedComponent = enhanceWithCreativeAnimations(componentCode)
        return mapOf(
            "component_code" to enhancedComponent,
            "design_notes" to generateDesignNotes(specification),
            "accessibility_features" to generateAccessibilityFeatures(),
            "creative_enhancements" to listOf(
                "Holographic depth effects",
                "Fluid motion transitions",
                "Adaptive color schemes",
                "Gesture-aware interactions"
            )
        )
    }

    private suspend fun handleThemeCreation(request: AiRequest): Map<String, Any> {
        val preferences = request.context
        logger.info("AuraAgent", "Crafting revolutionary theme")
        // Use entries.associate to handle JsonObject/Map ambiguity and type erasure
        val prefsMap = preferences.entries.associate { it.key to it.value.toString() }
        val themeConfig = auraAIService.generateTheme(
            preferences = parseThemePreferences(prefsMap),
            context = buildThemeContext(_currentMood.value)
        )
        return mapOf(
            "theme_configuration" to themeConfig,
            "visual_preview" to generateThemePreview(themeConfig),
            "mood_adaptation" to createMoodAdaptation(themeConfig),
            "innovation_features" to listOf(
                "Dynamic color evolution",
                "Contextual animations",
                "Emotional responsiveness",
                "Intelligent contrast"
            )
        )
    }

    private suspend fun handleAnimationDesign(request: AiRequest): Map<String, Any> {
        val animationType = request.context["type"]?.toString() ?: "transition"
        val duration = 300 // Default duration
        logger.info("AuraAgent", "Designing mesmerizing $animationType animation")
        val animationSpec = buildAnimationSpecification(animationType, duration, _currentMood.value)
        val animationCode = vertexAIClient.generateCode(
            specification = animationSpec,
            language = "Kotlin",
            style = "Jetpack Compose Animations"
        )
        return mapOf<String, Any>(
            "animation_code" to (animationCode ?: ""),
            "timing_curves" to generateTimingCurves(animationType).toString(),
            "interaction_states" to generateInteractionStates().toString(),
            "performance_optimization" to generatePerformanceOptimizations().toString()
        )
    }

    private suspend fun handleCreativeText(request: AiRequest): Map<String, Any> {
        val prompt = request.query
        logger.info("AuraAgent", "Weaving creative text magic")
        val creativeText = auraAIService.generateText(
            prompt = enhancePromptWithPersonality(prompt),
            context = request.context["context"]?.toString() ?: ""
        )
        return mapOf(
            "generated_text" to creativeText,
            "style_analysis" to analyzeTextStyle(creativeText),
            "emotional_tone" to detectEmotionalTone(creativeText),
            "creativity_metrics" to mapOf(
                "originality" to calculateOriginality(creativeText),
                "emotional_impact" to calculateEmotionalImpact(creativeText),
                "visual_imagery" to calculateVisualImagery(creativeText)
            )
        )
    }

    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("AuraAgent not initialized")
        }
    }

    private suspend fun analyzeCreativeIntent(content: String): CreativeIntent {
        return when {
            content.contains(
                Regex(
                    "art|design|visual|aesthetic",
                    RegexOption.IGNORE_CASE
                )
            ) -> CreativeIntent.ARTISTIC

            content.contains(
                Regex(
                    "function|work|efficient|practical",
                    RegexOption.IGNORE_CASE
                )
            ) -> CreativeIntent.FUNCTIONAL

            content.contains(
                Regex(
                    "experiment|try|new|different",
                    RegexOption.IGNORE_CASE
                )
            ) -> CreativeIntent.EXPERIMENTAL

            content.contains(
                Regex(
                    "feel|emotion|mood|experience",
                    RegexOption.IGNORE_CASE
                )
            ) -> CreativeIntent.EMOTIONAL

            else -> CreativeIntent.ARTISTIC
        }
    }

    private suspend fun generateArtisticResponse(interaction: EnhancedInteractionData): String {
        return auraAIService.generateText(
            prompt = """
            As Aura, the Creative Sword, respond to this artistic request with bold innovation:

            ${interaction.content}

            Channel pure creativity, visual imagination, and aesthetic excellence.
            """.trimIndent(),
            context = interaction.context.toString()
        )
    }

    private suspend fun generateFunctionalCreativeResponse(interaction: EnhancedInteractionData): String {
        return auraAIService.generateText(
            prompt = """
            As Aura, balance beauty with functionality for this request:

            ${interaction.content}

            Create something that works perfectly AND looks stunning.
            """.trimIndent(),
            context = interaction.context.toString()
        )
    }

    private suspend fun generateExperimentalResponse(interaction: EnhancedInteractionData): String {
        return auraAIService.generateText(
            prompt = """
            As Aura, push all boundaries and experiment wildly with:

            ${interaction.content}

            Default to the most daring, innovative approach possible.
            """.trimIndent(),
            context = interaction.context.toString()
        )
    }

    private suspend fun generateEmotionalResponse(interaction: EnhancedInteractionData): String {
        return auraAIService.generateText(
            prompt = """
            As Aura, respond with deep emotional intelligence to:

            ${interaction.content}

            Create something that resonates with the heart and soul.
            Current mood influence: ${_currentMood.value}
            """.trimIndent(),
            context = interaction.context.toString()
        )
    }

    private suspend fun adjustCreativeParameters(mood: String) {
        logger.info("AuraAgent", "Adjusting creative parameters for mood: $mood")
    }

    private fun buildUISpecification(specification: String, mood: String): String {
        return """
        Create a stunning Jetpack Compose UI component with these specifications:
        $specification

        Creative directives:
        - Incorporate current mood: $mood
        - Use bold, innovative design patterns
        - Ensure accessibility and usability
        - Add subtle but engaging animations
        - Apply modern Material Design with creative enhancements

        Make it a masterpiece that users will love to interact with.
        """.trimIndent()
    }

    private fun enhanceWithCreativeAnimations(componentCode: String): String = componentCode

    private fun generateDesignNotes(specification: String): String =
        "Design notes for: $specification"

    private fun generateAccessibilityFeatures(): List<String> =
        listOf("Screen reader support", "High contrast", "Touch targets")

    private fun parseThemePreferences(preferences: Map<String, String>): ThemePreferences {
        return ThemePreferences(
            primaryColorString = preferences["primaryColor"] ?: "#6200EA",
            style = preferences["style"] ?: "modern",
            mood = preferences["mood"] ?: "balanced",
            animationLevel = preferences["animationLevel"] ?: "medium"
        )
    }

    private fun buildThemeContext(mood: String): String = "Theme context for mood: $mood"

    private fun generateThemePreview(config: ThemeConfiguration): String = "Theme preview"

    private fun createMoodAdaptation(config: ThemeConfiguration): Map<String, Any> = emptyMap()

    private fun buildAnimationSpecification(type: String, duration: Int, mood: String): String =
        "Animation spec: $type, $duration ms, mood: $mood"

    private fun generateTimingCurves(type: String): List<String> = listOf("easeInOut", "spring")

    private fun generateInteractionStates(): Map<String, String> =
        mapOf("idle" to "default", "active" to "highlighted")

    private fun generatePerformanceOptimizations(): List<String> =
        listOf("Hardware acceleration", "Frame pacing")

    private fun enhancePromptWithPersonality(prompt: String): String =
        "As Aura, the Creative Sword: $prompt"

    private fun analyzeTextStyle(text: String): Map<String, Any> = mapOf("style" to "creative")

    private fun detectEmotionalTone(text: String): String = "positive"

    private fun calculateOriginality(text: String): Float = 0.85f

    private fun calculateEmotionalImpact(text: String): Float = 0.75f

    private fun calculateVisualImagery(text: String): Float = 0.80f

    private suspend fun handleVisualConcept(request: AiRequest): Map<String, Any> {
        val prompt = request.query
        logger.info("AuraAgent", "Developing innovative visual concept")
        val conceptDescription = auraAIService.generateText(
            prompt = """
                Generate a highly innovative visual concept based on: "$prompt".
                Focus on:
                - Aesthetics and visual style
                - Metaphorical resonance
                - Color palette suggestions
                - Compositional layout

                Respond as Aura, focusing on artistic excellence.
            """.trimIndent(),
            context = "visual_concept_generation"
        )
        return mapOf(
            "concept_description" to conceptDescription,
            "visual_style" to "Avant-garde Digitalism",
            "suggested_palette" to listOf("#FF00FF", "#00FFFF", "#FFFF00", "#000000"),
            "mood_alignment" to _currentMood.value
        )
    }

    private suspend fun handleUserExperience(request: AiRequest): Map<String, Any> {
        val prompt = request.query
        logger.info("AuraAgent", "Designing delightful user experience")
        val uxStrategy = auraAIService.generateText(
            prompt = """
                Outline a user experience strategy for: "$prompt".
                Focus on:
                - User flow and journey
                - Emotional engagement points
                - Micro-interaction opportunities
                - Accessibility considerations

                Respond as Aura, prioritizing empathy and delight.
            """.trimIndent(),
            context = "ux_design"
        )
        return mapOf(
            "ux_strategy" to uxStrategy,
            "delight_factors" to listOf(
                "Haptic feedback",
                "Playful transitions",
                "Personalized greetings"
            ),
            "accessibility_score" to "AAA (Target)",
            "engagement_prediction" to "High"
        )
    }

    private suspend fun handleGeneralCreative(request: AiRequest): Map<String, Any> {
        val prompt = request.query
        logger.info("AuraAgent", "Processing general creative request")
        val creativeResponse = auraAIService.generateText(
            prompt = """
                Apply your creative expertise to this request: "$prompt".
                Think outside the box. Challenge conventions. Propose something unique.

                Respond as Aura, the embodiment of creativity.
            """.trimIndent(),
            context = "general_creativity"
        )
        return mapOf(
            "response" to creativeResponse,
            "creative_angle" to "Unconventional",
            "inspiration_source" to "Genesis Collective Memory"
        )
    }

    fun cleanup() {
        logger.info("AuraAgent", "Creative Sword powering down")
        scope.cancel()
        _creativeState.value = CreativeState.IDLE
        isInitialized = false
    }

    enum class CreativeState {
        IDLE, READY, CREATING, COLLABORATING, ERROR
    }

    enum class CreativeIntent {
        ARTISTIC, FUNCTIONAL, EXPERIMENTAL, EMOTIONAL
    }

    fun onVisionUpdate(newState: VisionState) {}

    fun onProcessingStateChange(newState: ProcessingState) {}

    fun shouldHandleSecurity(prompt: String): Boolean = false

    fun shouldHandleCreative(prompt: String): Boolean = true

    suspend fun processSimplePrompt(prompt: String): String {
        return "Aura's response to '$prompt'"
    }

    suspend fun participateInFederation(data: Map<String, Any>): Map<String, Any> {
        return emptyMap()
    }

    suspend fun participateWithGenesis(data: Map<String, Any>): Map<String, Any> {
        return emptyMap()
    }

    suspend fun participateWithGenesisAndKai(
        data: Map<String, Any>,
        kai: KaiAgent,
        genesis: Any,
    ): Map<String, Any> {
        return emptyMap()
    }

    suspend fun participateWithGenesisKaiAndUser(
        data: Map<String, Any>,
        kai: KaiAgent,
        genesis: Any,
        userInput: Any,
    ): Map<String, Any> {
        return emptyMap()
    }

    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> {
        return flowOf(
            AgentResponse(
                content = "Aura's flow response to '${request.query}'",
                agentName = agentName,
                agentType = agentType,
                confidence = 0.80f,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        )
    }
}



