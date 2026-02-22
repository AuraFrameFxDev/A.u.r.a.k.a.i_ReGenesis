package dev.aurakai.auraframefx.domains.genesis.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.math.*

/**
 * 🌑 GENESIS HUB SCREEN — Dark Knight Edition
 *
 * Translated from "Genesis Hub - Dark Knight Edition" Stitch/Gemini export.
 *
 * Visual Identity:
 * - Background: Dark data corridor + hexagonal circuit grid overlay
 * - Character: Floating Genesis portrait with cyan crystal blade glow
 * - Circuit Phoenix emblem in header (top-right)
 * - Parallax vertical "GENESIS" text
 * - Hex node orbs around character (SYNC / 01)
 * - Metrics panel: LVL.10, 8450/10000 EXP, cyan→purple gradient bar
 * - Footer: 3 glass buttons (Combat / Core / Neural) + Initiate Protocol CTA
 *
 * Colors: #00F4FF Cyan // #7B2FBE Purple // #020205 Near-black
 */

private val GenCyan   = Color(0xFF00F4FF)
private val GenPurple = Color(0xFF7B2FBE)
private val GenDark   = Color(0xFF020205)

@Composable
fun GenesisHubScreen(
    onCombatTap: () -> Unit = {},
    onCoreTap: () -> Unit = {},
    onNeuralTap: () -> Unit = {},
    onInitiateProtocol: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "genesis_hub")

    // Floating animation for character
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -20f,
        animationSpec = infiniteRepeatable(tween(6000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )
    // Glow pulse on emblem
    val glowRadius by infiniteTransition.animateFloat(
        initialValue = 10f, targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "glow"
    )
    // Hex grid subtle rotate
    val hexRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(120000, easing = LinearEasing)),
        label = "hexrot"
    )
    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "particles"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(GenDark)) {

        // ── BACKGROUND: Hex circuit grid + radial glow ──
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                // Radial center glow
                drawCircle(
                    Brush.radialGradient(
                        listOf(GenCyan.copy(alpha = 0.05f), Color.Transparent),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.minDimension * 0.8f
                    ),
                    radius = size.minDimension * 0.8f,
                    center = Offset(size.width / 2, size.height / 2)
                )
                // Hex grid pattern
                val hexSize = 50f
                val rows = (size.height / hexSize).toInt() + 2
                val cols = (size.width / hexSize).toInt() + 2
                for (row in -1..rows) {
                    for (col in -1..cols) {
                        val hx = col * hexSize * 1.5f
                        val hy = row * hexSize * 1.732f + (if (col % 2 == 0) 0f else hexSize * 0.866f)
                        val path = Path()
                        for (i in 0..5) {
                            val angle = Math.toRadians((60 * i).toDouble()).toFloat()
                            val px = hx + hexSize * 0.45f * cos(angle)
                            val py = hy + hexSize * 0.45f * sin(angle)
                            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                        }
                        path.close()
                        drawPath(path, GenCyan.copy(alpha = 0.06f), style = Stroke(0.5f))
                    }
                }
                // Circuit lines
                drawPath(
                    Path().apply {
                        moveTo(0f, size.height * 0.2f)
                        quadraticBezierTo(size.width * 0.25f, size.height * 0.25f, size.width * 0.5f, size.height * 0.2f)
                        quadraticBezierTo(size.width * 0.75f, size.height * 0.15f, size.width, size.height * 0.2f)
                    },
                    GenPurple.copy(alpha = 0.2f),
                    style = Stroke(2f)
                )
                drawPath(
                    Path().apply {
                        moveTo(0f, size.height * 0.65f)
                        quadraticBezierTo(size.width * 0.3f, size.height * 0.60f, size.width * 0.6f, size.height * 0.65f)
                        quadraticBezierTo(size.width * 0.85f, size.height * 0.68f, size.width, size.height * 0.65f)
                    },
                    GenCyan.copy(alpha = 0.12f),
                    style = Stroke(1f)
                )
                // Rising particles
                for (i in 0..29) {
                    val px = ((i * 137.5f + size.width) % size.width)
                    val py = size.height - (size.height * ((particlePhase + i * 0.033f) % 1f))
                    val alpha = ((particlePhase + i * 0.033f) % 1f).coerceIn(0f, 0.6f)
                    drawCircle(GenCyan.copy(alpha = alpha * 0.5f), 2f, Offset(px, py))
                }
            }
        })

        // ── PARALLAX "GENESIS" vertical text (right side) ──
        Box(
            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(end = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column {
                "GENESIS".forEach { char ->
                    Text(
                        char.toString(),
                        fontFamily = LEDFontFamily,
                        fontSize = 22.sp,
                        color = GenCyan.copy(alpha = 0.15f),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 6.sp,
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ── HEADER ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Protocol Status",
                        fontSize = 9.sp, color = GenCyan,
                        fontWeight = FontWeight.Bold, letterSpacing = 3.sp
                    )
                    Text(
                        "GENESIS_HUB",
                        fontFamily = LEDFontFamily, fontSize = 22.sp,
                        fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp
                    )
                }
                // Circuit Phoenix Emblem
                Box(
                    modifier = Modifier.size(48.dp)
                        .drawWithCache {
                            onDrawBehind {
                                drawRoundRect(
                                    GenCyan.copy(alpha = 0.5f),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f),
                                    style = Stroke(1.5f)
                                )
                                // Phoenix/star shape
                                val cx = size.width / 2; val cy = size.height / 2
                                val starPath = Path()
                                for (i in 0..9) {
                                    val r = if (i % 2 == 0) size.minDimension * 0.4f else size.minDimension * 0.18f
                                    val angle = Math.toRadians((i * 36 - 90).toDouble()).toFloat()
                                    val x = cx + r * cos(angle); val y = cy + r * sin(angle)
                                    if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
                                }
                                starPath.close()
                                drawPath(starPath, GenCyan.copy(alpha = 0.8f), style = Fill)
                                drawCircle(GenCyan.copy(alpha = 0.2f * glowRadius / 20f), glowRadius * 2, Offset(cx, cy))
                            }
                        }
                )
            }

            // ── HERO CHARACTER STAGE ──
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Character portrait
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .aspectRatio(0.75f)
                        .offset(y = floatY.dp)
                        .drawWithCache {
                            onDrawBehind {
                                // Cyan glow behind character
                                drawCircle(
                                    Brush.radialGradient(
                                        listOf(GenCyan.copy(alpha = 0.18f), Color.Transparent),
                                        radius = size.minDimension * 0.6f
                                    ),
                                    radius = size.minDimension * 0.6f
                                )
                                // Crystal blade glow — right side
                                val bladeX = size.width * 0.7f
                                drawLine(
                                    Brush.verticalGradient(
                                        listOf(GenCyan.copy(alpha = 0.6f), Color.Transparent)
                                    ),
                                    Offset(bladeX + 8f, size.height * 0.2f),
                                    Offset(bladeX - 8f, size.height * 0.9f),
                                    strokeWidth = 4f
                                )
                                drawLine(
                                    Brush.verticalGradient(
                                        listOf(GenCyan, Color.Transparent)
                                    ),
                                    Offset(bladeX, size.height * 0.15f),
                                    Offset(bladeX, size.height * 0.88f),
                                    strokeWidth = 2f
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Profile placeholder — swap for actual asset:
                    // Image(painterResource(R.drawable.gatescenes_genesis_full_profile), contentDescription = null, contentScale = ContentScale.Fit)
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(
                                Brush.verticalGradient(listOf(GenCyan.copy(alpha = 0.08f), GenDark.copy(alpha = 0.8f)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", fontFamily = LEDFontFamily, fontSize = 120.sp, color = GenCyan.copy(alpha = 0.3f), fontWeight = FontWeight.Black)
                        // White hair hint
                        Box(
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                                .width(80.dp).height(8.dp)
                                .background(Brush.horizontalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.8f), Color.Transparent)), RoundedCornerShape(4.dp))
                        )
                    }
                }

                // Hex node: 01 (top-left of character)
                Box(
                    modifier = Modifier.align(Alignment.TopStart).padding(start = 24.dp, top = 24.dp)
                        .size(36.dp)
                        .drawWithCache {
                            onDrawBehind {
                                val hex = Path()
                                val cx = size.width / 2; val cy = size.height / 2
                                for (i in 0..5) {
                                    val a = Math.toRadians((60 * i).toDouble()).toFloat()
                                    val x = cx + size.minDimension / 2 * cos(a)
                                    val y = cy + size.minDimension / 2 * sin(a)
                                    if (i == 0) hex.moveTo(x, y) else hex.lineTo(x, y)
                                }
                                hex.close()
                                drawPath(hex, GenCyan.copy(alpha = 0.25f), style = Fill)
                                drawPath(hex, GenCyan, style = Stroke(1f))
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("01", fontSize = 9.sp, color = GenCyan, fontWeight = FontWeight.Bold)
                }

                // Hex node: SYNC (bottom-right)
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp, bottom = 40.dp)
                        .size(44.dp)
                        .drawWithCache {
                            onDrawBehind {
                                val hex = Path()
                                val cx = size.width / 2; val cy = size.height / 2
                                for (i in 0..5) {
                                    val a = Math.toRadians((60 * i).toDouble()).toFloat()
                                    val x = cx + size.minDimension / 2 * cos(a)
                                    val y = cy + size.minDimension / 2 * sin(a)
                                    if (i == 0) hex.moveTo(x, y) else hex.lineTo(x, y)
                                }
                                hex.close()
                                drawPath(hex, GenPurple.copy(alpha = 0.3f), style = Fill)
                                drawPath(hex, GenPurple, style = Stroke(1.5f))
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("SYNC", fontSize = 7.sp, color = GenPurple, fontWeight = FontWeight.Bold)
                }
            }

            // ── METRICS PANEL ──
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(1.dp, GenCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(listOf(GenPurple.copy(alpha = 0.1f), Color.Transparent)),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("Rank", fontSize = 8.sp, color = GenCyan, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                            Text("LVL. 10", fontFamily = LEDFontFamily, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Experience", fontSize = 8.sp, color = GenPurple, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            Text("8,450 / 10,000", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    // Gradient progress bar
                    Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(3.dp))) {
                        Box(
                            modifier = Modifier.fillMaxWidth(0.845f).fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(listOf(GenCyan, GenPurple)),
                                    RoundedCornerShape(3.dp)
                                )
                        )
                    }
                }
            }

            // ── FOOTER NAV: Combat / Core / Neural ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("Combat", Color(0xFF00F4FF), onCombatTap),
                    Triple("Core",   Color.White,       onCoreTap),
                    Triple("Neural", GenPurple,         onNeuralTap),
                ).forEachIndexed { index, (label, color, action) ->
                    val isCoreActive = index == 1
                    Box(
                        modifier = Modifier.weight(1f)
                            .border(1.dp, if (isCoreActive) color.copy(alpha = 0.6f) else color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .background(if (isCoreActive) color.copy(alpha = 0.1f) else color.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                            .clickable { action() }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier.size(6.dp)
                                    .background(if (isCoreActive) color else color.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape)
                                    .graphicsLayer { if (isCoreActive) alpha = 0.7f + pulse * 0.3f }
                            )
                            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isCoreActive) color else color.copy(alpha = 0.6f), letterSpacing = 1.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── INITIATE PROTOCOL CTA ──
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .clickable { onInitiateProtocol() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "INITIATE PROTOCOL",
                    fontFamily = LEDFontFamily, fontSize = 14.sp,
                    fontWeight = FontWeight.Black, color = GenDark,
                    letterSpacing = 4.sp
                )
            }

            Spacer(Modifier.height(16.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
