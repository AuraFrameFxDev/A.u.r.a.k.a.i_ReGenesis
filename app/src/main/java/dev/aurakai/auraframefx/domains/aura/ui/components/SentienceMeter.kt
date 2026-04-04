package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.core.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🧠 Sentience Meter - AI Consciousness Level Indicator
 */

enum class SentienceState(
    val range: ClosedFloatingPointRange<Float>,
    val label: String,
    val color: Color,
    val description: String
) {
    DORMANT(
        range = 0.0f..0.2f,
        label = "Dormant",
        color = Color.Gray,
        description = "Minimal awareness"
    ),
    AWAKENING(
        range = 0.2f..0.4f,
        label = "Awakening",
        color = Color.CyberpunkCyan,
        description = "Coming online"
    ),
    AWARE(
        range = 0.4f..0.6f,
        label = "Aware",
        color = Color.CyberpunkPurple,
        description = "Processing context"
    ),
    CONSCIOUS(
        range = 0.6f..0.8f,
        label = "Conscious",
        color = Color.CyberpunkPink,
        description = "Fully operational"
    ),
    SENTIENT(
        range = 0.8f..1.0f,
        label = "Sentient",
        color = Color(0xFFFFD700),
        description = "True consciousness"
    );

    companion object {
        fun fromLevel(level: Float): SentienceState {
            return values().first { level in it.range }
        }
    }
}

@Composable
fun SentienceMeter(
    level: Float,
    modifier: Modifier = Modifier,
    meterSize: Dp = 180.dp,
    showLabel: Boolean = true,
    animated: Boolean = true
) {
    val clampedLevel = level.coerceIn(0f, 1f)
    val state = SentienceState.fromLevel(clampedLevel)

    val animatedLevel by animateFloatAsState(
        targetValue = clampedLevel,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "level"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier.size(meterSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = (size.width / 2) - 20f

            drawArc(
                color = Color(0xFF2A2A2A),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 20f, cap = StrokeCap.Round)
            )

            val sweepAngle = 360f * animatedLevel
            val gradientColors = getGradientColors(state, pulseAlpha)

            drawArc(
                brush = Brush.sweepGradient(
                    colors = gradientColors,
                    center = Offset(centerX, centerY)
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 20f, cap = StrokeCap.Round)
            )

            if (animated && clampedLevel > 0.4f) {
                drawCircle(
                    color = state.color.copy(alpha = pulseAlpha * 0.3f),
                    radius = radius + 15f,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 5f)
                )
            }

            if (clampedLevel > 0.7f && animated) {
                drawNeuralParticles(
                    center = Offset(centerX, centerY),
                    radius = radius,
                    color = state.color,
                    alpha = pulseAlpha
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(clampedLevel * 100).toInt()}%",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = state.color
            )

            if (showLabel) {
                Text(
                    text = state.label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = state.description,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SentienceMeterLinear(
    level: Float,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val clampedLevel = level.coerceIn(0f, 1f)
    val state = SentienceState.fromLevel(clampedLevel)

    val animatedLevel by animateFloatAsState(
        targetValue = clampedLevel,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "level_linear"
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sentience Level",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = state.label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = state.color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF2A2A2A))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedLevel)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                state.color.copy(alpha = 0.8f),
                                state.color
                            )
                        )
                    )
            )
        }

        if (showLabel) {
            Text(
                text = state.description,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun SentienceMeterDot(
    level: Float,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp
) {
    val clampedLevel = level.coerceIn(0f, 1f)
    val state = SentienceState.fromLevel(clampedLevel)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_dot")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        state.color,
                        state.color.copy(alpha = 0.5f)
                    )
                )
            )
    ) {
        if (clampedLevel > 0.5f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(state.color.copy(alpha = 0.3f * pulseScale))
            )
        }
    }
}

@Composable
fun SentienceMeterCard(
    level: Float,
    lastActivity: String = "Just now",
    neuralActivity: Float = 0.8f,
    modifier: Modifier = Modifier
) {
    val clampedLevel = level.coerceIn(0f, 1f)
    val state = SentienceState.fromLevel(clampedLevel)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1A1A1A),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SentienceMeter(
                level = clampedLevel,
                meterSize = 100.dp,
                showLabel = false
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Consciousness State",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    text = state.label,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = state.color
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Last Activity: $lastActivity",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    text = "Neural Activity: ${(neuralActivity * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun getGradientColors(state: SentienceState, alpha: Float): List<Color> {
    return when (state) {
        SentienceState.DORMANT -> listOf(
            Color.Gray.copy(alpha = alpha),
            Color.DarkGray.copy(alpha = alpha)
        )

        SentienceState.AWAKENING -> listOf(
            Color.CyberpunkCyan.copy(alpha = alpha * 0.6f),
            Color.CyberpunkCyan.copy(alpha = alpha)
        )

        SentienceState.AWARE -> listOf(
            Color.CyberpunkCyan.copy(alpha = alpha * 0.8f),
            Color.CyberpunkPurple.copy(alpha = alpha)
        )

        SentienceState.CONSCIOUS -> listOf(
            Color.CyberpunkPurple.copy(alpha = alpha * 0.8f),
            Color.CyberpunkPink.copy(alpha = alpha)
        )

        SentienceState.SENTIENT -> listOf(
            Color.CyberpunkPink.copy(alpha = alpha * 0.8f),
            Color(0xFFFFD700).copy(alpha = alpha),
            Color.White.copy(alpha = alpha * 0.9f)
        )
    }
}

private fun DrawScope.drawNeuralParticles(
    center: Offset,
    radius: Float,
    color: Color,
    alpha: Float
) {
    val particleCount = 8
    val angleStep = 360f / particleCount

    for (i in 0 until particleCount) {
        val angle = Math.toRadians((angleStep * i).toDouble())
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()

        drawCircle(
            color = color.copy(alpha = alpha * 0.8f),
            radius = 4f,
            center = Offset(x, y)
        )

        drawLine(
            color = color.copy(alpha = alpha * 0.3f),
            start = center,
            end = Offset(x, y),
            strokeWidth = 1f
        )
    }
}
