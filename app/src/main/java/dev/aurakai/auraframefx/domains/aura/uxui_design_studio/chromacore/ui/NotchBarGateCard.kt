package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.ui

// ═══════════════════════════════════════════════════════════════════════════════
// NotchBarGateCard.kt — Image 11
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// The "NOTCH BAR" gate card — neon circuit board card art drawn in Canvas.
// Used as: the gate card for Personal Screen & Shortcuts / Notch Bar domain.
// Also exported as a standalone composable for use anywhere a circuit card
// art piece is needed (e.g. a "quick settings" floating card).
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.grid.items
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

// ── Notch Bar Gate Card (standalone canvas art) ───────────────────────────────

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
        val border = 10f                       // frame thickness

        // ── Black void background ─────────────────────────────────────────
        drawRect(Color(0xFF000000))

        // ── Electric spark effect on edges ────────────────────────────────
        val sparkAlpha = electricSpark * 0.6f
        for (i in 0..20) {
            val t = i.toFloat() / 20f
            val sx = t * w; val sy = 0f
            drawCircle(Color(0xFF00E5FF).copy(alpha = sparkAlpha * (i % 3).toFloat() / 3f),
                radius = 3f + electricSpark * 4f, center = Offset(sx, sy + 5f))
            drawCircle(Color(0xFF00E5FF).copy(alpha = sparkAlpha * 0.5f),
                radius = 2f, center = Offset(sx, h - 5f))
        }

        // ── Outer frame: Red-Orange top, Cyan bottom gradient ─────────────
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

        // ── Circuit trace inset frame ─────────────────────────────────────
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

        // ── Circuit trace patterns on frame ───────────────────────────────
        drawCircuitTraces(w, h, electricPulse)

        // ── Octagonal shield center mount ─────────────────────────────────
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

        // ── Inner screen (the phone icon card) ────────────────────────────
        val screenLeft = w * 0.28f; val screenTop = h * 0.2f
        val screenW = w * 0.44f; val screenH = h * 0.52f
        drawRoundRect(Color(0xFF001010).copy(alpha = 0.9f),
            Offset(screenLeft, screenTop), Size(screenW, screenH),
            CornerRadius(10f, 10f))
        drawRoundRect(Color(0xFFFF3300).copy(alpha = 0.5f + electricPulse * 0.2f),
            Offset(screenLeft, screenTop), Size(screenW, screenH),
            CornerRadius(10f, 10f), style = Stroke(1.5f))

        // ── Phone notch circle ────────────────────────────────────────────
        val notchCx = w / 2f; val notchCy = screenTop + screenH * 0.12f
        drawCircle(Color(0xFF001010), radius = screenW * 0.12f, center = Offset(notchCx, notchCy))
        drawCircle(Color(0xFFFF3300).copy(alpha = 0.6f), radius = screenW * 0.12f,
            center = Offset(notchCx, notchCy), style = Stroke(1f))

        // ── Screen content: status bar icons + bars ────────────────────────
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

        // ── Scan line across screen ────────────────────────────────────────
        val scanY = screenTop + screenH * scanLine
        if (scanY < screenTop + screenH) {
            drawLine(Color(0xFF00FFFF).copy(alpha = 0.25f),
                Offset(screenLeft, scanY), Offset(screenLeft + screenW, scanY), 1f)
        }

        // ── NOTCH BAR label ────────────────────────────────────────────────
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

// ── Notch Bar Gate Card Wrapper Screen ────────────────────────────────────────

@Composable
fun NotchBarGateScreen(navController: NavController, onNavigateBack: () -> Unit = {}) {
    val infiniteTransition = rememberInfiniteTransition(label = "notch_screen")
    val electricPulse by infiniteTransition.animateFloat(
        0f, 1f, infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF000000))) {
        // Circuit pattern full-screen backdrop
        Canvas(modifier = Modifier.fillMaxSize()) {
            val traceColor = Color(0xFFFF3300).copy(alpha = 0.06f)
            val traceColor2 = Color(0xFF00CED1).copy(alpha = 0.05f)
            // Background circuit grid
            for (x in 0..(size.width / 40f).toInt() + 1)
                drawLine(traceColor, Offset(x * 40f, 0f), Offset(x * 40f, size.height), 0.5f)
            for (y in 0..(size.height / 40f).toInt() + 1)
                drawLine(traceColor2, Offset(0f, y * 40f), Offset(size.width, y * 40f), 0.5f)
            // Diagonal corner neon sparks
            drawLine(Color(0xFF00CED1).copy(alpha = 0.3f + electricPulse * 0.3f),
                Offset(0f, 0f), Offset(size.width * 0.3f, 0f), 3f)
            drawLine(Color(0xFFFF3300).copy(alpha = 0.3f + electricPulse * 0.3f),
                Offset(size.width, 0f), Offset(size.width * 0.7f, 0f), 3f)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFF00CED1)) }
                Column {
                    Text("NOTCH BAR", fontFamily = FontFamily.Monospace, fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 5.sp,
                        color = Color(0xFF00CED1))
                    Text("PERSONAL SCREEN & SHORTCUTS", fontSize = 8.sp,
                        letterSpacing = 2.sp, color = Color(0xFFFF6600).copy(0.7f))
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { navController.navigate("gate_image_picker") }) {
                    Icon(Icons.Default.SwapHoriz, null, tint = Color(0xFF00CED1).copy(0.6f))
                }
            }

            // Centered gate card art
            Box(modifier = Modifier.fillMaxWidth().weight(0.4f).padding(horizontal = 40.dp),
                contentAlignment = Alignment.Center) {
                NotchBarGateCard(
                    modifier = Modifier.fillMaxWidth().aspectRatio(0.65f),
                    isActive = true
                )
                // "NOTCH BAR" text overlay at bottom of card
                Text(
                    "NOTCH BAR",
                    fontFamily = FontFamily.Monospace, fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 4.sp,
                    color = Color(0xFF00BFFF),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
                )
            }

            // Shortcut grid
            val shortcuts = listOf(
                Triple("STATUS BAR",    Icons.Default.BarChart,     Color(0xFFFF3300)),
                Triple("QUICK TILES",   Icons.Default.GridView,     Color(0xFF00CED1)),
                Triple("NOTCH STYLE",   Icons.Default.Smartphone,   Color(0xFFFFD700)),
                Triple("GESTURES",      Icons.Default.TouchApp,     Color(0xFF9B30FF)),
                Triple("BRIGHTNESS",    Icons.Default.WbSunny,      Color(0xFFFF9B00)),
                Triple("VOLUME",        Icons.Default.VolumeUp,     Color(0xFF00FF80)),
            )
            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(shortcuts.size) { idx ->
                    val (label, icon, color) = shortcuts[idx]
                    Box(
                        modifier = Modifier.aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, color.copy(0.4f), RoundedCornerShape(8.dp))
                            .background(color.copy(0.06f))
                            .clickable { }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(icon, label, tint = color, modifier = Modifier.size(24.dp))
                            Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp, color = Color.White.copy(0.7f),
                                textAlign = TextAlign.Center, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CollabCanvas Gate Screen — Image 9 (Eye Rune card gate)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CollabCanvasGateScreen(navController: NavController, onNavigateBack: () -> Unit = {}) {
    val infiniteTransition = rememberInfiniteTransition(label = "collab")
    val paintSplash by infiniteTransition.animateFloat(
        0f, 1f, infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "paint"
    )
    val eyePulse by infiniteTransition.animateFloat(
        0.6f, 1f, infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "eye"
    )
    val orbitAngle by infiniteTransition.animateFloat(
        0f, 2f * PI.toFloat(),
        infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "orbit"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // Layer 1: Eye Rune canvas background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f; val cy = size.height * 0.4f

            // Black void
            drawRect(Color(0xFF000000))

            // Paint splash — magenta (top-left)
            drawEyeRunePaintSplash(cx, cy, paintSplash, orbitAngle)

            // Circuit board border lines
            val cBorder = Color(0xFFFF2D78).copy(0.15f)
            for (i in 1..4) drawLine(cBorder, Offset(0f, i * size.height / 5f), Offset(size.width * 0.1f, i * size.height / 5f), 1f)
            for (i in 1..4) drawLine(cBorder, Offset(size.width * 0.9f, i * size.height / 5f), Offset(size.width, i * size.height / 5f), 1f)

            // Eye rune symbol
            drawEyeRune(Offset(cx, cy), eyePulse)
        }

        // Layer 2: Content
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFFFF2D78))
                }
                Text("COLLAB CANVAS", fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp, color = Color(0xFFFF2D78))
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { navController.navigate("gate_image_picker") }) {
                    Icon(Icons.Default.SwapHoriz, null, tint = Color(0xFFFF2D78).copy(0.6f))
                }
            }

            Spacer(Modifier.height(250.dp)) // Eye rune space

            // Collaboration tools
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val collabItems = listOf(
                    "Live Collaborative Drawing" to Color(0xFFFF2D78),
                    "Shared UI Mockups"           to Color(0xFF00BFFF),
                    "Agent Vision Board"          to Color(0xFF9B30FF),
                    "Color Palette Sync"          to Color(0xFFFF9B00),
                    "Export & Share"              to Color(0xFF00FF80),
                )
                collabItems.forEach { (label, color) ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, color.copy(0.3f), RoundedCornerShape(8.dp))
                            .background(color.copy(0.07f))
                            .clickable { }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(0.85f))
                        Text("→", fontSize = 14.sp, color = color)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawEyeRunePaintSplash(cx: Float, cy: Float, t: Float, angle: Float) {
    // Magenta paint blob top-left
    drawCircle(Color(0xFFFF2D78).copy(alpha = 0.3f + t * 0.2f), radius = 120f,
        center = Offset(cx - 80f, cy - 100f))
    // Blue paint blob top-right
    drawCircle(Color(0xFF1E90FF).copy(alpha = 0.25f + t * 0.15f), radius = 100f,
        center = Offset(cx + 80f, cy - 80f))
    // Paint drips
    for (i in 0..5) {
        val px = cx - 100f + i * 30f
        drawLine(Color(0xFFFF2D78).copy(alpha = 0.4f),
            Offset(px, cy - 120f), Offset(px + 5f, cy - 60f), strokeWidth = 8f,
            cap = StrokeCap.Round)
    }
    // Circuit border frame
    val borderColor = Color(0xFFFF2D78).copy(0.4f + t * 0.2f)
    drawRect(Color(0xFF000000))
    drawRoundRect(borderColor, Offset(20f, 20f), Size(size.width - 40f, size.height * 0.7f),
        CornerRadius(8f), style = Stroke(width = 2f))
    // Orbiting particles
    for (i in 0..5) {
        val a = angle + i * (2 * PI.toFloat() / 6)
        val r = 140f + i * 10f
        val px = cx + r * cos(a); val py = cy + r * sin(a) * 0.5f
        drawCircle(Color(0xFFFF2D78).copy(0.6f), radius = 4f, center = Offset(px, py))
    }
}

private fun DrawScope.drawEyeRune(center: Offset, pulse: Float) {
    val r = 70f
    // Outer eye oval
    drawOval(Color(0xFF00BFFF).copy(alpha = 0.7f + pulse * 0.2f),
        topLeft = Offset(center.x - r, center.y - r * 0.5f),
        size = Size(r * 2, r), style = Stroke(2.5f))
    // Iris
    drawCircle(Color(0xFFFF2D78).copy(0.5f), radius = r * 0.4f, center = center)
    drawCircle(Color(0xFF00BFFF).copy(0.8f + pulse * 0.1f), radius = r * 0.4f,
        center = center, style = Stroke(2f))
    // Pupil
    drawCircle(Color(0xFF000000), radius = r * 0.2f, center = center)
    drawCircle(Color.White.copy(0.9f), radius = r * 0.08f,
        center = Offset(center.x + r * 0.1f, center.y - r * 0.1f))

    // Rune cross-hash below eye
    val runeY = center.y + r * 0.7f
    drawLine(Color(0xFF00BFFF).copy(0.6f),
        Offset(center.x, runeY), Offset(center.x, runeY + r * 0.5f), 2.5f)
    drawLine(Color(0xFF00BFFF).copy(0.4f),
        Offset(center.x - r * 0.3f, runeY + r * 0.2f),
        Offset(center.x + r * 0.3f, runeY + r * 0.2f), 2f)
    // Anchor swirl
    drawCircle(Color(0xFF00BFFF).copy(0.3f), radius = r * 0.15f,
        center = Offset(center.x, runeY + r * 0.5f), style = Stroke(1.5f))

    // Radiating glow
    drawCircle(Color(0xFF00BFFF).copy(alpha = (1f - pulse) * 0.3f),
        radius = r * 1.4f * pulse, center = center, style = Stroke(1f))
}
