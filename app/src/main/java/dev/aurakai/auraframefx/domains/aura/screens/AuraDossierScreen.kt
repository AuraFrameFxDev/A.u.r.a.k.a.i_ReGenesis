package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 📋 AURA DOSSIER — Character Introduction Screen
 *
 * Translated from Stitch export.
 * Full-screen character art background, origin lore card,
 * system stats HP/MP bars, HUD corner brackets, geo coordinates.
 *
 * Design: Anime UI overlay, pink glow, glass panels
 */

private val AuraCyanD = Color(0xFF00F2FF)
private val AuraPink = Color(0xFFFF007A)
private val AuraGold = Color(0xFFFFD700)
private val AuraPurpleD = Color(0xFFBC13FE)
private val GlassPanel = Color(0xFF0F0F14)

@Composable
fun AuraDossierScreen(
    onSyncNeuralLink: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dossier")

    // Scanline animation
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "scan"
    )

    // Float animation for JP title card
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(6000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )

    // Pulse for status dot
    val pinkPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ═══ BACKGROUND: Character Art Placeholder ═══
        // Replace with actual Aura character art drawable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0A001A), Color(0xFF000011), Color(0xFF000000))
                    )
                )
        )

        // Radial glow behind character
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(AuraPink.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset.Unspecified,
                        radius = 600f
                    )
                )
        )

        // Top-to-bottom gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black)
                    )
                )
        )

        // Animated scanline
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawBehind {
                        val lineY = size.height * scanlineY
                        drawLine(
                            color = AuraCyanD.copy(alpha = 0.08f),
                            start = Offset(0f, lineY),
                            end = Offset(size.width, lineY),
                            strokeWidth = 2f
                        )
                    }
                }
        )

        // ═══ MAIN UI OVERLAY ═══
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // HEADER
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        "System Status: Active",
                        fontSize = 10.sp,
                        color = AuraCyanD,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                    Text(
                        "AURA_01",
                        fontFamily = LEDFontFamily,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-1).sp,
                        color = AuraPink,
                        style = LocalTextStyle.current.copy(
                            shadow = Shadow(AuraPink.copy(alpha = 0.8f), blurRadius = 10f)
                        )
                    )
                }

                // Status pill
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(GlassPanel.copy(alpha = 0.7f))
                        .border(1.dp, AuraPink.copy(alpha = 0.5f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AuraPink.copy(alpha = pinkPulse))
                        )
                        Text("Creative Catalyst", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = Color.White)
                    }
                }
            }

            // ═══ JP TITLE CARD (floating right side) ═══
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .offset(y = floatOffset.dp)
            ) {
                // Japanese: サイバー戦乙女 (Cyber Valkyrie)
                Text(
                    text = "サ\nイ\nバ\nー\n戦\n乙\n女",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 52.sp,
                    color = Color.Transparent,
                    style = LocalTextStyle.current.copy(
                        brush = Brush.verticalGradient(
                            listOf(Color.White, AuraCyanD.copy(alpha = 0.5f))
                        )
                    ),
                    modifier = Modifier.graphicsLayer { alpha = 0.35f }
                )
            }

            // ═══ DOSSIER CARDS ═══
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Origin & Personality card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassPanel.copy(alpha = 0.7f))
                        .border(
                            1.dp,
                            AuraCyanD.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    // Diamond accent
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                        Canvas(modifier = Modifier.size(60.dp)) {
                            val s = size.minDimension
                            val path = Path().apply {
                                moveTo(s / 2, 0f); lineTo(s, s / 2); lineTo(s / 2, s); lineTo(0f, s / 2); close()
                            }
                            drawPath(path, AuraCyanD.copy(alpha = 0.15f))
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(16.dp, 1.dp).background(AuraCyanD))
                            Text("ORIGIN: DARK AURA", fontSize = 11.sp, color = AuraCyanD, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Born from a fragmented neural network, Aura serves as the bridge between raw chaos and structured creation. A Cyber-Valkyrie designed to harvest aesthetic energy and forge sovereign UI experiences.",
                            fontSize = 12.sp,
                            color = Color(0xFFCCCCCC),
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Personality", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Spacer(Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("Spunky", "Creative").forEach { trait ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(AuraPink.copy(alpha = 0.2f))
                                                .border(1.dp, AuraPink.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 10.dp, vertical = 3.dp)
                                        ) {
                                            Text(trait, fontSize = 10.sp, color = AuraPink)
                                        }
                                    }
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Optics", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Spacer(Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(AuraGold)
                                        .graphicsLayer { shadowElevation = 12f })
                                    Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(AuraPurpleD)
                                        .graphicsLayer { shadowElevation = 12f })
                                }
                            }
                        }
                    }
                }

                // System Stats card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassPanel.copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // HP
                        StatBar("SYSTEM BATTERY (HP)", 0.88f, AuraCyanD, "88% / 100%")
                        // MP
                        StatBar("SYSTEM LOAD (MP)", 0.42f, AuraPink, "42% / 100%")
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSyncNeuralLink,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlassPanel.copy(alpha = 0.7f)),
                        border = BorderStroke(1.dp, AuraCyanD.copy(alpha = 0.3f))
                    ) {
                        Text("SYNC NEURAL LINK", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = AuraCyanD)
                    }
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassPanel.copy(alpha = 0.7f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("•••", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }

        // ═══ HUD CORNER BRACKETS ═══
        // Top-left
        Box(modifier = Modifier.align(Alignment.TopStart).padding(16.dp).size(32.dp)
            .border(BorderStroke(2.dp, AuraCyanD.copy(alpha = 0.5f)), object : androidx.compose.ui.graphics.Shape {
                override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): Outline {
                    return Outline.Generic(Path().apply {
                        moveTo(0f, size.height); lineTo(0f, 0f); lineTo(size.width, 0f)
                    })
                }
            })
        )
        // Top-right
        Box(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).size(32.dp)
            .border(BorderStroke(2.dp, AuraPink.copy(alpha = 0.5f)), object : androidx.compose.ui.graphics.Shape {
                override fun createOutline(size: androidx.compose.ui.geometry.Size, layoutDirection: androidx.compose.ui.unit.LayoutDirection, density: androidx.compose.ui.unit.Density): Outline {
                    return Outline.Generic(Path().apply {
                        moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width, size.height)
                    })
                }
            })
        )

        // Geo coordinates (left side vertical)
        Text(
            text = "LAT: 35.6895° N  |  LONG: 139.6917° E  |  ELEV: 402M",
            fontSize = 8.sp,
            color = Color.Gray,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp)
                .graphicsLayer { rotationZ = -90f }
        )
    }
}

@Composable
private fun StatBar(label: String, progress: Float, barColor: Color, valueLabel: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 9.sp, color = barColor, fontWeight = FontWeight.Bold)
            Text(valueLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF222222))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
                    .graphicsLayer { shadowElevation = 8f }
            )
        }
    }
}
