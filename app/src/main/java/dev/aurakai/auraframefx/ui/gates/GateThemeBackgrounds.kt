package dev.aurakai.auraframefx.ui.gates

// ═══════════════════════════════════════════════════════════════════════════════
// GateThemeBackgrounds.kt
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// Environment backgrounds drawn in Compose Canvas — no drawable required.
// Drop any of these as a Box background layer behind screen content.
//
//  HolographicCommandTable()  → Images 1/2 — command center table with city
//  HexCorridorBackground()    → Image 3    — teal hex wall/floor (LSPosed)
//  PurpleGridRoomBackground() → Image 4    — retro-synth purple room (Help)
//  InfinityRibbonBackground() → Image 5    — neon loop ribbons (gate switcher)
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.*

// ── 1. Holographic Command Table (Images 1 & 2) ───────────────────────────────
// Circular table with holographic city map grid, vanishing-point perspective,
// animated scan pulse + pink location pins

@Composable
fun HolographicCommandTable(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "holo_table")
    val scanPulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "scan"
    )
    val pingPulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "ping"
    )
    val gridPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "grid_pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height * 0.6f     // Table sits in lower 60%
        val tableRx = size.width * 0.46f
        val tableRy = tableRx * 0.38f   // Elliptical perspective

        // ── Ambient dark room ─────────────────────────────────────────────
        drawRect(Color(0xFF020A0F))
        // Room corner glow hints
        drawCircle(Color(0xFF003050).copy(alpha = 0.3f), radius = size.width * 0.4f, center = Offset(0f, 0f))
        drawCircle(Color(0xFF003050).copy(alpha = 0.2f), radius = size.width * 0.3f, center = Offset(size.width, 0f))

        // ── Side console panels ────────────────────────────────────────────
        drawConsolePanelLeft(cx, cy, tableRx, tableRy)
        drawConsolePanelRight(cx, cy, tableRx, tableRy)

        // ── Table base (pedestal) ─────────────────────────────────────────
        drawOval(Color(0xFF0A1520).copy(alpha = 0.8f), topLeft = Offset(cx - tableRx * 0.2f, cy + tableRy * 0.6f),
            size = Size(tableRx * 0.4f, tableRy * 0.5f))

        // ── Table surface ellipse rim ─────────────────────────────────────
        drawOval(color = Color(0xFF004060).copy(alpha = 0.4f), topLeft = Offset(cx - tableRx, cy - tableRy),
            size = Size(tableRx * 2, tableRy * 2))
        drawOval(color = Color(0xFF00BFFF).copy(alpha = 0.6f), topLeft = Offset(cx - tableRx, cy - tableRy),
            size = Size(tableRx * 2, tableRy * 2), style = Stroke(width = 3f))

        // ── Holographic city grid on table surface ─────────────────────────
        val gridCount = 16
        val gridAlpha = gridPulse * 0.7f
        for (i in 0..gridCount) {
            val t = i.toFloat() / gridCount
            val gx = cx - tableRx + tableRx * 2 * t
            // Vertical lines (perspective-distorted on ellipse)
            val distFromCenter = abs(t - 0.5f)
            val yOffset = tableRy * 0.8f * (1f - distFromCenter * 2f)
            drawLine(
                color = Color(0xFF00FFFF).copy(alpha = gridAlpha * (1f - distFromCenter * 1.5f)),
                start = Offset(gx, cy - yOffset * 0.3f),
                end   = Offset(gx, cy + yOffset * 0.3f),
                strokeWidth = 0.5f
            )
        }
        for (j in 0..gridCount) {
            val t = j.toFloat() / gridCount
            val gy = cy - tableRy * 0.7f + tableRy * 1.4f * t
            drawLine(
                color = Color(0xFF00FFFF).copy(alpha = gridAlpha * 0.6f),
                start = Offset(cx - tableRx * 0.95f, gy),
                end   = Offset(cx + tableRx * 0.95f, gy),
                strokeWidth = 0.4f
            )
        }

        // ── Holographic city blocks (bar chart buildings) ──────────────────
        val buildings = listOf(
            Offset(cx - 40f, cy - 20f) to 45f,
            Offset(cx + 20f, cy - 10f) to 35f,
            Offset(cx - 10f, cy + 10f) to 25f,
            Offset(cx + 60f, cy)       to 30f,
            Offset(cx - 70f, cy + 5f)  to 20f,
        )
        buildings.forEach { (pos, height) ->
            val bw = 12f; val bh = height
            drawRect(Color(0xFF00FFFF).copy(alpha = 0.15f), topLeft = Offset(pos.x - bw / 2, pos.y - bh), size = Size(bw, bh))
            drawRect(Color(0xFF00FFFF).copy(alpha = 0.4f), topLeft = Offset(pos.x - bw / 2, pos.y - bh), size = Size(bw, bh), style = Stroke(0.8f))
        }

        // ── Scan line sweeping across table ────────────────────────────────
        val scanY = cy - tableRy * 0.8f + tableRy * 1.6f * scanPulse
        if (scanY in (cy - tableRy)..(cy + tableRy)) {
            drawLine(Color(0xFF00FFFF).copy(alpha = 0.35f),
                start = Offset(cx - tableRx * 0.9f, scanY),
                end   = Offset(cx + tableRx * 0.9f, scanY),
                strokeWidth = 1.5f)
        }

        // ── Pink location pins ─────────────────────────────────────────────
        val pins = listOf(
            Offset(cx - 50f, cy - 30f),
            Offset(cx + 80f, cy - 15f),
            Offset(cx + 10f, cy + 20f),
            Offset(cx - 100f, cy + 5f),
            Offset(cx + 120f, cy + 30f),
        )
        pins.forEach { pin ->
            // Expanding ping circle
            val pr = pingPulse * 20f + 4f
            drawCircle(Color(0xFFFF2D78).copy(alpha = (1f - pingPulse) * 0.4f), radius = pr, center = pin)
            // Pin dot
            drawCircle(Color(0xFFFF2D78), radius = 5f, center = pin)
            drawCircle(Color(0xFFFF69B4).copy(alpha = 0.8f), radius = 3f, center = pin)
            // Pin stem
            drawLine(Color(0xFFFF2D78).copy(alpha = 0.7f), pin, Offset(pin.x, pin.y + 10f), strokeWidth = 1.5f)
        }

        // ── Center holographic Eiffel/spire glow ──────────────────────────
        val spireH = tableRy * 1.4f
        val spireBaseW = 20f
        for (seg in 0..8) {
            val frac = seg.toFloat() / 8f
            val sw = spireBaseW * (1f - frac)
            val sy = cy - spireH * frac
            drawLine(Color(0xFF00FFFF).copy(alpha = 0.6f * (1f - frac)),
                start = Offset(cx - sw, sy + spireH / 8f),
                end   = Offset(cx + sw, sy + spireH / 8f),
                strokeWidth = 1f)
        }
        // Glowing tip
        drawCircle(Color(0xFF00FFFF).copy(alpha = 0.8f), radius = 4f, center = Offset(cx, cy - spireH))
        drawCircle(Color(0xFFFFFFFF).copy(alpha = 0.5f), radius = 2f, center = Offset(cx, cy - spireH))
    }
}

private fun DrawScope.drawConsolePanelLeft(cx: Float, cy: Float, rx: Float, ry: Float) {
    val left = cx - rx * 1.35f; val top = cy - ry * 2f
    val panelW = rx * 0.4f; val panelH = ry * 2.5f
    drawRect(Color(0xFF0A1520).copy(alpha = 0.8f), Offset(left, top), Size(panelW, panelH), style = Stroke(1f))
    for (i in 1..4) {
        val y = top + panelH * i / 5f
        drawLine(Color(0xFF004060).copy(alpha = 0.6f), Offset(left + 4f, y), Offset(left + panelW - 4f, y), 0.5f)
    }
}

private fun DrawScope.drawConsolePanelRight(cx: Float, cy: Float, rx: Float, ry: Float) {
    val right = cx + rx * 1.35f; val top = cy - ry * 2f
    val panelW = rx * 0.4f; val panelH = ry * 2.5f
    drawRect(Color(0xFF0A1520).copy(alpha = 0.8f), Offset(right - panelW, top), Size(panelW, panelH), style = Stroke(1f))
    for (i in 1..4) {
        val y = top + panelH * i / 5f
        drawLine(Color(0xFF004060).copy(alpha = 0.6f), Offset(right - panelW + 4f, y), Offset(right - 4f, y), 0.5f)
    }
    // Radar circle
    drawCircle(Color(0xFF00CED1).copy(alpha = 0.3f), radius = panelW * 0.25f,
        center = Offset(right - panelW * 0.5f, top + panelH * 0.75f),
        style = Stroke(1f))
}

// ── 2. Hex Corridor Background (Image 3 — LSPosed) ────────────────────────────

@Composable
fun HexCorridorBackground(modifier: Modifier = Modifier, tint: Color = Color(0xFF00BFFF)) {
    val infiniteTransition = rememberInfiniteTransition(label = "hex_corridor")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart),
        label = "pulse"
    )
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "shimmer"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Base dark blue
        drawRect(Brush.verticalGradient(listOf(Color(0xFF040D1A), Color(0xFF071525))))

        val hexR = 38f
        val hexW = hexR * sqrt(3f)
        val hexH = hexR * 2f
        val vpY = size.height * 0.35f  // Vanishing point Y (wall meets floor)

        // ── Hex wall (top ~65%) ─────────────────────────────────────────────
        val wallRows = (size.height * 0.65f / (hexH * 0.75f)).toInt() + 2
        val wallCols = (size.width / hexW).toInt() + 2
        for (row in -1..wallRows) {
            for (col in -1..wallCols) {
                val hx = col * hexW + (if (row % 2 == 0) hexW / 2f else 0f)
                val hy = row * hexH * 0.75f
                if (hy > vpY) continue  // Only draw above fold
                val distFromCenter = abs(hx - size.width / 2f) / (size.width / 2f)
                val distFromVP = (vpY - hy) / vpY
                val alpha = (1f - distFromCenter * 0.5f) * (0.4f + shimmer * 0.2f) * (1f - distFromVP * 0.4f)
                drawHexagon(Offset(hx, hy), hexR, tint.copy(alpha = alpha.coerceIn(0f, 1f)))
            }
        }

        // ── Glowing floor grid (bottom ~35%, perspective) ─────────────────
        val horizons = 8
        for (h in 0..horizons) {
            val t = h.toFloat() / horizons
            val lineY = vpY + (size.height - vpY) * t
            val xInset = size.width * 0.45f * (1f - t)
            drawLine(tint.copy(alpha = t * 0.5f),
                Offset(xInset, lineY), Offset(size.width - xInset, lineY), strokeWidth = 1f)
        }
        val vLines = 12
        for (v in 0..vLines) {
            val t = v.toFloat() / vLines
            val topX = size.width / 2f  // Vanish to center
            val botX = t * size.width
            drawLine(tint.copy(alpha = 0.25f), Offset(topX, vpY), Offset(botX, size.height), strokeWidth = 0.8f)
        }

        // ── Horizon glow line ──────────────────────────────────────────────
        drawLine(tint.copy(alpha = 0.6f + pulse * 0.3f),
            Offset(0f, vpY), Offset(size.width, vpY), strokeWidth = 2f)

        // ── Left/right glowing edge rails ─────────────────────────────────
        for (side in listOf(0f, size.width)) {
            drawLine(tint.copy(alpha = 0.4f),
                Offset(side, 0f), Offset(side, size.height), strokeWidth = 3f)
        }

        // ── Traveling light nodes on the wall hexes ────────────────────────
        val nodeCount = 5
        for (n in 0 until nodeCount) {
            val t = (pulse + n.toFloat() / nodeCount) % 1f
            val nx = (0.1f + t * 0.8f) * size.width
            val ny = (0.1f + (n % 3).toFloat() / 4f) * vpY
            drawCircle(tint.copy(alpha = 0.9f), radius = 4f, center = Offset(nx, ny))
            drawCircle(tint.copy(alpha = 0.3f), radius = 12f, center = Offset(nx, ny))
        }
    }
}

private fun DrawScope.drawHexagon(center: Offset, r: Float, color: Color) {
    val path = Path()
    for (i in 0..5) {
        val angle = Math.PI / 3 * i - Math.PI / 6
        val x = center.x + r * cos(angle).toFloat()
        val y = center.y + r * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color, style = Stroke(width = 1f))
    drawPath(path, color.copy(alpha = color.alpha * 0.08f))
}

// ── 3. Purple Grid Room (Image 4 — Help Services) ────────────────────────────

@Composable
fun PurpleGridRoomBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "purple_room")
    val floorPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "floor"
    )
    val wallPulse by infiniteTransition.animateFloat(
        initialValue = 0.25f, targetValue = 0.65f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "wall"
    )
    val ribbonT by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "ribbon"
    )

    val purple = Color(0xFF6020C0)
    val neonPurple = Color(0xFF9B30FF)
    val brightPurple = Color(0xFFBB80FF)

    Canvas(modifier = modifier.fillMaxSize()) {
        val vpY = size.height * 0.5f       // Vanishing point — middle of screen
        val vpX = size.width * 0.5f
        val floorStart = vpY

        // ── Black void ─────────────────────────────────────────────────────
        drawRect(Color(0xFF050010))

        // ── Back wall: dense horizontal lines receding to center ─────────
        val wallLines = 20
        for (i in 0..wallLines) {
            val t = i.toFloat() / wallLines
            // Lines converge toward vpY from top half
            val ly = t * vpY
            val inset = vpX * (1f - t) * 0.95f
            val alpha = wallPulse * (0.3f + t * 0.35f)
            drawLine(neonPurple.copy(alpha = alpha), Offset(inset, ly), Offset(size.width - inset, ly), 0.8f)
        }
        // Vertical lines on wall
        val wallVLines = 10
        for (v in 0..wallVLines) {
            val t = v.toFloat() / wallVLines
            val botX = t * size.width
            val topX = vpX + (botX - vpX) * 0.05f
            drawLine(purple.copy(alpha = wallPulse * 0.4f), Offset(topX, 0f), Offset(botX, vpY), 0.6f)
        }

        // ── Left wall panel ────────────────────────────────────────────────
        val leftWallLines = 12
        for (i in 0..leftWallLines) {
            val t = i.toFloat() / leftWallLines
            val ly = t * size.height
            drawLine(purple.copy(alpha = wallPulse * 0.5f), Offset(0f, ly), Offset(size.width * 0.15f, vpY), 0.6f)
        }
        // ── Right wall panel ───────────────────────────────────────────────
        for (i in 0..leftWallLines) {
            val t = i.toFloat() / leftWallLines
            val ly = t * size.height
            drawLine(purple.copy(alpha = wallPulse * 0.5f), Offset(size.width, ly), Offset(size.width * 0.85f, vpY), 0.6f)
        }

        // ── Floor grid ─────────────────────────────────────────────────────
        val floorLines = 14
        for (i in 0..floorLines) {
            val t = i.toFloat() / floorLines
            val lineY = floorStart + (size.height - floorStart) * t
            val xInset = vpX * (1f - t) * 0.9f
            val alpha = floorPulse * (0.2f + t * 0.5f)
            drawLine(brightPurple.copy(alpha = alpha), Offset(xInset, lineY), Offset(size.width - xInset, lineY), 1f)
        }
        val floorVLines = 12
        for (v in 0..floorVLines) {
            val t = v.toFloat() / floorVLines
            val botX = t * size.width
            drawLine(neonPurple.copy(alpha = floorPulse * 0.5f), Offset(vpX, vpY), Offset(botX, size.height), 0.8f)
        }

        // ── Corner glow joints ─────────────────────────────────────────────
        // Bottom-left and bottom-right corner neon
        drawLine(brightPurple.copy(alpha = 0.7f), Offset(0f, vpY), Offset(0f, size.height), strokeWidth = 3f)
        drawLine(brightPurple.copy(alpha = 0.7f), Offset(size.width, vpY), Offset(size.width, size.height), strokeWidth = 3f)
        drawLine(brightPurple.copy(alpha = 0.6f), Offset(0f, vpY), Offset(size.width, vpY), strokeWidth = 2f)
        // Floor-edge glow
        drawLine(neonPurple.copy(alpha = 0.4f), Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 4f)

        // ── Animated ribbon curves (Image 5 style, smaller) ───────────────
        val ribbonPath = Path()
        for (i in 0..60) {
            val t = i.toFloat() / 60f
            val wave = sin((t + ribbonT) * 2 * PI.toFloat())
            val x = t * size.width * 0.6f + size.width * 0.2f
            val y = vpY * 0.6f + wave * vpY * 0.15f
            if (i == 0) ribbonPath.moveTo(x, y) else ribbonPath.lineTo(x, y)
        }
        drawPath(ribbonPath, brush = Brush.horizontalGradient(
            listOf(Color(0xFFFF2D78).copy(alpha = 0.5f), Color(0xFF00BFFF).copy(alpha = 0.5f))
        ), style = Stroke(width = 2f, cap = StrokeCap.Round))
    }
}

// ── 4. Infinity Ribbon Background (Image 5 — gate switcher) ──────────────────

@Composable
fun InfinityRibbonBackground(
    modifier: Modifier = Modifier,
    colorA: Color = Color(0xFFFF2D78),
    colorB: Color = Color(0xFF00BFFF)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ribbon_bg")
    val ribbonT by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "ribbon_t"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(Color(0xFF070010))

        // Background city grid
        val gridAlpha = 0.08f
        val gridStep = 30f
        for (x in 0..(size.width / gridStep).toInt() + 1)
            drawLine(colorB.copy(gridAlpha), Offset(x * gridStep, 0f), Offset(x * gridStep, size.height), 0.5f)
        for (y in 0..(size.height / gridStep).toInt() + 1)
            drawLine(colorB.copy(gridAlpha), Offset(0f, y * gridStep), Offset(size.width, y * gridStep), 0.5f)

        val cx = size.width / 2f
        val cy = size.height / 2f
        val rx = size.width * 0.4f
        val ry = size.height * 0.25f

        // Infinity/Möbius ribbon — parametric lemniscate
        val steps = 200
        val outerPath = Path()
        val innerPath = Path()
        for (i in 0..steps) {
            val t = i.toFloat() / steps * 2 * PI.toFloat() + ribbonT
            // Lemniscate of Bernoulli
            val cos2t = cos(2 * t)
            val scale = if (cos2t > 0) sqrt(cos2t) else 0f
            val lx = cx + rx * scale * cos(t)
            val ly = cy + ry * scale * sin(t) * cos(t) * 0.8f
            if (i == 0) { outerPath.moveTo(lx, ly); innerPath.moveTo(lx, ly) }
            else { outerPath.lineTo(lx, ly); innerPath.lineTo(lx, ly) }
        }
        // Ribbon glow layers
        listOf(30f, 18f, 8f, 3f).forEachIndexed { idx, strokeW ->
            val frac = idx.toFloat() / 4f
            drawPath(outerPath, brush = Brush.sweepGradient(
                listOf(colorA.copy(alpha = glowPulse * (0.15f + frac * 0.1f)),
                    colorB.copy(alpha = glowPulse * (0.15f + frac * 0.1f)))
            ), style = Stroke(width = strokeW, cap = StrokeCap.Round))
        }
        // Sharp core ribbon
        drawPath(outerPath, brush = Brush.sweepGradient(listOf(colorA, colorB, colorA)),
            style = Stroke(width = 2f, cap = StrokeCap.Round))

        // HUD decoration fragments (Image 5 has floating text/code panels)
        drawHudFragment(Offset(size.width * 0.05f, size.height * 0.15f), colorB, 0.3f)
        drawHudFragment(Offset(size.width * 0.7f, size.height * 0.7f), colorB, 0.25f)
    }
}

private fun DrawScope.drawHudFragment(pos: Offset, color: Color, alpha: Float) {
    drawRect(color.copy(alpha = alpha), pos, Size(100f, 60f), style = Stroke(1f))
    for (i in 1..4) drawLine(color.copy(alpha = alpha * 0.6f),
        Offset(pos.x + 6f, pos.y + i * 12f), Offset(pos.x + 80f, pos.y + i * 12f), 0.8f)
}
