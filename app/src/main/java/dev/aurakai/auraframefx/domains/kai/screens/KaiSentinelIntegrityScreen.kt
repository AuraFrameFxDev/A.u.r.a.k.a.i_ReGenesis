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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.random.Random

/**
 * 🔍 KAI SENTINEL INTEGRITY SCREEN
 *
 * System health monitor: 2x2 thermal grid showing Root/Bootloader/LSPosed/SELinux
 * Each zone pulses with color representing health state (green=secure, amber=warn, red=threat)
 * Real-time SIGINT intercept feed on right side, scrolling kernel logs.
 * CRT scanline + grid overlay, reticle corner brackets.
 *
 * Design: Orange-primary (#FB923C) on deep purple-black (#0A050F), FLIR thermal zones
 */

private val SentOrange  = Color(0xFFFB923C)
private val SentGreen   = Color(0xFF22C55E)
private val SentAmber   = Color(0xFFF59E0B)
private val SentRed     = Color(0xFFEF4444)
private val SentPurple  = Color(0xFFA855F7)
private val SentBg      = Color(0xFF0A050F)

data class IntegrityZone(
    val id: String,
    val label: String,
    val status: String,
    val detail: String,
    val health: ZoneHealth
)
enum class ZoneHealth { SECURE, WARNING, CRITICAL }

private val defaultZones = listOf(
    IntegrityZone("ZONE_01", "SYSTEM_ROOT", "SECURE",    "INTEGRITY_INDEX: 0.99", ZoneHealth.SECURE),
    IntegrityZone("ZONE_02", "BOOTLOADER",  "LOCKED",    "VERIFIED_BOOT: ON",     ZoneHealth.SECURE),
    IntegrityZone("ZONE_03", "LSPOSED",     "HOOKED",    "MODULES_ACTIVE: 04",    ZoneHealth.WARNING),
    IntegrityZone("ZONE_04", "SELINUX",     "PERMISSIVE","THREAT_LEVEL: ELEVATED", ZoneHealth.CRITICAL),
)

private val sigintMessages = listOf(
    "[INFO]" to "Handshake initiated: 192.168.1.1",
    "[WARN]" to "Packet loss detected on ETH0",
    "[CRIT]" to "Unauthorized shell attempt blocked",
    "[INFO]" to "Kernel audit: PID 4553 valid",
    "[INFO]" to "Scanned 12 port(s) - 0 open",
    "[WARN]" to "SELinux state change logged",
    "[INFO]" to "Encryption key rotated successfully",
    "[CRIT]" to "BRUTEFORCE detection active",
    "[INFO]" to "VLAN 10 traffic normalized",
    "[CRIT]" to "Hook detected in Zygote module",
    "[INFO]" to "SU: Request granted for UID 10045",
    "[INFO]" to "BINDER: Transaction 0xFA21 processed",
)

@Composable
fun KaiSentinelIntegrityScreen(
    zones: List<IntegrityZone> = defaultZones,
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "integrity")

    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "scan"
    )
    val thermalPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "thermal"
    )

    // Live SIGINT feed
    val feedItems = remember { mutableStateListOf(*sigintMessages.shuffled().take(12).toTypedArray()) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2000)
            feedItems.add(0, sigintMessages[Random.nextInt(sigintMessages.size)])
            if (feedItems.size > 20) feedItems.removeAt(feedItems.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(SentBg)) {

        // CRT overlay + scanline
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                // Scanline
                val y = size.height * scanlineY
                drawLine(SentOrange.copy(alpha = 0.08f), Offset(0f, y), Offset(size.width, y), 2f)
                // CRT grid
                var gx = 0f; while (gx < size.width) { drawLine(Color.White.copy(alpha = 0.03f), Offset(gx, 0f), Offset(gx, size.height), 0.5f); gx += 40f }
                var gy = 0f; while (gy < size.height) { drawLine(Color.White.copy(alpha = 0.03f), Offset(0f, gy), Offset(size.width, gy), 0.5f); gy += 40f }
            }
        })

        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {

            // ═══ HEADER ═══
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    .border(BorderStroke(1.dp, SentOrange.copy(alpha = 0.3f))).padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Kai Sentinel // Integrity Screen", fontSize = 9.sp, color = SentOrange, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("VERSION 4.2.0 // STATUS: MONITORING", fontSize = 7.sp, color = SentOrange.copy(alpha = 0.5f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    // Clock
                    val clock = remember { mutableStateOf("--:--:--") }
                    LaunchedEffect(Unit) {
                        while (true) {
                            val now = java.time.LocalTime.now()
                            clock.value = String.format("%02d:%02d:%02d", now.hour, now.minute, now.second)
                            kotlinx.coroutines.delay(1000)
                        }
                    }
                    Text(clock.value, fontSize = 13.sp, color = SentOrange.copy(alpha = 0.9f), fontFamily = LEDFontFamily)
                    Text("LAT: 34.0522 N | LONG: 118.2437 W", fontSize = 7.sp, color = SentOrange.copy(alpha = 0.4f))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // ═══ THERMAL 2x2 GRID ═══
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThermalZoneCard(zones[0], thermalPulse, Modifier.weight(1f).fillMaxHeight())
                            ThermalZoneCard(zones[1], thermalPulse, Modifier.weight(1f).fillMaxHeight(), delay = 0.5f)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThermalZoneCard(zones[2], thermalPulse, Modifier.weight(1f).fillMaxHeight(), delay = 1.2f)
                            ThermalZoneCard(zones[3], thermalPulse, Modifier.weight(1f).fillMaxHeight(), delay = 2.0f)
                        }
                    }
                }

                // ═══ SIGINT FEED ═══
                Column(
                    modifier = Modifier.width(160.dp).fillMaxHeight()
                        .border(BorderStroke(1.dp, SentOrange.copy(alpha = 0.3f)))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SIGINT FEED", fontSize = 8.sp, color = SentOrange, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Text("● LIVE", fontSize = 8.sp, color = SentOrange, modifier = Modifier.graphicsLayer { alpha = thermalPulse - 0.3f })
                    }
                    Spacer(Modifier.height(8.dp))
                    Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                        feedItems.forEach { (type, msg) ->
                            val color = when {
                                type.contains("CRIT") -> SentRed
                                type.contains("WARN") -> SentAmber
                                else -> SentOrange.copy(alpha = 0.6f)
                            }
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text("$type ", fontSize = 7.sp, color = color, fontWeight = FontWeight.Bold)
                                Text(msg, fontSize = 7.sp, color = SentOrange.copy(alpha = 0.65f), lineHeight = 10.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("DATA_STREAM_0X7F", fontSize = 7.sp, color = SentOrange.copy(alpha = 0.4f))
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFF1A0800), RoundedCornerShape(2.dp))) {
                        Box(modifier = Modifier.fillMaxWidth(0.65f).fillMaxHeight().background(SentOrange, RoundedCornerShape(2.dp)))
                    }
                }
            }

            // ═══ FOOTER ═══
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    .border(BorderStroke(1.dp, SentOrange.copy(alpha = 0.3f))).padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    listOf("CPU_TEMP" to "42°C", "ENCRYPT" to "AES-256", "SIGNAL" to "92dBm").forEach { (k, v) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(k, fontSize = 7.sp, color = SentOrange.copy(alpha = 0.5f))
                            Text(v, fontSize = 11.sp, color = if (k == "ENCRYPT") SentGreen else SentOrange)
                        }
                    }
                }
                Text("KAI_SENTINEL_EYE_01 // SECURE_RELAY_ACTIVE", fontSize = 7.sp, color = SentOrange.copy(alpha = 0.4f))
            }
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }

        // Reticle corner brackets
        ReticleCorners()
    }
}

@Composable
private fun ThermalZoneCard(zone: IntegrityZone, pulse: Float, modifier: Modifier, delay: Float = 0f) {
    val zoneColor = when (zone.health) {
        ZoneHealth.SECURE -> SentGreen
        ZoneHealth.WARNING -> SentAmber
        ZoneHealth.CRITICAL -> SentRed
    }
    Box(
        modifier = modifier
            .border(1.dp, SentOrange.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .background(Color(0xFF190A2E).copy(alpha = 0.4f), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Thermal glow bg
        Box(
            modifier = Modifier.fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(zoneColor.copy(alpha = 0.25f * (pulse - 0.7f)), Color.Transparent)
                    ),
                    RoundedCornerShape(4.dp)
                )
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(zone.label, fontSize = 9.sp, color = zoneColor, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(zone.status, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.9f), fontFamily = LEDFontFamily)
            Text(zone.detail, fontSize = 7.sp, color = zoneColor.copy(alpha = 0.65f))
        }
        Text(zone.id, fontSize = 7.sp, color = SentOrange.copy(alpha = 0.3f),
            modifier = Modifier.align(Alignment.TopStart).padding(4.dp))
    }
}

@Composable
private fun BoxScope.ReticleCorners() {
    // Top-left
    Canvas(modifier = Modifier.align(Alignment.TopStart).size(24.dp).padding(4.dp)) {
        drawLine(SentOrange.copy(alpha = 0.5f), Offset(0f, size.height), Offset(0f, 0f), 2f)
        drawLine(SentOrange.copy(alpha = 0.5f), Offset(0f, 0f), Offset(size.width, 0f), 2f)
    }
    Canvas(modifier = Modifier.align(Alignment.TopEnd).size(24.dp).padding(4.dp)) {
        drawLine(SentOrange.copy(alpha = 0.5f), Offset(size.width, 0f), Offset(0f, 0f), 2f)
        drawLine(SentOrange.copy(alpha = 0.5f), Offset(size.width, 0f), Offset(size.width, size.height), 2f)
    }
    Canvas(modifier = Modifier.align(Alignment.BottomStart).size(24.dp).padding(4.dp)) {
        drawLine(SentOrange.copy(alpha = 0.5f), Offset(0f, 0f), Offset(0f, size.height), 2f)
        drawLine(SentOrange.copy(alpha = 0.5f), Offset(0f, size.height), Offset(size.width, size.height), 2f)
    }
    Canvas(modifier = Modifier.align(Alignment.BottomEnd).size(24.dp).padding(4.dp)) {
        drawLine(SentOrange.copy(alpha = 0.5f), Offset(size.width, size.height), Offset(0f, size.height), 2f)
        drawLine(SentOrange.copy(alpha = 0.5f), Offset(size.width, size.height), Offset(size.width, 0f), 2f)
    }
}
