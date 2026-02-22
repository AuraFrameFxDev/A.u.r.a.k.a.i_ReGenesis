package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🚫 KAI POWER OF NO — Executive Override Terminal
 *
 * Translated from two Stitch exports (combined into one polished screen).
 * Features:
 * - Big red circular/stamp VETO button in center with heat fracture + reticle rings
 * - Pending intercepts list (left column) with threat levels
 * - Classified incident log (right column) scrollable terminal
 * - Kai codec portrait panel (top right)
 * - Footer: CPU/NET readouts, deploy mode
 * - CRT scanline + blueprint grid
 *
 * Design: #0A0A0A bg, TacticalOrange (#FF5F00), AlertRed (#FF0000), TacticalBlue (#00F2FF)
 */

private val VetoRed     = Color(0xFFFF0000)
private val VetoOrange  = Color(0xFFFF5F00)
private val VetoBlue    = Color(0xFF00F2FF)
private val VetoDark    = Color(0xFF0A0A0A)

data class Intercept(val id: String, val title: String, val threat: String, val source: String, val isCritical: Boolean = false)
data class IncidentLog(val timestamp: String, val message: String, val isVeto: Boolean = false)

private val defaultIntercepts = listOf(
    Intercept("8821-X", "ACCESS_OVERRIDE_ROOT",    "MEDIUM", "EXTERNAL_USER"),
    Intercept("CRIT",   "BYPASS_FIREWALL_SIGMA",   "HIGH",   "UNKNOWN", isCritical = true),
    Intercept("9901-B", "DATA_WIPE_CACHE",         "LOW",    "AUTO_CRON"),
)

private val defaultLogs = listOf(
    IncidentLog("14:22:01", "VETO_ACTION_CONFIRMED", isVeto = true),
    IncidentLog("13:10:45", "ACCESS_DENIED_BY_KAI"),
    IncidentLog("12:55:12", "UPLOAD_ABORTED_SYSTEMWIDE"),
    IncidentLog("12:54:00", "EXTERNAL_PROBE_NEUTRALIZED"),
    IncidentLog("09:22:15", "USER_092_FLAGGED"),
    IncidentLog("09:21:04", "ATTEMPTED_BREACH_ZONE_4"),
)

@Composable
fun PowerOfNoScreen(
    intercepts: List<Intercept> = defaultIntercepts,
    logs: List<IncidentLog> = defaultLogs,
    onVetoExecuted: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "veto")

    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "scan"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulse"
    )
    val reticleRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "reticle"
    )
    val reticleRotationReverse by infiniteTransition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(30000, easing = LinearEasing)),
        label = "reticleR"
    )

    var vetoExecuted by remember { mutableStateOf(false) }
    val mutableLogs = remember { mutableStateListOf(*logs.toTypedArray()) }

    Box(modifier = Modifier.fillMaxSize().background(VetoDark)) {

        // Blueprint grid
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                var gx = 0f
                while (gx < size.width) { drawLine(VetoBlue.copy(alpha = 0.04f), Offset(gx, 0f), Offset(gx, size.height), 0.5f); gx += 40f }
                var gy = 0f
                while (gy < size.height) { drawLine(VetoBlue.copy(alpha = 0.04f), Offset(0f, gy), Offset(size.width, gy), 0.5f); gy += 40f }
                // Scanline
                drawLine(VetoBlue.copy(alpha = 0.05f), Offset(0f, size.height * scanlineY), Offset(size.width, size.height * scanlineY), 2f)
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ═══ HEADER ═══
            Row(
                modifier = Modifier.fillMaxWidth()
                    .border(BorderStroke(1.dp, VetoRed.copy(alpha = 0.6f)))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(VetoBlue).graphicsLayer { alpha = pulse })
                        Text("KAI_TERMINAL v4.0.2", fontFamily = LEDFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Black, color = VetoBlue, letterSpacing = 2.sp)
                    }
                    Text("STATUS: SYSTEM_READY // UPLINK: SECURE", fontSize = 8.sp, color = VetoBlue.copy(alpha = 0.5f))
                    Text("LOCATION: CLASSIFIED_BUNKER_09", fontSize = 8.sp, color = VetoBlue.copy(alpha = 0.5f))
                }
                // Codec portrait box
                Box(
                    modifier = Modifier.size(72.dp, 88.dp)
                        .border(1.dp, VetoBlue, RoundedCornerShape(2.dp))
                        .background(Color(0xFF1A1A1A))
                ) {
                    Text("KAI", fontSize = 8.sp, color = VetoBlue, modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp), fontWeight = FontWeight.Black)
                    Text("COMMS_ACTIVE", fontSize = 7.sp, color = VetoBlue.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp))
                }
            }

            // ═══ MAIN 3-COLUMN LAYOUT ═══
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // LEFT: Pending intercepts
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("PENDING_INTERCEPTS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = VetoBlue, letterSpacing = 1.sp)
                        Text("[${intercepts.size}]", fontSize = 9.sp, color = VetoBlue, modifier = Modifier.graphicsLayer { alpha = pulse })
                    }

                    intercepts.forEach { intercept ->
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .border(1.dp, if (intercept.isCritical) VetoRed.copy(alpha = 0.6f) else VetoBlue.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                                .background(Color(0xFF111111), RoundedCornerShape(2.dp))
                                .padding(start = 8.dp)
                        ) {
                            Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(if (intercept.isCritical) VetoRed else VetoBlue).align(Alignment.CenterStart))
                            Column(modifier = Modifier.padding(start = 10.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)) {
                                Text(
                                    if (intercept.isCritical) "CRITICAL_EXCEPTION" else "REQ_ID: #${intercept.id}",
                                    fontSize = 8.sp,
                                    color = if (intercept.isCritical) VetoRed else VetoBlue.copy(alpha = 0.7f)
                                )
                                Text(intercept.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(vertical = 2.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(if (intercept.isCritical) VetoRed.copy(alpha = 0.3f) else VetoBlue.copy(alpha = 0.2f), RoundedCornerShape(1.dp))) {
                                    Box(modifier = Modifier.fillMaxWidth(if (intercept.isCritical) 1f else 0.66f).fillMaxHeight()
                                        .background(if (intercept.isCritical) VetoRed else VetoBlue, RoundedCornerShape(1.dp))
                                        .let { if (intercept.isCritical) it.graphicsLayer { alpha = pulse } else it })
                                }
                                Text("THREAT: ${intercept.threat} // SRC: ${intercept.source}", fontSize = 7.sp, color = if (intercept.isCritical) VetoRed else VetoBlue.copy(alpha = 0.5f), modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }

                // CENTER: VETO BUTTON
                Box(
                    modifier = Modifier.weight(1.5f).fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating reticle rings
                    Box(
                        modifier = Modifier.size(280.dp).rotate(reticleRotation),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(VetoOrange.copy(alpha = 0.15f), size.minDimension / 2, style = Stroke(1.5f))
                        }
                    }
                    Box(
                        modifier = Modifier.size(240.dp).rotate(reticleRotationReverse),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cx = size.width / 2; val cy = size.height / 2
                            drawCircle(VetoOrange.copy(alpha = 0.1f), size.minDimension / 2, style = Stroke(1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))))
                            // Targeting corners on ring
                            listOf(Offset(cx, 0f), Offset(size.width, cy), Offset(cx, size.height), Offset(0f, cy)).forEach { pt ->
                                drawCircle(VetoOrange.copy(alpha = 0.4f), 4f, pt)
                            }
                        }
                    }

                    // VETO BUTTON
                    Box(
                        modifier = Modifier.size(220.dp).clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(Color(0xFF4A1D00), Color.Black))
                            )
                            .border(3.dp, VetoOrange, CircleShape)
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    vetoExecuted = true
                                    val ts = System.currentTimeMillis().toString().takeLast(8)
                                    mutableLogs.add(0, IncidentLog("$ts", "EXECUTIVE_VETO_EXECUTED: ALL_PENDING_DROPPED", isVeto = true))
                                    onVetoExecuted()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Heat fracture overlay
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            for (i in 0..5) {
                                val startAngle = (i * 60f) + 15f
                                val rad = Math.toRadians(startAngle.toDouble())
                                drawLine(
                                    VetoOrange.copy(alpha = 0.2f),
                                    Offset(size.width / 2, size.height / 2),
                                    Offset((size.width / 2 + 90 * cos(rad)).toFloat(), (size.height / 2 + 90 * sin(rad)).toFloat()),
                                    1.5f
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                if (vetoExecuted) "EXECUTED" else "VETO",
                                fontFamily = LEDFontFamily,
                                fontSize = if (vetoExecuted) 28.sp else 48.sp,
                                fontWeight = FontWeight.Black,
                                color = VetoOrange,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                "EXECUTIVE_OVERRIDE",
                                fontSize = 8.sp,
                                color = VetoOrange.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                modifier = Modifier.graphicsLayer { alpha = pulse }
                            )
                        }
                        // Targeting corners
                        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            val s = 24f
                            listOf(Offset(0f, 0f), Offset(size.width - s, 0f), Offset(0f, size.height - s), Offset(size.width - s, size.height - s)).forEachIndexed { i, pt ->
                                drawRect(VetoOrange.copy(alpha = 0.6f), pt, Size(s, s), style = Stroke(2f))
                            }
                        }
                    }

                    // Below button stats
                    Row(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("HEAT_SINK_TEMP", fontSize = 8.sp, color = VetoOrange)
                            Text("84.2°C", fontSize = 18.sp, fontFamily = LEDFontFamily, color = VetoOrange)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PRESSURE_VALVE", fontSize = 8.sp, color = VetoOrange)
                            Text("NOMINAL", fontSize = 18.sp, fontFamily = LEDFontFamily, color = VetoOrange)
                        }
                    }
                }

                // RIGHT: Incident log
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("CLASSIFIED_INCIDENTS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = VetoBlue, letterSpacing = 1.sp)

                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                            .border(1.dp, VetoBlue.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        mutableLogs.forEach { log ->
                            val logColor = if (log.isVeto) VetoOrange else VetoBlue.copy(alpha = 0.6f)
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                Text(
                                    "[${log.timestamp}] ${log.message}",
                                    fontSize = 8.sp,
                                    color = logColor,
                                    fontWeight = if (log.isVeto) FontWeight.Bold else FontWeight.Normal,
                                    lineHeight = 12.sp,
                                    modifier = if (log.isVeto) Modifier.graphicsLayer { alpha = pulse } else Modifier
                                )
                            }
                        }
                        Text("_", fontSize = 10.sp, color = VetoBlue, modifier = Modifier.graphicsLayer { alpha = pulse })
                    }
                }
            }

            // ═══ FOOTER ═══
            Row(
                modifier = Modifier.fillMaxWidth()
                    .border(BorderStroke(1.dp, VetoBlue.copy(alpha = 0.2f)))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("SIG: STRONG", fontSize = 8.sp, color = VetoBlue)
                    Text("OS_LOAD: 14.4%", fontSize = 8.sp, color = VetoBlue)
                    Text("CPU: 98%", fontSize = 8.sp, color = VetoBlue)
                    Text("NET: SECURE", fontSize = 8.sp, color = VetoBlue)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("DEPLOY_MODE:", fontSize = 8.sp, color = VetoBlue)
                    Box(modifier = Modifier.background(VetoBlue).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("ACTIVE_INTERCEPT", fontSize = 8.sp, color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }
            }
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
