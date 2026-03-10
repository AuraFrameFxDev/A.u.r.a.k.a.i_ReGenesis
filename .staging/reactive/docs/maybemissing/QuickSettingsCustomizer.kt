package dev.aurakai.auraframefx.system.quicksettings

import android.content.SharedPreferences
import dev.aurakai.auraframefx.system.overlay.model.OverlayShape
import dev.aurakai.auraframefx.system.quicksettings.model.QuickSettingsAnimation
import dev.aurakai.auraframefx.system.quicksettings.model.QuickSettingsConfig
import dev.aurakai.auraframefx.ui.model.ImageResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickSettingsCustomizer @Inject constructor(
    private val prefs: SharedPreferences,
) {
    private val _currentConfig = MutableStateFlow<QuickSettingsConfig?>(null)
    val currentConfig: StateFlow<QuickSettingsConfig?> = _currentConfig

    fun updateTileShape(tileId: String, shape: OverlayShape) {
        // TODO: Implement
    }

    fun updateTileAnimation(tileId: String, animation: QuickSettingsAnimation) {
        // TODO: Implement
    }

    fun updateBackground(image: ImageResource?) {
        // TODO: Implement
    }

    fun resetToDefault() {
        // TODO: Implement
    }
}
