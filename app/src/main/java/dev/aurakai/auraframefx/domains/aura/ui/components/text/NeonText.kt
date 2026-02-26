package dev.aurakai.auraframefx.domains.aura.ui.components.text

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun NeonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Cyan,
    glowColor: Color = color.copy(alpha = 0.5f),
    fontSize: TextUnit = 32.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.Center,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    glowRadius: Dp = 16.dp,
    animateGlow: Boolean = true,
    animateTyping: Boolean = true,
    typingSpeedMs: Int = 100,
    onTypingComplete: (() -> Unit)? = null,
) {
    val density = LocalDensity.current
    val glowRadiusPx = with(density) { glowRadius.toPx() }

    // Animation for the glow effect
    val infiniteTransition = rememberInfiniteTransition(label = "neonGlow")
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0.7f at 0 with LinearEasing
                1f at 1000 with LinearEasing
                0.7f at 2000 with LinearEasing
            },
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowIntensity"
    )

    // Animation for the typing effect
    var visibleCharCount by remember { mutableIntStateOf(if (animateTyping) 0 else text.length) }

    LaunchedEffect(animateTyping, text) {
        if (animateTyping) {
            visibleCharCount = 0
            text.forEachIndexed { index, _ ->
                delay(typingSpeedMs.toLong())
                visibleCharCount = index + 1
            }
            onTypingComplete?.invoke()
        } else {
            visibleCharCount = text.length
        }
    }

    val visibleText = text.take(visibleCharCount)

    // Standard Text Measurer
    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        letterSpacing = letterSpacing,
        color = color
    )

    val textLayoutResult = remember(visibleText, textStyle) {
        textMeasurer.measure(
            text = AnnotatedString(visibleText),
            style = textStyle
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Glow Layer (Canvas)
        Canvas(
            modifier = Modifier.size(
            width = with(density) { textLayoutResult.size.width.toDp() + glowRadius * 2 },
            height = with(density) { textLayoutResult.size.height.toDp() + glowRadius * 2 }
        )) {
            val centerOffset = Offset(size.width / 2, size.height / 2)
            val textOffset = Offset(
                centerOffset.x - textLayoutResult.size.width / 2,
                centerOffset.y - textLayoutResult.size.height / 2
            )

            // Multiple passes for neon glow bloom
            for (i in 1..3) {
                drawText(
                    textLayoutResult = textLayoutResult,
                    color = glowColor.copy(alpha = (glowIntensity * 0.4f) / i),
                    topLeft = textOffset,
                    drawStyle = Stroke(width = glowRadiusPx * i * 0.3f)
                )
            }
        }

        // Foreground Text
        Text(
            text = visibleText,
            style = textStyle,
            modifier = Modifier.padding(glowRadius)
        )
    }
}

@Preview
@Composable
fun NeonTextPreview() {
    NeonText(text = "AURA PROTOCOL")
}

