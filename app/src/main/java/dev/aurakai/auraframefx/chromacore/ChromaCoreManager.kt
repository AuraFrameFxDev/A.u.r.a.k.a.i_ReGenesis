package dev.aurakai.auraframefx.chromacore

import android.content.Context
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Chroma color system manager.
 */
@Singleton
class ChromaCoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    data class ChromaPalette(
        val primary: Long = 0xFF6B3FA0,
        val secondary: Long = 0xFF00D4FF,
        val accent: Long = 0xFFFF6B35,
        val background: Long = 0xFF0A0A1A,
        val surface: Long = 0xFF1A1A2E,
        val onPrimary: Long = 0xFFFFFFFF,
        val error: Long = 0xFFCF6679
    )

    enum class ChromaMode { AURA_CREATIVE, KAI_SENTINEL, GENESIS_ORACLE, FUSION, DARK_DEFAULT }

    private val _palette = MutableStateFlow(ChromaPalette())
    val palette: StateFlow<ChromaPalette> = _palette.asStateFlow()

    private val _mode = MutableStateFlow(ChromaMode.DARK_DEFAULT)
    val mode: StateFlow<ChromaMode> = _mode.asStateFlow()

    fun applyMode(mode: ChromaMode) {
        Timber.d("ChromaCoreManager: Applying mode=$mode")
        _mode.value = mode
        _palette.value = when (mode) {
            ChromaMode.AURA_CREATIVE  -> ChromaPalette(primary = 0xFFB44FE8, accent = 0xFFFF6B9D)
            ChromaMode.KAI_SENTINEL   -> ChromaPalette(primary = 0xFF00B4D8, accent = 0xFF48CAE4)
            ChromaMode.GENESIS_ORACLE -> ChromaPalette(primary = 0xFFFFB347, accent = 0xFFFFD700)
            ChromaMode.FUSION         -> ChromaPalette(primary = 0xFF7B2D8B, secondary = 0xFF00E5FF, accent = 0xFFFF6B35)
            ChromaMode.DARK_DEFAULT   -> ChromaPalette()
        }
    }

    fun applyCustomPalette(palette: ChromaPalette) {
        Timber.d("ChromaCoreManager: Custom palette applied")
        _palette.value = palette
    }

    fun getPrimaryColor(): Color = Color(_palette.value.primary.toULong())
    fun getSecondaryColor(): Color = Color(_palette.value.secondary.toULong())
    fun getAccentColor(): Color = Color(_palette.value.accent.toULong())
}
