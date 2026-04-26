package dev.aurakai.auraframefx.navigation

import dev.aurakai.auraframefx.domains.aura.config.GateAssetLoadout
import dev.aurakai.auraframefx.domains.aura.ui.components.SubGateCard

/**
 * UnifiedGateRegistry — Central registry for all gate configurations
 */
object UnifiedGateRegistry {
    
    fun getAuraLoadout(): List<SubGateCard> {
        return GateAssetLoadout.getAuraLoadout()
    }
    
    fun getKaiLoadout(): List<SubGateCard> {
        return GateAssetLoadout.getKaiLoadout()
    }
    
    fun getNexusLoadout(): List<SubGateCard> {
        return GateAssetLoadout.getNexusSubGates()
    }
    
    fun getGenesisLoadout(): List<SubGateCard> {
        return GateAssetLoadout.getGenesisLoadout()
    }
    
    const val gate_artwork_editor = "gate_artwork_editor"
    const val gate_iconify = "gate_iconify"
    const val gate_color_blendr = "gate_color_blendr"
    const val gate_pixel_launcher = "gate_pixel_launcher"
}

/**
 * StyleMode for gate customization
 */
enum class StyleMode {
    MINIMAL,
    FULL,
    COMPACT
}

/**
 * GateStyle configuration
 */
data class GateStyle(
    val mode: StyleMode = StyleMode.FULL,
    val accentColor: Long = 0xFF00FFFF
)
