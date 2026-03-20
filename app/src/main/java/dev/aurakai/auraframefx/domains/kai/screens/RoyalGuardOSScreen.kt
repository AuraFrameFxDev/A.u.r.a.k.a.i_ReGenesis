package dev.aurakai.auraframefx.domains.kai.screens

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.math.*

/**
 * 🎖️ KAI ROYAL GUARD OS SCREEN
 *
 * Translated from Stitch export (RoyalGuardTacticalInterface).
 * Features:
 * - Central hexagonal shield emblem (RG) with thermal bloom bloom + rotating rings
 * - FLIR-style Kai character render (heat bloom gradient + grayscale image)
 * - Left SIGINT log sidebar (scrolling terminal)
 * - Bottom command buttons: COUNTER_MEASURE, STEALTH_CLOAK, SIGNAL_JAMMER, ULTIMATE_OVERRIDE
 * - CRT scanline + dot grid overlay
 * - Header: ROYAL GUARD OS v4.2.0 + DEFCON: 1
 *
 * Design: #050005 bg, NeonPurple (#9D00FF), HotOrange (#FF4500), thermal bloom
 */

private val RGPurple   = Color(0xFF9D00FF)
private val RGOrange   = Color(0xFFFF4500)
private val RGGreen    = Color(0xFF00FF41)
private val RGDark     = Color(0xFF050005)
private val RGRed      = Color(0xFF660000)

@Composable
fun RoyalGuardOSScreen(
    onCommandExecuted: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rg")

    val scanlineY by infiniteTransition.animateFloat(
        initialValue = -100f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "scan"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )
    val hexRingRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "ring1"
    )
    val hexRingReverseRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -360f,
        animationSpec = infiniteRepeatable(tween(30000, easing = LinearEasing)),
        label = "ring2"
    )
    val boomPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "boom"
    )

    // Scrolling SIGINT logs
    val sigintLogs = remember {
        listOf(
            "[INIT] SIGNAL INTERCEPT...", "PACKET 0x442 RECEIVED", "BYPASSING FIREWALL...",
            "ENCRYPTION: AES-256", "THREAT LEVEL: LOW", "KAI BIOMETRICS: NOMINAL",
            "HEART RATE: 72BPM", "ADRENALINE: 12%", "TEMP: 36.5C", "RE-ROUTING IP...",
            "PROXY: MOSCOW/NOD-4", "[ALERT] PERIMETER BREACH", "VOID DETECTED",
            "RE-CALIBRATING HUD...",
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(RGDark)) {

        // Dot grid
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                var gx = 0f; while (gx < size.width) {
                    var gy = 0f; while (gy < size.height) {
                        drawCircle(RGPurple.copy(alpha = 0.05f), 1f, Offset(gx, gy))
                        gy += 30f
                    }; gx += 30f
                }
                // CRT lines
                drawLine(RGPurple.copy(alpha = 0.08f), Offset(0f, size.height * scanlineY.coerceIn(0f, 1f)), Offset(size.width, size.height * scanlineY.coerceIn(0f, 1f)), 100f)
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ═══ HEADER ═══
            Row(
                modifier = Modifier.fillMaxWidth()
                    .border(BorderStroke(1.dp, RGPurple.copy(alpha = 0.3f)))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row {
                        Text("ROYAL GUARD ", fontFamily = LEDFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Black, color = RGOrange, fontStyle = FontStyle.Italic)
                        Text("OS v4.2.0", fontFamily = LEDFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Black, color = RGPurple)
                    }
                    Text("Executive Override Enabled // UID: KAI-RG-01", fontSize = 9.sp, color = RGOrange.copy(alpha = 0.8f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("GRID COORDS: 35.6895° N, 139.6917° E", fontSize = 9.sp, color = RGPurple, fontWeight = FontWeight.Bold)
                    Text("DEFCON: 1", fontFamily = LEDFontFamily, fontSize = 22.sp, color = RGOrange, modifier = Modifier.graphicsLayer { alpha = pulse })
                }
            }

            // ═══ MAIN CONTENT ═══
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // LEFT: SIGINT log sidebar
                Column(
                    modifier = Modifier.width(120.dp).fillMaxHeight()
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    sigintLogs.forEach { log ->
                        Text(
                            log,
                            fontSize = 7.sp,
                            color = if (log.contains("ALERT")) RGOrange else RGGreen.copy(alpha = 0.5f),
                            lineHeight = 11.sp,
                            fontWeight = if (log.contains("ALERT")) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // CENTER: Hex shield + FLIR
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {

                    // Thermal bloom BG
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.radialGradient(
                                listOf(RGOrange.copy(alpha = 0.15f), RGRed.copy(alpha = 0.1f), Color.Transparent)
                            )
                        )
                    )

                    // Rotating outer ring (dashed)
                    Canvas(modifier = Modifier.size(360.dp).graphicsLayer { rotationZ = hexRingRotation }) {
                        drawCircle(RGPurple.copy(alpha = 0.25f), size.minDimension / 2, style = Stroke(1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))))
                    }

                    // Rotating inner ring (dotted)
                    Canvas(modifier = Modifier.size(300.dp).graphicsLayer { rotationZ = hexRingReverseRotation }) {
                        drawCircle(RGOrange.copy(alpha = 0.15f), size.minDimension / 2, style = Stroke(1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 8f))))
                    }

                    // Hexagonal shield clip-shape
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .graphicsLayer { scaleX = boomPulse; scaleY = boomPulse },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val path = hexPath(size.width / 2, size.height / 2, size.minDimension / 2 * 0.9f)
                            drawPath(path, RGPurple.copy(alpha = 0.08f))
                            drawPath(path, RGPurple.copy(alpha = 0.4f), style = Stroke(4f))
                            // Pulse ring
                            drawPath(path, RGOrange.copy(alpha = 0.15f), style = Stroke(8f))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(
                                "RG",
                                fontFamily = LEDFontFamily,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Black,
                                color = RGOrange,
                            )
                            Text("Protection State: ACTIVE", fontSize = 9.sp, color = RGPurple, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }

                    // Identity strata (bottom left of center)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .border(BorderStroke(2.dp, RGOrange))
                            .padding(start = 8.dp, top = 4.dp, end = 16.dp, bottom = 4.dp)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Text("IDENTITY_STRATA", fontSize = 8.sp, color = RGOrange)
                        Text("KAI // UNIT-00", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = LEDFontFamily)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.background(RGPurple).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("ACTIVE", fontSize = 8.sp, color = Color.Black, fontWeight = FontWeight.Black)
                            }
                            Text("VERIFIED_ORIGIN", fontSize = 9.sp, color = RGPurple)
                        }
                    }
                }
            }

            // ═══ COMMAND BUTTONS ═══
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple("CMD_01", "COUNTER_MEASURE",   RGOrange),
                        Triple("CMD_02", "STEALTH_CLOAK",     RGPurple),
                        Triple("CMD_03", "SIGNAL_JAMMER",     RGPurple),
                        Triple("CMD_04", "ULTIMATE_OVERRIDE", RGOrange),
                    ).forEach { (cmd, label, color) ->
                        Box(
                            modifier = Modifier.weight(1f)
                                .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                .background(color.copy(alpha = 0.08f), RoundedCornerShape(2.dp))
                                .clickable { onCommandExecuted(label) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(cmd, fontSize = 7.sp, color = color.copy(alpha = 0.8f))
                                Text(label, fontFamily = LEDFontFamily, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            }
                        }
                    }
                }

                // Status footer
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("STATUS: KAI_PROTECTION_PROTOCOL_ENABLED", fontSize = 7.sp, color = RGPurple.copy(alpha = 0.5f))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("MEM: 12.4GB/16GB", fontSize = 7.sp, color = RGPurple.copy(alpha = 0.5f))
                        Text("UPTIME: 04:21:55", fontSize = 7.sp, color = RGOrange, modifier = Modifier.graphicsLayer { alpha = pulse })
                    }
                }
            }
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

private fun hexPath(cx: Float, cy: Float, r: Float): Path {
    return Path().apply {
        val angles = (0..5).map { Math.toRadians((60.0 * it - 30.0)) }
        moveTo(cx + (r * cos(angles[0])).toFloat(), cy + (r * sin(angles[0])).toFloat())
        for (i in 1..5) lineTo(cx + (r * cos(angles[i])).toFloat(), cy + (r * sin(angles[i])).toFloat())
        close()
    }
}
