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
 * 🏰 KAI SENTINEL FORTRESS HUD
 *
 * Translated from Stitch export (SentinelFortressTacticalHUD / unnamed).
 * The full 3-panel tactical command UI:
 * - Left: System cores thermal readout (KERNEL/BOOTLOADER/LSPOSED_ENV) + stencil logs
 * - Center: FLIR effect character area + radar overlay + Identity Strata + dot grid
 * - Right: Live terminal stream + Alert status
 * - Footer: CPU/RAM/NET readouts
 *
 * Design: #05050A bg, NeonPurple (#9D00FF), TacticalOrange (#F97316), FLIR sepia filter sim
 */

private val FortPurple  = Color(0xFF9D00FF)
private val FortOrange  = Color(0xFFF97316)
private val FortDark    = Color(0xFF05050A)
private val FortGrey    = Color(0xFF1A1A1A)

data class CoreStat(val label: String, val temp: String, val pct: Float, val isHot: Boolean = false)

private val defaultCores = listOf(
    CoreStat("KERNEL_CORE",   "42°C", 0.65f),
    CoreStat("BOOTLOADER",    "38°C", 0.42f),
    CoreStat("LSPOSED_ENV",   "51°C", 0.88f, isHot = true),
)

private val liveLogs = listOf(
    "PACKET_INTERCEPTED: 10.0.0.1",
    "DECRYPTING_HASH_SHA256",
    "KERNEL_PANIC: RESOLVED",
    "LSPOSED_HOOK_ACTIVE",
    "SYS_THROTTLING: DISABLED",
    "SH_EXECUTED: /sbin/sentinel",
    "BUFFER_OVERFLOW_PREVENTED",
    "ENCRYPTING_LOCAL_NODE...",
)

@Composable
fun KaiSentinelFortressScreen(
    cores: List<CoreStat> = defaultCores,
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fortress")

    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "radar"
    )
    val statusPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "scan"
    )

    val terminalLines = remember { mutableStateListOf(
        "INITIALIZING_FORTRESS...",
        ">> AUTHENTICATING_ROOT",
        "MEMORY_ALLOC: 4GB_SWAP",
        "TUNNELING_LSP...",
    ) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2000)
            terminalLines.add(liveLogs[kotlin.random.Random.nextInt(liveLogs.size)])
            if (terminalLines.size > 15) terminalLines.removeAt(0)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(FortDark)) {

        // Scanline overlay
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                drawLine(FortPurple.copy(alpha = 0.08f), Offset(0f, size.height * scanlineY), Offset(size.width, size.height * scanlineY), 4f)
                // CRT h-lines
                var gy = 0f; while (gy < size.height) {
                    drawLine(Color.Black.copy(alpha = 0.08f), Offset(0f, gy), Offset(size.width, gy), 0.5f)
                    gy += 4f
                }
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ═══ TOP BAR ═══
            Row(
                modifier = Modifier.fillMaxWidth()
                    .border(BorderStroke(1.dp, FortPurple.copy(alpha = 0.4f)))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("MODE: SENTINEL CATALYST", fontSize = 9.sp, color = FortOrange, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("PHASE_INITIATED // 0.88.24", fontFamily = LEDFontFamily, fontSize = 18.sp, color = FortPurple, letterSpacing = (-0.5).sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("ENCRYPTION_LEVEL: 4096-RSA", fontSize = 9.sp, color = FortPurple.copy(alpha = 0.5f))
                    Row { Text("SIGNAL: ", fontSize = 12.sp); Text("STABLE", fontSize = 12.sp, color = FortOrange, fontWeight = FontWeight.Bold) }
                }
            }

            // ═══ MAIN 3-COLUMN ═══
            Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // LEFT: System cores
                Column(
                    modifier = Modifier.width(160.dp).fillMaxHeight()
                        .border(1.dp, FortPurple.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                        .background(FortGrey.copy(alpha = 0.4f))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Corner brackets
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text("SYSTEM_CORES", fontSize = 9.sp, color = FortOrange, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Canvas(modifier = Modifier.align(Alignment.TopEnd).size(12.dp)) {
                            drawLine(FortOrange.copy(alpha = 0.8f), Offset(0f, 0f), Offset(size.width, 0f), 2f)
                            drawLine(FortOrange.copy(alpha = 0.8f), Offset(size.width, 0f), Offset(size.width, size.height), 2f)
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(FortOrange.copy(alpha = 0.4f)))
                    Spacer(Modifier.height(4.dp))

                    cores.forEach { core ->
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(core.label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(core.temp, fontSize = 9.sp, color = FortOrange)
                            }
                            Spacer(Modifier.height(3.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(FortPurple.copy(alpha = 0.2f), RoundedCornerShape(1.dp))) {
                                Box(modifier = Modifier.fillMaxWidth(core.pct).fillMaxHeight()
                                    .background(if (core.isHot) FortOrange else FortPurple, RoundedCornerShape(1.dp)))
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // Stencil log
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        listOf(
                            "[SYS] HOOK_INJECTED: SUCCESS",
                            "[MOD] MODULE_Z_LOADED",
                            "[SEC] SELINUX: PERMISSIVE",
                            "[IO]  BUS_VOLTAGE: 3.82V"
                        ).forEach { log ->
                            Text(log, fontSize = 7.sp, color = FortPurple.copy(alpha = 0.55f), lineHeight = 10.sp)
                        }
                    }

                    // Bottom corner
                    Canvas(modifier = Modifier.size(12.dp)) {
                        drawLine(FortOrange.copy(alpha = 0.8f), Offset(0f, size.height), Offset(size.width, size.height), 2f)
                        drawLine(FortOrange.copy(alpha = 0.8f), Offset(0f, 0f), Offset(0f, size.height), 2f)
                    }
                }

                // CENTER: FLIR character + radar
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                        .border(1.dp, FortPurple.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                        .background(Brush.verticalGradient(listOf(FortPurple.copy(alpha = 0.08f), Color.Transparent)))
                ) {

                    // Dot grid overlay
                    Box(modifier = Modifier.fillMaxSize().drawWithCache {
                        onDrawBehind {
                            var gx = 0f; while (gx < size.width) {
                                var gy = 0f; while (gy < size.height) {
                                    drawCircle(FortPurple.copy(alpha = 0.05f), 1f, Offset(gx, gy))
                                    gy += 30f
                                }; gx += 30f
                            }
                        }
                    })

                    // FLIR thermal bloom (simulated via gradient + blend)
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(Brush.radialGradient(
                                listOf(
                                    FortOrange.copy(alpha = 0.25f),
                                    Color(0xFF660000).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            ))
                    )

                    // Radar overlay (subtle, bottom center)
                    Canvas(modifier = Modifier.size(200.dp).align(Alignment.Center)) {
                        val cx = size.width / 2; val cy = size.height / 2
                        listOf(1.0f, 0.75f, 0.5f).forEach { scale ->
                            drawCircle(FortPurple.copy(alpha = 0.15f), size.minDimension / 2 * scale, Offset(cx, cy), style = Stroke(1f))
                        }
                        drawLine(FortPurple.copy(alpha = 0.2f), Offset(0f, cy), Offset(size.width, cy), 0.5f)
                        drawLine(FortPurple.copy(alpha = 0.2f), Offset(cx, 0f), Offset(cx, size.height), 0.5f)
                        rotate(radarAngle, Offset(cx, cy)) {
                            drawArc(FortPurple.copy(alpha = 0.15f), 0f, 60f, true, Offset(cx - size.minDimension / 2, cy - size.minDimension / 2), Size(size.minDimension, size.minDimension))
                        }
                    }

                    // Identity strata
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                            .border(BorderStroke(2.dp, FortOrange))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(start = 8.dp, top = 4.dp, end = 12.dp, bottom = 4.dp)
                    ) {
                        Text("IDENTITY_STRATA", fontSize = 8.sp, color = FortOrange)
                        Text("KAI // UNIT-00", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = LEDFontFamily, color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.background(FortPurple).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("ACTIVE", fontSize = 7.sp, color = Color.Black, fontWeight = FontWeight.Black)
                            }
                            Text("VERIFIED_ORIGIN", fontSize = 8.sp, color = FortPurple)
                        }
                    }
                }

                // RIGHT: Terminal + alert
                Column(
                    modifier = Modifier.width(160.dp).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Live terminal
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                            .border(1.dp, FortPurple.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                            .background(FortGrey.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        Text("LIVE_STREAMS", fontSize = 9.sp, color = FortOrange, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(FortOrange.copy(alpha = 0.4f)).padding(bottom = 4.dp))
                        Spacer(Modifier.height(4.dp))
                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            terminalLines.forEach { line ->
                                Text(
                                    line,
                                    fontSize = 8.sp,
                                    color = if (line.contains(">>")) FortOrange else FortPurple.copy(alpha = 0.8f),
                                    fontWeight = if (line.contains(">>")) FontWeight.Bold else FontWeight.Normal,
                                    lineHeight = 12.sp
                                )
                            }
                            Text("_", fontSize = 9.sp, color = FortPurple, modifier = Modifier.graphicsLayer { alpha = statusPulse })
                        }
                    }

                    // Alert status
                    Box(
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                            .border(1.dp, FortOrange.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            .background(FortOrange.copy(alpha = 0.08f), RoundedCornerShape(2.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("ALERT_STATUS", fontSize = 9.sp, color = FortOrange, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(FortOrange).graphicsLayer { alpha = statusPulse })
                                Text("INTRUSION_NULL", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            // ═══ FOOTER ═══
            Row(
                modifier = Modifier.fillMaxWidth()
                    .border(BorderStroke(1.dp, FortPurple.copy(alpha = 0.3f)))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row { Text("CPU: "); Text("24%", color = FortOrange, fontWeight = FontWeight.Bold) }
                    Row { Text("RAM: "); Text("1.2G/8.0G", color = FortOrange, fontWeight = FontWeight.Bold) }
                }
                Text("[ENCRYPTED_STATION_ALPHA_7]", fontSize = 8.sp, color = FortPurple.copy(alpha = 0.3f), fontStyle = FontStyle.Italic)
                Row { Text("NET: "); Text("582 KB/S", color = FortOrange, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
