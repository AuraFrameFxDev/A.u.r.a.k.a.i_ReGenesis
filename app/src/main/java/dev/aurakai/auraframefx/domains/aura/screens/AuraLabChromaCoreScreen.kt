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
 * 🎨 AURA'S LAB — ChromaCore UX/UI
 *
 * Visual: Parallax spine (magenta→cyan gradient line), Color Physics Reactor (spinning rings,
 * core orb with canvas particles), control panels (LUMINANCE, CHROMA, TONE_MAP sliders),
 * STAR-BLADE weapon preview, recursive meta-instruct terminal log, bottom tab nav.
 *
 * Colors: #FF00FF neon-magenta / #00E5FF neon-cyan / #B026FF neon-purple / #050505 void
 */

private val NeonMagenta = Color(0xFFFF00FF)
private val NeonCyan    = Color(0xFF00E5FF)
private val NeonPurple  = Color(0xFFB026FF)
private val VoidBlack   = Color(0xFF050505)

data class ColorLayer(val label: String, val valueLabelColor: Color, val valueText: String, var sliderFraction: Float)

private val defaultLayers = listOf(
    ColorLayer("LUMINANCE", NeonCyan, "84%", 0.84f),
    ColorLayer("CHROMA", NeonMagenta, "MAX", 1f),
    ColorLayer("TONE_MAP", NeonPurple, "ACTIVE", 0.6f),
)

private val terminalLogs = listOf(
    "[08:42:11] Initializing HCT color vectors...",
    "[08:42:12] Scanning UI/UX architecture for bottlenecks...",
    "[08:42:13] Learning: User preference for high-contrast neon detected.",
    "[08:42:14] Injecting chromatic aberration to secondary headers.",
    "[08:42:15] Aura state: Creative Flow [LEVEL 10]",
    "[08:42:16] Compiling layout directives... SUCCESS.",
)

@Composable
fun AuraLabChromaCoreScreen(
    layers: List<ColorLayer> = defaultLayers,
    onTabSelect: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura_lab")
    val spinOuter by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "outer"
    )
    val spinInner by infiniteTransition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing)),
        label = "inner"
    )
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000), RepeatMode.Reverse),
        label = "core"
    )
    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "particles"
    )
    val terminalBlink by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "blink"
    )

    var selectedTab by remember { mutableIntStateOf(1) }
    val layerState = remember { layers.map { it.sliderFraction.toMutableStateOf() }.toMutableList() }

    Box(modifier = Modifier.fillMaxSize().background(VoidBlack)) {

        // Radial glow corners
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                drawCircle(NeonMagenta.copy(alpha = 0.04f), size.width * 0.5f, Offset(size.width * 0.1f, size.height * 0.2f))
                drawCircle(NeonCyan.copy(alpha = 0.04f), size.width * 0.5f, Offset(size.width * 0.9f, size.height * 0.8f))
            }
        })

        // Parallax spine
        Box(modifier = Modifier.fillMaxHeight().align(Alignment.CenterStart).padding(start = 8.dp)) {
            Box(
                modifier = Modifier.width(2.dp).fillMaxHeight(0.8f).align(Alignment.Center)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, NeonMagenta, NeonCyan, Color.Transparent)))
            )
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars).padding(top = 4.dp))

            // ── HEADER ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                            .border(2.dp, NeonMagenta, CircleShape)
                            .background(NeonMagenta.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) { Text("A", fontFamily = LEDFontFamily, fontSize = 20.sp, color = NeonMagenta, fontWeight = FontWeight.Black) }
                    Column {
                        Text("AURA STUDIO", fontFamily = LEDFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.background(NeonMagenta, RoundedCornerShape(2.dp)).padding(horizontal = 4.dp, vertical = 1.dp)) {
                                Text("LV.10", fontSize = 7.sp, color = VoidBlack, fontWeight = FontWeight.Black)
                            }
                            Text("Creative Catalyst", fontSize = 9.sp, color = NeonCyan, letterSpacing = 1.sp)
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("SYSTEM_STATUS", fontSize = 8.sp, color = NeonPurple.copy(alpha = 0.7f))
                    Text("OPTIMIZED", fontFamily = LEDFontFamily, fontSize = 11.sp, color = NeonCyan)
                }
            }

            // ── CHROMATIC REACTOR CORE ──
            Box(modifier = Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                // Floating label TL
                Text("HCT_COLOR", fontSize = 8.sp, color = NeonMagenta, fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.TopStart).padding(start = 20.dp))
                // Floating label BR
                Text("CAM16_PHYSICS", fontSize = 8.sp, color = NeonCyan, fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp))

                // Outer dashed ring
                Box(modifier = Modifier.size(220.dp).graphicsLayer { rotationZ = spinOuter }.drawWithCache {
                    onDrawBehind {
                        drawCircle(NeonCyan.copy(alpha = 0.3f), size.minDimension / 2, style = Stroke(1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 6f))))
                    }
                })
                // Inner ring reverse
                Box(modifier = Modifier.size(195.dp).graphicsLayer { rotationZ = spinInner }.drawWithCache {
                    onDrawBehind {
                        drawCircle(NeonMagenta.copy(alpha = 0.2f), size.minDimension / 2, style = Stroke(1f))
                    }
                })
                // Core orb
                Box(
                    modifier = Modifier.size(140.dp).clip(CircleShape)
                        .background(Brush.radialGradient(listOf(NeonPurple.copy(alpha = 0.3f + corePulse * 0.1f), NeonCyan.copy(alpha = 0.1f), Color.Transparent)))
                        .drawWithCache {
                            onDrawBehind {
                                val cx = size.width / 2; val cy = size.height / 2
                                for (i in 0..9) {
                                    val angle = particlePhase * 2 * PI.toFloat() + i * 0.628f
                                    val r = 30f + sin(particlePhase * PI.toFloat() * 2 + i) * 10f
                                    val px = cx + cos(angle) * r; val py = cy + sin(angle) * r
                                    drawCircle(if (i % 2 == 0) NeonMagenta.copy(alpha = 0.8f) else NeonCyan.copy(alpha = 0.8f), 2.5f, Offset(px, py))
                                }
                                // Arcs
                                for (i in 0..4) {
                                    val a = particlePhase * 2 * PI.toFloat() + i * 1.256f
                                    val r = 40f + sin(particlePhase * PI.toFloat()) * 10f
                                    drawArc(if (i % 2 == 0) NeonMagenta.copy(alpha = 0.5f) else NeonCyan.copy(alpha = 0.5f),
                                        Math.toDegrees(a.toDouble()).toFloat(), 216f, false, style = Stroke(1f))
                                }
                            }
                        }
                )
            }
            Text("Reactor Stability: 98.4%", fontSize = 9.sp, color = Color.Gray, letterSpacing = 2.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp))

            // ── COLOR PHYSICS LAYERS ──
            Text("COLOR PHYSICS LAYERS", fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f), letterSpacing = 3.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp).border(BorderStroke(0.dp, Color.Transparent)).drawWithCache {
                    onDrawBehind { drawLine(NeonMagenta, Offset(-8f, size.height / 2), Offset(-2f, size.height / 2), 12f) }
                })

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                layers.forEachIndexed { index, layer ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(layer.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 3.sp)
                                Text(layer.valueText, fontSize = 8.sp, color = layer.valueLabelColor, fontFamily = LEDFontFamily)
                            }
                            if (index < 2) {
                                // Slider
                                Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color(0xFF1A1A1A), RoundedCornerShape(3.dp))) {
                                    Box(modifier = Modifier.fillMaxWidth(layerState.getOrNull(index)?.value ?: 0.5f).fillMaxHeight()
                                        .background(if (index == 0) NeonMagenta else NeonCyan, RoundedCornerShape(3.dp))
                                    )
                                }
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("LINEAR" to false, "RECURSIVE" to true).forEach { (label, active) ->
                                        Box(
                                            modifier = Modifier.weight(1f)
                                                .background(if (active) NeonCyan else NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .border(1.dp, if (active) NeonCyan else NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (active) VoidBlack else Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── STAR-BLADE PREVIEW ──
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(16.dp))
                    .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.05f)).padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ARMAMENT: STAR-BLADE", fontSize = 8.sp, fontFamily = LEDFontFamily, color = Color.White)
                        Text("SPELLHOOK v2.4", fontSize = 7.sp, color = Color.White.copy(alpha = 0.4f))
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        // Weapon visual
                        Box(modifier = Modifier.fillMaxSize().drawWithCache {
                            onDrawBehind {
                                val bladePath = Path()
                                bladePath.moveTo(size.width * 0.1f, size.height * 0.9f)
                                bladePath.lineTo(size.width * 0.9f, size.height * 0.1f)
                                bladePath.quadraticBezierTo(size.width * 0.93f, size.height * 0.07f, size.width * 0.95f, size.height * 0.1f)
                                bladePath.lineTo(size.width * 0.9f, size.height * 0.16f)
                                bladePath.lineTo(size.width * 0.1f, size.height * 0.96f)
                                bladePath.close()
                                drawPath(bladePath, Brush.linearGradient(listOf(NeonMagenta.copy(alpha = 0.9f), NeonCyan.copy(alpha = 0.9f)), Offset(0f, size.height), Offset(size.width, 0f)))
                            }
                        })
                        Text("STAR-BLADE", fontFamily = LEDFontFamily, fontSize = 20.sp, color = Color.White.copy(alpha = 0.1f), fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── TERMINAL ──
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NeonCyan).graphicsLayer { alpha = 0.5f + terminalBlink * 0.5f })
                    Text("RECURSIVE META-INSTRUCT LOGS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 2.sp)
                }
                Box(
                    modifier = Modifier.fillMaxWidth().height(110.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        terminalLogs.forEach { log ->
                            val parts = log.split("] ", limit = 2)
                            Row {
                                if (parts.size == 2) {
                                    Text("[${parts[0].trimStart('[')}", fontSize = 8.sp, color = NeonMagenta, fontFamily = LEDFontFamily)
                                    Text("] ${parts[1]}", fontSize = 8.sp, color = NeonCyan.copy(alpha = 0.8f), fontFamily = LEDFontFamily, lineHeight = 11.sp)
                                } else {
                                    Text(log, fontSize = 8.sp, color = NeonCyan.copy(alpha = 0.8f))
                                }
                            }
                        }
                        Text("_ LOADING NEXT SEQUENCE...", fontSize = 8.sp, color = Color.White, fontFamily = LEDFontFamily,
                            modifier = Modifier.graphicsLayer { alpha = terminalBlink })
                    }
                }
            }

            Spacer(Modifier.height(80.dp)) // Space for bottom nav
        }

        // ── BOTTOM TAB NAV ──
        Box(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 24.dp)
                .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(50))
                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(50))
                .padding(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                val icons = listOf("🏠", "⚡", "📦", "👤")
                icons.forEachIndexed { i, icon ->
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                            .background(if (i == selectedTab) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                            .border(if (i == selectedTab) 1.dp else 0.dp, if (i == selectedTab) NeonCyan.copy(alpha = 0.5f) else Color.Transparent, CircleShape)
                            .clickable { selectedTab = i; onTabSelect(i) },
                        contentAlignment = Alignment.Center
                    ) { Text(icon, fontSize = 16.sp) }
                }
            }
        }
    }
}

@Composable
private fun Float.toMutableStateOf() = mutableFloatStateOf(this)
