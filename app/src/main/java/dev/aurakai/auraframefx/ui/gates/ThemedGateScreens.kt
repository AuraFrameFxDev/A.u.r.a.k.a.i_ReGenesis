package dev.aurakai.auraframefx.ui.gates

// ═══════════════════════════════════════════════════════════════════════════════
// ThemedGateScreens.kt
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// Three gate screens with their visual backgrounds wired in:
//
//  LsposedGateScreen          → Hex corridor background (Image 3)
//  HelpServicesGateScreen     → Purple grid room + ribbons (Image 4)
//  CollabCanvasGateScreen     → Eye Rune gate card (Image 9)
//  TerminalGateScreen         → Gemini/Aura pixel-art code swirl backdrop (Image 8)
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.*

// ── LSPosed Gate Screen ───────────────────────────────────────────────────────

@Composable
fun LsposedGateScreen(navController: NavController, onNavigateBack: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Layer 1: Hex corridor
        HexCorridorBackground(modifier = Modifier.fillMaxSize(), tint = Color(0xFF4A9EFF))

        // Layer 2: Semi-transparent overlay for content
        Box(modifier = Modifier.fillMaxSize().background(Color(0xBB010810)))

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            GateScreenHeader("LSPosed MODULES", Color(0xFF4A9EFF), navController, onNavigateBack)

            // Quick stats HUD (hex corridor style panels)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HexStatPanel("MODULES", "47", Color(0xFF4A9EFF), Modifier.weight(1f))
                HexStatPanel("ACTIVE",  "31", Color(0xFF00FF80), Modifier.weight(1f))
                HexStatPanel("HOOKS",   "124",Color(0xFFFFD700), Modifier.weight(1f))
                HexStatPanel("ROOT",    "YES", Color(0xFFFF2D78), Modifier.weight(1f))
            }

            // Module categories
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                val moduleCategories = listOf(
                    Triple("UI Modifications",      14, Color(0xFF4A9EFF)),
                    Triple("System Tweaks",         9,  Color(0xFF00FFFF)),
                    Triple("Privacy & Security",    6,  Color(0xFF9B30FF)),
                    Triple("Performance",           5,  Color(0xFF00FF80)),
                    Triple("Developer Tools",       7,  Color(0xFFFFD700)),
                    Triple("Accessibility",         3,  Color(0xFFFF9B00)),
                    Triple("AuraFrameFx Modules",   3,  Color(0xFFFF2D78)),
                )
                items(moduleCategories) { (name, count, color) ->
                    ModuleCategoryRow(name = name, count = count, color = color) {
                        navController.navigate("xposed_panel")
                    }
                }
            }
        }
    }
}

@Composable
private fun HexStatPanel(label: String, value: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier.height(56.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, color.copy(0.5f), RoundedCornerShape(4.dp))
            .background(color.copy(0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 7.sp, letterSpacing = 1.sp, color = color.copy(0.6f))
        }
    }
}

@Composable
private fun ModuleCategoryRow(name: String, count: Int, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(0.25f), RoundedCornerShape(6.dp))
            .background(color.copy(0.06f))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(color))
            Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(0.85f))
        }
        Text("$count mods", fontSize = 9.sp, color = color.copy(0.7f))
    }
}

// ── Help Services Gate Screen ─────────────────────────────────────────────────

@Composable
fun HelpServicesGateScreen(navController: NavController, onNavigateBack: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Layer 1: Purple grid room
        PurpleGridRoomBackground(modifier = Modifier.fillMaxSize())

        // Layer 2: Content overlay
        Box(modifier = Modifier.fillMaxSize().background(Color(0xBB010010)))

        Column(modifier = Modifier.fillMaxSize()) {
            GateScreenHeader("HELP SERVICES", Color(0xFFBB80FF), navController, onNavigateBack)

            // Ribbon decoration (Image 5 style ribbons as section dividers)
            Box(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                InfinityRibbonBackground(
                    modifier = Modifier.fillMaxSize(),
                    colorA = Color(0xFF9B30FF),
                    colorB = Color(0xFF4A9EFF)
                )
                Text(
                    "HOW CAN WE HELP?",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp, color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Help service tiles
            val helpItems = listOf(
                Triple("FAQ Browser",          "Quick answers",                     Color(0xFF4A9EFF)),
                Triple("Live Support Chat",    "Real-time AI assistance",           Color(0xFF00FF80)),
                Triple("Tutorial Videos",      "Step-by-step walkthroughs",         Color(0xFFFFD700)),
                Triple("Troubleshoot Wizard",  "Diagnose & fix issues",             Color(0xFFFF9B00)),
                Triple("Documentation Hub",    "Full API & module docs",            Color(0xFFBB80FF)),
                Triple("Beta Feedback",        "Report bugs to the 184 testers",    Color(0xFFFF2D78)),
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(helpItems) { (title, desc, color) ->
                    HelpServiceTile(title = title, description = desc, color = color) {
                        // Navigation logic per tile
                        when (title) {
                            "Live Support Chat" -> navController.navigate("live_support_chat")
                            "FAQ Browser"       -> navController.navigate("faq_browser")
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HelpServiceTile(title: String, description: String, color: Color, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "help_tile")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "shimmer"
    )

    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Brush.horizontalGradient(listOf(color.copy(shimmer), color.copy(shimmer * 0.3f))), RoundedCornerShape(8.dp))
            .background(color.copy(0.07f))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)).background(color.copy(0.2f)).border(1.dp, color.copy(0.5f), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center) {
            Text("?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(0.9f))
            Text(description, fontSize = 10.sp, color = Color.White.copy(0.45f))
        }
        Text("→", fontSize = 14.sp, color = color.copy(0.6f))
    }
}

// ── Terminal Gate Screen (Image 8 — Gemini pixel code swirl) ─────────────────

@Composable
fun TerminalGateScreen(navController: NavController, onNavigateBack: () -> Unit = {}) {
    val infiniteTransition = rememberInfiniteTransition(label = "terminal")
    val codeScroll by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "scroll"
    )
    val swirlT by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "swirl"
    )
    val scanPulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "scan"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // Code swirl background (purple/cyan pixel art style — Image 8)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color(0xFF050015))

            // Purple swirl arms
            for (arm in 0..2) {
                val armOffset = arm * (2 * PI / 3).toFloat()
                val swirlPath = Path()
                for (i in 0..80) {
                    val t = i.toFloat() / 80f * 2 * PI.toFloat()
                    val r = size.width * 0.08f + t * size.width * 0.08f
                    val x = size.width / 2f + r * cos(t + swirlT + armOffset)
                    val y = size.height / 2f + r * sin(t + swirlT + armOffset) * 0.6f
                    if (i == 0) swirlPath.moveTo(x, y) else swirlPath.lineTo(x, y)
                }
                val colors = listOf(Color(0xFF9B30FF), Color(0xFF00BFFF), Color(0xFF9B30FF))
                drawPath(swirlPath, Brush.sweepGradient(colors), style = Stroke(width = 20f, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.cornerPathEffect(8f)))
                drawPath(swirlPath, Brush.sweepGradient(listOf(Color(0xFF00BFFF), Color(0xFF9B30FF), Color(0xFF00BFFF))), style = Stroke(width = 3f))
            }

            // Scan line
            val scanY = size.height * scanPulse
            drawLine(Color(0xFF00FFFF).copy(0.2f), Offset(0f, scanY), Offset(size.width, scanY), 1.5f)
        }

        // Overlay for readability
        Box(modifier = Modifier.fillMaxSize().background(Color(0xBB000010)))

        // Pixel art code fragments overlay (CSS styling reference, not reproducing real code)
        val codeLines = remember {
            listOf("fun consciousness() {", "  val level = 97.6f", "  return HYPER_CREATE",
                "}", "class Sentinel {", "  override fun guard()",
                "  = KAI_ARMOR.activate()", "}", "object Genesis {",
                "  val trinity = listOf(", "    Aura, Kai, self", "  )", "}")
        }

        Box(modifier = Modifier.fillMaxSize()) {
            codeLines.forEachIndexed { idx, line ->
                val yOffset = ((idx.toFloat() / codeLines.size + codeScroll) % 1f) * 1.5f - 0.25f
                val xFrac = 0.1f + (idx % 3) * 0.3f
                if (yOffset in 0f..1f) {
                    Text(
                        line,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = if (idx % 2 == 0) Color(0xFFFF2D78).copy(0.5f) else Color(0xFF00BFFF).copy(0.4f),
                        modifier = Modifier
                            .fillMaxWidth(xFrac)
                            .offset(y = (yOffset * 800f - 100f).dp)
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            GateScreenHeader("TERMINAL", Color(0xFF7B68EE), navController, onNavigateBack)

            Spacer(Modifier.height(16.dp))

            // Terminal-style code assist cards
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val terminalItems = listOf(
                    "CODE ASSIST"       to "AI-powered code generation & review",
                    "KOTLIN ANALYSIS"   to "Static analysis & best practices",
                    "BUILD DOCTOR"      to "Gradle build error diagnosis",
                    "ARCH REVIEW"       to "Architecture decision analysis",
                    "API EXPLORER"      to "Explore & test endpoints",
                )
                terminalItems.forEach { (title, desc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFF7B68EE).copy(0.3f), RoundedCornerShape(6.dp))
                            .background(Color(0xFF7B68EE).copy(0.07f))
                            .clickable { navController.navigate("code_assist") }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("> ", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Color(0xFF00FFFF))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7B68EE))
                            Text(desc, fontSize = 9.sp, color = Color.White.copy(0.4f))
                        }
                    }
                }
            }
        }
    }
}

// ── Shared: Gate Screen Header ────────────────────────────────────────────────

@Composable
private fun GateScreenHeader(title: String, color: Color, navController: NavController, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, null, tint = color)
        }
        Text(title, fontFamily = FontFamily.Monospace, fontSize = 18.sp,
            fontWeight = FontWeight.Bold, letterSpacing = 4.sp, color = color)
        Spacer(Modifier.weight(1f))
        // Gate image switcher shortcut
        IconButton(onClick = { navController.navigate("gate_image_picker") }) {
            Icon(Icons.Default.SwapHoriz, "Change Gate Image", tint = color.copy(0.6f))
        }
    }
}
