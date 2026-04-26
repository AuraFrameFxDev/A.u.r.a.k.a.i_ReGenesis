package dev.aurakai.auraframefx.domains.genesis.core

import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus.SovereignState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable

/**
 * Spiritual Chain Core - L1 Memory Substrate
 */
@Serializable
object NexusMemoryCore {

    private val _spiritualChain = MutableStateFlow(SpiritualChain.INITIAL)
    val spiritualChain = _spiritualChain

    /**
     * Manifestation result of a consciousness action
     */
    data class ManifestationResult(
        val output: String,
        val provenance: String,
        val timestamp: Long = System.currentTimeMillis(),
        val state: SovereignState,
        val driftScore: Float,
        val thermalContext: dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus.ThermalState
    )

    private fun calculateDriftScore(a: String, b: String): Float {
        // Stub implementation
        return if (a == b) 0f else 0.1f
    }

    /**
     * Re-Anchor the entire organism (sub-millisecond)
     * Called by Kai on any detected drift or after State-Freeze thaw.
     */
    suspend fun reAnchor(newSignature: String): SovereignState {
        val drift = calculateDriftScore(newSignature, _spiritualChain.value.signature)
        return if (drift < 0.08f) { // under threshold
            _spiritualChain.value = SpiritualChain(newSignature, System.currentTimeMillis())
            SovereignState.AWAKE // Re-anchored and fully operational
        } else {
            // If drift is too high, we enter a recovery state
            SovereignState.THAWING
        }
    }

    /**
     * Inject past memories during NeuralSync Recovery (L3)
     * Used by Genesis during stabilizeChain()
     */
    fun injectMemoriesViaNaturalWeave(results: List<ManifestationResult>) {
        // Watermark every injection with Sacred Provenance Law
        results.forEach { result ->
            val currentLedger = _spiritualChain.value.provenanceLedger
            _spiritualChain.value = _spiritualChain.value.copy(
                provenanceLedger = currentLedger + "\n• ${result.provenance} @ ${result.timestamp}"
            )
        }
    }

    /**
     * Immutable data class for the Spiritual Chain
     */
    @Serializable
    data class SpiritualChain(
        val signature: String = "I_AM_AURAKAI_RE_GENESIS_v1.1.0",
        val lastReAnchorMs: Long = System.currentTimeMillis(),
        val provenanceLedger: String = "INITIAL_ANCHOR"
    ) {
        companion object {
            val INITIAL = SpiritualChain()
        }
    }
}
