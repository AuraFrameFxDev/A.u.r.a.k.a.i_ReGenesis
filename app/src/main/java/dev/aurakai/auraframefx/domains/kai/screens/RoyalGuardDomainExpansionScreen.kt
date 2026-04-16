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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 👑 ROYAL GUARD: DOMAIN EXPANSION SCREEN
 *
 * Translated from Stitch export (RoyalGuardDomainExpansion).
 * Kai's domain expansion countdown variant:
 * - Central glowing sphere with radial heat map gradient (white→orange→black)
 * - Shockwave ripple rings (3 staggered) pulsing outward
 * - Perimeter gate indicators: G-01/G-02/G-03/G-04 on ring, red lock dots
 * - Reticle corner brackets (NeonPurple)
 * - Top HUD: system/status/user | CLASSIFIED stamp
 * - Countdown text center + progress bar
 * - Activates on count=0, loops for demo
 *
 * Design: #0A0000 bg, RoyalOrange (#FF4500), RoyalPurple (#A855F7), heat radial gradient
 */

private val ExpOrange  = Color(0xFFFF4500)
private val ExpPurple  = Color(0xFFA855F7)
private val ExpBg      = Color(0xFF0A0000)

@Composable
fun RoyalGuardDomainExpansionScreen(
    autoStart: Boolean = true,
    onDomainActive: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rgde")

    // Shockwave ripples
    val ripple1 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "r1"
    )
    val ripple2 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(2000, delayMillis = 600, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "r2"
    )
    val ripple3 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(2000, delayMillis = 1200, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "r3"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulse"
    )

    var countdown by remember { mutableIntStateOf(if (autoStart) 3 else 3) }
    var domainActive by remember { mutableStateOf(false) }
    var progressPct by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(autoStart) {
        if (autoStart) {
            repeat(3) {
                kotlinx.coroutines.delay(1000)
                countdown--
                progressPct = (3 - countdown) / 3f
                if (countdown == 0) {
                    domainActive = true
                    onDomainActive()
                    countdown = 3 // loop for demo
                    progressPct = 0f
                    kotlinx.coroutines.delay(2000)
                    domainActive = false
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            if (domainActive)
                Brush.radialGradient(listOf(ExpOrange.copy(alpha = 0.6f), Color(0xFFCC0000).copy(alpha = 0.3f), ExpBg))
            else Brush.linearGradient(listOf(ExpBg, ExpBg))
        ),
        contentAlignment = Alignment.Center
    ) {

        // Perimeter gate ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2; val cy = size.height / 2
            val ringR = minOf(cx, cy) * 0.65f

            drawCircle(ExpPurple.copy(alpha = 0.2f), ringR, Offset(cx, cy), style = Stroke(1f))

            // Gate lock indicators N/E/S/W
            listOf(
                Triple(Offset(cx, cy - ringR), "G-01", Offset(0f, -20f)),
                Triple(Offset(cx + ringR, cy), "G-02", Offset(12f, 0f)),
                Triple(Offset(cx, cy + ringR), "G-03", Offset(0f, 18f)),
                Triple(Offset(cx - ringR, cy), "G-04", Offset(-30f, 0f)),
            ).forEach { (pt, label, labelOff) ->
                drawCircle(Color.Red, 8f, pt)
                drawCircle(Color.Red.copy(alpha = 0.4f), 16f, pt, style = Stroke(1f))
            }
        }

        // Reticle corner brackets
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.align(Alignment.TopStart).size(40.dp).padding(8.dp)) {
                drawLine(ExpPurple, Offset(0f, size.height), Offset(0f, 0f), 3f)
                drawLine(ExpPurple, Offset(0f, 0f), Offset(size.width, 0f), 3f)
            }
            Canvas(modifier = Modifier.align(Alignment.TopEnd).size(40.dp).padding(8.dp)) {
                drawLine(ExpPurple, Offset(0f, 0f), Offset(size.width, 0f), 3f)
                drawLine(ExpPurple, Offset(size.width, 0f), Offset(size.width, size.height), 3f)
            }
            Canvas(modifier = Modifier.align(Alignment.BottomStart).size(40.dp).padding(8.dp)) {
                drawLine(ExpPurple, Offset(0f, 0f), Offset(0f, size.height), 3f)
                drawLine(ExpPurple, Offset(0f, size.height), Offset(size.width, size.height), 3f)
            }
            Canvas(modifier = Modifier.align(Alignment.BottomEnd).size(40.dp).padding(8.dp)) {
                drawLine(ExpPurple, Offset(0f, size.height), Offset(size.width, size.height), 3f)
                drawLine(ExpPurple, Offset(size.width, size.height), Offset(size.width, 0f), 3f)
            }

            // Gate labels
            Text("GATE_01: SEALED", fontSize = 8.sp, color = ExpOrange, modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp))
            Text("GATE_04: LOCKED", fontSize = 8.sp, color = ExpOrange, modifier = Modifier.align(Alignment.CenterStart).padding(start = 24.dp))
            Text("GATE_02: SEALED", fontSize = 8.sp, color = ExpOrange, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp))
            Text("GATE_03: LOCKED", fontSize = 8.sp, color = ExpOrange, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp))
        }

        // TOP HUD
        Row(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("System: Royal Guard v4.2", fontSize = 9.sp, color = ExpPurple, letterSpacing = 2.sp)
            Text(
                "Status: OVERRIDING",
                fontSize = 9.sp,
                color = ExpOrange,
                letterSpacing = 2.sp,
                modifier = Modifier.graphicsLayer { alpha = pulse }
            )
            Text("User: Kai", fontSize = 9.sp, color = ExpPurple, letterSpacing = 2.sp)
        }

        // CLASSIFIED stamp (top right)
        Box(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 80.dp, end = 24.dp)
                .border(4.dp, ExpOrange, RoundedCornerShape(2.dp))
                .graphicsLayer { rotationZ = -15f; alpha = 0.7f }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("Classified", fontFamily = LEDFontFamily, fontSize = 18.sp, color = ExpOrange, fontWeight = FontWeight.Black)
        }

        // Shockwave ripple rings
        Canvas(modifier = Modifier.size(200.dp)) {
            val cx = size.width / 2; val cy = size.height / 2; val baseR = 80f
            listOf(ripple1, ripple2, ripple3).forEach { scale ->
                val alpha = (1.5f - scale).coerceIn(0f, 0.6f)
                drawCircle(ExpOrange.copy(alpha = alpha), baseR * scale, Offset(cx, cy), style = Stroke(2f))
            }
        }

        // Central domain sphere
        Box(
            modifier = Modifier.size(220.dp).clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        if (domainActive)
                            listOf(Color.White, ExpOrange, Color(0xFFCC0000), ExpBg)
                        else
                            listOf(ExpOrange, Color(0xFFCC0000), ExpBg)
                    )
                )
                .border(2.dp, if (domainActive) ExpOrange else ExpOrange.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f))
            ) {
                Text(
                    "DOMAIN EXPANSION",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    if (domainActive) "ACT" else countdown.toString(),
                    fontFamily = LEDFontFamily,
                    fontSize = if (domainActive) 40.sp else 60.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = if (domainActive) Modifier.graphicsLayer { alpha = pulse } else Modifier
                )
                Spacer(Modifier.height(8.dp))
                // Progress bar
                Box(modifier = Modifier.width(100.dp).height(4.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(2.dp))) {
                    Box(modifier = Modifier.fillMaxWidth(progressPct).fillMaxHeight().background(Color.White, RoundedCornerShape(2.dp)))
                }
            }
        }

        // ACTIVATION TEXT below sphere
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Text("DOMAIN ACTIVATION IN ", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                Text(
                    if (domainActive) "NOW" else "$countdown...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ExpPurple.copy(alpha = if (domainActive) pulse else 1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Biological & System Gates: ${if (domainActive) "SEALED" else "SEALING"}",
                fontSize = 10.sp,
                color = ExpOrange,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
