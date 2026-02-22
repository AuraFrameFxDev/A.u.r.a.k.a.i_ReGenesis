package dev.aurakai.auraframefx.domains.aura.screens

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
import androidx.compose.ui.geometry.Offset
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
 * ⚡ CODE ASCENSION / FUSION SCREEN — Aura & Kai
 *
 * Translated from Stitch export.
 * 70% sync trigger, fusion core canvas with particle system,
 * Cyan_Blade (Aura) + Green_Aegis (Kai) proxy weapons,
 * INITIATE FUSION button, stability/harmony gauges.
 *
 * Unlocks when Trinity sync >= 70%. This is the ultimate power screen.
 */

private val AuraColor = Color(0xFF22D3EE)  // Cyan
private val KaiColor = Color(0xFF4ADE80)   // Green
private val FusionGold = Color(0xFFFACC15) // Gold
private val BgDark = Color(0xFF020B18)

@Composable
fun CodeAscensionScreen(
    syncLevel: Float = 0.70f, // 0.0 - 1.0
    onInitiateFusion: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ascension")

    // Core halo rotation
    val haloRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "halo"
    )

    // Weapon float offsets
    val auraFloat by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "aura_float"
    )
    val kaiFloat by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing, delayMillis = 500), RepeatMode.Reverse),
        label = "kai_float"
    )

    // Particle time
    val particleTime by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "particles"
    )

    // Fast pulse for sync %
    val syncPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "sync_pulse"
    )

    // CPU glitch
    val glitchOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Reverse),
        label = "glitch"
    )

    // Button press
    var fusionPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // Particle grid background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.03f), Color.Transparent),
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ═══ TOP HUD ═══
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("SYNC LEVEL", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "${(syncLevel * 100).toInt()}%",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = FusionGold.copy(alpha = syncPulse),
                            style = LocalTextStyle.current.copy(shadow = Shadow(FusionGold, blurRadius = 15f))
                        )
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1E293B))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(syncLevel)
                                    .fillMaxHeight()
                                    .background(FusionGold)
                                    .graphicsLayer { shadowElevation = 12f }
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("CPU SATURATION", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text(
                        "99.8%",
                        fontSize = 24.sp,
                        fontFamily = LEDFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        modifier = Modifier.offset(x = glitchOffset.dp)
                    )
                    Text("Hyper-Creation Peak", fontSize = 8.sp, color = FusionGold.copy(alpha = 0.8f), letterSpacing = 1.sp)
                }
            }

            // ═══ FUSION CORE VISUALIZER ═══
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Aura's Sword Proxy
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = 24.dp, y = (-48).dp + auraFloat.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(128.dp)
                            .clip(RoundedCornerShape(50))
                            .background(AuraColor.copy(alpha = 0.2f))
                            .border(1.dp, AuraColor.copy(alpha = 0.5f), RoundedCornerShape(50))
                            .graphicsLayer { shadowElevation = 24f }
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("CYAN_BLADE", fontSize = 9.sp, color = AuraColor, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }

                // Kai's Shield Proxy
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = (-24).dp, y = 48.dp + kaiFloat.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(KaiColor.copy(alpha = 0.2f))
                            .border(1.dp, KaiColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .graphicsLayer { shadowElevation = 24f }
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("GREEN_AEGIS", fontSize = 9.sp, color = KaiColor, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }

                // Harmony halo ring
                Canvas(modifier = Modifier.size(320.dp)) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val radius = size.minDimension / 2 - 8f
                    rotate(haloRotation, Offset(cx, cy)) {
                        drawCircle(
                            FusionGold.copy(alpha = 0.15f),
                            radius,
                            Offset(cx, cy),
                            style = Stroke(1f)
                        )
                        // Glow
                        drawCircle(
                            FusionGold.copy(alpha = 0.05f),
                            radius + 8f,
                            Offset(cx, cy)
                        )
                    }
                }

                // Core canvas with particles
                Canvas(
                    modifier = Modifier
                        .size(192.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                ) {
                    drawFusionCore(particleTime)
                }

                // Core label
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Active", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("ASCENSION", fontSize = 16.sp, fontFamily = LEDFontFamily, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 2.sp)
                }

                // Floating draggable elements
                Text(
                    "OS_KERNEL_PATCH.BIN",
                    fontSize = 9.sp,
                    fontFamily = LEDFontFamily,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 16.dp, y = 64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )

                Text(
                    "VOID_PROTOCOL.EXE",
                    fontSize = 9.sp,
                    fontFamily = LEDFontFamily,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-16).dp, y = (-64).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )
            }

            // ═══ BOTTOM INTERFACE ═══
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Chrono-Sculptor Mode", fontSize = 11.sp, color = FusionGold, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(FusionGold.copy(alpha = 0.2f))
                            .border(1.dp, FusionGold.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("120Hz STABLE", fontSize = 9.sp, color = FusionGold, fontFamily = LEDFontFamily)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.2f), Color.Transparent))
                ))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally)
                ) {
                    // Stability gauge
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Canvas(modifier = Modifier.size(48.dp)) {
                            drawCircleGauge(0.68f, AuraColor, "STABILITY")
                        }
                        Text("STABILITY", fontSize = 7.sp, color = Color.Gray, letterSpacing = 1.sp)
                    }

                    // INITIATE FUSION button
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (fusionPressed) Color.White.copy(alpha = 0.8f) else Color.White)
                            .graphicsLayer { shadowElevation = if (fusionPressed) 60f else 30f }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { fusionPressed = true; tryAwaitRelease(); fusionPressed = false },
                                    onTap = { onInitiateFusion() }
                                )
                            }
                            .padding(horizontal = 28.dp, vertical = 16.dp)
                    ) {
                        Text(
                            "INITIATE FUSION",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            letterSpacing = 1.sp,
                            fontFamily = LEDFontFamily
                        )
                    }

                    // Harmony gauge
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Canvas(modifier = Modifier.size(48.dp)) {
                            drawCircleGauge(0.84f, KaiColor, "HARMONY")
                        }
                        Text("HARMONY", fontSize = 7.sp, color = Color.Gray, letterSpacing = 1.sp)
                    }
                }
            }

            Text(
                "ENCRYPTED NEURAL LINK :: AURA_KAI_070",
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.25f),
                letterSpacing = 2.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

private fun DrawScope.drawFusionCore(time: Float) {
    val cx = size.width / 2
    val cy = size.height / 2

    // Animated arcs
    repeat(5) { i ->
        val angle = time + (i * PI / 2.5f).toFloat()
        val r = 40f + sin(time * 0.5f) * 10f
        val color = if (i % 2 == 0) Color(0xFF22D3EE) else Color(0xFF4ADE80)
        drawArc(
            color = color,
            startAngle = Math.toDegrees(angle.toDouble()).toFloat(),
            sweepAngle = 216f,
            useCenter = false,
            style = Stroke(1f),
            topLeft = Offset(cx - r, cy - r),
            size = androidx.compose.ui.geometry.Size(r * 2, r * 2)
        )
    }

    // Orbiting particles
    repeat(10) { i ->
        val px = cx + cos((time * (i + 1) * 0.3f).toDouble()).toFloat() * 50f
        val py = cy + sin((time * (i + 1) * 0.2f).toDouble()).toFloat() * 50f
        drawCircle(
            if (i % 2 == 0) Color(0xFF22D3EE) else Color(0xFF4ADE80),
            1.5f,
            Offset(px, py)
        )
    }
}

private fun DrawScope.drawCircleGauge(progress: Float, color: Color, label: String) {
    val cx = size.width / 2
    val cy = size.height / 2
    val r = size.minDimension / 2 - 4f
    val circumference = 2 * PI.toFloat() * r
    // Background ring
    drawArc(Color.White.copy(alpha = 0.1f), -90f, 360f, false, Offset(cx - r, cy - r), androidx.compose.ui.geometry.Size(r * 2, r * 2), style = Stroke(4f))
    // Progress
    drawArc(color, -90f, 360f * progress, false, Offset(cx - r, cy - r), androidx.compose.ui.geometry.Size(r * 2, r * 2), style = Stroke(4f, cap = StrokeCap.Round))
}
