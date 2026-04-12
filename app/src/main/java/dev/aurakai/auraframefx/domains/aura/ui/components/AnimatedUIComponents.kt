package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * ✨ SparkleButton - Brutalist action button with particle feedback
 */
@Composable
fun SparkleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00D9FF)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background sparkle effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sparkleCount = 5
            repeat(sparkleCount) { i ->
                val x = size.width * (0.2f + 0.6f * (i / sparkleCount.toFloat()))
                val y = size.height * (0.3f + 0.4f * (sin(i.toFloat()).coerceIn(0f, 1f)))
                drawCircle(
                    color = color,
                    radius = 2.dp.toPx() * sparkleAlpha,
                    center = Offset(x, y),
                    alpha = sparkleAlpha * 0.5f
                )
            }
        }

        Text(
            text = text.uppercase(),
            color = color,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            fontSize = 14.sp
        )
    }
}

/**
 * 👾 GlitchText - Cyberpunk aesthetic text with displacement
 */
@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    glitchColor: Color = Color.Red
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glitch")
    val offset1 by infiniteTransition.animateValue(
        initialValue = Offset(0f, 0f),
        targetValue = Offset(2f, -1f),
        typeConverter = Offset.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(50, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )

    Box(modifier = modifier) {
        Text(
            text = text,
            color = glitchColor.copy(alpha = 0.5f),
            modifier = Modifier.offset(offset1.x.dp, offset1.y.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * ⬢ HexagonGrid - Geometric background for sovereign domains
 */
@Composable
fun HexagonGrid(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00D9FF).copy(alpha = 0.1f)
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val hexSize = 40.dp.toPx()
        val hexHeight = hexSize * 2f
        val hexWidth = sqrt(3f) * hexSize
        val horizontalSpacing = hexWidth
        val verticalSpacing = hexHeight * 0.75f

        for (y in 0..(size.height / verticalSpacing).toInt() + 1) {
            for (x in 0..(size.width / horizontalSpacing).toInt() + 1) {
                val xOffset = if (y % 2 == 1) horizontalSpacing / 2f else 0f
                val centerX = x * horizontalSpacing + xOffset
                val centerY = y * verticalSpacing
                
                drawHexagon(centerX, centerY, hexSize, color)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHexagon(
    x: Float, y: Float, size: Float, color: Color
) {
    val path = Path().apply {
        for (i in 0..5) {
            val angle = 60f * i - 30f
            val px = x + size * cos(Math.toRadians(angle.toDouble())).toFloat()
            val py = y + size * sin(Math.toRadians(angle.toDouble())).toFloat()
            if (i == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
    }
    drawPath(path, color, style = Stroke(width = 1.dp.toPx()))
}

/**
 * 🌋 PulsatingGlow - Ambient energy for the LDO core
 */
@Composable
fun PulsatingGlow(
    modifier: Modifier = Modifier,
    color: Color = Color.Red
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(300.dp)
            .blur(60.dp)
            .alpha(0.4f * scale)
            .background(
                Brush.radialGradient(
                    colors = listOf(color, Color.Transparent)
                )
            )
    )
}

/**
 * 🔮 StaticOrb - Entropic visualizer for DataStream
 */
@Composable
fun StaticOrb(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFBB86FC)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.size(200.dp)) {
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(color.copy(alpha = 0.1f), color, color.copy(alpha = 0.1f)),
                center = center
            ),
            radius = size.minDimension / 2,
            style = Stroke(width = 2.dp.toPx())
        )
        
        repeat(3) { i ->
            drawCircle(
                color = color.copy(alpha = 0.3f),
                radius = (size.minDimension / 3) * (1f - (i * 0.2f)),
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

/**
 * 🪟 CyberWindow - Brutalist container with OS-like header
 */
@Composable
fun CyberWindow(
    title: String,
    modifier: Modifier = Modifier,
    accentColor: Color = Color.Red,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, accentColor.copy(alpha = 0.5f))
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(accentColor.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.uppercase(),
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f)
            )
            Box(Modifier.size(8.dp).background(accentColor))
        }
        
        // Body
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}
