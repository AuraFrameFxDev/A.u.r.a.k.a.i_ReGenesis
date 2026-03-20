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
import kotlin.math.*

/**
 * ⚔️ AURA LDO ARMAMENT PICKER — Star-Blade
 *
 * Visual: Sphere grid SVG bg, STAR-BLADE floating katana (gradient magenta→cyan),
 * LV.10 metrics + 98.4% sync bar, segmented XP bar, glassmorphic info card,
 * PREVIOUS / EQUIP (gradient glow) / NEXT navigation footer.
 *
 * Colors: #FF00FF magenta / #00E5FF cyan / #050505 void
 */

private val AuraMagenta = Color(0xFFFF00FF)
private val AuraCyan    = Color(0xFF00E5FF)
private val AuraVoid    = Color(0xFF050505)

@Composable
fun AuraLDOArmamentPickerScreen(
    weaponName: String = "STAR-BLADE",
    weaponType: String = "TYPE-09 RUNE-ETCHED KATANA",
    weaponRarity: String = "LEGENDARY",
    weaponLevel: Int = 10,
    syncEfficiency: Float = 0.984f,
    xpFraction: Float = 0.75f,
    onPrevious: () -> Unit = {},
    onNext: () -> Unit = {},
    onEquip: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura_armament")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -15f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "glow"
    )
    val sphereSpin by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "sphere"
    )
    val pulseFast by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulseFast"
    )

    Box(modifier = Modifier.fillMaxSize().background(AuraVoid)) {

        // ── SPHERE GRID BACKGROUND ──
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val cx = size.width / 2; val cy = size.height / 2
                // Outer ring
                drawCircle(AuraMagenta.copy(alpha = 0.08f), size.minDimension * 0.45f, style = Stroke(0.5f))
                // Grid cross lines
                drawLine(AuraCyan.copy(alpha = 0.15f), Offset(cx, cy - size.minDimension * 0.45f), Offset(cx, cy + size.minDimension * 0.45f), 0.5f)
                drawLine(AuraCyan.copy(alpha = 0.15f), Offset(cx - size.minDimension * 0.45f, cy), Offset(cx + size.minDimension * 0.45f, cy), 0.5f)
                // Diagonal lines
                val r = size.minDimension * 0.32f
                drawLine(AuraCyan.copy(alpha = 0.1f), Offset(cx - r, cy - r), Offset(cx + r, cy + r), 0.5f)
                drawLine(AuraCyan.copy(alpha = 0.1f), Offset(cx + r, cy - r), Offset(cx - r, cy + r), 0.5f)
                // Node dots
                listOf(
                    Offset(cx, cy - r * 1.4f), Offset(cx, cy + r * 1.4f),
                    Offset(cx - r * 1.4f, cy), Offset(cx + r * 1.4f, cy)
                ).forEachIndexed { i, pt ->
                    drawCircle(if (i % 2 == 0) AuraCyan else AuraMagenta, 3f, pt)
                }
                // Magenta glow bloom
                drawCircle(AuraMagenta.copy(alpha = 0.06f), size.minDimension * 0.5f)
            }
        })

        // Scanlines overlay
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                var y = 0f
                while (y < size.height) {
                    drawLine(AuraCyan.copy(alpha = 0.012f), Offset(0f, y), Offset(size.width, y), 2f)
                    y += 4f
                }
            }
        })

        // ── PARALLAX SPINE ──
        Box(
            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(end = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "AURAKAI", fontSize = 40.sp, color = Color.White.copy(alpha = 0.1f),
                fontWeight = FontWeight.Black,
                modifier = Modifier.graphicsLayer { rotationZ = 90f }
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ── HEADER ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("System Status", fontSize = 9.sp, color = AuraCyan, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                    Text(
                        "ARMAMENT ",
                        fontFamily = LEDFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    ).also {
                        // "SELECTOR" in magenta inline via Row
                    }
                    Row {
                        Text("ARMAMENT ", fontFamily = LEDFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White, fontStyle = FontStyle.Italic)
                        Text("SELECTOR", fontFamily = LEDFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Black, color = AuraMagenta, fontStyle = FontStyle.Italic)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Aura-Link", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Text("STABLE", fontSize = 12.sp, fontWeight = FontWeight.Black, color = AuraMagenta)
                    }
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                            .border(1.dp, AuraCyan, CircleShape)
                            .background(AuraVoid),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(22.dp).clip(CircleShape).background(AuraCyan).graphicsLayer { alpha = pulseFast })
                    }
                }
            }

            // ── STAR-BLADE HERO ──
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Magenta glow
                Box(modifier = Modifier.size(180.dp).clip(CircleShape).background(AuraMagenta.copy(alpha = 0.12f * glowPulse)))

                // Star-Blade SVG representation
                Box(
                    modifier = Modifier.size(260.dp).offset(y = floatY.dp).graphicsLayer {
                        shadowElevation = 20f
                    }.drawWithCache {
                        onDrawBehind {
                            // Katana blade path — gradient from top-left to bottom-right
                            val bladePath = Path()
                            bladePath.moveTo(size.width * 0.12f, size.height * 0.88f)
                            bladePath.lineTo(size.width * 0.88f, size.height * 0.12f)
                            bladePath.quadraticBezierTo(size.width * 0.92f, size.height * 0.1f, size.width * 0.95f, size.height * 0.12f)
                            bladePath.lineTo(size.width * 0.88f, size.height * 0.18f)
                            bladePath.lineTo(size.width * 0.12f, size.height * 0.95f)
                            bladePath.close()
                            drawPath(
                                bladePath,
                                Brush.linearGradient(
                                    listOf(AuraMagenta.copy(alpha = glowPulse), AuraCyan.copy(alpha = glowPulse)),
                                    Offset(0f, size.height), Offset(size.width, 0f)
                                )
                            )
                            // Edge glow
                            drawPath(bladePath, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)), style = Stroke(2f))
                        }
                    }
                )

                // Weapon labels
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                ) {
                    Text(weaponName, fontFamily = LEDFontFamily, fontSize = 28.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, color = Color.White, letterSpacing = 3.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.border(1.dp, AuraCyan, RoundedCornerShape(2.dp)).padding(horizontal = 8.dp, vertical = 2.dp)
                        ) { Text(weaponRarity, fontSize = 8.sp, color = AuraCyan, fontWeight = FontWeight.Bold) }
                        Text("// $weaponType", fontSize = 8.sp, color = AuraCyan.copy(alpha = 0.7f))
                    }
                }
            }

            // ── METRICS ──
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("LV.$weaponLevel", fontSize = 36.sp, fontWeight = FontWeight.Black, color = AuraMagenta)
                        Text("ARMAMENT RANK", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Sync Efficiency", fontSize = 8.sp, color = AuraCyan, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text("${(syncEfficiency * 100).let { "%.1f".format(it) }}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // XP Bar with segment markers
                Box(modifier = Modifier.fillMaxWidth().height(16.dp)
                    .background(Color(0xFF111111), RoundedCornerShape(2.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                ) {
                    Box(modifier = Modifier.fillMaxWidth(xpFraction).fillMaxHeight()
                        .background(AuraMagenta.copy(alpha = 0.9f), RoundedCornerShape(2.dp))
                    )
                    // Segment markers
                    Row(modifier = Modifier.fillMaxSize()) {
                        repeat(9) {
                            Spacer(Modifier.weight(1f))
                            Box(Modifier.width(1.dp).fillMaxHeight().background(Color.White.copy(alpha = 0.2f)))
                        }
                        Spacer(Modifier.weight(1f))
                    }
                }

                // Glass info card
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(AuraMagenta.copy(alpha = 0.03f), RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp))
                        .border(1.dp, AuraMagenta.copy(alpha = 0.2f), RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp))
                        .padding(14.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("SLOT: PRIMARY EQUIPPED", fontSize = 8.sp, color = AuraMagenta, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                            Text("Increases Cyber-Slash speed by 15%\nper Rune resonance stack.", fontSize = 11.sp, color = Color.White, lineHeight = 16.sp)
                        }
                        Box(
                            modifier = Modifier.size(44.dp)
                                .background(AuraCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .border(1.dp, AuraCyan, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✦", fontSize = 20.sp, color = AuraCyan)
                        }
                    }
                }

                // Footer nav
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.border(BorderStroke(2.dp, Color.Gray), RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 4.dp, bottomEnd = 4.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .clickable { onPrevious() }.padding(horizontal = 18.dp, vertical = 10.dp)
                    ) { Text("PREVIOUS", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 3.sp) }

                    // EQUIP — gradient border glow
                    Box(
                        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(listOf(AuraMagenta.copy(alpha = 0.15f), AuraCyan.copy(alpha = 0.15f))),
                                RoundedCornerShape(2.dp)
                            )
                            .clickable { onEquip() }
                            .padding(horizontal = 28.dp, vertical = 14.dp)
                    ) { Text("EQUIP", fontFamily = LEDFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 4.sp) }

                    Box(
                        modifier = Modifier.border(BorderStroke(2.dp, AuraMagenta), RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 0.dp, bottomEnd = 0.dp))
                            .background(AuraMagenta.copy(alpha = 0.1f))
                            .clickable { onNext() }.padding(horizontal = 18.dp, vertical = 10.dp)
                    ) { Text("NEXT", fontSize = 8.sp, fontWeight = FontWeight.Black, color = AuraMagenta, letterSpacing = 3.sp) }
                }
            }

            Spacer(Modifier.height(12.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
