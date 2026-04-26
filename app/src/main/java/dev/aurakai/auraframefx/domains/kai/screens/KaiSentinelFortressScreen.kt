package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.aura.ui.theme.FortDark
import dev.aurakai.auraframefx.domains.aura.ui.theme.FortGrey
import kotlin.math.*

/**
 * 🏰 KAI SENTINEL FORTRESS HUD
 */

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
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("SENTINEL FORTRESS", fontFamily = LEDFontFamily, color = Color.White, fontSize = 18.sp)
                Box(modifier = Modifier.size(24.dp)) // Spacer
            }

            // ═══ MAIN 3-COLUMN ═══
            Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // LEFT: System cores
                Column(
                    modifier = Modifier.width(160.dp).fillMaxHeight()
                        .background(FortGrey.copy(alpha = 0.4f))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    cores.forEach { core ->
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(core.label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(core.temp, fontSize = 9.sp, color = if(core.isHot) Color.Red else Color.Cyan)
                            }
                            Spacer(Modifier.height(3.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.Black.copy(alpha = 0.3f))) {
                                Box(modifier = Modifier.fillMaxWidth(core.pct).fillMaxHeight().background(if(core.isHot) Color.Red else Color.Cyan))
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
                            Text(log, fontSize = 8.sp, color = Color.White.copy(alpha = 0.5f))
                        }
                    }
                }

                // CENTER: FLIR character + radar
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    // Radar overlay (subtle, bottom center)
                    Canvas(modifier = Modifier.size(200.dp).align(Alignment.Center)) {
                        val cx = size.width / 2; val cy = size.height / 2
                        drawCircle(Color.Green.copy(alpha = 0.1f), radius = size.minDimension / 2, style = Stroke(1f))
                        rotate(radarAngle, Offset(cx, cy)) {
                            drawLine(Color.Green.copy(alpha = 0.3f), Offset(cx, cy), Offset(cx, 0f), 2f)
                        }
                    }

                    // Identity strata
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(start = 8.dp, top = 4.dp, end = 12.dp, bottom = 4.dp)
                    ) {
                        Text("KAI // UNIT-00", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = LEDFontFamily, color = Color.White)
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
                            .background(FortGrey.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        Text("SENTINEL_LOG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            terminalLines.forEach { line ->
                                Text(
                                    line,
                                    fontSize = 8.sp,
                                    color = Color.Green.copy(alpha = 0.8f),
                                    fontWeight = if (line.contains(">>")) FontWeight.Bold else FontWeight.Normal,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }

                    // Alert status
                    Box(
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                            .background(Color.Red.copy(alpha = 0.1f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("STATUS_MONITOR", fontSize = 9.sp, color = Color.Red.copy(alpha = 0.7f))
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red.copy(alpha = statusPulse)))
                                Text("INTRUSION_NULL", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
