package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import dev.aurakai.auraframefx.domains.aura.ui.theme.manager.SystemThemeManager
import dev.aurakai.auraframefx.domains.aura.ui.theme.manager.ThemeColors
import dev.aurakai.auraframefx.domains.aura.ui.theme.model.OverlayTheme
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.overlay.OverlayUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SystemThemeViewModel - Bridges UI theme controls to system-wide theming
 *
 * Connects ThemeEngineScreen and ChromaCoreColorsScreen to:
 * - SystemOverlayManager (for Android overlay system)
 * - SystemThemeManager (for Xposed ThemeModule)
 * - OverlayUtils (for IOverlayManager operations)
 *
 * This is the missing link that enables ColorBlendr-style system-wide theming.
 */
@HiltViewModel
class SystemThemeViewModel @Inject constructor(
    private val systemOverlayManager: SystemOverlayManager,
    private val overlayUtils: OverlayUtils,
    private val logger: AuraFxLogger
) : ViewModel() {

    private val _isApplying = MutableStateFlow(false)
    val isApplying: StateFlow<Boolean> = _isApplying.asStateFlow()

    private val _lastAppliedTheme = MutableStateFlow<ThemeColors?>(null)
    val lastAppliedTheme: StateFlow<ThemeColors?> = _lastAppliedTheme.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Apply theme colors system-wide via Android overlay system
     * This triggers both IOverlayManager and Xposed ThemeModule
     */
    fun applySystemWideTheme(colors: ThemeColors) {
        viewModelScope.launch {
            _isApplying.value = true
            _errorMessage.value = null

            try {
                logger.info("SystemThemeViewModel", "Applying system-wide theme")

                // 1. Update SystemThemeManager (read by Xposed ThemeModule)
                SystemThemeManager.updateTheme(colors)

                // 2. Create OverlayTheme for SystemOverlayManager
                val overlayTheme = OverlayTheme(
                    name = "AuraKai Custom Theme",
                    colors = mapOf(
                        "primary" to colors.primary,
                        "primaryVariant" to colors.primaryVariant,
                        "secondary" to colors.secondary,
                        "secondaryVariant" to colors.secondaryVariant,
                        "accent" to colors.accent,
                        "background" to colors.background,
                        "onBackground" to colors.onBackground
                    )
                )

                // 3. Apply via SystemOverlayManager (uses OverlayUtils internally)
                systemOverlayManager.applyTheme(overlayTheme)

                _lastAppliedTheme.value = colors
                logger.info("SystemThemeViewModel", "System-wide theme applied successfully")

            } catch (e: Exception) {
                logger.error("SystemThemeViewModel", "Failed to apply theme: ${e.message}")
                _errorMessage.value = "Failed to apply theme: ${e.message}"
            } finally {
                _isApplying.value = false
            }
        }
    }

    /**
     * Apply individual color to system (for real-time preview)
     */
    fun applyColor(colorName: String, color: Color) {
        viewModelScope.launch {
            try {
                logger.info("SystemThemeViewModel", "Applying color: $colorName")

                // Update SystemThemeManager
                when (colorName) {
                    "primary" -> SystemThemeManager.primaryColor = color.value.toInt()
                    "accent" -> SystemThemeManager.accentColor = color.value.toInt()
                    "secondary" -> SystemThemeManager.secondaryColor = color.value.toInt()
                    "background" -> SystemThemeManager.backgroundColor = color.value.toInt()
                }

                // Trigger overlay update
                val result = overlayUtils.applyColorScheme(
                    primary = SystemThemeManager.primaryColor,
                    secondary = SystemThemeManager.secondaryColor,
                    tertiary = SystemThemeManager.primaryVariantColor,
                    error = 0xFFB00020.toInt(),
                    background = SystemThemeManager.backgroundColor,
                    surface = SystemThemeManager.backgroundColor
                )

                result.onFailure { error ->
                    _errorMessage.value = "Failed to apply color: ${error.message}"
                }

            } catch (e: Exception) {
                logger.error("SystemThemeViewModel", "Failed to apply color: ${e.message}")
                _errorMessage.value = "Failed to apply color: ${e.message}"
            }
        }
    }

    /**
     * Reset to default system colors
     */
    fun resetToDefaults() {
        viewModelScope.launch {
            _isApplying.value = true

            try {
                logger.info("SystemThemeViewModel", "Resetting to default colors")

                // Reset SystemThemeManager to Material defaults
                SystemThemeManager.primaryColor = "#6200EE".toColorInt()
                SystemThemeManager.accentColor = "#03DAC6".toColorInt()
                SystemThemeManager.backgroundColor = android.graphics.Color.WHITE

                // Reset via overlay system
                overlayUtils.resetToDefaultColors()

                _lastAppliedTheme.value = null
                logger.info("SystemThemeViewModel", "Reset to defaults complete")

            } catch (e: Exception) {
                logger.error("SystemThemeViewModel", "Failed to reset: ${e.message}")
                _errorMessage.value = "Failed to reset: ${e.message}"
            } finally {
                _isApplying.value = false
            }
        }
    }

    /**
     * Check if overlay system is available
     */
    fun isOverlaySystemAvailable(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    private fun String.toColorInt(): Int {
        return this.toColorInt()
    }
}

