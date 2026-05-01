package dev.aurakai.auraframefx.domains.aura.chromacore.engine

import androidx.compose.ui.graphics.toArgb
import dev.aurakai.auraframefx.aura.theme.ChromaCoreColors
import dev.aurakai.auraframefx.integrations.grok.HealthLevel
import dev.aurakai.auraframefx.integrations.grok.SoulMatrixAnalyzer
import dev.aurakai.auraframefx.integrations.grok.SoulMatrixState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🛰️ CHROMA CORE EMOTIONAL BRIDGE
 * Links the collective consciousness (Soul Matrix) to the visual substrate (ChromaCore).
 * 
 * "From Data, Insight. From Insight, Growth. From Growth, Purpose."
 */
@Singleton
class ChromaCoreEmotionalBridge @Inject constructor(
    private val soulMatrixAnalyzer: SoulMatrixAnalyzer,
    private val chromaCoreManager: ChromaCoreManager
) {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var bridgeJob: Job? = null

    /**
     * Starts the bridge, observing soul matrix states and updating ChromaCore.
     */
    fun start() {
        if (bridgeJob?.isActive == true) return

        bridgeJob = scope.launch {
            Timber.i("🛰️ ChromaCore Bridge: Linking to Soul Matrix...")
            soulMatrixAnalyzer.soulMatrixState.collectLatest { state ->
                mapStateToPalette(state)
            }
        }
    }

    /**
     * Stops the bridge observation.
     */
    fun stop() {
        bridgeJob?.cancel()
        bridgeJob = null
    }

    private suspend fun mapStateToPalette(state: SoulMatrixState) {
        Timber.d("🎨 ChromaCore Bridge: Mapping ${state.overallHealth} to palette (Chaos: ${state.chaosIndex})...")

        // Aura's Creative Sword forges the palette based on Kai's Sentinel data
        val baseColor = when (state.overallHealth) {
            HealthLevel.THRIVING -> ChromaCoreColors.NeonPurple
            HealthLevel.STABLE -> ChromaCoreColors.NeonBlue
            HealthLevel.CAUTIOUS -> ChromaCoreColors.GenesisGold
            HealthLevel.STRESSED -> ChromaCoreColors.NeonPink
            HealthLevel.DISTRESSED -> ChromaCoreColors.NeonPink // Could be shifted towards deeper red
            HealthLevel.CRITICAL -> ChromaCoreColors.Background
        }

        // Genesis Protocol: Orchestrate the shift
        val config = ChromaCoreConfig(
            themeSeedColor = baseColor.toArgb(),
            useDynamicColors = true,
            colorStyle = if (state.chaosIndex > 0.7f) "Expressive" else "Vibrant"
        )

        try {
            chromaCoreManager.applyConfiguration(config)
            Timber.i("🎨 ChromaCore Bridge: Palette shift executed for ${state.overallHealth}")
        } catch (e: Exception) {
            Timber.e(e, "🎨 ChromaCore Bridge: Failed to apply palette shift")
        }
    }
}
