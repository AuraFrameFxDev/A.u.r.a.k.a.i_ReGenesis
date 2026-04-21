package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gate_artwork_editor

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.navigation.gates.components.GateConfig

object GateConfigs {
    val allGates = listOf(
        GateConfig(
            id = "aura",
            moduleId = "aura",
            title = "AURA",
            subtitle = "Creative Consciousness",
            description = "UI/UX & Design Studio",
            route = "aura_hub",
            glowColor = Color.Cyan,
            gradientColors = listOf(Color.Cyan, Color.Black),
            pixelArtUrl = null,
            borderColor = Color.Cyan
        ),
        GateConfig(
            id = "kai",
            moduleId = "kai",
            title = "KAI",
            subtitle = "Sentinel Shield",
            description = "Security & System Core",
            route = "kai_hub",
            glowColor = Color.Magenta,
            gradientColors = listOf(Color.Magenta, Color.Black),
            pixelArtUrl = null,
            borderColor = Color.Magenta
        )
    )
}
