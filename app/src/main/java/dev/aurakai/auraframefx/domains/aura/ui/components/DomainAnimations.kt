package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.*

/**
 * 🎭 DOMAIN ANIMATIONS & BACKGROUNDS
 */

@Composable
fun SynapticWebBackground(modifier: Modifier = Modifier, glowColor: Color = Color.Cyan) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(glowColor.copy(alpha = 0.1f), radius = 300f, center = Offset(size.width / 2, size.height / 2))
        }
    }
}

@Composable
fun PaintSplashBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "paint")
    val dripPhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "drip"
    )

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF050505))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val colors = listOf(Color(0xFF00FBFF), Color(0xFFFF00D0), Color(0xFFBC00FF))
            repeat(12) { i ->
                val color = colors[i % colors.size]
                val x = (i * 80f) % size.width
                val startY = -100f
                val endY = size.height * 0.8f
                val currentY = startY + (endY - startY) * ((dripPhase + i * 0.1f) % 1f)
                
                drawCircle(color.copy(alpha = 0.2f), radius = 40f, center = Offset(x, currentY))
                drawLine(color.copy(alpha = 0.3f), Offset(x, 0f), Offset(x, currentY), strokeWidth = 4f)
            }
        }
    }
}

@Composable
fun ColorWaveBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "colorWave")
    val wave by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "wave"
    )

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF0F0F1E))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0..50) {
                val y = size.height * (i / 50f)
                val off = sin(wave + i * 0.2f) * 50f
                val color = Color.hsv((i * 7f) % 360f, 0.7f, 0.9f)
                drawLine(color.copy(alpha = 0.2f), Offset(off, y), Offset(size.width + off, y), 3f)
            }
        }
    }
}

@Composable
fun WoodsyPlainsBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0F2027), Color(0xFF2C5364))))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Color(0xFF4CAF50).copy(alpha = 0.1f), radius = 200f, center = Offset(size.width * 0.3f, size.height * 0.4f))
        }
    }
}

@Composable
fun IcyTundraBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ice")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse),
        label = "shimmer"
    )

    Box(modifier = modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF1A2980), Color(0xFF26D0CE))))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(30) { i ->
                val x = (i * 137L % size.width.toInt()).toFloat()
                val y = (i * 271L % size.height.toInt()).toFloat()
                drawRect(Color.White.copy(alpha = shimmer), topLeft = Offset(x, y), size = Size(10f, 10f))
            }
        }
    }
}

@Composable
fun ShieldGridBackground(modifier: Modifier = Modifier, primaryColor: Color = Color(0xFFFF6B00)) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(primaryColor.copy(alpha = 0.1f), radius = 150f, center = Offset(size.width / 2, size.height / 2))
        }
    }
}

@Composable
fun CircuitPhoenixBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(Color(0xFF00FF85).copy(alpha = 0.5f), Offset(size.width / 2, 0f), Offset(size.width / 2, size.height), 2f)
        }
    }
}

@Composable
fun NeuralNetworkBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(Color(0xFF0A0A1A))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Color(0xFFB026FF).copy(alpha = 0.2f), radius = 100f, center = Offset(size.width / 2, size.height / 2))
        }
    }
}

@Composable
fun LavaApocalypseBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black, Color(0xFF4B0000))))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Color.Red.copy(alpha = 0.2f), radius = 150f, center = Offset(size.width / 2, size.height / 2))
        }
    }
}

@Composable
fun StarfieldBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(60000, easing = LinearEasing)),
        label = "rotation"
    )

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            rotate(rotation, Offset(cx, cy)) {
                repeat(100) { i ->
                    val x = (i * 137L % 2000L - 1000L).toFloat() + cx
                    val y = (i * 271L % 2000L - 1000L).toFloat() + cy
                    drawCircle(Color.White.copy(alpha = 0.6f), radius = 2f, center = Offset(x, y))
                }
            }
        }
    }
}

@Composable
fun SoftGlowBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0A1A0F), Color(0xFF0A140A))))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Color(0xFF4CAF50).copy(alpha = 0.1f), radius = 200f, center = Offset(size.width / 2, size.height / 2))
        }
    }
}

@Composable
fun HolographicCommandTable(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(Color(0xFF020A0F))
        drawOval(Color.Cyan.copy(alpha = 0.3f), topLeft = Offset(size.width * 0.1f, size.height * 0.5f), size = Size(size.width * 0.8f, size.height * 0.2f), style = Stroke(2f))
    }
}

@Composable
fun HexCorridorBackground(modifier: Modifier = Modifier, tint: Color = Color(0xFF00BFFF)) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(Brush.verticalGradient(listOf(Color(0xFF040D1A), Color(0xFF071525))))
    }
}

@Composable
fun PurpleGridRoomBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(Color(0xFF050010))
    }
}

@Composable
fun InfinityRibbonBackground(modifier: Modifier = Modifier, colorA: Color = Color(0xFFFF2D78), colorB: Color = Color(0xFF00BFFF)) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(Color(0xFF070010))
    }
}

@Composable
fun DataRibbonsBackground(
    modifier: Modifier = Modifier,
    baseColor: Color = Color.Cyan,
    accentColor: Color = Color.White,
    speedMin: Float = 0.2f,
    speedMax: Float = 0.5f,
    ribbons: Int = 8
) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(ribbons) { i ->
                drawLine(baseColor.copy(alpha = 0.2f), Offset(0f, size.height * i / ribbons), Offset(size.width, size.height * i / ribbons), 2f)
            }
        }
    }
}

@Composable
fun HexagonGridBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.Cyan,
    secondaryColor: Color = Color.Magenta,
    accentColor: Color = Color.White,
    hexSize: Float = 60f,
    alpha: Float = 0.1f
) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(primaryColor.copy(alpha = alpha))
        }
    }
}

@Composable
fun HoloHUDOverlay(modifier: Modifier = Modifier, color: Color = Color.Cyan) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(color.copy(alpha = 0.05f), style = Stroke(4f))
    }
}

@Composable
fun NeuralLinkBackground(
    modifier: Modifier = Modifier,
    speed: Float = 1.0f,
    primaryColor: Color = Color.Cyan,
    secondaryColor: Color = Color.Blue
) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Brush.verticalGradient(listOf(primaryColor.copy(alpha = 0.1f), secondaryColor.copy(alpha = 0.1f))))
        }
    }
}
