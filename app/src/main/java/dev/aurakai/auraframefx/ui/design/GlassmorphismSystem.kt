package dev.aurakai.auraframefx.ui.design

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.*

// ═══════════════════════════════════════════════════════════════════════════
// 🔮 REGENESIS GLASSMORPHISM DESIGN SYSTEM
// ═══════════════════════════════════════════════════════════════════════════
//
// LEVEL HIERARCHY:
//   L1 — Sovereign Gate Cards    → portrait 1080×1080 AAA art (ExodusHUD)
//   L2 — Hub Screens             → landscape animated bg + sub-gate cards
//   L3 — Tool Screens            → glassmorphism panels, menus, modals
//
// REFERENCE IMAGES:
//   Image 1 — Holographic notched MENU frame      → GlassMenuFrame
//   Image 2 — Neon analytics dashboard bg         → HubAnimatedBackground
//   Image 3 — Black glassmorphism UI elements     → GlassWindow, GlassListItem
//   Image 4 — Neon pill buttons, cards, modals    → GlassButton, GlassModal
//   Image 5 — Frosted glass card                  → GlassWindow (surfaceMid)
//   Image 6 — Volumetric light cube               → used in hub bg node rendering
//
// ═══════════════════════════════════════════════════════════════════════════


// ─────────────────────────────────────────────────────────────────────────
// GLASS DESIGN TOKENS  —  single source of truth
// ─────────────────────────────────────────────────────────────────────────

object GlassTokens {

    // Surface fills (matches Image 3 & 5 — dark frosted glass)
    val surfaceDeep      = Color(0x12FFFFFF)  //  7% white — deepest glass
    val surfaceDark      = Color(0x1AFFFFFF)  // 10% white — window glass
    val surfaceMid       = Color(0x26FFFFFF)  // 15% white — card glass
    val surfaceLight     = Color(0x33FFFFFF)  // 20% white — highlighted glass
    val surfaceHighlight = Color(0x08FFFFFF)  //  3% white — top sheen layer

    // Per-agent border gradients
    fun borderBrush(accentColor: Color) = Brush.linearGradient(
        colors = listOf(
            accentColor.copy(alpha = 0.9f),
            accentColor.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.15f),
            accentColor.copy(alpha = 0.5f),
        )
    )

    // Silver rim — black glass aesthetic (Image 3)
    val silverRim = Brush.linearGradient(
        colors = listOf(
            Color(0x66FFFFFF),
            Color(0x22FFFFFF),
            Color(0x44FFFFFF),
            Color(0x11FFFFFF),
        )
    )

    // Corner radii
    val cornerWindow : Dp = 16.dp
    val cornerCard   : Dp = 12.dp
    val cornerButton : Dp = 50.dp   // pill
    val cornerChip   : Dp = 8.dp
    val cornerModal  : Dp = 20.dp
}


// ─────────────────────────────────────────────────────────────────────────
// GLASS WINDOW  —  base container for all L3 panels (Images 3, 4, 5)
// ─────────────────────────────────────────────────────────────────────────

/**
 * 🔮 GlassWindow
 *
 * The universal glassmorphism container. Every tool screen panel,
 * settings section, and content card uses this as its outer shell.
 *
 * Renders:
 *  • Dark semi-transparent fill (frosted glass body)
 *  • Animated diagonal sheen sweep (Image 3 — reflective highlight)
 *  • Agent-coloured neon border gradient
 *  • Subtle scanline texture overlay
 */
@Composable
fun GlassWindow(
    modifier: Modifier = Modifier,
    accentColor: Color = NeonCyan,
    cornerRadius: Dp = GlassTokens.cornerWindow,
    showSheen: Boolean = true,
    showScanlines: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    val infiniteTransition = rememberInfiniteTransition(label = "glass")
    val sheenOffset by infiniteTransition.animateFloat(
        initialValue = -300f, targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "sheen"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(GlassTokens.surfaceDark)
            .border(1.dp, GlassTokens.borderBrush(accentColor), shape)
            .drawBehind {
                // Top-edge highlight sheen (always on)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(0.09f), Color.Transparent),
                        endY = 56f
                    )
                )
                // Animated diagonal sheen sweep (Image 3 reflective glass)
                if (showSheen) {
                    val sweep = Path().apply {
                        moveTo(sheenOffset, 0f)
                        lineTo(sheenOffset + 80f, 0f)
                        lineTo(sheenOffset + 10f, size.height)
                        lineTo(sheenOffset - 70f, size.height)
                        close()
                    }
                    drawPath(
                        path = sweep,
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(0.07f), Color.Transparent)
                        )
                    )
                }
                // Fine scanline texture (Image 3 — black glass texture)
                if (showScanlines) {
                    var y = 0f
                    while (y < size.height) {
                        drawLine(Color.Black.copy(0.035f), Offset(0f, y), Offset(size.width, y), 1f)
                        y += 3f
                    }
                }
            }
    ) {
        content()
    }
}


// ─────────────────────────────────────────────────────────────────────────
// GLASS SUB-GATE CARD  —  L2 landscape cards inside hub screens
// ─────────────────────────────────────────────────────────────────────────

/**
 * 🃏 GlassSubGateCard
 *
 * Small landscape card used in Level-2 hub screens.
 * Features animated glow pulse border + cyber corner marks.
 */
@Composable
fun GlassSubGateCard(
    title: String,
    subtitle: String,
    accentColor: Color = NeonCyan,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cardGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "glow"
    )

    val shape = RoundedCornerShape(GlassTokens.cornerCard)
    val cornerLen = 18f

    Box(
        modifier = modifier
            .clip(shape)
            .background(GlassTokens.surfaceMid)
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        accentColor.copy(glowAlpha),
                        accentColor.copy(glowAlpha * 0.25f),
                        Color.White.copy(0.08f),
                    )
                ),
                shape
            )
            .drawBehind {
                val c = accentColor.copy(alpha = 0.85f)
                // Cyber corner bracket marks
                drawLine(c, Offset(0f, cornerLen), Offset(0f, 0f), 2f)
                drawLine(c, Offset(0f, 0f), Offset(cornerLen, 0f), 2f)
                drawLine(c, Offset(size.width - cornerLen, 0f), Offset(size.width, 0f), 2f)
                drawLine(c, Offset(size.width, 0f), Offset(size.width, cornerLen), 2f)
                drawLine(c, Offset(0f, size.height - cornerLen), Offset(0f, size.height), 2f)
                drawLine(c, Offset(0f, size.height), Offset(cornerLen, size.height), 2f)
                drawLine(c, Offset(size.width, size.height - cornerLen), Offset(size.width, size.height), 2f)
                drawLine(c, Offset(size.width - cornerLen, size.height), Offset(size.width, size.height), 2f)
            }
    ) {
        // Top sheen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(
                    Brush.verticalGradient(listOf(Color.White.copy(0.06f), Color.Transparent))
                )
                .align(Alignment.TopCenter)
        )
        content()
    }
}


// ─────────────────────────────────────────────────────────────────────────
// GLASS MENU FRAME  —  holographic notched frame (Image 1)
// ─────────────────────────────────────────────────────────────────────────

/**
 * 🎛️ GlassMenuFrame
 *
 * The cyberpunk holographic menu panel with angled cuts at corners.
 * Matches Image 1: blue/purple neon, hourglass-notched side edges.
 * Used for nav drawers, context menus, settings panels.
 */
@Composable
fun GlassMenuFrame(
    accentColor: Color = NeonBlue,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "menuPulse")
    val borderPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "pulse"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "glow"
    )

    Box(
        modifier = modifier
            .wrapContentSize()
            .drawBehind {
                val notch = 28f
                val sideIndent = 18f   // the hourglass waist (Image 1 side cuts)
                val w = size.width
                val h = size.height

                // Outer notched path — matches Image 1 shape
                val outerPath = Path().apply {
                    moveTo(notch, 0f)
                    lineTo(w - notch, 0f)
                    lineTo(w, notch)
                    // Top-right to bottom-right with side indent
                    lineTo(w, h * 0.35f)
                    lineTo(w - sideIndent, h * 0.45f)       // waist in
                    lineTo(w, h * 0.55f)
                    lineTo(w, h - notch)
                    lineTo(w - notch, h)
                    lineTo(notch, h)
                    lineTo(0f, h - notch)
                    // Bottom-left to top-left with side indent
                    lineTo(0f, h * 0.55f)
                    lineTo(sideIndent, h * 0.45f)            // waist in
                    lineTo(0f, h * 0.35f)
                    lineTo(0f, notch)
                    close()
                }

                // Outer glow (purple/blue ambient — Image 1)
                drawPath(
                    outerPath,
                    color = accentColor.copy(alpha = 0.12f * glowScale),
                    style = Stroke(width = 18f)
                )
                // Secondary glow (cyan inner)
                drawPath(
                    outerPath,
                    color = NeonCyan.copy(alpha = 0.06f * borderPulse),
                    style = Stroke(width = 8f)
                )
                // Main crisp border
                drawPath(
                    outerPath,
                    brush = Brush.linearGradient(
                        listOf(
                            NeonCyan.copy(alpha = borderPulse),
                            accentColor.copy(alpha = 0.6f * borderPulse),
                            Color.White.copy(alpha = 0.2f),
                            accentColor.copy(alpha = 0.7f * borderPulse),
                        )
                    ),
                    style = Stroke(width = 1.5f)
                )
                // Fill
                drawPath(outerPath, color = Color(0x1A000814))
            }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            content()
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────
// GLASS MODAL  —  floating dialog (Image 4 — stacked card modal)
// ─────────────────────────────────────────────────────────────────────────

@Composable
fun GlassModal(
    title: String,
    accentColor: Color = NeonCyan,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    // Stacked depth effect (Image 4 — cards behind modal)
    Box(modifier = modifier) {
        // Shadow card 2
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 6.dp, y = 6.dp)
                .clip(RoundedCornerShape(GlassTokens.cornerModal))
                .background(Color.White.copy(0.03f))
                .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(GlassTokens.cornerModal))
        )
        // Shadow card 1
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 3.dp, y = 3.dp)
                .clip(RoundedCornerShape(GlassTokens.cornerModal))
                .background(Color.White.copy(0.05f))
                .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(GlassTokens.cornerModal))
        )
        // Main modal
        GlassWindow(
            modifier = Modifier.width(320.dp).wrapContentHeight(),
            accentColor = accentColor,
            cornerRadius = GlassTokens.cornerModal
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween, Alignment.CenterVertically
                ) {
                    Text(
                        title.uppercase(), color = accentColor,
                        fontSize = 13.sp, letterSpacing = 3.sp
                    )
                    if (onDismiss != null) {
                        Box(
                            Modifier.size(8.dp)
                                .background(accentColor.copy(0.6f), RoundedCornerShape(4.dp))
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(accentColor.copy(0.3f)))
                Spacer(Modifier.height(16.dp))
                content()
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────
// GLASS BUTTON  —  neon pill buttons (Image 4)
// ─────────────────────────────────────────────────────────────────────────

@Composable
fun GlassButton(
    text: String,
    accentColor: Color = NeonCyan,
    isPrimary: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val shape = RoundedCornerShape(GlassTokens.cornerButton)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(
                if (isPrimary)
                    Brush.horizontalGradient(listOf(accentColor.copy(0.4f), accentColor.copy(0.15f)))
                else
                    Brush.horizontalGradient(listOf(Color.White.copy(0.07f), Color.White.copy(0.03f)))
            )
            .border(
                1.dp,
                if (isPrimary)
                    Brush.horizontalGradient(listOf(accentColor, accentColor.copy(0.45f)))
                else
                    Brush.horizontalGradient(listOf(Color.White.copy(0.25f), Color.White.copy(0.08f))),
                shape
            )
            .drawBehind {
                // Top sheen on button (Image 4 — glassy pill highlight)
                drawRect(
                    Brush.verticalGradient(listOf(Color.White.copy(0.12f), Color.Transparent), endY = size.height * 0.5f)
                )
            }
            .padding(horizontal = 28.dp)
    ) {
        Text(
            text.uppercase(),
            color = if (isPrimary) accentColor else Color.White.copy(0.7f),
            fontSize = 11.sp, letterSpacing = 2.sp
        )
    }
}


// ─────────────────────────────────────────────────────────────────────────
// GLASS LIST ITEM  —  menu rows (Image 3 — black glass rows)
// ─────────────────────────────────────────────────────────────────────────

@Composable
fun GlassListItem(
    label: String,
    subtitle: String? = null,
    accentColor: Color = NeonCyan,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(GlassTokens.cornerChip)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(GlassTokens.surfaceDeep)
            .border(1.dp, GlassTokens.silverRim, shape)
            .drawBehind {
                // Silver rim top highlight (Image 3)
                drawRect(
                    Brush.verticalGradient(listOf(Color.White.copy(0.06f), Color.Transparent), endY = 28f)
                )
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        leadingContent?.invoke()
        Column(Modifier.weight(1f)) {
            Text(label, color = Color.White.copy(0.9f), fontSize = 14.sp, letterSpacing = 0.5.sp)
            if (subtitle != null)
                Text(subtitle, color = accentColor.copy(0.5f), fontSize = 11.sp, letterSpacing = 1.sp)
        }
        trailingContent?.invoke()
    }
}


// ─────────────────────────────────────────────────────────────────────────
// HUB ANIMATED BACKGROUND  —  L2 procedural canvas bg (Image 2)
// ─────────────────────────────────────────────────────────────────────────

/**
 * 📊 HubAnimatedBackground
 *
 * Level-2 hub screen animated background — fully procedural, no images needed.
 * Renders a converging perspective grid, neon data stream lines,
 * pulsing circuit nodes, and volumetric corner brackets.
 *
 * Inspired by: Image 2 (cyan/pink analytics dashboard) + Image 6 (light cube volume)
 *
 * Pass the agent's accentColor to theme it per domain.
 */
@Composable
fun HubAnimatedBackground(
    accentColor: Color = NeonCyan,
    secondaryColor: Color = NeonPink,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "hubBg")

    val flow1   by infinite.animateFloat(0f, 1f, infiniteRepeatable(tween(3000, easing = LinearEasing)), "f1")
    val flow2   by infinite.animateFloat(0f, 1f, infiniteRepeatable(tween(4200, easing = LinearEasing)), "f2")
    val pulse   by infinite.animateFloat(0.2f, 1f, infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse), "p")
    val gridT   by infinite.animateFloat(0f, 1f, infiniteRepeatable(tween(5000, easing = LinearEasing)), "g")
    val cubeGlow by infinite.animateFloat(0f, 1f, infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse), "c")

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width; val h = size.height

        // ── Deep space gradient base ──
        drawRect(
            Brush.radialGradient(
                listOf(accentColor.copy(0.07f), Color(0xFF030810)),
                center = Offset(w * 0.5f, h * 0.38f),
                radius = w * 0.75f
            )
        )

        // ── Perspective grid (Image 2 — converging floor grid) ──
        val vX = w / 2f; val vY = h * 0.32f
        val gridAlpha = 0.13f
        for (i in 0..14) {
            val t = ((i.toFloat() / 14f) + gridT) % 1f
            val tSq = t * t
            val y = vY + (h - vY) * tSq
            val spread = w * 0.62f * t
            drawLine(accentColor.copy(gridAlpha * t), Offset(vX - spread, y), Offset(vX + spread, y), 1f)
        }
        for (i in -7..7) {
            val t = i.toFloat() / 7f
            drawLine(accentColor.copy(gridAlpha * 0.6f), Offset(vX, vY), Offset(vX + w * 0.62f * t, h), 1f)
        }

        // ── Horizontal data stream lines — cyan (Image 2 left→right) ──
        for (i in 0..5) {
            val y = h * (0.4f + i * 0.11f)
            val offset = (flow1 + i * 0.18f) % 1f
            val x = -200f + offset * (w + 400f)
            val len = 100f + i * 50f
            drawLine(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, accentColor.copy(if (i % 2 == 0) 0.7f else 0.35f), Color.Transparent),
                    startX = x - len, endX = x + len
                ),
                Offset(x - len, y), Offset(x + len, y),
                if (i % 2 == 0) 1.5f else 0.8f
            )
        }

        // ── Horizontal data stream lines — pink (Image 2 right→left) ──
        for (i in 0..3) {
            val y = h * (0.48f + i * 0.14f)
            val offset = (1f - flow2 + i * 0.25f) % 1f
            val x = -180f + offset * (w + 360f)
            val len = 80f + i * 35f
            drawLine(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, secondaryColor.copy(0.45f), Color.Transparent),
                    startX = x - len, endX = x + len
                ),
                Offset(x - len, y), Offset(x + len, y), 0.8f
            )
        }

        // ── Pulsing circuit nodes (Image 2 + Image 6 volumetric feel) ──
        val nodes = listOf(
            Offset(w * 0.18f, h * 0.62f), Offset(w * 0.38f, h * 0.78f),
            Offset(w * 0.5f,  h * 0.68f), Offset(w * 0.62f, h * 0.78f),
            Offset(w * 0.82f, h * 0.62f),
        )
        // Connect nodes with data lines
        for (i in 0 until nodes.size - 1) {
            drawLine(accentColor.copy(0.15f * pulse), nodes[i], nodes[i + 1], 0.8f)
        }
        nodes.forEachIndexed { idx, pos ->
            val p = (pulse + idx * 0.22f) % 1f
            drawCircle(accentColor, 2.5f, pos)
            drawCircle(accentColor.copy(p * 0.35f), 7f + p * 14f, pos)
            drawCircle(accentColor.copy(p * 0.12f), 18f + p * 24f, pos)
        }

        // ── Volumetric ambient cube glow (Image 6 — white light volume) ──
        val cx = w * 0.5f; val cy = h * 0.25f
        val cubeR = h * 0.22f * (0.9f + cubeGlow * 0.1f)
        drawCircle(
            Brush.radialGradient(
                listOf(Color.White.copy(0.04f * cubeGlow), Color.Transparent),
                center = Offset(cx, cy), radius = cubeR
            ), cubeR, Offset(cx, cy)
        )

        // ── HUD corner brackets ──
        val b = 28f; val bw = 2f; val bc = accentColor.copy(0.45f)
        drawLine(bc, Offset(0f, b), Offset(0f, 0f), bw)
        drawLine(bc, Offset(0f, 0f), Offset(b, 0f), bw)
        drawLine(bc, Offset(w - b, 0f), Offset(w, 0f), bw)
        drawLine(bc, Offset(w, 0f), Offset(w, b), bw)
        drawLine(bc, Offset(0f, h - b), Offset(0f, h), bw)
        drawLine(bc, Offset(0f, h), Offset(b, h), bw)
        drawLine(bc, Offset(w, h - b), Offset(w, h), bw)
        drawLine(bc, Offset(w - b, h), Offset(w, h), bw)
    }
}


// ─────────────────────────────────────────────────────────────────────────
// GLASS SCREEN SCAFFOLD  —  wraps every L3 tool screen
// ─────────────────────────────────────────────────────────────────────────

/**
 * 🖥️ GlassScreenScaffold
 *
 * The complete Level-3 screen template.
 * Every tool screen (ROMFlasher, ThemeEngine, AgentMonitor, etc.) uses this.
 *
 * Provides:
 *   • HubAnimatedBackground with agent-themed colors
 *   • Glass title bar with back button
 *   • Scrollable content area with proper padding
 */
@Composable
fun GlassScreenScaffold(
    title: String,
    subtitle: String? = null,
    accentColor: Color = NeonCyan,
    secondaryColor: Color = NeonPink,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(Color(0xFF030810))) {

        HubAnimatedBackground(
            accentColor = accentColor,
            secondaryColor = secondaryColor,
            modifier = Modifier.fillMaxSize()
        )

        Column(Modifier.fillMaxSize()) {

            // ── Glass title bar ──
            GlassWindow(
                modifier = Modifier.fillMaxWidth().height(68.dp),
                accentColor = accentColor,
                cornerRadius = 0.dp,
                showScanlines = false
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (onBack != null) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(accentColor.copy(0.1f))
                                .border(1.dp, accentColor.copy(0.35f), RoundedCornerShape(8.dp))
                        ) { Text("←", color = accentColor, fontSize = 18.sp) }
                    }
                    Column(Modifier.weight(1f)) {
                        Text(title.uppercase(), color = Color.White, fontSize = 15.sp, letterSpacing = 3.sp)
                        if (subtitle != null)
                            Text(subtitle.uppercase(), color = accentColor.copy(0.6f), fontSize = 10.sp, letterSpacing = 2.sp)
                    }
                    // Live status dot
                    Box(Modifier.size(7.dp).background(accentColor, RoundedCornerShape(4.dp)))
                }
            }

            // ── Content ──
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) { content() }
        }
    }
}
