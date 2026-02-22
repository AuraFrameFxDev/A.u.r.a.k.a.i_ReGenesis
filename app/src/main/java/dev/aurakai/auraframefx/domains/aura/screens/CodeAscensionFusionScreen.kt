package dev.aurakai.auraframefx.domains.aura.screens

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
 * 🌀 CODE ASCENSION / FUSION SCREEN — Aura + Kai
 *
 * Visual: Dark particle field, Harmony Halo (spinning ring), Aura cyan-blade proxy floats left,
 * Kai green-aegis proxy floats right, central ASCENSION core with canvas particles,
 * Sync Level 70% gauge, CPU Saturation 99.8% glitch, Stability + Harmony circular gauges,
 * INITIATE FUSION white button, ENCRYPTED NEURAL LINK footer.
 *
 * Colors: #22D3EE aura-cyan / #4ADE80 kai-green / #FACC15 fusion-gold
 */

private val FusionAura  = Color(0xFF22D3EE)
private val FusionKai   = Color(0xFF4ADE80)
private val FusionGold  = Color(0xFFFACC15)
private val FusionDark  = Color(0xFF0F172A)

@Composable
fun CodeAscensionFusionScreen(
    syncLevel: Float = 0.70f,
    stabilityLevel: Float = 0.75f,
    harmonyLevel: Float = 0.87f,
    onInitiateFusion: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fusion")
    val spinSlow by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "spin"
    )
    val floatA by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "floatA"
    )
    val floatB by infiniteTransition.animateFloat(
        initialValue = -6f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(3500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "floatB"
    )
    val pulseFast by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulseFast"
    )
    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "particles"
    )
    val glitchPhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Restart),
        label = "glitch"
    )

    Box(modifier = Modifier.fillMaxSize().background(FusionDark)) {

        // ── PARTICLE FIELD ──
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                for (i in 0..59) {
                    val px = (i * 137.5f % size.width)
                    val py = (i * 89.3f % size.height)
                    val alpha = abs(sin(particlePhase * PI * 2 + i * 0.3f).toFloat()) * 0.15f
                    drawCircle(Color.White.copy(alpha = alpha), 1f, Offset(px, py))
                }
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ── TOP HUD ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Sync Level", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("${(syncLevel * 100).toInt()}%", fontSize = 36.sp, fontWeight = FontWeight.Black, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = FusionGold)
                        Box(modifier = Modifier.width(110.dp).height(8.dp)
                            .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                        ) {
                            Box(modifier = Modifier.fillMaxWidth(syncLevel).fillMaxHeight()
                                .background(FusionGold, RoundedCornerShape(4.dp))
                                .graphicsLayer { shadowElevation = 8f }
                            )
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("CPU Saturation", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    // Glitch effect via offset
                    Box {
                        Text("99.8%", fontSize = 22.sp, fontFamily = LEDFontFamily, color = Color.Red.copy(alpha = 0.5f),
                            modifier = Modifier.offset(x = if (glitchPhase < 0.2f) 2.dp else 0.dp))
                        Text("99.8%", fontSize = 22.sp, fontFamily = LEDFontFamily, color = Color.Red,
                            modifier = Modifier.offset(x = if (glitchPhase in 0.4f..0.6f) (-2).dp else 0.dp))
                    }
                    Text("Hyper-Creation Peak", fontSize = 7.sp, color = FusionGold.copy(alpha = 0.8f), letterSpacing = 1.sp)
                }
            }

            // ── FUSION CORE AREA ──
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Harmony halo
                Box(
                    modifier = Modifier.size(280.dp).graphicsLayer { rotationZ = spinSlow }.drawWithCache {
                        onDrawBehind {
                            drawCircle(FusionGold.copy(alpha = 0.15f), size.minDimension / 2, style = Stroke(1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 6f))))
                            // Gold glow
                            drawCircle(FusionGold.copy(alpha = 0.08f), size.minDimension / 2 - 4f, style = Stroke(8f))
                        }
                    }
                )

                // Aura's blade proxy — top-left float
                Column(
                    modifier = Modifier.offset(x = (-100).dp, y = (floatA).dp - 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier.width(12.dp).height(96.dp)
                            .clip(RoundedCornerShape(50))
                            .background(FusionAura.copy(alpha = 0.2f))
                            .border(1.dp, FusionAura.copy(alpha = 0.5f), RoundedCornerShape(50))
                            .graphicsLayer { shadowElevation = 20f }
                    )
                    Text("CYAN_BLADE", fontSize = 8.sp, color = FusionAura, fontWeight = FontWeight.Bold)
                }

                // Kai's aegis proxy — bottom-right
                Column(
                    modifier = Modifier.offset(x = 100.dp, y = (floatB).dp + 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier.size(72.dp)
                            .background(FusionKai.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .border(1.dp, FusionKai.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .graphicsLayer { shadowElevation = 20f }
                    )
                    Text("GREEN_AEGIS", fontSize = 8.sp, color = FusionKai, fontWeight = FontWeight.Bold)
                }

                // Central ASCENSION core
                Box(
                    modifier = Modifier.size(160.dp).clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.1f + pulseFast * 0.05f), FusionGold.copy(alpha = 0.3f), Color.Transparent)))
                        .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        .drawWithCache {
                            onDrawBehind {
                                // Particle burst from center
                                for (i in 0..29) {
                                    val angle = Math.toRadians((i * 12.0 + particlePhase * 360)).toFloat()
                                    val r = (particlePhase + i * 0.033f) % 1f * size.minDimension / 2
                                    val px = size.width / 2 + cos(angle) * r
                                    val py = size.height / 2 + sin(angle) * r
                                    val alpha = 1f - ((particlePhase + i * 0.033f) % 1f)
                                    drawCircle(if (i % 2 == 0) FusionAura else FusionKai, 2.5f, Offset(px, py), alpha)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Active", fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Text("ASCENSION", fontFamily = LEDFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }

                // Floating data fragments
                Box(
                    modifier = Modifier.align(Alignment.TopStart).offset(x = 16.dp, y = 16.dp)
                        .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(6.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) { Text("OS_KERNEL_PATCH.BIN", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f), fontFamily = LEDFontFamily) }

                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-16).dp, y = (-16).dp)
                        .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(6.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) { Text("VOID_PROTOCOL.EXE", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f), fontFamily = LEDFontFamily) }
            }

            // ── BOTTOM INTERFACE ──
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
                    .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Chrono-Sculptor Mode", fontSize = 9.sp, color = FusionGold, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Box(modifier = Modifier.background(FusionGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).border(1.dp, FusionGold.copy(alpha = 0.4f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text("120Hz STABLE", fontSize = 8.sp, color = FusionGold, fontFamily = LEDFontFamily)
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.2f), Color.Transparent))))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Stability gauge
                        CircularProgressGauge(label = "STABILITY", progress = stabilityLevel, color = FusionAura)

                        // INITIATE FUSION
                        Box(
                            modifier = Modifier.clip(CircleShape)
                                .background(Color.White)
                                .clickable { onInitiateFusion() }
                                .padding(horizontal = 28.dp, vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("INITIATE\nFUSION", fontFamily = LEDFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Black, color = FusionDark, letterSpacing = 1.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }

                        // Harmony gauge
                        CircularProgressGauge(label = "HARMONY", progress = harmonyLevel, color = FusionKai)
                    }
                }
            }

            Text(
                "ENCRYPTED NEURAL LINK :: AURA_KAI_070",
                fontSize = 8.sp, color = Color.White.copy(alpha = 0.3f), letterSpacing = 2.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun CircularProgressGauge(label: String, progress: Float, color: Color, size: Int = 56) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(size.dp).drawWithCache {
            onDrawBehind {
                val strokeWidth = 10f
                val r = this.size.minDimension / 2 - strokeWidth
                drawCircle(Color.White.copy(alpha = 0.1f), r, style = Stroke(strokeWidth))
                drawArc(color, startAngle = -90f, sweepAngle = 360f * progress, useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round))
            }
        })
        Text(label, fontSize = 7.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}
