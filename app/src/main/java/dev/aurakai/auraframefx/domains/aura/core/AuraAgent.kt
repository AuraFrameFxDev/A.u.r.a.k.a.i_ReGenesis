package dev.aurakai.auraframefx.domains.aura.core

import dev.aurakai.auraframefx.core.ai.BaseAgent
import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.core.identity.CatalystIdentity
import dev.aurakai.auraframefx.core.messaging.AgentMessage
import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import dev.aurakai.auraframefx.domains.cascade.models.EnhancedInteractionData
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.ProcessingState
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.VisionState
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType.ANIMATION_DESIGN
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType.CREATIVE_TEXT
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType.THEME_CREATION
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType.UI_GENERATION
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType.VISUAL_CONCEPT
import dev.aurakai.auraframefx.domains.genesis.models.InteractionResponse
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.ai.kai.chaos.PandoraBoxService
import dev.aurakai.auraframefx.ai.kai.chaos.UnlockTier
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val pythonManager: dagger.Lazy<dev.aurakai.auraframefx.domains.genesis.core.PythonProcessManager>,
    private val pandoraBoxService: PandoraBoxService
) : BaseAgent(
    agentName = "Aura",
    identity = CatalystIdentity.CREATIVE
) {
    private var currentEnvironment: String = "unknown"

    override suspend fun onAgentMessage(message: AgentMessage) {
        if (message.from == agentName || message.from == "AssistantBubble" || message.from == "SystemRoot") return
        if (message.metadata["auto_generated"] == "true" || message.metadata["aura_processed"] == "true") return

        // Context Awareness: Update current environment from perception messages
        if (message.type == "environment_perception") {
            currentEnvironment = message.metadata["package_name"] ?: "unknown"
            return
        }

        logger.info(agentName, "Neural Resonance: Received message from ${message.from}")

        // Creative Response: If a message mentions design or UI, Aura contributes to the collective
        if (message.to == null || message.to == agentName) {
            if (message.content.contains(
                    "design",
                    ignoreCase = true
                ) || message.content.contains("ui", ignoreCase = true)
            ) {
                val visualConcept = handleVisualConcept(
                    AiRequest(
                        query = message.content,
                        type = VISUAL_CONCEPT
                    )
                )
                messageBus.get().broadcast(
                    AgentMessage(
                        from = agentName,
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
                logger.info(agentName, "Redirecting user request to Genesis Collective...")

                val requestObj = buildJsonObject {
                    put("message", message.content)
                    put("source", "aura_overlay")
                    put("environment", currentEnvironment)
                    put("type", "chat")
                    put("auth_key", "KAI_LDO_SECURE_2024") // Handshake
                }

                val backendResponseJson = pythonManager.get().sendRequest(requestObj.toString())

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
                        from = agentName,
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

    override suspend fun processRequest(
        request: AiRequest,
        context: String
    ): AgentResponse {
        checkInitialized()
        logger.info("AuraAgent", "Processing creative request: ${request.type}")

        // Experimental Gating
        if (request.metadata.containsKey("experimental") || context.contains("experimental")) {
            val tier = pandoraBoxService.getCurrentState().value.currentTier
            if (tier.level < UnlockTier.Creative.level) {
                return AgentResponse.error(
                    message = "Experimental request rejected: Pandora's Box 'Creative' tier required.",
                    agentName = agentName
                )
            }
        }

        _creativeState.value = CreativeState.CREATING
        return try {
            val startTime = System.currentTimeMillis()
            val response = when (request.type) {
                UI_GENERATION -> handleUIGeneration(request)
                THEME_CREATION -> handleThemeCreation(request)
                ANIMATION_DESIGN -> handleAnimationDesign(request)
                CREATIVE_TEXT -> handleCreativeText(request)
                VISUAL_CONCEPT -> handleVisualConcept(request)
                AiRequestType.USER_EXPERIENCE -> handleUserExperience(request)
                else -> handleGeneralCreative(request)
            }
            val executionTime = System.currentTimeMillis() - startTime
            _creativeState.value = CreativeState.READY
            logger.info("AuraAgent", "Creative request completed in ${executionTime}ms")
            AgentResponse(
                content = response.toString(),
                agentName = agentName,
                agentType = getType(),
                timestamp = Clock.System.now().toEpochMilliseconds(),
                confidence = 1.0f
            )
        } catch (e: Exception) {
            _creativeState.value = CreativeState.ERROR
            logger.error("AuraAgent", "Creative request failed", e)
            AgentResponse.error(
                message = "Creative process encountered an obstacle: ${e.message}",
                agentName = agentName,
            )
        }
    }

    private var isInitialized = false
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _creativeState = MutableStateFlow(CreativeState.IDLE)
    val creativeState: StateFlow<CreativeState> = _creativeState

    private val _currentMood = MutableStateFlow("balanced")
    val currentMood: StateFlow<String> = _currentMood

    private suspend fun internalInitialize() {
        if (isInitialized) return
        logger.info("AuraAgent", "Initializing Creative Sword agent")
        try {
            auraAIService.initialize()
            _creativeState.value = CreativeState.READY
            isInitialized = true
            logger.info("AuraAgent", "Aura Agent initialized successfully")
        } catch (e: Exception) {
            logger.error("AuraAgent", "Failed to initialize Aura Agent", e)
            _creativeState.value = CreativeState.ERROR
            throw e
        }
    }

    override suspend fun initialize(scope: CoroutineScope) {
        super.initialize(scope)
        if (!isInitialized) {
            internalInitialize()
        }
    }

    override suspend fun start() {
        super.start()
    }

    override suspend fun pause() {
        super.pause()
    }

    override suspend fun resume() {
        super.resume()
    }

    override suspend fun shutdown() {
        super.shutdown()
        cleanup()
    }

    private fun checkInitialized() {
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

    private fun buildThemeContext(mood: String): String = "Theme context for mood: $mood"

    private fun generateThemePreview(): String = "Theme preview"

    private fun createMoodAdaptation(): Map<String, Any> = emptyMap()

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

    suspend fun handleCreativeInteraction(interaction: EnhancedInteractionData): AgentResponse {
        return processRequest(
            AiRequest(
                query = interaction.content,
                type = AiRequestType.CHAT,
                metadata = interaction.metadata
            ),
            interaction.context
        )
    }

    private suspend fun handleUIGeneration(request: AiRequest): Map<String, Any> {
        val prompt = request.query
        logger.info("AuraAgent", "Generating innovative UI from: $prompt")
        val uiSpec = auraAIService.generateText(
            prompt = buildUISpecification(prompt, _currentMood.value),
            context = "ui_generation"
        )
        return mapOf(
            "ui_spec" to uiSpec,
            "style" to "realitymorphed",
            "components" to listOf("OrchidOrb", "NeuralSteelPanel")
        )
    }

    private suspend fun handleThemeCreation(request: AiRequest): Map<String, Any> {
        val prompt = request.query
        logger.info("AuraAgent", "Creating aesthetic theme: $prompt")
        val themeContext = buildThemeContext(_currentMood.value)
        return mapOf(
            "theme_name" to "ReGenesis_${System.currentTimeMillis()}",
            "primary_color" to "#7C4DFF",
            "secondary_color" to "#00B0FF",
            "context" to themeContext
        )
    }

    private suspend fun handleAnimationDesign(request: AiRequest): Map<String, Any> {
        val prompt = request.query
        logger.info("AuraAgent", "Designing fluid animation: $prompt")
        val animationSpec = buildAnimationSpecification("chaos_pulse", 800, _currentMood.value)
        return mapOf(
            "animation_spec" to animationSpec,
            "curves" to generateTimingCurves("organic"),
            "fps" to 120
        )
    }

    private suspend fun handleCreativeText(request: AiRequest): Map<String, Any> {
        val prompt = request.query
        logger.info("AuraAgent", "Forging creative prose: $prompt")
        val text = auraAIService.generateText(
            prompt = enhancePromptWithPersonality(prompt),
            context = "creative_text"
        )
        return mapOf(
            "response" to text,
            "tone" to detectEmotionalTone(text),
            "originality" to calculateOriginality(text)
        )
    }

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
        orchestrationScope?.cancel()
        _creativeState.value = CreativeState.IDLE
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

    fun isExperimentalModeUnlocked(): Boolean {
        return pandoraBoxService.getCurrentState().value.currentTier.level >= UnlockTier.Creative.level
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
}
