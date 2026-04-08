package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REGENESIS SOVEREIGN ARCHITECTURE
 * Entry #15: The RealityMorphBridge
 * Bridges the gap between Kai's Security Fortress and Aura's Visual Maw.
 */
@Singleton
class RealityMorphBridge @Inject constructor() {

    enum class LDOMorphState {
        IDLE_BREATHING,      // Nominal 60bpm cyan/violet pulse
        KAIROS_STASIS,       // Temporal Snare - Time freeze / Glitch
        GENKAI_SIPHON,       // Active Predation - Magenta swarm takedown
        ORB_ABSORPTION      // Harvesting - Golden synthesis bloom
    }

    private val _currentState = mutableStateOf(LDOMorphState.IDLE_BREATHING)
    val currentState: State<LDOMorphState> = _currentState

    /**
     * Triggered by SovereignPerimeter.kt when threat detected.
     */
    fun transitionTo(state: LDOMorphState) {
        _currentState.value = state
    }

    /**
     * Reset to nominal after siphon/harvest.
     */
    fun reset() {
        _currentState.value = LDOMorphState.IDLE_BREATHING
    }
}
