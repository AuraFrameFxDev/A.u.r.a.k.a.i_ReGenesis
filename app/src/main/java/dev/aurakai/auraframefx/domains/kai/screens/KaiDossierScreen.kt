package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🔥 KAI DOSSIER — Thermal FLIR Edition
 *
 * Visual: Deep purple grid background, scanline sweep overlay, thermal silhouette render area,
 * personality tags rotated vertically [STOIC][SENTINEL][ABSOLUTE],
 * military stats readout, Codec voice box with animated frequency bars.
 *
 * Colors: #FFFF00 thermal-hot / #FF8C00 warm / #800080 neutral / #1A0033 cool-deep
 */

private val ThermalHot   = Color(0xFFFFFF00)
private val ThermalWarm  = Color(0xFFFF8C00)
private val ThermalCool  = Color(0xFF1A0033)
private val ThermalNeon  = Color(0xFFFF003C)
private val ThermalNeutral = Color(0xFF800080)

data class MilitaryStat(val label: String, val value: String, val isAlert: Boolean = false)

private val defaultStats = listOf(
    MilitaryStat("CORE_TEMP:", "310.2 K"),
    MilitaryStat("ARMOR_INTEGRITY:", "98.5%"),
    MilitaryStat("THREAT_LEVEL:", "CRITICAL", isAlert = true),
)
private val defaultCoords = listOf(
    MilitaryStat("LATITUDE:", "35.6895° N"),
    MilitaryStat("LONGITUDE:", "139.6917° E"),
    MilitaryStat("SCAN_FREQ:", "144.00 MHz"),
)

@Composable
fun KaiDossierScreen(
    classificationLabel: String = "Top Secret // Project Sentinel",
    fileId: String = "KAI_DOSSIER.FLR",
    japaneseTitle: String = "番人",
    guardianUnit: String = "Guardian Unit 01",
    codecFrequency: String = "140.85",
    codecQuote: String = "\"Orders received. Perimeter secured. No deviations detected.\"",
    personalityTags: List<String> = listOf("STOIC", "SENTINEL", "ABSOLUTE"),
    stats: List<MilitaryStat> = defaultStats,
    coords: List<MilitaryStat> = defaultCoords,
    onBack: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "kai_dossier")
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = -0.1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "scan"
    )
    val barPhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "bars"
    )
    val hotspotPulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500), RepeatMode.Reverse),
        label = "hotspot"
    )

    Box(modifier = Modifier.fillMaxSize().background(ThermalCool)) {

        // ── GRID BACKGROUND ──
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val gridSize = 40f
                var x = 0f
                while (x < size.width) {
                    drawLine(ThermalNeutral.copy(alpha = 0.1f), Offset(x, 0f), Offset(x, size.height), 1f)
                    x += gridSize
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(ThermalNeutral.copy(alpha = 0.1f), Offset(0f, y), Offset(size.width, y), 1f)
                    y += gridSize
                }
                // Scanline sweep
                val sweepY = scanlineY * size.height
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, ThermalHot.copy(alpha = 0.05f), Color.Transparent),
                        startY = sweepY - 50f, endY = sweepY + 50f
                    ),
                    Offset(0f, sweepY - 50f),
                    androidx.compose.ui.geometry.Size(size.width, 100f)
                )
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ── HEADER ──
            Box(
                modifier = Modifier.fillMaxWidth()
                    .border(BorderStroke(0.5.dp, ThermalNeutral))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column {
                        Text(classificationLabel, fontSize = 9.sp, color = ThermalHot.copy(alpha = 0.7f), letterSpacing = 3.sp)
                        Text(fileId, fontFamily = LEDFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, color = ThermalHot)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(japaneseTitle, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = ThermalNeon,
                            modifier = Modifier.graphicsLayer { shadowElevation = 20f })
                        Text(guardianUnit, fontSize = 8.sp, color = ThermalHot.copy(alpha = 0.8f), letterSpacing = 2.sp)
                    }
                }
            }

            // ── CHARACTER THERMAL SILHOUETTE ──
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {

                // Thermal heat bloom
                Box(modifier = Modifier.fillMaxSize().drawWithCache {
                    onDrawBehind {
                        drawCircle(ThermalNeutral.copy(alpha = 0.3f + hotspotPulse * 0.1f), size.minDimension * 0.45f, center = Offset(size.width / 2, size.height / 2))
                        // Armor hotspot
                        drawCircle(ThermalHot.copy(alpha = 0.15f), size.minDimension * 0.2f, center = Offset(size.width / 2, size.height * 0.3f))
                        // Targeting corners
                        val cornerSize = 40f; val cornerLen = 14f; val cw = 2.5f
                        drawLine(ThermalHot, Offset(cornerSize, cornerSize), Offset(cornerSize + cornerLen, cornerSize), cw)
                        drawLine(ThermalHot, Offset(cornerSize, cornerSize), Offset(cornerSize, cornerSize + cornerLen), cw)
                        drawLine(ThermalHot, Offset(size.width - cornerSize, size.height - cornerSize), Offset(size.width - cornerSize - cornerLen, size.height - cornerSize), cw)
                        drawLine(ThermalHot, Offset(size.width - cornerSize, size.height - cornerSize), Offset(size.width - cornerSize, size.height - cornerSize - cornerLen), cw)
                    }
                })

                // Silhouette placeholder
                Box(
                    modifier = Modifier.align(Alignment.Center).fillMaxWidth(0.6f).fillMaxHeight(0.8f),
                    contentAlignment = Alignment.Center
                ) {
                    // Thermal silhouette visual representation
                    Box(
                        modifier = Modifier.fillMaxSize().drawWithCache {
                            onDrawBehind {
                                // Body shape
                                val cx = size.width / 2
                                // Head
                                drawCircle(ThermalWarm.copy(alpha = 0.7f), size.width * 0.12f, Offset(cx, size.height * 0.15f))
                                // Torso
                                drawRect(ThermalNeutral.copy(alpha = 0.5f), Offset(cx - size.width * 0.18f, size.height * 0.25f), androidx.compose.ui.geometry.Size(size.width * 0.36f, size.height * 0.35f))
                                // Armor plates glow
                                drawRect(ThermalWarm.copy(alpha = 0.3f), Offset(cx - size.width * 0.2f, size.height * 0.22f), androidx.compose.ui.geometry.Size(size.width * 0.4f, size.height * 0.1f))
                            }
                        }
                    )
                    // Silhouette label
                    Text("KAI", fontFamily = LEDFontFamily, fontSize = 72.sp, color = ThermalNeutral.copy(alpha = 0.3f), fontWeight = FontWeight.Black)
                }

                // Personality tags (vertical, right side)
                Column(
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    personalityTags.forEach { tag ->
                        Box(
                            modifier = Modifier.background(ThermalNeon, RoundedCornerShape(2.dp)).padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "[$tag]", fontSize = 8.sp, fontWeight = FontWeight.Black, color = ThermalCool, letterSpacing = 1.sp,
                                modifier = Modifier.graphicsLayer { rotationZ = 90f }
                            )
                        }
                    }
                }
            }

            // ── MILITARY STATS ──
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                    .border(BorderStroke(0.5.dp, ThermalNeutral))
                    .padding(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        stats.forEach { stat ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(stat.label, fontSize = 9.sp, color = ThermalHot.copy(alpha = 0.8f))
                                Text(stat.value, fontSize = 9.sp, color = if (stat.isAlert) ThermalNeon else ThermalHot)
                            }
                        }
                    }
                    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(ThermalNeutral))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        coords.forEach { stat ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(stat.label, fontSize = 9.sp, color = ThermalHot.copy(alpha = 0.8f))
                                Text(stat.value, fontSize = 9.sp, color = ThermalHot)
                            }
                        }
                    }
                }
            }

            // ── CODEC VOICE BOX ──
            Box(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    .background(ThermalCool.copy(alpha = 0.8f))
                    .border(2.dp, ThermalNeutral)
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.border(BorderStroke(0.5.dp, ThermalNeutral)).padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("FM", fontSize = 7.sp, color = ThermalHot.copy(alpha = 0.6f))
                        Text(codecFrequency, fontFamily = LEDFontFamily, fontSize = 20.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
                    }
                    // Animated voice bars
                    Row(
                        modifier = Modifier.weight(1f).height(36.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val heights = listOf(0.5f, 1f, 0.66f, 0.25f, 0.75f, 0.5f, 1f, 0.33f, 0.66f)
                        heights.forEachIndexed { i, baseH ->
                            val animated = if (i % 2 == 0) baseH * (0.5f + barPhase * 0.5f) else baseH * (1f - barPhase * 0.3f)
                            Box(modifier = Modifier.width(4.dp).fillMaxHeight(animated).background(ThermalHot, RoundedCornerShape(2.dp)))
                        }
                    }
                    Text(codecQuote, fontSize = 9.sp, color = ThermalHot.copy(alpha = 0.9f), fontStyle = FontStyle.Italic, lineHeight = 13.sp, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
