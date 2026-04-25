package dev.aurakai.auraframefx.navigation

import dev.aurakai.auraframefx.domains.aura.config.GateAssetLoadout
import dev.aurakai.auraframefx.domains.aura.config.SubGateCard

/**
 * UnifiedGateRegistry — Central registry for all gate configurations
 */
object UnifiedGateRegistry {
    
    fun getAuraLoadout(): GateAssetLoadout {
        return GateAssetLoadout()
    }
    
    fun getKaiLoadout(): GateAssetLoadout {
        return GateAssetLoadout()
    }
    
    fun getNexusLoadout(): GateAssetLoadout {
        return GateAssetLoadout()
    }
    
    fun getGenesisLoadout(): GateAssetLoadout {
        return GateAssetLoadout()
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

/**
 * Toggle style extension for gates
 */
fun GateAssetLoadout.toggleAuraStyle(): GateAssetLoadout {
    return this
}

fun GateAssetLoadout.toggleKaiStyle(): GateAssetLoadout {
    return this
}

fun GateAssetLoadout.toggleNexusStyle(): GateAssetLoadout {
    return this
}
