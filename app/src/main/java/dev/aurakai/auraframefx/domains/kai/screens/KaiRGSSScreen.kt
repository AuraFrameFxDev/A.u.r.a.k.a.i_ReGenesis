package dev.aurakai.auraframefx.domains.kai.screens

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
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.math.*

/**
 * 🛡️ KAI RGSS — Reality Gate Security System
 *
 * Translated from Stitch export.
 * Full-screen radar sweep, threat blips, thermal activity log,
 * POWER OF NO veto stamp, CRT scanline overlay, LOCKED/BREACHED status.
 *
 * Design: Military green on near-black, red threats, amber warnings.
 * Metal Gear codec energy + SIGINT dashboard.
 */

// Kai color palette
private val KaiPurple   = Color(0xFF9D00FF)
private val KaiOrange   = Color(0xFFFF4500)
private val KaiAmber    = Color(0xFFFFB000)
private val KaiGreen    = Color(0xFF00FF41)
private val KaiRed      = Color(0xFFFF0000)
private val KaiDark     = Color(0xFF0A0A0A)
private val KaiDarkGreen = Color(0xFF003B00)

data class ThreatBlip(
    val name: String,
    val xFrac: Float,   // 0-1 relative to radar center
    val yFrac: Float,
    val status: ThreatStatus
)

enum class ThreatStatus { CLEAR, STABLE, THREAT }

data class ThermalLog(val label: String, val message: String, val isThreat: Boolean = false)

@Composable
fun KaiRGSSScreen(
    onVetoPressed: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rgss")

    // Radar sweep rotation
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "radar"
    )

    // Scanline
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "scan"
    )

    // Status pulse
    val statusPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )

    var isVetoed by remember { mutableStateOf(false) }

    val blips = remember {
        listOf(
            ThreatBlip("APP_SIG: 42.92.11", -0.22f, -0.28f, ThreatStatus.THREAT),
            ThreatBlip("PROC_ID: 11.05.88", 0.20f, 0.18f, ThreatStatus.CLEAR),
            ThreatBlip("GATE_01: 00.00.01", -0.05f, -0.05f, ThreatStatus.STABLE),
        )
    }

    val thermalLogs = remember {
        listOf(
            ThermalLog("INTERCEPT_01", "Heat signature detected in sector 7G. Filter applied."),
            ThermalLog("SYSTEM_FLUSH", "Reality Gate buffer cleared. All cycles synced."),
            ThermalLog("UNAUTHORIZED_ACCESS", "External probe blocked by RGSS Sentinel.", true),
            ThermalLog("HEARTBEAT", "Pulse verified. Gateway stable."),
            ThermalLog("APP_GATE_UP", "Process verified by Kai's tactical system."),
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(KaiDark)) {

        // CRT scanline overlay
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val y = size.height * scanlineY
                drawLine(KaiGreen.copy(alpha = 0.08f), Offset(0f, y), Offset(size.width, y), 2f)
                // Grid lines
                var gx = 0f
                while (gx < size.width) {
                    drawLine(KaiGreen.copy(alpha = 0.03f), Offset(gx, 0f), Offset(gx, size.height), 0.5f)
                    gx += 40f
                }
                var gy = 0f
                while (gy < size.height) {
                    drawLine(KaiGreen.copy(alpha = 0.03f), Offset(0f, gy), Offset(size.width, gy), 0.5f)
                    gy += 40f
                }
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            // ═══ HEADER ═══
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("RGSS // VERSION 4.2.0", fontSize = 9.sp, color = KaiGreen.copy(alpha = 0.7f), letterSpacing = 2.sp)
                    Text("KAI REALITY GATE", fontFamily = LEDFontFamily, fontSize = 24.sp, fontWeight = FontWeight.Black, color = KaiGreen, letterSpacing = (-0.5).sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ENCRYPTION: AES-REALITY-GATE", fontSize = 9.sp, color = KaiGreen.copy(alpha = 0.6f))
                        Text("THREAT LEVEL: OMEGA", fontSize = 9.sp, color = KaiGreen.copy(alpha = 0.6f))
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(KaiRed)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            if (isVetoed) "LOCKED" else "BREACHED",
                            fontFamily = LEDFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            modifier = Modifier.graphicsLayer { alpha = statusPulse }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ═══ RADAR ═══
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(2.dp, KaiGreen.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    // Radar canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2
                        val cy = size.height / 2
                        val maxR = minOf(cx, cy) * 0.88f

                        // Radar rings
                        listOf(1.0f, 0.66f, 0.33f).forEach { scale ->
                            drawCircle(KaiGreen.copy(alpha = 0.2f), maxR * scale, Offset(cx, cy), style = Stroke(1f))
                        }
                        // Crosshairs
                        drawLine(KaiGreen.copy(alpha = 0.3f), Offset(0f, cy), Offset(size.width, cy), 1f)
                        drawLine(KaiGreen.copy(alpha = 0.3f), Offset(cx, 0f), Offset(cx, size.height), 1f)

                        // Sweep
                        rotate(radarAngle, Offset(cx, cy)) {
                            val sweepBrush = Brush.sweepGradient(
                                listOf(Color.Transparent, KaiGreen.copy(alpha = 0.25f), Color.Transparent),
                                center = Offset(cx, cy)
                            )
                            drawArc(KaiGreen.copy(alpha = 0.2f), 0f, 90f, true,
                                Offset(cx - maxR, cy - maxR),
                                androidx.compose.ui.geometry.Size(maxR * 2, maxR * 2))
                        }

                        // Threat blips
                        blips.forEach { blip ->
                            val bx = cx + blip.xFrac * maxR * 2
                            val by = cy + blip.yFrac * maxR * 2
                            val blipColor = when (blip.status) {
                                ThreatStatus.THREAT -> KaiRed
                                ThreatStatus.CLEAR -> KaiGreen
                                ThreatStatus.STABLE -> KaiAmber
                            }
                            drawCircle(blipColor, 5f, Offset(bx, by))
                            drawCircle(blipColor.copy(alpha = 0.3f), 12f, Offset(bx, by))
                        }
                    }

                    // Status text bottom-left
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                    ) {
                        Text(
                            if (isVetoed) "LOCKED" else "BREACHED",
                            fontFamily = LEDFontFamily,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isVetoed) KaiGreen else KaiRed,
                            modifier = Modifier.graphicsLayer { alpha = if (isVetoed) 1f else statusPulse }
                        )
                        Text("SECTOR: REALITY_GATE_01", fontSize = 9.sp, color = KaiGreen)
                        Text("COORD: 34.711 / -118.412", fontSize = 8.sp, color = KaiGreen.copy(alpha = 0.7f))
                    }
                }

                // ═══ SIDE PANEL ═══
                Column(
                    modifier = Modifier.width(220.dp).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Thermal log
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .border(1.dp, KaiGreen.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(12.dp)
                    ) {
                        Text(
                            "THERMAL ACTIVITY LOG",
                            fontSize = 9.sp,
                            color = KaiGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                                .border(BorderStroke(1.dp, KaiGreen.copy(alpha = 0.3f)))
                                .padding(bottom = 4.dp)
                        )

                        // Thermal bars
                        Row(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            listOf(0.4f, 0.85f, 0.95f, 0.60f, 0.50f, 0.75f).forEachIndexed { i, h ->
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .fillMaxHeight(h)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    if (h > 0.7f) KaiRed else KaiAmber,
                                                    KaiGreen
                                                )
                                            )
                                        )
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Metrics
                        listOf(
                            Triple("CORE_TEMP", "72.4°C", KaiAmber),
                            Triple("GATE_LOAD", "94.2%", KaiRed),
                            Triple("VETO_READY", "TRUE", KaiGreen)
                        ).forEach { (label, value, color) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                                    .border(BorderStroke(1.dp, KaiGreen.copy(alpha = 0.1f))),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, fontSize = 9.sp, color = KaiGreen)
                                Text(value, fontSize = 9.sp, color = color, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Log entries
                        thermalLogs.forEach { log ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .border(start = BorderStroke(2.dp, if (log.isThreat) KaiRed else KaiGreen))
                                    .padding(start = 6.dp, top = 2.dp, bottom = 2.dp)
                            ) {
                                Column {
                                    Text(log.label, fontSize = 8.sp, color = if (log.isThreat) KaiRed else KaiGreen, fontWeight = FontWeight.Bold)
                                    Text(log.message, fontSize = 7.sp, color = Color.White.copy(alpha = 0.7f), lineHeight = 10.sp)
                                }
                            }
                        }
                    }

                    // POWER OF NO veto button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Glow
                        Box(
                            modifier = Modifier.matchParentSize()
                                .background(KaiRed.copy(alpha = 0.15f * statusPulse), RoundedCornerShape(2.dp))
                        )

                        Box(
                            modifier = Modifier
                                .border(6.dp, KaiRed, RoundedCornerShape(2.dp))
                                .clickable {
                                    isVetoed = !isVetoed
                                    onVetoPressed()
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .graphicsLayer { rotationZ = -4f }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "POWER OF NO",
                                    fontFamily = LEDFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = KaiRed,
                                    letterSpacing = 2.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(Modifier.fillMaxWidth().height(1.dp).background(KaiRed))
                                Spacer(Modifier.height(4.dp))
                                Text("CLASSIFIED", fontFamily = LEDFontFamily, fontSize = 10.sp, color = KaiRed, letterSpacing = 4.sp)
                            }
                        }
                        // CLASSIFIED badge
                        Box(
                            modifier = Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp)
                                .background(KaiRed).padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("CLASSIFIED", fontSize = 7.sp, color = Color.Black, fontWeight = FontWeight.Black, fontFamily = LEDFontFamily)
                        }
                    }
                }
            }

            // ═══ FOOTER ═══
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KaiGreen.copy(alpha = 0.08f))
                    .border(BorderStroke(1.dp, KaiGreen.copy(alpha = 0.3f)))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("SYS_MODE: DEFENSIVE", fontSize = 8.sp, color = KaiGreen, letterSpacing = 1.sp)
                    Text("ENCRYPTION: AES-REALITY-GATE", fontSize = 8.sp, color = KaiGreen, letterSpacing = 1.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("UPTIME: 14:22:01", fontSize = 8.sp, color = KaiGreen)
                    Text("● SIGNAL_CONNECTED", fontSize = 8.sp, color = KaiGreen,
                        modifier = Modifier.graphicsLayer { alpha = statusPulse })
                }
            }
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

private fun Modifier.border(start: BorderStroke): Modifier = this.drawWithCache {
    onDrawBehind {
        drawLine(
            brush = start.brush ?: SolidColor(Color.Transparent),
            start = Offset(0f, 0f), end = Offset(0f, size.height),
            strokeWidth = start.width.toPx()
        )
    }
}
