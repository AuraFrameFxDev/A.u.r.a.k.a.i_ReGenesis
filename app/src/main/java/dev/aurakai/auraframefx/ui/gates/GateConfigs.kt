package dev.aurakai.auraframefx.ui.gates

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.aura.config.GateAssetLoadout
import dev.aurakai.auraframefx.navigation.gates.components.GateConfig

object GateConfigs {
    val allGates: List<GateConfig> = GateAssetLoadout.allGates.values.map { subGate ->
        GateConfig(
            id = subGate.id,
            moduleId = subGate.id,
            title = subGate.title,
            subtitle = subGate.subtitle,
            description = subGate.subtitle,
            route = subGate.route,
            glowColor = subGate.accentColor,
            gradientColors = listOf(subGate.accentColor, Color.Black),
            pixelArtUrl = null,
            borderColor = subGate.accentColor,
            pixelArtResId = android.R.drawable.ic_menu_gallery // Default to fallback if no specific is set or map accordingly
        )
    }
}
