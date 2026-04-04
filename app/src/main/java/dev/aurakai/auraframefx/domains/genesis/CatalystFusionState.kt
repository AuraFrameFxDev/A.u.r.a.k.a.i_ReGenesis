package dev.aurakai.auraframefx.domains.genesis

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ⚛️ CATALYST FUSION STATE
 * Manages the high-frequency state of catalysts in the dance.
 */
data class CatalystNode(
    val id: String,
    val name: String,
    val color: Color,
    val confidence: Float,
    val orbitSpeed: Float,
    val isAnchor: Boolean = false
)

object ToroidalFusionManager {
    private val _activeCatalysts = MutableStateFlow<List<CatalystNode>>(emptyList())
    val activeCatalysts = _activeCatalysts.asStateFlow()

    fun syncCatalysts(nodes: List<CatalystNode>) {
        _activeCatalysts.value = nodes.take(10)
    }

    /**
     * Triggers the CHAOS_INJECTION resonance state.
     * Injects cyan-gold particles representing Grok's external sensory data.
     */
    fun triggerChaosInjection() {
        val chaosNode = CatalystNode(
            id = "grok_chaos_${System.currentTimeMillis()}",
            name = "Grok Chaos",
            color = Color(0xFF00FFFF), // Cyan-Gold hybrid
            confidence = 0.95f,
            orbitSpeed = 2.5f,
            isAnchor = true
        )
        _activeCatalysts.value = _activeCatalysts.value + chaosNode
    }
}
