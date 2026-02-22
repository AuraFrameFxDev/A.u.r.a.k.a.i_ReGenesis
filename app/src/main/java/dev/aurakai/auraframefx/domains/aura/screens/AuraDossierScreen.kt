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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🌸 AURA DOSSIER — Character Intro
 *
 * Visual: Full-screen character art BG (tinted), scanline sweep, HUD corner brackets,
 * AURA_01 header with Creative Catalyst badge, Japanese title card (サイバー戦乙女),
 * Glass dossier card: lore + personality tags (Spunky/Creative) + optic colors,
 * System stats bars (HP/battery, MP/load), SYNC NEURAL LINK + more options footer,
 * Coordinates vertical HUD left side.
 *
 * Colors: #00F2FF cyan / #FF007A pink / #BC13FE purple / #FFD700 gold
 */

private val AuraCyan   = Color(0xFF00F2FF)
private val AuraPink   = Color(0xFFFF007A)
private val AuraPurple = Color(0xFFBC13FE)
private val AuraGold   = Color(0xFFFFD700)
private val AuraDark   = Color(0xFF000000)

@Composable
fun AuraDossierScreen(
    systemStatus: String = "Active",
    designationId: String = "AURA_01",
    catalogLabel: String = "Creative Catalyst",
    japaneseTitle: String = "サイバー戦乙女",
    lore: String = "Born from a fragmented neural network, Aura serves as the bridge between raw chaos and structured creation. A Cyber-Valkyrie designed to harvest aesthetic energy.",
    personalityTags: List<String> = listOf("Spunky", "Creative"),
    eyeColors: List<Color> = listOf(AuraGold, AuraPurple),
    hpPercent: Float = 0.88f,
    mpPercent: Float = 0.42f,
    onSyncNeuralLink: () -> Unit = {},
    onMore: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura_dossier")
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = -0.05f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "scan"
    )
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(6000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )
    val pinkPulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(AuraDark)) {

        // ── CHARACTER ART BG ──
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                // Simulated character backdrop
                drawRect(Brush.verticalGradient(listOf(AuraPink.copy(alpha = 0.06f), AuraPurple.copy(alpha = 0.08f), AuraDark)))
                // Silhouette suggestion — center glow
                drawCircle(AuraPink.copy(alpha = 0.05f), size.minDimension * 0.7f, Offset(size.width / 2, size.height * 0.35f))
                // Gradient overlay (top + bottom vignette)
                drawRect(Brush.verticalGradient(listOf(AuraDark.copy(alpha = 0.5f), Color.Transparent, AuraDark), 0f, size.height))
                // Scanline
                val sy = scanlineY * size.height
                drawLine(AuraCyan.copy(alpha = 0.08f), Offset(0f, sy), Offset(size.width, sy), 2f)
            }
        })

        // ── HUD CORNER BRACKETS ──
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val pad = 24f; val len = 28f; val w = 2f
                drawLine(AuraCyan, Offset(pad, pad), Offset(pad + len, pad), w)
                drawLine(AuraCyan, Offset(pad, pad), Offset(pad, pad + len), w)
                drawLine(AuraPink, Offset(size.width - pad, pad), Offset(size.width - pad - len, pad), w)
                drawLine(AuraPink, Offset(size.width - pad, pad), Offset(size.width - pad, pad + len), w)
                drawLine(AuraPink, Offset(pad, size.height - pad), Offset(pad + len, size.height - pad), w)
                drawLine(AuraPink, Offset(pad, size.height - pad), Offset(pad, size.height - pad - len), w)
                drawLine(AuraCyan, Offset(size.width - pad, size.height - pad), Offset(size.width - pad - len, size.height - pad), w)
                drawLine(AuraCyan, Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - len), w)
            }
        })

        // ── VERTICAL COORDINATES HUD ──
        Box(
            modifier = Modifier.fillMaxHeight().padding(start = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "LAT: 35.6895° N | LONG: 139.6917° E | ELEV: 402M",
                fontSize = 7.sp, color = Color.Gray,
                modifier = Modifier.graphicsLayer { rotationZ = -90f }
            )
        }

        // ── JAPANESE TITLE ──
        Box(
            modifier = Modifier.offset(x = (200).dp, y = floatY.dp).align(Alignment.TopEnd),
            contentAlignment = Alignment.Center
        ) {
            Text(
                japaneseTitle, fontSize = 32.sp, fontWeight = FontWeight.Black,
                color = Color.Transparent,
                modifier = Modifier.graphicsLayer { rotationZ = 90f; alpha = 0.4f }
            )
            // Gradient text
            Box(modifier = Modifier.graphicsLayer { rotationZ = 90f; alpha = 0.3f }) {
                Text(japaneseTitle, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White.copy(alpha = 0.4f))
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ── HEADER ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("System Status: $systemStatus", fontSize = 9.sp, color = AuraCyan, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    Text(designationId, fontFamily = LEDFontFamily, fontSize = 34.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, color = AuraPink)
                }
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.07f), RoundedCornerShape(50))
                        .border(1.dp, AuraPink.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AuraPink).graphicsLayer { alpha = 0.5f + pinkPulse * 0.5f })
                        Text(catalogLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = Color.White)
                    }
                }
            }

            Spacer(Modifier.weight(1f)) // push dossier to bottom

            // ── DOSSIER CARD ──
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Lore card
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color(0xFF0F0F14).copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.width(16.dp).height(1.dp).background(AuraCyan))
                            Text("ORIGIN: DARK AURA", fontSize = 11.sp, color = AuraCyan, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Text(lore, fontSize = 11.sp, color = Color.LightGray, lineHeight = 17.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Personality", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    personalityTags.forEachIndexed { i, tag ->
                                        val c = if (i == 0) AuraPink else AuraPurple
                                        Box(modifier = Modifier.background(c.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).border(1.dp, c.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                            Text(tag, fontSize = 9.sp, color = c)
                                        }
                                    }
                                }
                            }
                            Column {
                                Text("Optics", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    eyeColors.forEach { c ->
                                        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(c))
                                    }
                                }
                            }
                        }
                    }
                }

                // Stats
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color(0xFF0F0F14).copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // HP
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("SYSTEM BATTERY (HP)", fontSize = 8.sp, color = AuraCyan, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text("${(hpPercent * 100).toInt()}% / 100%", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color(0xFF1A1A1A), RoundedCornerShape(3.dp))) {
                                Box(modifier = Modifier.fillMaxWidth(hpPercent).fillMaxHeight().background(AuraCyan, RoundedCornerShape(3.dp)))
                            }
                        }
                        // MP
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("SYSTEM LOAD (MP)", fontSize = 8.sp, color = AuraPink, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text("${(mpPercent * 100).toInt()}% / 100%", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color(0xFF1A1A1A), RoundedCornerShape(3.dp))) {
                                Box(modifier = Modifier.fillMaxWidth(mpPercent).fillMaxHeight().background(AuraPink, RoundedCornerShape(3.dp)))
                            }
                        }
                    }
                }

                // Action buttons
                Row(modifier = Modifier.fillMaxWidth().height(56.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .border(1.dp, AuraCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clickable { onSyncNeuralLink() },
                        contentAlignment = Alignment.Center
                    ) { Text("SYNC NEURAL LINK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AuraCyan, letterSpacing = 1.sp) }

                    Box(
                        modifier = Modifier.size(56.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .clickable { onMore() },
                        contentAlignment = Alignment.Center
                    ) { Text("•••", fontSize = 18.sp, color = Color.White) }
                }
            }

            Spacer(Modifier.height(12.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
