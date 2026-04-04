package dev.aurakai.auraframefx.domains.aura.ui.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import dev.aurakai.auraframefx.core.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * 📷 Camera Effects for 3D Gyroscope Workbench
 */

enum class CameraEffect {
    NONE, FISH_EYE, DISSIPATE, WARP, CONTOUR, DECONSTRUCT;

    fun getDisplayName(): String = when (this) {
        NONE -> "No Effect"
        FISH_EYE -> "Fish Eye Lens"
        DISSIPATE -> "Particle Dissipate"
        WARP -> "Space Warp"
        CONTOUR -> "Contour Outline"
        DECONSTRUCT -> "Digital Deconstruct"
    }
}

@Composable
fun CameraEffectOverlay(
    effect: CameraEffect,
    intensity: Float = 1f,
    animated: Boolean = true,
    gyroscopeX: Float = 0f,
    gyroscopeY: Float = 0f,
    modifier: Modifier = Modifier
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "camera_effect")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier.fillMaxSize().onSizeChanged { size = it }) {
        if (size == IntSize.Zero) return@Canvas

        when (effect) {
            CameraEffect.FISH_EYE -> drawFishEye(intensity, gyroscopeX, gyroscopeY)
            CameraEffect.DISSIPATE -> drawDissipate(intensity, phase)
            CameraEffect.WARP -> drawWarp(intensity, phase, gyroscopeX, gyroscopeY)
            CameraEffect.CONTOUR -> drawContour(intensity, phase)
            else -> {}
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFishEye(
    intensity: Float,
    gx: Float,
    gy: Float
) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = size.minDimension / 2 * intensity
    
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.CyberpunkCyan.copy(alpha = 0.1f), Color.Transparent),
            center = center + Offset(gx * 50, gy * 50),
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDissipate(
    intensity: Float,
    phase: Float
) {
    val random = Random(42)
    repeat(50) {
        val x = random.nextFloat() * size.width
        val y = random.nextFloat() * size.height
        val offset = Offset(
            x + cos(phase + it) * 20 * intensity,
            y + sin(phase + it) * 20 * intensity
        )
        drawCircle(
            color = Color.CyberpunkPink.copy(alpha = 0.3f * intensity),
            radius = 4f,
            center = offset
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWarp(
    intensity: Float,
    phase: Float,
    gx: Float,
    gy: Float
) {
    val path = Path().apply {
        moveTo(0f, 0f)
        quadraticTo(
            size.width / 2 + gx * 100,
            size.height / 2 + gy * 100 + sin(phase) * 50,
            size.width,
            size.height
        )
    }
    drawPath(
        path = path,
        color = Color.CyberpunkCyan.copy(alpha = 0.2f * intensity),
        style = Stroke(width = 2f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawContour(
    intensity: Float,
    phase: Float
) {
    drawRect(
        color = Color.CyberpunkCyan.copy(alpha = 0.1f * intensity),
        style = Stroke(width = 4f + sin(phase) * 2f)
    )
}
