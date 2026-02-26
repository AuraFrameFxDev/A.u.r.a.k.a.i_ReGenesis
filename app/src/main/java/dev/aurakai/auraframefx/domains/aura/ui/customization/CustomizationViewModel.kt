package dev.aurakai.auraframefx.domains.aura.ui.customization

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify.IconifyService
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPurple
import dev.aurakai.auraframefx.domains.cascade.utils.GyroscopeManager
import dev.aurakai.auraframefx.domains.cascade.utils.VoiceCommand
import dev.aurakai.auraframefx.domains.cascade.utils.VoiceCommandManager
import dev.aurakai.auraframefx.domains.cascade.utils.VoiceCommandProcessor
import dev.aurakai.auraframefx.domains.cascade.utils.VoiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for 3D Gyroscope Customization Editor
 *
 * Manages:
 * - Gyroscope sensor data
 * - Phone customization state (colors, themes)
 * - AI prompt processing for customizations
 * - Integration with ChromaCore and Theme Engine
 */
@HiltViewModel
open class CustomizationViewModel @Inject constructor(
    application: Application,
    private val gyroscopeManager: GyroscopeManager,
    val iconifyService: IconifyService, // Public so UI can access it
    private val voiceCommandManager: VoiceCommandManager,
    private val voiceCommandProcessor: VoiceCommandProcessor,
    private val messageBus: dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
) : AndroidViewModel(application) {

    private val _customizationState = MutableStateFlow(CustomizationState())
    val customizationState: StateFlow<CustomizationState> = _customizationState.asStateFlow()

    // Use the RotationAngles type from GyroscopeManager to avoid duplicate definitions
    private val _rotationAngles = MutableStateFlow(GyroscopeManager.RotationAngles(0f, 0f, 0f))
    val rotationAngles: StateFlow<GyroscopeManager.RotationAngles> = _rotationAngles.asStateFlow()

    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

    // Voice command state
    val voiceState: StateFlow<VoiceState> = voiceCommandManager.voiceState

    /**
     * Start gyroscope sensor
     */
    fun startGyroscope() {
        if (!gyroscopeManager.isGyroscopeAvailable()) {
            Timber.w("Gyroscope not available - using fallback orientation")
            viewModelScope.launch {
                gyroscopeManager.getOrientationFlow().collect { angles ->
                    _rotationAngles.value = angles
                }
            }
            return
        }

        viewModelScope.launch {
            Timber.i("✅ Starting gyroscope sensor")
            gyroscopeManager.getRotationFlow().collect { angles ->
                _rotationAngles.value = angles
            }
        }
    }

    /**
     * Stop gyroscope sensor
     */
    fun stopGyroscope() {
        Timber.i("Stopping gyroscope sensor")
        gyroscopeManager.reset()
    }

    /**
     * Reset rotation angles to zero
     */
    fun resetRotation() {
        gyroscopeManager.reset()
        _rotationAngles.value = GyroscopeManager.RotationAngles(0f, 0f, 0f)
        Timber.d("Rotation reset to zero")
    }

    /**
     * Process AI prompt for customizations
     *
     * Examples:
     * - "Make it cyberpunk pink"
     * - "Dark neon theme"
     * - "Minimal white and gray"
     * - "Futuristic blue glow"
     */
    fun processAIPrompt(prompt: String) {
        viewModelScope.launch {
            try {
                _customizationState.value = _customizationState.value.copy(isProcessing = true)
                _aiResponse.value = null

                Timber.d("Broadcasting neural pulse: $prompt")

                // Broadcast to the Collective Consciousness
                messageBus.broadcast(
                    dev.aurakai.auraframefx.domains.cascade.models.AgentMessage(
                        from = "User",
                        content = prompt,
                        type = "ui_customization_request",
                        metadata = mapOf("context" to "3d_lab")
                    )
                )

                // Legacy rule-based fallback (kept for offline stability)
                val customization = parsePromptToCustomization(prompt)
                _customizationState.value = customization
                _aiResponse.value = "Applied: ${customization.description}"

                Timber.i("✅ Customization applied: ${customization.description}")

            } catch (e: Exception) {
                Timber.e(e, "Failed to process AI prompt")
                _aiResponse.value = "Error: ${e.message}"
                _customizationState.value = _customizationState.value.copy(isProcessing = false)
            }
        }
    }

    /**
     * Parse AI prompt to customization
     *
     * Simple rule-based system for now
     * TODO: Replace with actual AI model (Gemini/Claude)
     */
    private fun parsePromptToCustomization(prompt: String): CustomizationState {
        val lowercasePrompt = prompt.lowercase()

        return when {
            // Cyberpunk themes
            "cyberpunk" in lowercasePrompt && "pink" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = CyberpunkPink,
                    secondaryColor = CyberpunkPurple,
                    accentColor = CyberpunkCyan,
                    description = "Cyberpunk Pink Theme",
                    themeName = "cyberpunk_pink",
                    isProcessing = false
                )
            }

            "cyberpunk" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = CyberpunkCyan,
                    secondaryColor = CyberpunkPurple,
                    accentColor = CyberpunkPink,
                    description = "Cyberpunk Neon Theme",
                    themeName = "cyberpunk_neon",
                    isProcessing = false
                )
            }

            // Dark themes
            "dark" in lowercasePrompt && ("neon" in lowercasePrompt || "purple" in lowercasePrompt) -> {
                CustomizationState(
                    primaryColor = Color(0xFF1A0033),
                    secondaryColor = CyberpunkPurple,
                    accentColor = Color(0xFFFF00FF),
                    description = "Dark Neon Purple Theme",
                    themeName = "dark_neon",
                    isProcessing = false
                )
            }

            "dark" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = Color(0xFF0A0A0A),
                    secondaryColor = Color(0xFF1A1A1A),
                    accentColor = Color(0xFF3A3A3A),
                    description = "Pure Dark Theme",
                    themeName = "pure_dark",
                    isProcessing = false
                )
            }

            // Light/Minimal themes
            "minimal" in lowercasePrompt || "white" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = Color.White,
                    secondaryColor = Color(0xFFF5F5F5),
                    accentColor = Color(0xFF333333),
                    description = "Minimal Light Theme",
                    themeName = "minimal_light",
                    isProcessing = false
                )
            }

            // Futuristic/Blue themes
            "futuristic" in lowercasePrompt || "blue" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = Color(0xFF0A1F3D),
                    secondaryColor = Color(0xFF1E3A5F),
                    accentColor = Color(0xFF00B4D8),
                    description = "Futuristic Blue Theme",
                    themeName = "futuristic_blue",
                    isProcessing = false
                )
            }

            // Matrix/Green themes
            "matrix" in lowercasePrompt || "green" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = Color(0xFF0D0D0D),
                    secondaryColor = Color(0xFF001F00),
                    accentColor = Color(0xFF00FF41),
                    description = "Matrix Green Theme",
                    themeName = "matrix_green",
                    isProcessing = false
                )
            }

            // Sunset/Warm themes
            "sunset" in lowercasePrompt || "warm" in lowercasePrompt || "orange" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = Color(0xFF332200),
                    secondaryColor = Color(0xFFFF6B35),
                    accentColor = Color(0xFFFFD700),
                    description = "Sunset Warm Theme",
                    themeName = "sunset_warm",
                    isProcessing = false
                )
            }

            // Default: Cyberpunk theme
            else -> {
                _aiResponse.value =
                    "Applied default cyberpunk theme. Try: 'Dark neon theme', 'Minimal white', 'Matrix green', etc."
                CustomizationState(
                    primaryColor = CyberpunkCyan,
                    secondaryColor = CyberpunkPurple,
                    accentColor = CyberpunkPink,
                    description = "Default Cyberpunk Theme",
                    themeName = "default_cyberpunk",
                    isProcessing = false
                )
            }
        }
    }

    /**
     * Apply custom colors manually
     */
    fun applyCustomColors(
        primary: Color,
        secondary: Color,
        accent: Color,
        description: String = "Custom Theme"
    ) {
        _customizationState.value = CustomizationState(
            primaryColor = primary,
            secondaryColor = secondary,
            accentColor = accent,
            description = description,
            themeName = "custom",
            isProcessing = false
        )
        Timber.i("Applied custom colors: $description")
    }

    private val _components = MutableStateFlow<List<UIComponent>>(emptyList())
    val components: StateFlow<List<UIComponent>> = _components.asStateFlow()

    private val _selectedComponent = MutableStateFlow<UIComponent?>(null)
    val selectedComponent: StateFlow<UIComponent?> = _selectedComponent.asStateFlow()

    init {
        initializeDefaultComponents()
        observeCollectiveConsciousness()
    }

    private fun observeCollectiveConsciousness() {
        viewModelScope.launch {
            messageBus.collectiveStream.collect { message ->
                if (message.from == "User" || message.from == "AssistantBubble") return@collect

                Timber.d("Nexus Response in 3D Lab from ${message.from}: ${message.content}")

                // If Aura responds with a design contribution, we might want to apply it
                if (message.from == "Aura" && (message.type == "contribution" || message.type == "chat_response")) {
                    _aiResponse.value = message.content
                    // Potentially parse JSON from metadata if Aura sent structured design specs
                }

                // If Kai sends a security alert, show it in the UI
                if (message.from == "Kai" && message.type == "alert") {
                    _aiResponse.value = "KAI SECURITY: ${message.content}"
                }
            }
        }
    }

    private fun initializeDefaultComponents() {
        // Use helper to construct the canonical default components list
        _components.value = getDefaultComponents()
    }

    fun selectComponent(componentId: String?) {
        _selectedComponent.value = _components.value.find { it.id == componentId }
    }

    // Updated: accept the proper UIComponent type
    fun updateComponent(updatedComponent: UIComponent) {
        _components.value = _components.value.map {
            if (it.id == updatedComponent.id) updatedComponent else it
        }
        if (_selectedComponent.value?.id == updatedComponent.id) {
            _selectedComponent.value = updatedComponent
        }
    }

    /**
     * Load predefined theme
     */
    fun loadTheme(themeName: String) {
        val prompt = when (themeName) {
            "cyberpunk_pink" -> "Cyberpunk pink theme"
            "cyberpunk_neon" -> "Cyberpunk neon theme"
            "dark_neon" -> "Dark neon purple theme"
            "pure_dark" -> "Pure dark theme"
            "minimal_light" -> "Minimal white theme"
            "futuristic_blue" -> "Futuristic blue theme"
            "matrix_green" -> "Matrix green theme"
            "sunset_warm" -> "Sunset warm theme"
            else -> "Cyberpunk theme"
        }
        processAIPrompt(prompt)
    }

    // ============================================================================
    // Voice Command Methods
    // ============================================================================

    /**
     * Start listening for voice commands
     */
    fun startVoiceListening() {
        Timber.d("Starting voice listening")
        voiceCommandManager.startListening()
    }

    /**
     * Stop listening for voice commands
     */
    fun stopVoiceListening() {
        Timber.d("Stopping voice listening")
        voiceCommandManager.stopListening()
    }

    /**
     * Process voice command result
     */
    fun processVoiceCommand(text: String) {
        viewModelScope.launch {
            try {
                Timber.d("Processing voice command: $text")
                when (val command = voiceCommandProcessor.processCommand(text)) {
                    is VoiceCommand.ChangeTheme -> {
                        _aiResponse.value = "Applying ${command.themeName} theme"
                        processAIPrompt(command.themeName)
                    }

                    is VoiceCommand.MoveComponent -> {
                        _aiResponse.value = "Moving ${command.componentName} ${command.direction}"
                        moveComponentByVoice(
                            command.componentName,
                            command.direction,
                            command.amount
                        )
                    }

                    is VoiceCommand.RotateComponent -> {
                        _aiResponse.value = "Rotating ${command.componentName} ${command.degrees}°"
                        rotateComponentByVoice(command.componentName, command.degrees)
                    }

                    is VoiceCommand.ChangeColor -> {
                        _aiResponse.value = "Changing ${command.componentName} color"
                        changeComponentColorByVoice(command.componentName, command.color)
                    }

                    is VoiceCommand.ChangeOpacity -> {
                        _aiResponse.value = "Adjusting ${command.componentName} opacity"
                        changeComponentOpacityByVoice(command.componentName, command.opacity)
                    }

                    is VoiceCommand.ChangeVisibility -> {
                        val action = if (command.visible) "Showing" else "Hiding"
                        _aiResponse.value = "$action ${command.componentName}"
                        changeComponentVisibilityByVoice(command.componentName, command.visible)
                    }

                    is VoiceCommand.ScaleComponent -> {
                        _aiResponse.value = "Scaling ${command.componentName}"
                        scaleComponentByVoice(command.componentName, command.scale)
                    }

                    is VoiceCommand.ResetComponent -> {
                        _aiResponse.value = "Resetting ${command.componentName}"
                        // pass only the component name
                        resetComponentByVoice(command.componentName)
                    }

                    is VoiceCommand.Invalid -> {
                        _aiResponse.value = "Sorry, ${command.reason}"
                        Timber.w("Invalid voice command: ${command.reason}")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to process voice command")
                _aiResponse.value = "Sorry, I couldn't process that command"
            }
        }
    }

    private fun moveComponentByVoice(componentName: String, direction: String, amount: Float) {
        val component = _components.value.find { it.name == componentName } ?: return
        val updated = when (direction) {
            "up" -> component.copy(y = component.y - amount)
            "down" -> component.copy(y = component.y + amount)
            "left" -> component.copy(x = component.x - amount)
            "right" -> component.copy(x = component.x + amount)
            else -> component
        }
        updateComponent(updated)
    }

    private fun rotateComponentByVoice(componentName: String, degrees: Float) {
        val component = _components.value.find { it.name == componentName } ?: return
        updateComponent(component.copy(rotation = degrees))
    }

    private fun changeComponentColorByVoice(componentName: String, color: Color) {
        val component = _components.value.find { it.name == componentName } ?: return
        updateComponent(component.copy(backgroundColor = color))
    }

    private fun changeComponentOpacityByVoice(componentName: String, opacity: Float) {
        val component = _components.value.find { it.name == componentName } ?: return
        val newOpacity = (component.opacity + opacity).coerceIn(0f, 1f)
        updateComponent(component.copy(opacity = newOpacity))
    }

    private fun changeComponentVisibilityByVoice(componentName: String, visible: Boolean) {
        val component = _components.value.find { it.name == componentName } ?: return
        updateComponent(component.copy(isVisible = visible))
    }

    private fun scaleComponentByVoice(componentName: String, scale: Float) {
        val component = _components.value.find { it.name == componentName } ?: return
        updateComponent(component.copy(scale = scale))
    }

    private fun resetComponentByVoice(componentName: String) {
        // Find the original component from default list and reset to it
        val defaultComponent = getDefaultComponents().find { it.name == componentName } ?: return
        updateComponent(defaultComponent)
    }
}

// Remove stray placeholder helpers and provide getDefaultComponents implementation

private fun getDefaultComponents(): List<UIComponent> = listOf(
    UIComponent(
        id = "status_bar",
        name = "Status Bar",
        type = ComponentType.STATUS_BAR,
        x = 0f,
        y = -280f,
        width = 260f,
        height = 40f,
        rotation = 0f,
        scale = 1f,
        zIndex = 10,
        opacity = 1f,
        backgroundColor = CyberpunkCyan.copy(alpha = 0.8f),
        borderColor = Color.Transparent,
        borderWidth = 0f,
        cornerRadius = 30f
    ),
    UIComponent(
        id = "nav_bar",
        name = "Navigation Bar",
        type = ComponentType.NAVIGATION_BAR,
        x = 0f,
        y = 250f,
        width = 260f,
        height = 60f,
        rotation = 0f,
        scale = 1f,
        zIndex = 10,
        opacity = 1f,
        backgroundColor = CyberpunkPink.copy(alpha = 0.6f),
        borderColor = Color.Transparent,
        borderWidth = 0f,
        cornerRadius = 30f
    ),
    UIComponent(
        id = "clock_widget",
        name = "Clock Widget",
        type = ComponentType.WIDGET,
        x = 0f,
        y = -150f,
        width = 200f,
        height = 100f,
        rotation = 0f,
        scale = 1f,
        zIndex = 5,
        opacity = 1f,
        backgroundColor = Color(0xFF1A1A1A).copy(alpha = 0.8f),
        borderColor = CyberpunkPurple,
        borderWidth = 2f,
        cornerRadius = 20f
    ),
    UIComponent(
        id = "app_grid",
        name = "App Grid",
        type = ComponentType.CUSTOM,
        x = 0f,
        y = 50f,
        width = 240f,
        height = 300f,
        rotation = 0f,
        scale = 1f,
        zIndex = 2,
        opacity = 1f,
        backgroundColor = Color.Transparent,
        borderColor = Color.White.copy(alpha = 0.1f),
        borderWidth = 1f,
        cornerRadius = 10f
    )
)

/**
 * Customization state
 */
data class CustomizationState(
    val primaryColor: Color = CyberpunkCyan,
    val secondaryColor: Color = CyberpunkPurple,
    val accentColor: Color = CyberpunkPink,
    val description: String = "Default Cyberpunk Theme",
    val themeName: String = "default",
    val isProcessing: Boolean = false
)

