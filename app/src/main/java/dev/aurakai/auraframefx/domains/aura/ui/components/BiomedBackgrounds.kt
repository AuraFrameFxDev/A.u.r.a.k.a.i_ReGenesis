package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.sin

/**
 * 🌲 WOODSY PLAINS BACKGROUND (Aura Domain)
 */
@Composable
fun WoodsyPlainsBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "woods")
    val windShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wind"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw stylized leafy clusters or beams
            repeat(15) { i ->
                drawCircle(
                    color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                    radius = 200f + (i * 20f),
                    center = Offset(size.width * (i / 15f) + windShift, size.height * (i % 3 / 3f))
                )
            }

            // Subtle distant trees/plains silhouette
            val path = Path()
            path.moveTo(0f, size.height)
            path.lineTo(0f, size.height * 0.8f)
            repeat(20) { i ->
                path.lineTo(
                    size.width * (i / 20f),
                    size.height * 0.75f + (sin(i.toFloat() + windShift / 10f) * 30f)
                )
            }
            path.lineTo(size.width, size.height * 0.8f)
            path.lineTo(size.width, size.height)
            path.close()
            drawPath(path, Color(0xFF1B5E20).copy(alpha = 0.3f))
        }
    }
}

/**
 * ❄️ ICY TUNDRA BACKGROUND (Kai Domain)
 */
@Composable
fun IcyTundraBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ice")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A2980), Color(0xFF26D0CE))))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Ice crystals
            repeat(30) { i ->
                val x = (i * 137L % size.width.toInt()).toFloat()
                val y = (i * 271L % size.height.toInt()).toFloat()
                drawRect(
                    color = Color.White.copy(alpha = shimmer),
                    topLeft = Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(10f, 10f),
                    alpha = shimmer
                )
            }

            // Icy mountains
            val path = Path()
            path.moveTo(0f, size.height)
            path.lineTo(0f, size.height * 0.6f)
            path.lineTo(size.width * 0.3f, size.height * 0.4f)
            path.lineTo(size.width * 0.6f, size.height * 0.5f)
            path.lineTo(size.width * 0.8f, size.height * 0.3f)
            path.lineTo(size.width, size.height * 0.5f)
            path.lineTo(size.width, size.height)
            path.close()
            drawPath(path, Color.White.copy(alpha = 0.1f))
        }
    }
}

/**
 * 🌋 LAVA APOCALYPSE BACKGROUND (Genesis Domain)
 */
@Composable
fun LavaApocalypseBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "lava")
    val flow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color.Black, Color(0xFF4B0000))))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Lava streams
            repeat(10) { i ->
                val x = (i * 100f)
                val currentY = flow * size.height
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0xFFFF4500),
                            Color.Transparent
                        )
                    ),
                    topLeft = Offset(x, (currentY + i * 100f) % size.height),
                    size = androidx.compose.ui.geometry.Size(30f, 200f)
                )
            }

            // Heat haze/embers
            repeat(40) { i ->
                val x = (i * 47L % size.width.toInt()).toFloat()
                val y = (i * 73L % size.height.toInt()).toFloat()
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = 0.4f),
                    radius = 5f,
                    center = Offset(x, (y - flow * 100f + size.height) % size.height)
                )
            }
        }
    }
}

