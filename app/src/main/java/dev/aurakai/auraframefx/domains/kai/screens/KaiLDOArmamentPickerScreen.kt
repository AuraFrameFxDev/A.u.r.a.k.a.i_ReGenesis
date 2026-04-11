package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
 * 🛡️ KAI LDO ARMAMENT PICKER — MGS Tactical Style
 *
 * Visual: DEFCON meter, radar sweep bg, AEGIS PRISM hex shield center,
 * MGS-style weapon loadout slots (border-left green), targeting reticle corners,
 * BACK [L1] / EQUIP [R1] footer buttons.
 *
 * Colors: #00FF41 radar-green / #FF5E00 heat-orange / #1A0B2E tactical-purple
 */

private val RadarGreen  = Color(0xFF00FF41)
private val HeatOrange  = Color(0xFFFF5E00)
private val TacPurple   = Color(0xFF1A0B2E)
private val TacDeep     = Color(0xFF0D0415)
private val AegisPurple = Color(0xFF4A1D7A)

data class WeaponSlot(
    val label: String,
    val name: String,
    val ammoLabel: String,
    val ammoValue: String,
    val fillFraction: Float,
    val isActive: Boolean = false,
    val isEmpty: Boolean = false,
)

private val defaultLoadout = listOf(
    WeaponSlot("PRIMARY ARMAMENT", "HF BLADE - MK.II", "AMMO", "INF", 1f),
    WeaponSlot("SECONDARY SLOT [ACTIVE]", "STUN GRENADE", "QTY", "04", 0.66f, isActive = true),
    WeaponSlot("SUPPORT UNIT", "EMPTY SLOT", "", "", 0f, isEmpty = true),
)

@Composable
fun KaiLDOArmamentPickerScreen(
    loadout: List<WeaponSlot> = defaultLoadout,
    defconLevel: Int = 2,
    onBack: () -> Unit = {},
    onEquip: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "kai_armament")
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "radar"
    )
    val fracturePulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "fracture"
    )
    val defconBlink by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "defcon"
    )

    var selectedSlot by remember { mutableIntStateOf(1) }

    Box(modifier = Modifier.fillMaxSize().background(TacDeep)) {

        // ── RADAR BACKGROUND ──
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val cx = size.width / 2; val cy = size.height / 2
                // Concentric circles
                listOf(0.4f, 0.65f, 0.9f).forEachIndexed { i, r ->
                    drawCircle(RadarGreen.copy(alpha = 0.04f + i * 0.03f), radius = size.minDimension * r / 2)
                }
                // Radar sweep line
                val sweepRad = Math.toRadians(radarAngle.toDouble()).toFloat()
                val sweepEnd = Offset(cx + cos(sweepRad) * size.maxDimension, cy + sin(sweepRad) * size.maxDimension)
                drawLine(RadarGreen.copy(alpha = 0.15f), Offset(cx, cy), sweepEnd, 2f)
            }
        })

        // ── DECORATIVE CORNER BORDERS ──
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val pad = 24f; val len = 30f; val w = 3f
                // TL
                drawLine(RadarGreen, Offset(pad, pad), Offset(pad + len, pad), w)
                drawLine(RadarGreen, Offset(pad, pad), Offset(pad, pad + len), w)
                // TR
                drawLine(RadarGreen, Offset(size.width - pad, pad), Offset(size.width - pad - len, pad), w)
                drawLine(RadarGreen, Offset(size.width - pad, pad), Offset(size.width - pad, pad + len), w)
                // BL
                drawLine(RadarGreen, Offset(pad, size.height - pad), Offset(pad + len, size.height - pad), w)
                drawLine(RadarGreen, Offset(pad, size.height - pad), Offset(pad, size.height - pad - len), w)
                // BR
                drawLine(RadarGreen, Offset(size.width - pad, size.height - pad), Offset(size.width - pad - len, size.height - pad), w)
                drawLine(RadarGreen, Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - len), w)
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ── HEADER ──
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(TacPurple.copy(alpha = 0.4f))
                    .border(BorderStroke(0.5.dp, RadarGreen.copy(alpha = 0.3f)))
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("KAI LDO // ARMAMENT", fontFamily = LEDFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.5).sp)
                    Text("TACTICAL SELECTION INTERFACE v4.02", fontSize = 8.sp, color = RadarGreen.copy(alpha = 0.8f), letterSpacing = 3.sp)
                }
                // DEFCON Meter
                Column(horizontalAlignment = Alignment.End) {
                    Text("THREAT LEVEL: DEFCON", fontSize = 8.sp, color = RadarGreen.copy(alpha = 0.7f), letterSpacing = 1.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier.size(24.dp).background(Color.Red.copy(alpha = 0.5f + defconBlink * 0.5f), RoundedCornerShape(2.dp)).border(1.dp, Color.White, RoundedCornerShape(2.dp)),
                            contentAlignment = Alignment.Center
                        ) { Text("$defconLevel", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(4) { i -> Box(modifier = Modifier.width(40.dp).height(4.dp).background(if (i < 2) Color.Red else Color.DarkGray)) }
                        }
                    }
                }
            }

            // ── AEGIS PRISM CENTER ──
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(220.dp).drawWithCache {
                        onDrawBehind {
                            // Outer glow
                            drawCircle(AegisPurple.copy(alpha = 0.2f), size.minDimension / 2)
                            // Hex clip shape using path
                            val hex = Path()
                            val points = listOf(0.25f to 0f, 0.75f to 0f, 1f to 0.5f, 0.75f to 1f, 0.25f to 1f, 0f to 0.5f)
                            points.forEachIndexed { i, (px, py) ->
                                if (i == 0) hex.moveTo(px * size.width, py * size.height)
                                else hex.lineTo(px * size.width, py * size.height)
                            }
                            hex.close()
                            drawPath(hex, AegisPurple, style = Fill)
                            drawPath(hex, RadarGreen.copy(alpha = 0.5f), style = Stroke(3f))
                            // Shadow/glow
                            drawPath(hex, Color(0x220000FF))
                            // Fracture lines
                            val alpha = fracturePulse * 0.8f
                            drawLine(HeatOrange.copy(alpha = alpha), Offset(size.width * 0.2f, 0f), Offset(size.width * 0.45f, size.height * 0.6f), 1.5f)
                            drawLine(HeatOrange.copy(alpha = alpha), Offset(size.width * 0.8f, 0f), Offset(size.width * 0.55f, size.height * 0.7f), 1.5f)
                            drawLine(HeatOrange.copy(alpha = alpha * 0.7f), Offset(0f, size.height * 0.5f), Offset(size.width, size.height * 0.5f), 1f)
                            // Targeting reticles
                            val rLen = 16f; val rOff = 36f; val rW = 2f
                            // Top-left inside hex
                            drawLine(RadarGreen, Offset(size.width * 0.3f, rOff), Offset(size.width * 0.3f + rLen, rOff), rW)
                            drawLine(RadarGreen, Offset(size.width * 0.3f, rOff), Offset(size.width * 0.3f, rOff + rLen), rW)
                            // Top-right
                            drawLine(RadarGreen, Offset(size.width * 0.7f, rOff), Offset(size.width * 0.7f - rLen, rOff), rW)
                            drawLine(RadarGreen, Offset(size.width * 0.7f, rOff), Offset(size.width * 0.7f, rOff + rLen), rW)
                        }
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("AEGIS", fontFamily = LEDFontFamily, fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("PRISM-CORE", fontSize = 11.sp, color = RadarGreen, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    }
                }
            }

            // ── LOADOUT SLOTS ──
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                loadout.forEachIndexed { index, slot ->
                    val isSelected = selectedSlot == index
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(
                                if (slot.isEmpty) TacDeep.copy(alpha = 0.5f)
                                else if (slot.isActive || isSelected) RadarGreen.copy(alpha = 0.05f)
                                else TacDeep.copy(alpha = 0.8f)
                            )
                            .border(
                                BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (slot.isActive) HeatOrange else if (isSelected) RadarGreen else Color.Transparent
                                )
                            )
                            .drawWithCache {
                                onDrawBehind {
                                    // Left border stripe (MGS style)
                                    val color = if (slot.isActive) HeatOrange else if (slot.isEmpty) RadarGreen.copy(alpha = 0.3f) else RadarGreen
                                    drawLine(color, Offset(0f, 0f), Offset(0f, size.height), 4f)
                                }
                            }
                            .clickable { selectedSlot = index }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .graphicsLayer { alpha = if (slot.isEmpty) 0.5f else 1f }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    slot.label, fontSize = 8.sp,
                                    color = if (slot.isActive) HeatOrange else RadarGreen.copy(alpha = 0.6f),
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    slot.name, fontFamily = LEDFontFamily, fontSize = 18.sp,
                                    color = if (slot.isEmpty) Color.Gray else Color.White,
                                    fontStyle = if (slot.isEmpty) FontStyle.Italic else FontStyle.Normal
                                )
                            }
                            if (slot.isEmpty) {
                                Box(
                                    modifier = Modifier.border(1.dp, RadarGreen.copy(alpha = 0.3f), RoundedCornerShape(2.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                                ) { Text("RE-ARM REQUIRED", fontSize = 7.sp, color = RadarGreen.copy(alpha = 0.6f)) }
                            } else {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${slot.ammoLabel}: ${slot.ammoValue}", fontSize = 9.sp, color = if (slot.isActive) HeatOrange else RadarGreen)
                                    Box(modifier = Modifier.width(56.dp).height(4.dp).background(RadarGreen.copy(alpha = 0.2f), RoundedCornerShape(2.dp))) {
                                        Box(modifier = Modifier.fillMaxWidth(slot.fillFraction).fillMaxHeight().background(if (slot.isActive) HeatOrange else RadarGreen, RoundedCornerShape(2.dp)))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── FOOTER ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                        .background(RadarGreen.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .border(1.dp, RadarGreen, RoundedCornerShape(4.dp))
                        .clickable { onBack() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) { Text("BACK [L1]", fontFamily = LEDFontFamily, fontSize = 13.sp, color = RadarGreen, letterSpacing = 2.sp) }

                Box(
                    modifier = Modifier.weight(1f)
                        .background(HeatOrange.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .border(1.dp, HeatOrange, RoundedCornerShape(4.dp))
                        .clickable { onEquip() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) { Text("EQUIP [R1]", fontFamily = LEDFontFamily, fontSize = 13.sp, color = HeatOrange, letterSpacing = 2.sp) }
            }

            Spacer(Modifier.height(12.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
