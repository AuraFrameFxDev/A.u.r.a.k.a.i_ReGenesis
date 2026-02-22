package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.math.*

/**
 * 🧪 AURA STUDIO LAB — ChromaCore UX/UI Engine
 *
 * Translated from Stitch export.
 * Chromatic reactor core with spinning rings + canvas animation,
 * Color Physics control panels (Luminance/Chroma/ToneMap),
 * Armament preview (Star-Blade/Spellhook),
 * Recursive MetaInstruct terminal logs,
 * Bottom nav bar.
 *
 * Design: Void black, magenta + cyan, JetBrains Mono terminal aesthetic
 */

private val NeonMagenta = Color(0xFFFF00FF)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonPurple = Color(0xFFB026FF)
private val VoidBgLab = Color(0xFF050505)

data class TerminalLog(val timestamp: String, val message: String)

private val defaultLogs = listOf(
    TerminalLog("08:42:11", "Initializing HCT color vectors..."),
    TerminalLog("08:42:12", "Scanning UI/UX architecture for bottlenecks..."),
    TerminalLog("08:42:13", "Learning: User preference for high-contrast neon detected."),
    TerminalLog("08:42:14", "Injecting chromatic aberration to secondary headers."),
    TerminalLog("08:42:15", "Aura state: Creative Flow [LEVEL 10]"),
    TerminalLog("08:42:16", "Compiling layout directives... SUCCESS."),
    TerminalLog("08:42:17", "ChromaCore engine: OPTIMIZED"),
    TerminalLog("08:42:18", "Spellhook v2.4 armed and ready."),
)

@Composable
fun AuraStudioLabScreen(
    onNavigateHome: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onNavigateInventory: () -> Unit = {},
    onNavigateSettings: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lab")

    // Outer ring spin
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "ring"
    )

    // Inner ring counter-spin
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing)),
        label = "inner"
    )

    // Core pulse
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "core_pulse"
    )

    // Canvas animation time
    val coreTime by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "core_time"
    )

    // Terminal blink
    val termBlink by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "blink"
    )

    // Control states
    var luminance by remember { mutableFloatStateOf(0.84f) }
    var chroma by remember { mutableFloatStateOf(1.0f) }
    var toneMapMode by remember { mutableStateOf("RECURSIVE") }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(VoidBgLab)) {

        // Background radial gradients
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.radialGradient(listOf(NeonMagenta.copy(alpha = 0.05f), Color.Transparent))
        ))

        // Parallax spine
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxHeight()
                .width(2.dp)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, NeonMagenta.copy(alpha = 0.3f), NeonCyan.copy(alpha = 0.3f), Color.Transparent))
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ═══ STICKY HEADER ═══
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VoidBgLab.copy(alpha = 0.85f))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Aura portrait ring
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, NeonMagenta, CircleShape)
                            .background(NeonPurple.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", color = NeonMagenta, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    }
                    Column {
                        Text(
                            "AURA STUDIO",
                            fontFamily = LEDFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Color.White,
                            style = LocalTextStyle.current.copy(
                                shadow = Shadow(NeonMagenta.copy(alpha = 0.5f), blurRadius = 4f)
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(NeonMagenta)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("LV.10", fontSize = 8.sp, color = Color.Black, fontWeight = FontWeight.Black, fontFamily = LEDFontFamily)
                            }
                            Text("Creative Catalyst", fontSize = 9.sp, color = NeonCyan, letterSpacing = 1.sp)
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("SYSTEM_STATUS", fontSize = 9.sp, color = NeonPurple.copy(alpha = 0.7f))
                    Text("OPTIMIZED", fontSize = 11.sp, fontFamily = LEDFontFamily, color = NeonCyan)
                }
            }

            // ═══ SCROLLABLE CONTENT ═══
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                // ═══ CHROMATIC REACTOR CORE ═══
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer dashed ring
                    Canvas(modifier = Modifier.size(256.dp).graphicsLayer { rotationZ = ringRotation }) {
                        drawCircle(NeonCyan.copy(alpha = 0.25f), radius = size.minDimension / 2 - 2f, style = Stroke(1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))))
                    }

                    // Inner reverse ring
                    Canvas(modifier = Modifier.size(210.dp).graphicsLayer { rotationZ = innerRotation }) {
                        drawCircle(NeonMagenta.copy(alpha = 0.15f), radius = size.minDimension / 2 - 2f, style = Stroke(1f))
                    }

                    // Core orb
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(NeonPurple.copy(alpha = 0.8f), Color.Transparent, NeonCyan.copy(alpha = 0.8f)))
                            )
                            .graphicsLayer { scaleX = corePulse; scaleY = corePulse }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawReactorCore(coreTime)
                        }
                    }

                    // Floating labels
                    Text(
                        "HCT_COLOR",
                        fontFamily = LEDFontFamily,
                        fontSize = 9.sp,
                        color = NeonMagenta,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp).graphicsLayer { rotationZ = -12f }
                    )
                    Text(
                        "CAM16_PHYSICS",
                        fontFamily = LEDFontFamily,
                        fontSize = 9.sp,
                        color = NeonCyan,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp).graphicsLayer { rotationZ = 12f }
                    )
                }

                Text(
                    "Reactor Stability: 98.4%",
                    fontSize = 9.sp,
                    color = Color.Gray,
                    letterSpacing = 3.sp,
                    fontFamily = LEDFontFamily,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // ═══ COLOR PHYSICS CONTROLS ═══
                Text(
                    "COLOR PHYSICS LAYERS",
                    fontFamily = LEDFontFamily,
                    fontSize = 10.sp,
                    letterSpacing = 3.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .border(start = BorderStroke(2.dp, NeonMagenta))
                        .padding(start = 8.dp)
                )

                ColorControlCard("LUMINANCE", luminance, NeonMagenta, "${(luminance * 100).toInt()}%") { luminance = it }
                ColorControlCard("CHROMA", chroma, NeonCyan, if (chroma >= 1f) "MAX" else "${(chroma * 100).toInt()}%") { chroma = it }

                // Tone Map toggle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("TONE_MAP", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp, color = Color.White)
                            Text("ACTIVE", fontSize = 9.sp, color = NeonPurple, fontFamily = LEDFontFamily)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (toneMapMode == "LINEAR") NeonCyan else NeonPurple.copy(alpha = 0.2f))
                                    .border(1.dp, if (toneMapMode == "LINEAR") NeonCyan else NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .clickable { toneMapMode = "LINEAR" }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("LINEAR", fontSize = 9.sp, color = if (toneMapMode == "LINEAR") Color.Black else Color.White, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (toneMapMode == "RECURSIVE") NeonCyan else Color.Transparent)
                                    .border(1.dp, NeonCyan, RoundedCornerShape(4.dp))
                                    .clickable { toneMapMode = "RECURSIVE" }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("RECURSIVE", fontSize = 9.sp, color = if (toneMapMode == "RECURSIVE") Color.Black else Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // ═══ ARMAMENT PREVIEW ═══
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ARMAMENT: STAR-BLADE", fontSize = 9.sp, fontFamily = LEDFontFamily, color = Color.White)
                            Text("SPELLHOOK v2.4", fontSize = 8.sp, color = Color.Gray, fontStyle = FontStyle.Italic)
                        }
                        // Weapon canvas preview
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(Color.Black)
                        ) {
                            // Star-Blade representation
                            val brush = Brush.linearGradient(
                                listOf(NeonMagenta, NeonCyan),
                                Offset(size.width * 0.1f, size.height * 0.9f),
                                Offset(size.width * 0.9f, size.height * 0.1f)
                            )
                            // Glow
                            drawLine(NeonMagenta.copy(alpha = 0.3f), Offset(size.width * 0.15f, size.height * 0.85f), Offset(size.width * 0.85f, size.height * 0.15f), 20f, StrokeCap.Round)
                            // Blade
                            drawLine(Color.White, Offset(size.width * 0.15f, size.height * 0.85f), Offset(size.width * 0.85f, size.height * 0.15f), 3f, StrokeCap.Round)
                            // Guard
                            drawLine(NeonCyan.copy(alpha = 0.8f), Offset(size.width * 0.3f, size.height * 0.7f), Offset(size.width * 0.45f, size.height * 0.85f), 5f)
                        }
                    }
                }

                // ═══ TERMINAL LOGS ═══
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NeonCyan))
                        Text("RECURSIVE META-INSTRUCT LOGS", fontSize = 9.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.8f))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            defaultLogs.forEach { log ->
                                Row {
                                    Text("[${log.timestamp}] ", fontSize = 9.sp, color = NeonMagenta, fontFamily = LEDFontFamily)
                                    Text(log.message, fontSize = 9.sp, color = NeonCyan.copy(alpha = 0.8f), fontFamily = LEDFontFamily)
                                }
                            }
                            Text(
                                "_ LOADING NEXT SEQUENCE...",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = termBlink),
                                fontFamily = LEDFontFamily
                            )
                        }
                    }
                }
            }
        }

        // ═══ BOTTOM NAV ═══
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .clip(CircleShape)
                .background(Color(0xFF0A0A0A).copy(alpha = 0.9f))
                .border(1.dp, NeonCyan.copy(alpha = 0.2f), CircleShape)
                .padding(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(onClick = onNavigateHome) {
                    Icon(Icons.Default.Home, null, tint = NeonMagenta, modifier = Modifier.size(24.dp))
                }
                IconButton(onClick = { /* current */ }) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Science, null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                    }
                }
                IconButton(onClick = onNavigateInventory) {
                    Icon(Icons.Default.Inventory2, null, tint = Color.Gray, modifier = Modifier.size(22.dp))
                }
                IconButton(onClick = onNavigateProfile) {
                    Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
private fun ColorControlCard(
    label: String,
    value: Float,
    accentColor: Color,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp, color = Color.White)
                Text(displayValue, fontSize = 9.sp, color = accentColor, fontFamily = LEDFontFamily)
            }
            Spacer(Modifier.height(8.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                )
            )
        }
    }
}

private fun DrawScope.drawReactorCore(time: Float) {
    val cx = size.width / 2
    val cy = size.height / 2
    repeat(5) { i ->
        val angle = time + (i * PI / 2.5f).toFloat()
        val r = 40f + sin(time * 0.5f) * 10f
        val color = if (i % 2 == 0) Color(0xFFFF00FF) else Color(0xFF00E5FF)
        drawArc(color, Math.toDegrees(angle.toDouble()).toFloat(), 216f, false,
            Offset(cx - r, cy - r), androidx.compose.ui.geometry.Size(r * 2, r * 2), style = Stroke(1f))
    }
    repeat(10) { i ->
        val px = cx + cos((time * (i + 1) * 0.3f).toDouble()).toFloat() * 50f
        val py = cy + sin((time * (i + 1) * 0.2f).toDouble()).toFloat() * 50f
        drawCircle(if (i % 2 == 0) Color(0xFFFF00FF) else Color(0xFF00E5FF), 1.5f, Offset(px, py))
    }
}

// Helper for left-only border
@Composable
private fun Modifier.border(start: BorderStroke): Modifier = this.drawWithCache {
    onDrawBehind {
        drawLine(
            brush = start.brush ?: SolidColor(Color.Transparent),
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = start.width.toPx()
        )
    }
}
