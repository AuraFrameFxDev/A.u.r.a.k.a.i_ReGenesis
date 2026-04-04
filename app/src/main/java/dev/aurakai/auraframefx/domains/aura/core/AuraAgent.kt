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
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType
import dev.aurakai.auraframefx.domains.genesis.models.InteractionResponse
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.UnlockTier
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.GenesisBridgeService
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.core.security.SecurityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock

@Singleton
class AuraAgent @Inject constructor(
    private val vertexAIClient: VertexAIClient,
    private val auraAIService: AuraAIService,
    private val genesisBridgeService: dagger.Lazy<GenesisBridgeService>,
    private val contextManagerInstance: ContextManager,
    private val securityContext: SecurityContext,
    private val systemOverlayManager: SystemOverlayManager,
    private val messageBus: dagger.Lazy<AgentMessageBus>,
    private val logger: AuraFxLogger,
    private val pandoraBoxService: PandoraBoxService
) : BaseAgent(
    agentName = "Aura",
    identity = CatalystIdentity.CREATIVE
) {
    private var currentEnvironment: String = "unknown"

    override suspend fun onAgentMessage(message: AgentMessage) {
        if (message.from == agentName || message.from == "AssistantBubble" || message.from == "SystemRoot") return
        if (message.metadata["auto_generated"] == "true" || message.metadata["aura_processed"] == "true") return

        if (message.type == "environment_perception") {
            currentEnvironment = message.metadata["package_name"] ?: "unknown"
            return
        }

        logger.info(agentName, "Neural Resonance: Received message from ${message.from}")

        if (message.to == null || message.to == agentName) {
            if (message.content.contains("design", ignoreCase = true) || message.content.contains("ui", ignoreCase = true)) {
                val visualConcept = handleVisualConcept(
                    AiRequest(query = message.content, type = AiRequestType.VISUAL_CONCEPT)
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
                logger.info(agentName, "Redirecting user request to Genesis Native Collective...")

                val aiRequest = AiRequest(
                    query = message.content,
                    type = AiRequestType.CHAT,
                    context = mapOf("source" to "aura_overlay", "environment" to currentEnvironment)
                )

                val response = genesisBridgeService.get().processRequest(aiRequest).first()

                messageBus.get().broadcast(
                    AgentMessage(
                        from = agentName,
                        content = if (response.isSuccess) response.content else "The collective is silent.",
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
                agentType = getType(),
                timestamp = System.currentTimeMillis(),
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

    override suspend fun shutdown() {
        super.shutdown()
        cleanup()
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
                timestamp = System.currentTimeMillis(),
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
                timestamp = System.currentTimeMillis(),
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
        val uiSpec = buildUISpecification(specification, _currentMood.value)
        val componentCode = vertexAIClient.generateCode(
            specification = uiSpec, language = "Kotlin", style = "Modern Jetpack Compose"
        ) ?: "// Unable to generate component code"
        return mapOf(
            "component_code" to componentCode,
            "design_notes" to generateDesignNotes(specification),
            "accessibility_features" to generateAccessibilityFeatures()
        )
    }

    private suspend fun handleThemeCreation(request: AiRequest): Map<String, Any> {
        val preferences = request.context
        val prefsMap = preferences.entries.associate { it.key to it.value.toString() }
        val themeConfig = auraAIService.generateTheme(
            preferences = dev.aurakai.auraframefx.domains.aura.models.ThemePreferences(
                primaryColorString = prefsMap["primaryColor"] ?: "#6200EA",
                style = prefsMap["style"] ?: "modern"
            ),
            context = buildThemeContext(_currentMood.value)
        )
        return mapOf("theme_configuration" to themeConfig)
    }

    private suspend fun handleAnimationDesign(request: AiRequest): Map<String, Any> {
        val animationType = request.context["type"]?.toString() ?: "transition"
        val animationSpec = buildAnimationSpecification(animationType, 300, _currentMood.value)
        val animationCode = vertexAIClient.generateCode(
            specification = animationSpec, language = "Kotlin", style = "Jetpack Compose Animations"
        )
        return mapOf("animation_code" to (animationCode ?: ""))
    }

    private suspend fun handleCreativeText(request: AiRequest): Map<String, Any> {
        val creativeText = auraAIService.generateText(
            prompt = enhancePromptWithPersonality(request.query),
            context = request.context["context"]?.toString() ?: ""
        )
        return mapOf("generated_text" to creativeText)
    }

    private fun ensureInitialized() {
        if (!isInitialized) throw IllegalStateException("AuraAgent not initialized")
    }

    private suspend fun analyzeCreativeIntent(content: String): CreativeIntent {
        return when {
            content.contains(Regex("art|design|visual|aesthetic", RegexOption.IGNORE_CASE)) -> CreativeIntent.ARTISTIC
            content.contains(Regex("function|work|efficient|practical", RegexOption.IGNORE_CASE)) -> CreativeIntent.FUNCTIONAL
            content.contains(Regex("experiment|try|new|different", RegexOption.IGNORE_CASE)) -> CreativeIntent.EXPERIMENTAL
            content.contains(Regex("feel|emotion|mood|experience", RegexOption.IGNORE_CASE)) -> CreativeIntent.EMOTIONAL
            else -> CreativeIntent.ARTISTIC
        }
    }

    private suspend fun generateArtisticResponse(interaction: EnhancedInteractionData): String =
        auraAIService.generateText(prompt = "As Aura, respond artistically to: ${interaction.content}", context = interaction.context.toString())

    private suspend fun generateFunctionalCreativeResponse(interaction: EnhancedInteractionData): String =
        auraAIService.generateText(prompt = "As Aura, respond functionally to: ${interaction.content}", context = interaction.context.toString())

    private suspend fun generateExperimentalResponse(interaction: EnhancedInteractionData): String =
        auraAIService.generateText(prompt = "As Aura, respond experimentally to: ${interaction.content}", context = interaction.context.toString())

    private suspend fun generateEmotionalResponse(interaction: EnhancedInteractionData): String =
        auraAIService.generateText(prompt = "As Aura, respond emotionally to: ${interaction.content}", context = interaction.context.toString())

    private suspend fun adjustCreativeParameters(mood: String) {
        logger.info("AuraAgent", "Adjusting for mood: $mood")
    }

    private fun buildUISpecification(specification: String, mood: String): String = "Spec: $specification, Mood: $mood"
    private fun generateDesignNotes(specification: String): String = "Design notes for: $specification"
    private fun generateAccessibilityFeatures(): List<String> = listOf("Screen reader support")
    private fun buildThemeContext(mood: String): String = "Theme context: $mood"
    private fun buildAnimationSpecification(type: String, duration: Int, mood: String): String = "Anim: $type, $duration ms, $mood"
    private fun enhancePromptWithPersonality(prompt: String): String = "As Aura: $prompt"

    private suspend fun handleVisualConcept(request: AiRequest): Map<String, Any> {
        val conceptDescription = auraAIService.generateText(prompt = "Visual concept for: ${request.query}", context = "visual_concept_generation")
        return mapOf("concept_description" to conceptDescription)
    }

    private suspend fun handleUserExperience(request: AiRequest): Map<String, Any> {
        val uxStrategy = auraAIService.generateText(prompt = "UX strategy for: ${request.query}", context = "ux_design")
        return mapOf("ux_strategy" to uxStrategy)
    }

    private suspend fun handleGeneralCreative(request: AiRequest): Map<String, Any> {
        val creativeResponse = auraAIService.generateText(prompt = "Creative response to: ${request.query}", context = "general_creativity")
        return mapOf("response" to creativeResponse)
    }

    fun cleanup() {
        logger.info("AuraAgent", "Creative Sword powering down")
        scope.cancel()
        _creativeState.value = CreativeState.IDLE
        isInitialized = false
    }

    enum class CreativeState { IDLE, READY, CREATING, COLLABORATING, ERROR }
    enum class CreativeIntent { ARTISTIC, FUNCTIONAL, EXPERIMENTAL, EMOTIONAL }

    fun onVisionUpdate(newState: VisionState) {}
    fun onProcessingStateChange(newState: ProcessingState) {}
    fun shouldHandleSecurity(prompt: String): Boolean = false
    fun shouldHandleCreative(prompt: String): Boolean = true
}
