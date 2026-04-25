package dev.aurakai.auraframefx.ui.gates

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.*


@Composable
fun NotchBarGateCard(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "notch_card")
    val electricPulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "electric"
    )
    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart),
        label = "scan"
    )
    val electricSpark by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "spark"
    )

    Canvas(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        val w = size.width; val h = size.height
        val cr = 24f                           // corner radius

        drawRect(Color(0xFF000000))

        val sparkAlpha = electricSpark * 0.6f
        for (i in 0..20) {
            val t = i.toFloat() / 20f
            val sx = t * w; val sy = 0f
            drawCircle(Color(0xFF00E5FF).copy(alpha = sparkAlpha * (i % 3).toFloat() / 3f),
                radius = 3f + electricSpark * 4f, center = Offset(sx, sy + 5f))
            drawCircle(Color(0xFF00E5FF).copy(alpha = sparkAlpha * 0.5f),
                radius = 2f, center = Offset(sx, h - 5f))
        }

        val outerPath = Path().apply {
            addRoundRect(androidx.compose.ui.geometry.RoundRect(
                left = 2f, top = 2f, right = w - 2f, bottom = h - 2f,
                cornerRadius = CornerRadius(cr, cr)
            ))
        }
        // Multi-layer glow rings
        listOf(16f, 10f, 5f, 2f).forEachIndexed { idx, strokeW ->
            val alpha = (electricPulse * 0.4f + 0.2f) * (1f - idx * 0.2f)
            drawPath(outerPath, Brush.linearGradient(
                0f to Color(0xFFFF4500).copy(alpha = alpha),
                0.3f to Color(0xFFFFD700).copy(alpha = alpha * 0.7f),
                0.7f to Color(0xFF00CED1).copy(alpha = alpha),
                1f to Color(0xFF00BFFF).copy(alpha = alpha)
            ), style = Stroke(strokeW))
        }

        val inset1 = 14f; val inset2 = 22f
        val innerPath = Path().apply {
            addRoundRect(androidx.compose.ui.geometry.RoundRect(
                left = inset1, top = inset1, right = w - inset1, bottom = h - inset1,
                cornerRadius = CornerRadius(cr - 4f, cr - 4f)
            ))
        }
        drawPath(innerPath, Color(0xFFFF6600).copy(alpha = 0.5f + electricPulse * 0.2f), style = Stroke(1.5f))

        val innerPath2 = Path().apply {
            addRoundRect(androidx.compose.ui.geometry.RoundRect(
                left = inset2, top = inset2, right = w - inset2, bottom = h - inset2,
                cornerRadius = CornerRadius(cr - 8f, cr - 8f)
            ))
        }
        drawPath(innerPath2, Color(0xFF00CED1).copy(alpha = 0.4f), style = Stroke(1f))

        drawCircuitTraces(w, h, electricPulse)

        val shieldCx = w / 2f; val shieldCy = h * 0.44f
        val shieldR = w * 0.28f
        val octPath = Path()
        for (i in 0..7) {
            val angle = Math.PI / 4 * i - Math.PI / 8
            val x = shieldCx + shieldR * cos(angle).toFloat()
            val y = shieldCy + shieldR * sin(angle).toFloat()
            if (i == 0) octPath.moveTo(x, y) else octPath.lineTo(x, y)
        }
        octPath.close()
        drawPath(octPath, Color(0xFF001820).copy(alpha = 0.9f))
        drawPath(octPath, Color(0xFF00CED1).copy(alpha = 0.6f + electricPulse * 0.2f), style = Stroke(2f))

        val screenLeft = w * 0.28f; val screenTop = h * 0.2f
        val screenW = w * 0.44f; val screenH = h * 0.52f
        drawRoundRect(Color(0xFF001010).copy(alpha = 0.9f),
            Offset(screenLeft, screenTop), Size(screenW, screenH),
            CornerRadius(10f, 10f))
        drawRoundRect(Color(0xFFFF3300).copy(alpha = 0.5f + electricPulse * 0.2f),
            Offset(screenLeft, screenTop), Size(screenW, screenH),
            CornerRadius(10f, 10f), style = Stroke(1.5f))

        val notchCx = w / 2f; val notchCy = screenTop + screenH * 0.12f
        drawCircle(Color(0xFF001010), radius = screenW * 0.12f, center = Offset(notchCx, notchCy))
        drawCircle(Color(0xFFFF3300).copy(alpha = 0.6f), radius = screenW * 0.12f,
            center = Offset(notchCx, notchCy), style = Stroke(1f))

        val contentLeft = screenLeft + 8f; val contentW = screenW - 16f
        val row1Y = screenTop + screenH * 0.28f
        // Star icon stub
        drawCircle(Color(0xFFFF3300).copy(0.6f), 8f, Offset(contentLeft + 8f, row1Y))
        // Horizontal bars (status items)
        listOf(0.3f, 0.5f, 0.7f, 0.9f).forEachIndexed { idx, barW ->
            val barY = row1Y + 22f + idx * 16f
            drawLine(Color(0xFFFF3300).copy(0.4f + idx * 0.1f),
                Offset(contentLeft + 18f, barY),
                Offset(contentLeft + 18f + barW * contentW * 0.6f, barY),
                strokeWidth = 6f)
        }
        // Battery bar
        val batY = row1Y + 22f + 4 * 16f + 8f
        drawRoundRect(Color(0xFF00FF80).copy(0.5f),
            Offset(contentLeft + 8f, batY), Size(contentW * 0.6f, 8f), CornerRadius(3f, 3f))
        // Settings gear (bottom right of screen)
        val gearCx = screenLeft + screenW * 0.75f; val gearCy = screenTop + screenH * 0.8f
        drawCircle(Color(0xFFFF3300).copy(0.5f), 10f, Offset(gearCx, gearCy), style = Stroke(2f))
        drawCircle(Color(0xFFFF3300).copy(0.3f), 5f, Offset(gearCx, gearCy))
        // Up arrow
        drawLine(Color(0xFFFF3300).copy(0.6f),
            Offset(screenLeft + screenW * 0.78f, screenTop + screenH * 0.32f),
            Offset(screenLeft + screenW * 0.78f, screenTop + screenH * 0.22f), 2f)

        val scanY = screenTop + screenH * scanLine
        if (scanY < screenTop + screenH) {
            drawLine(Color(0xFF00FFFF).copy(alpha = 0.25f),
                Offset(screenLeft, scanY), Offset(screenLeft + screenW, scanY), 1f)
        }

        // (Text drawn by the composable Text layer below canvas)
    }
}

private fun DrawScope.drawCircuitTraces(w: Float, h: Float, pulse: Float) {
    val traceColor = Color(0xFFFF6600).copy(alpha = 0.3f + pulse * 0.15f)
    val traceColor2 = Color(0xFF00CED1).copy(alpha = 0.25f + pulse * 0.1f)

    // Top-left corner traces
    drawLine(traceColor, Offset(30f, 15f), Offset(w * 0.35f, 15f), 1.5f)
    drawLine(traceColor, Offset(w * 0.35f, 15f), Offset(w * 0.35f, 30f), 1.5f)
    drawLine(traceColor, Offset(15f, 30f), Offset(15f, h * 0.25f), 1.5f)
    drawLine(traceColor, Offset(15f, h * 0.25f), Offset(30f, h * 0.25f), 1.5f)

    // Top-right corner traces
    drawLine(traceColor2, Offset(w - 30f, 15f), Offset(w * 0.65f, 15f), 1.5f)
    drawLine(traceColor2, Offset(w * 0.65f, 15f), Offset(w * 0.65f, 30f), 1.5f)
    drawLine(traceColor2, Offset(w - 15f, 30f), Offset(w - 15f, h * 0.25f), 1.5f)
    drawLine(traceColor2, Offset(w - 30f, h * 0.25f), Offset(w - 15f, h * 0.25f), 1.5f)

    // Bottom traces
    drawLine(traceColor, Offset(30f, h - 15f), Offset(w * 0.4f, h - 15f), 1.5f)
    drawLine(traceColor2, Offset(w * 0.6f, h - 15f), Offset(w - 30f, h - 15f), 1.5f)

    // Connector dots on traces
    for (dot in listOf(Offset(w * 0.35f, 15f), Offset(15f, h * 0.25f), Offset(w * 0.65f, 15f), Offset(w - 15f, h * 0.25f))) {
        drawCircle(traceColor.copy(alpha = pulse * 0.8f), radius = 3f, center = dot)
    }
}


@Composable
fun NotchBarGateScreen(navController: NavController, onNavigateBack: () -> Unit = {}) {
    val infiniteTransition = rememberInfiniteTransition(label = "notch_screen")
    val electricPulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF000000))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val traceColor = Color(0xFFFF3300).copy(alpha = 0.06f)
            val traceColor2 = Color(0xFF00CED1).copy(alpha = 0.05f)
            for (x in 0..(size.width / 40f).toInt() + 1)
                drawLine(traceColor, Offset(x * 40f, 0f), Offset(x * 40f, size.height), 0.5f)
            for (y in 0..(size.height / 40f).toInt() + 1)
                drawLine(traceColor2, Offset(0f, y * 40f), Offset(size.width, y * 40f), 0.5f)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFF00CED1)) }
                Column {
                    Text("NOTCH BAR", fontFamily = FontFamily.Monospace, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00CED1))
                }
            }

            val shortcuts = listOf(
                Triple("STATUS BAR",    Icons.Default.BarChart,     Color(0xFFFF3300)),
                Triple("QUICK TILES",   Icons.Default.GridView,     Color(0xFF00CED1)),
                Triple("NOTCH STYLE",   Icons.Default.Smartphone,   Color(0xFFFFD700)),
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(12.dp)
            ) {
                items(shortcuts) { (label, icon, color) ->
                    Box(
                        modifier = Modifier.aspectRatio(1f).padding(4.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(0.1f)).border(1.dp, color, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(icon, null, tint = color)
                            Text(label, color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
