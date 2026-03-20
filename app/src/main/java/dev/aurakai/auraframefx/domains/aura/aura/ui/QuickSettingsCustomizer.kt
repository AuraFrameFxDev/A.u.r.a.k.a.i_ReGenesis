package dev.aurakai.auraframefx.domains.aura.aura.ui

import android.content.SharedPreferences
import dev.aurakai.auraframefx.domains.aura.ui.OverlayShape
import dev.aurakai.auraframefx.domains.aura.ui.QuickSettingsAnimation
import dev.aurakai.auraframefx.domains.aura.ui.QuickSettingsConfig
import dev.aurakai.auraframefx.domains.aura.ui.QuickSettingsTileConfig
import dev.aurakai.auraframefx.domains.aura.ui.ImageResource
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

    init {
        // Load saved configuration on initialization
        loadConfiguration()
    }

    /**
     * Updates the visual shape of a specific Quick Settings tile.
     *
     * @param tileId The unique identifier for the tile to modify
     * @param shape The new overlay shape to apply
     */
    fun updateTileShape(tileId: String, shape: OverlayShape) {
        prefs.edit()
            .putString("tile_shape_$tileId", shape.name)
            .apply()

        val currentTiles = _currentConfig.value?.tiles.orEmpty().toMutableList()
        val tileIndex = currentTiles.indexOfFirst { it.id == tileId }
        if (tileIndex >= 0) {
            currentTiles[tileIndex] = currentTiles[tileIndex].copy(shape = shape)
        } else {
            currentTiles.add(
                QuickSettingsTileConfig(
                    id = tileId,
                    label = tileId,
                    shape = shape,
                    animation = QuickSettingsAnimation.FADE
                )
            )
        }

        val config = _currentConfig.value?.copy(tiles = currentTiles)
            ?: QuickSettingsConfig(tiles = currentTiles)

        _currentConfig.value = config
    }

    /**
     * Updates the animation typography for a specific Quick Settings tile.
     *
     * @param tileId The unique identifier for the tile to modify
     * @param animation The new animation typography to apply
     */
    fun updateTileAnimation(tileId: String, animation: QuickSettingsAnimation) {
        prefs.edit()
            .putString("tile_animation_$tileId", animation.name)
            .apply()

        val currentTiles = _currentConfig.value?.tiles.orEmpty().toMutableList()
        val tileIndex = currentTiles.indexOfFirst { it.id == tileId }
        if (tileIndex >= 0) {
            currentTiles[tileIndex] = currentTiles[tileIndex].copy(animation = animation)
        } else {
            currentTiles.add(
                QuickSettingsTileConfig(
                    id = tileId,
                    label = tileId,
                    shape = OverlayShape.ROUNDED_RECTANGLE,
                    animation = animation
                )
            )
        }

        val config = _currentConfig.value?.copy(tiles = currentTiles)
            ?: QuickSettingsConfig(tiles = currentTiles)

        _currentConfig.value = config
    }

    /**
     * Updates the background image for the Quick Settings panel.
     *
     * @param image The new background image resource, or null to clear
     */
    fun updateBackground(image: ImageResource?) {
        if (image != null) {
            prefs.edit()
                .putString("background_image_path", image.path)
                .putString("background_image_id", image.id)
                .putString("background_image_type", image.type)
                .apply()
        } else {
            prefs.edit()
                .remove("background_image_path")
                .remove("background_image_id")
                .remove("background_image_type")
                .apply()
        }

        val config = _currentConfig.value?.copy(background = image)
            ?: QuickSettingsConfig(background = image)

        _currentConfig.value = config
    }

    /**
     * Resets all Quick Settings customizations to default values.
     * Clears all saved preferences and resets the config state flow.
     */
    fun resetToDefault() {
        // Clear all Quick Settings-related preferences
        prefs.edit()
            .apply {
                // Remove all tile shape preferences
                prefs.all.keys.filter { it.startsWith("tile_shape_") }.forEach { remove(it) }
                // Remove all tile animation preferences
                prefs.all.keys.filter { it.startsWith("tile_animation_") }.forEach { remove(it) }
                // Remove background image
                remove("background_image_path")
                remove("background_image_id")
                remove("background_image_type")
            }
            .apply()

        // Reset to default configuration
        _currentConfig.value = QuickSettingsConfig()
    }

    /**
     * Loads the saved Quick Settings configuration from SharedPreferences.
     */
    private fun loadConfiguration() {
        val tiles = mutableListOf<QuickSettingsTileConfig>()

        // Load tile configurations
        val tileIds = prefs.all.keys
            .filter { it.startsWith("tile_shape_") || it.startsWith("tile_animation_") }
            .map { key ->
                when {
                    key.startsWith("tile_shape_") -> key.removePrefix("tile_shape_")
                    else -> key.removePrefix("tile_animation_")
                }
            }
            .distinct()

        for (tileId in tileIds) {
            val shapeStr = prefs.getString("tile_shape_$tileId", null)
            val animationStr = prefs.getString("tile_animation_$tileId", null)

            val shape = shapeStr?.let { OverlayShape.valueOf(it) } ?: OverlayShape.ROUNDED_RECTANGLE
            val animation = animationStr?.let { QuickSettingsAnimation.valueOf(it) }
                ?: QuickSettingsAnimation.FADE

            tiles.add(
                QuickSettingsTileConfig(
                    id = tileId,
                    label = tileId,
                    shape = shape,
                    animation = animation
                )
            )
        }

        // Load background image
        val backgroundPath = prefs.getString("background_image_path", null)
        val backgroundId = prefs.getString("background_image_id", null)
        val backgroundType = prefs.getString("background_image_type", null)
        val background =
            if (backgroundPath != null && backgroundId != null && backgroundType != null) {
                ImageResource(id = backgroundId, type = backgroundType, path = backgroundPath)
            } else null

        _currentConfig.value = QuickSettingsConfig(
            tiles = tiles,
            background = background
        )
    }
}

