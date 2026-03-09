package dev.aurakai.auraframefx.ui.gates

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val ProjectionBlue = Color(0xFF00D1FF)

enum class GateCardSize { Large, Medium, Small }

@Composable
fun NeonWireframeGateCard(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: GateCardSize = GateCardSize.Medium,
    cornerRadius: Dp = 16.dp,
) {
    val (borderWidth, gridStep, projectionDepth) = when (size) {
        GateCardSize.Large  -> Triple(3.dp, 28.dp, 16.dp)
        GateCardSize.Medium -> Triple(2.dp, 24.dp, 14.dp)
        GateCardSize.Small  -> Triple(1.5.dp, 20.dp, 12.dp)
    }
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2.2f / 1f) // Wide rectangular (like the pixel frame reference)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF05070F), Color(0xFF0A0F1C), Color(0xFF05070F))
                ),
                shape = shape
            )
            .border(
                width = 3.dp, // Thicker pixel-style border
                color = ProjectionBlue.copy(alpha = 0.9f),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Neon wireframe grid
        WireframeGrid(accent = ProjectionBlue, gridStep = gridStep)

        // Center icon with pulsing halo
        CenterIconWithHalo(icon = icon, tint = ProjectionBlue)

        // Holographic projection pad
        ProjectionPad(accent = ProjectionBlue, depth = projectionDepth)

        // Title/subtitle overlay - pixel-bordered box
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.85f), shape = RoundedCornerShape(4.dp)) // Sharp corners
                .border(2.dp, ProjectionBlue.copy(alpha = 0.7f), RoundedCornerShape(4.dp)) // Pixel border
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = title.uppercase(), // Uppercase for pixel aesthetic
                color = ProjectionBlue,
                style = MaterialTheme.typography.titleMedium.copy(
                    letterSpacing = 1.5.sp // Spaced lettering
                ),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle!!,
                    color = Color(0xFFFFB000).copy(alpha = 0.9f), // Yellow accent like reference
                    style = MaterialTheme.typography.bodySmall.copy(
                        letterSpacing = 0.8.sp
                    ),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BoxScope.WireframeGrid(accent: Color, gridStep: Dp) {
    val stepPx = with(LocalDensity.current) { gridStep.toPx() }
    val lineColor = accent.copy(alpha = 0.25f)
    val crossColor = accent.copy(alpha = 0.15f)
    Canvas(Modifier.matchParentSize()) {
        var x = 0f
        while (x <= size.width) {
            drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), 1.2f); x += stepPx
        }
        var y = 0f
        while (y <= size.height) {
            drawLine(lineColor, Offset(0f, y), Offset(size.width, y), 1.2f); y += stepPx
        }
        var d = -size.height
        while (d < size.width) {
            drawLine(crossColor, Offset(d, 0f), Offset(d + size.height, size.height), 0.8f); d += stepPx * 2
        }
    }
}

@Composable
private fun BoxScope.CenterIconWithHalo(icon: ImageVector, tint: Color) {
    val inf = rememberInfiniteTransition(label = "halo")
    val haloAlpha by inf.animateFloat(
        0.25f, 0.6f,
        infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "haloAlpha"
    )
    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .size(96.dp)
            .background(
                brush = Brush.radialGradient(
                    listOf(
                        tint.copy(alpha = haloAlpha),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.Center)
            .size(44.dp),
        tint = tint
    )
}

@Composable
private fun BoxScope.ProjectionPad(accent: Color, depth: Dp) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(0.82f)
            .height(depth)
            .background(
                brush = Brush.radialGradient(listOf(accent.copy(alpha = 0.35f), Color.Transparent)),
                shape = RoundedCornerShape(50)
            )
            .blur(10.dp)
    )
}
