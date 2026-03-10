package dev.aurakai.auraframefx.aura.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color

fun Modifier.cyberEdgeGlow(color: Color = Color.Cyan): Modifier = this.drawWithContent {
    drawContent()
    // Add glow effect
    drawRect(color.copy(alpha = 0.3f), blendMode = BlendMode.Lighten)
}

fun Modifier.digitalPixelEffect(intensity: Float = 0.5f): Modifier = this.drawWithContent {
    drawContent()
    // Add pixel/glitch effect - placeholder implementation
}
