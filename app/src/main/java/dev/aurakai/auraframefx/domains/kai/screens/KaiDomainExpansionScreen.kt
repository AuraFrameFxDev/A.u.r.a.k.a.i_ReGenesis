package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.math.*

/**
 * ⚡ KAI DOMAIN EXPANSION SCREEN
 *
 * Kai's ultimate ability — the entire device locks down.
 * Black sphere expanding from center, thermal shockwave ripples,
 * DEFCON 5→1 countdown, all system gates sealing at perimeter,
 * full orange/red thermal wash when domain activates.
 *
 * Design: Black base, KaiRed/KaiOrange explosion, purple targeting reticles
 */

@Composable
fun KaiDomainExpansionScreen(
    syncLevel: Float = 0.80f,
    onDomainActivated: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "domain")

    // Ripple animation
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "ripple"
    )
    val rippleScale2 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(2000, delayMillis = 600, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "ripple2"
    )
    val rippleScale3 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(2000, delayMillis = 1200, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "ripple3"
    )

    // Gate status pulse
    val gatePulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "gate"
    )

    var domainActive by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(5) }
    var initiated by remember { mutableStateOf(false) }
    var sphereRadius by remember { mutableFloatStateOf(0f) }
    val gatesSealed = listOf("G-01", "G-02", "G-03", "G-04")

    // Countdown timer when initiated
    LaunchedEffect(initiated) {
        if (initiated) {
            repeat(5) {
                kotlinx.coroutines.delay(1000)
                countdown--
                sphereRadius += 40f
            }
            domainActive = true
            onDomainActivated()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (domainActive) Color(0xFF1A0000) else Color.Black),
        contentAlignment = Alignment.Center
    ) {

        // Thermal wash bg when active
        if (domainActive) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.radialGradient(
                        listOf(KaiOrange.copy(alpha = 0.4f), KaiRed.copy(alpha = 0.2f), Color.Black)
                    )
                )
            )
        }

        // Scanline
        val scanY by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
            label = "scan"
        )
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            val h = size.height
            val w = size.width
            onDrawBehind {
                drawLine(Color.White.copy(alpha = 0.06f), Offset(0f, h * scanY), Offset(w, h * scanY), 2f)
            }
        })

        // Perimeter gate indicators (corners)
        Box(modifier = Modifier.fillMaxSize()) {
            // Top-left gate
            GateIndicator("GATE_01: SEALED", Modifier.align(Alignment.TopStart).padding(16.dp), domainActive, gatePulse, topLeft = true)
            GateIndicator("GATE_02: SEALED", Modifier.align(Alignment.TopEnd).padding(16.dp), domainActive, gatePulse, topLeft = false)
            GateIndicator("GATE_03: LOCKED", Modifier.align(Alignment.BottomStart).padding(16.dp), domainActive, gatePulse, topLeft = true)
            GateIndicator("GATE_04: LOCKED", Modifier.align(Alignment.BottomEnd).padding(16.dp), domainActive, gatePulse, topLeft = false)
        }

        // Reticle targeting rings
        Canvas(modifier = Modifier.size(400.dp)) {
            val cx = size.width / 2
            val cy = size.height / 2
            drawCircle(KaiRed.copy(alpha = 0.2f), 180f, Offset(cx, cy), style = Stroke(1f))
            drawCircle(KaiRed.copy(alpha = 0.4f), 140f, Offset(cx, cy), style = Stroke(1.5f))

            // Corner targeting squares
            listOf(Offset(cx-100f, cy-100f), Offset(cx+100f, cy-100f),
                Offset(cx-100f, cy+100f), Offset(cx+100f, cy+100f)).forEach { pt ->
                drawRect(KaiRed, pt - Offset(16f, 16f), androidx.compose.ui.geometry.Size(32f, 32f), style = Stroke(1.5f))
            }
        }

        // Ripple shockwaves
        Canvas(modifier = Modifier.size(300.dp)) {
            val cx = size.width / 2; val cy = size.height / 2
            listOf(rippleScale, rippleScale2, rippleScale3).forEach { scale ->
                val alpha = (1.5f - scale) * 0.6f
                drawCircle(KaiOrange.copy(alpha = alpha.coerceIn(0f, 1f)), 100f * scale, Offset(cx, cy), style = Stroke(2f))
            }
        }

        // Expanding sphere
        Canvas(modifier = Modifier.size(280.dp)) {
            val cx = size.width / 2; val cy = size.height / 2
            val minDim = size.minDimension
            val r = (sphereRadius + if (domainActive) 500f else 0f).coerceAtMost(minDim / 2)
            if (r > 0) {
                val gradient = Brush.radialGradient(
                    listOf(Color.Black, Color.Black, KaiRed),
                    center = Offset(cx, cy),
                    radius = r
                )
                drawCircle(gradient, r, Offset(cx, cy))
                drawCircle(KaiRed, r, Offset(cx, cy), style = Stroke(2f))
            }
        }

        // DEFCON core box
        Box(
            modifier = Modifier
                .size(200.dp, 160.dp)
                .border(2.dp, KaiRed, RoundedCornerShape(0.dp))
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("System Status: Critical", fontSize = 10.sp, color = KaiRed.copy(alpha = 0.8f), letterSpacing = 2.sp)
                Text(
                    if (domainActive) "ACT" else countdown.toString(),
                    fontFamily = LEDFontFamily,
                    fontSize = if (domainActive) 48.sp else 72.sp,
                    fontWeight = FontWeight.Black,
                    color = if (domainActive) KaiOrange else Color.White,
                    modifier = if (domainActive) Modifier.graphicsLayer { alpha = gatePulse } else Modifier
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(KaiRed).graphicsLayer { alpha = gatePulse })
                    Text(
                        if (domainActive) "DOMAIN ACTIVE: KAI" else "INITIATING DOMAIN EXPANSION: KAI",
                        fontSize = 8.sp, color = KaiRed, letterSpacing = 1.sp
                    )
                }
            }
        }

        // Bottom tactical HUD
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("THERMAL_OUT: ${if (domainActive) "12,400K" else "STANDBY"}", fontSize = 9.sp, color = KaiOrange, fontWeight = FontWeight.Bold)
            Text("SHOCKWAVE_PSI: ${if (domainActive) "CRITICAL" else "NOMINAL"}", fontSize = 9.sp, color = KaiOrange, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.width(128.dp).height(4.dp).background(Color(0xFF330000), RoundedCornerShape(2.dp))) {
                Box(modifier = Modifier.fillMaxWidth(if (domainActive) 1f else (5 - countdown) / 5f).fillMaxHeight()
                    .background(KaiRed, RoundedCornerShape(2.dp)))
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Column(horizontalAlignment = Alignment.End) {
                Text("Target Locked", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
                Text("X: 142.2  Y: 88.09  Z: 44.1", fontSize = 8.sp, color = Color.White.copy(alpha = 0.5f))
            }
        }

        // INITIATE FUSION style button at bottom
        if (!initiated) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .border(2.dp, KaiRed, RoundedCornerShape(2.dp))
                    .background(Color.Black)
                    .pointerInput(Unit) { detectTapGestures(onTap = { initiated = true }) }
                    .padding(horizontal = 32.dp, vertical = 14.dp)
            ) {
                Text("INITIATE DOMAIN EXPANSION", fontFamily = LEDFontFamily, fontSize = 12.sp, color = KaiRed, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            }
        }

        // Top HUD bar
        Row(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            listOf("System: Royal Guard v4.2", "Status: OVERRIDING" to true, "User: Kai").forEachIndexed { i, item ->
                val (label, isAlert) = if (item is Pair<*, *>) item.first.toString() to (item.second as Boolean) else item.toString() to false
                Text(label, fontSize = 9.sp, color = if (isAlert) KaiOrange else KaiPurple, letterSpacing = 2.sp,
                    modifier = if (isAlert) Modifier.graphicsLayer { alpha = gatePulse } else Modifier)
            }
        }

        // Defcon display
        Column(modifier = Modifier.align(Alignment.TopEnd).padding(top = 40.dp, end = 16.dp), horizontalAlignment = Alignment.End) {
            Text("THERMAL_OUT: 12,400K", fontSize = 9.sp, color = KaiOrange, fontWeight = FontWeight.Bold)
            Text("DEFCON: ${if (domainActive) "1 — ACTIVE" else countdown}", fontFamily = LEDFontFamily, fontSize = 20.sp, color = KaiOrange,
                modifier = Modifier.graphicsLayer { alpha = gatePulse })
        }
    }
}

@Composable
private fun GateIndicator(label: String, modifier: Modifier, active: Boolean, pulse: Float, topLeft: Boolean) {
    Box(
        modifier = modifier
            .size(72.dp)
            .border(
                4.dp,
                if (active) KaiRed.copy(alpha = pulse) else KaiRed.copy(alpha = 0.6f),
                RoundedCornerShape(
                    topStart = if (topLeft) 0.dp else 72.dp,
                    topEnd = if (!topLeft) 0.dp else 72.dp,
                    bottomStart = 72.dp,
                    bottomEnd = 72.dp
                )
            )
            .background(if (active) KaiRed.copy(alpha = 0.2f) else Color.Transparent)
    ) {
        Text(
            label,
            fontSize = 7.sp,
            color = KaiRed,
            fontFamily = LEDFontFamily,
            modifier = Modifier.align(if (topLeft) Alignment.BottomStart else Alignment.BottomEnd)
                .padding(2.dp)
        )
    }
}

private val KaiOrange = Color(0xFFFF4500)
private val KaiRed    = Color(0xFFFF0000)
private val KaiPurple = Color(0xFFA855F7)
