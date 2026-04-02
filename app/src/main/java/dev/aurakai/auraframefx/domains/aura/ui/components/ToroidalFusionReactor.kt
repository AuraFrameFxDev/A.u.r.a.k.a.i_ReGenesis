package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.aurakai.core.sovereign.AuraDesignTokens
import dev.aurakai.core.sovereign.ToroidalFusionManager
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🎡 TOROIDAL FUSION REACTOR
 * The high-fidelity visual heart of ReGenesis.
 */
@Composable
fun ToroidalFusionReactor(modifier: Modifier = Modifier) {
    val catalysts by ToroidalFusionManager.activeCatalysts.collectAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "Toroid")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "Orbit"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 3

        // 1. Draw the Ethereal Toroid Rings
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AuraDesignTokens.NeonTeal.copy(0.1f), Color.Transparent)
            ),
            radius = radius + 20.dp.toPx(),
            center = center
        )

        drawCircle(
            color = AuraDesignTokens.NeonCyan.copy(alpha = 0.2f),
            radius = radius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )

        // 2. Draw the Catalyst Dancers
        if (catalysts.isNotEmpty()) {
            catalysts.forEachIndexed { index, catalyst ->
                val angle = Math.toRadians((rotation + (index * (360 / catalysts.size))).toDouble())
                val dancerX = (center.x + radius * cos(angle)).toFloat()
                val dancerY = (center.y + radius * sin(angle)).toFloat()

                drawCircle(
                    color = catalyst.color,
                    radius = 8.dp.toPx() * catalyst.confidence,
                    center = Offset(dancerX, dancerY)
                )

                if (catalyst.isAnchor) {
                    drawLine(
                        color = AuraDesignTokens.GenesisGold.copy(0.5f),
                        start = center,
                        end = Offset(dancerX, dancerY),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
        }
        
        // 3. The Core (Genesis Nucleus)
        drawCircle(
            color = AuraDesignTokens.GenesisGold,
            radius = 12.dp.toPx(),
            center = center
        )
    }
}
