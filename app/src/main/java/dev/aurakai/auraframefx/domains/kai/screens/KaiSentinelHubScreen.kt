package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * 🏰 KAI SENTINEL HUB SCREEN — Tactical Command Center
 *
 * Translated from Stitch exports (KaiSentinelHub + FortressProtocol variants).
 * Bunker aesthetic: purple sweep radar BG, mission briefing dossier cards,
 * CRT scanlines, emergency orange lighting, corner targeting brackets.
 *
 * Sub-gates: OracleDrive, ROM Tools, Bootloader, RGSS, Sentinel Integrity, Domain Expansion
 * Styles: FORTRESS ↔ CYBER SENTINEL toggle
 */

private val HubPurple   = Color(0xFF9D00FF)
private val HubOrange   = Color(0xFFFF4500)
private val HubGreen    = Color(0xFF00FF41)
private val HubDark     = Color(0xFF0A0A0C)
private val HubGrey     = Color(0xFF1A1A1E)

data class MissionBriefing(
    val fileRef: String,
    val title: String,
    val description: String,
    val classification: String,
    val accentColor: Color
)

private val defaultMissions = listOf(
    MissionBriefing("882-BETA",  "Oracle Drive",         "Root access management — APatch/Magisk/KernelSU unified. Module integrity verified.", "TOP SECRET", HubOrange),
    MissionBriefing("404-SIGMA", "ROM Tools",            "Flash ZIP controls, TWRP integration, partition manager. Wipe gates active.",        "CONFIDENTIAL", HubPurple),
    MissionBriefing("001-ALPHA", "Bootloader Gate",      "Lock status: CUSTOM. Verified boot: OFF. AVB check pending kernel patch.",           "TOP SECRET", HubOrange),
    MissionBriefing("333-GAMMA", "RGSS Scanner",         "Reality Gate Security System — LSPosed audit, permission inspector, veto panel.",    "CLASSIFIED", HubPurple),
    MissionBriefing("777-OMEGA", "Sentinel Integrity",   "System immune check — SELinux, root, LSPosed, build fingerprint, threat feed.",      "TOP SECRET", HubOrange),
    MissionBriefing("999-PRIME", "Domain Expansion",     "Kai's ultimate — all gates seal, thermal shockwave, DEFCON 1 activation.",          "CLASSIFIED", HubPurple),
)

enum class HubStyle { FORTRESS, CYBER_SENTINEL }

@Composable
fun KaiSentinelHubScreen(
    missions: List<MissionBriefing> = defaultMissions,
    onMissionSelected: (MissionBriefing) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hub")

    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "radar"
    )
    val statusPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "scan"
    )

    var hubStyle by remember { mutableStateOf(HubStyle.FORTRESS) }

    Box(modifier = Modifier.fillMaxSize().background(HubDark)) {

        // Radar BG sweep
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val cx = size.width / 2; val cy = size.height / 2
                val r = maxOf(cx, cy) * 1.5f
                rotate(radarAngle, Offset(cx, cy)) {
                    drawArc(
                        HubPurple.copy(alpha = 0.08f),
                        0f, 90f, true,
                        Offset(cx - r, cy - r),
                        androidx.compose.ui.geometry.Size(r * 2, r * 2)
                    )
                }
                // Grid dots
                var gx = 0f; while (gx < size.width) {
                    var gy = 0f; while (gy < size.height) {
                        drawCircle(HubPurple.copy(alpha = 0.06f), 1f, Offset(gx, gy))
                        gy += 30f
                    }; gx += 30f
                }
            }
        })

        // CRT scanline
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                drawLine(HubPurple.copy(alpha = 0.1f), Offset(0f, size.height * scanlineY), Offset(size.width, size.height * scanlineY), 2f)
            }
        })

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ═══ HEADER ═══
            Column(modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, HubPurple.copy(alpha = 0.4f)))
                .background(HubGrey.copy(alpha = 0.6f))
                .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("KAI SENTINEL", fontFamily = LEDFontFamily, fontSize = 28.sp, fontWeight = FontWeight.Black, color = HubPurple, letterSpacing = 2.sp)
                        Text("Tactical Command Center // Sector 7G", fontSize = 9.sp, color = HubOrange.copy(alpha = 0.8f), letterSpacing = 1.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "SYSTEM: ACTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = HubOrange,
                            modifier = Modifier.graphicsLayer { alpha = statusPulse }
                        )
                        Text("REL_TIME: 14:02:44", fontSize = 8.sp, color = Color.White.copy(alpha = 0.4f))
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Style toggle FORTRESS ↔ CYBER SENTINEL
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally)
                ) {
                    HubStyle.entries.forEach { style ->
                        val active = hubStyle == style
                        Box(
                            modifier = Modifier
                                .border(1.dp, if (active) HubOrange else HubPurple.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                                .background(if (active) HubOrange.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { hubStyle = style }
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(style.name.replace('_', ' '), fontSize = 10.sp, color = if (active) HubOrange else HubPurple, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal, letterSpacing = 1.sp)
                        }
                    }
                }
            }

            // ═══ MISSION BRIEFINGS ═══
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(start = BorderStroke(4.dp, HubOrange))
                        .padding(start = 8.dp)
                ) {
                    Text("MISSION BRIEFINGS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HubOrange, letterSpacing = 2.sp)
                }

                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    missions.forEach { mission ->
                        DossierCard(mission, hubStyle) { onMissionSelected(mission) }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            // ═══ FOOTER ═══
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HubOrange.copy(alpha = 0.08f))
                    .border(BorderStroke(1.dp, HubOrange.copy(alpha = 0.3f)))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(HubOrange))
                    Column {
                        Text("BUNKER STATUS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = HubOrange)
                        Text("ALL SYSTEMS NOMINAL // LOW LUX DETECTED", fontSize = 7.sp, color = Color.White.copy(alpha = 0.5f))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { i ->
                        Box(modifier = Modifier.size(6.dp).background(if (i == 0) HubOrange else HubOrange.copy(alpha = 0.3f)))
                    }
                }
            }
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }

        // Targeting corner brackets
        TargetingCorners(modifier = Modifier.fillMaxSize(), color = HubOrange)
    }
}

@Composable
private fun DossierCard(mission: MissionBriefing, style: HubStyle, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, mission.accentColor.copy(alpha = 0.6f)), RoundedCornerShape(2.dp))
            .background(
                if (style == HubStyle.FORTRESS) Color(0xFFD1D5DB) else HubGrey,
                RoundedCornerShape(2.dp)
            )
            .clickable(onClick = onClick)
            .padding(start = 12.dp)
    ) {
        // Left accent bar
        Box(modifier = Modifier.width(6.dp).fillMaxHeight().background(mission.accentColor).align(Alignment.CenterStart))

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "FILE_REF: ${mission.fileRef}",
                    fontSize = 8.sp,
                    color = if (style == HubStyle.FORTRESS) Color(0xFF555555) else Color.White.copy(alpha = 0.5f)
                )
                Text(
                    mission.title,
                    fontSize = 16.sp,
                    fontFamily = LEDFontFamily,
                    fontWeight = FontWeight.Black,
                    color = if (style == HubStyle.FORTRESS) Color(0xFF1A1A1E) else mission.accentColor,
                    letterSpacing = 0.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    mission.description,
                    fontSize = 9.sp,
                    fontStyle = FontStyle.Italic,
                    color = if (style == HubStyle.FORTRESS) Color(0xFF444444) else Color.White.copy(alpha = 0.7f),
                    lineHeight = 13.sp
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .background(HubDark, RoundedCornerShape(2.dp))
                        .clickable(onClick = onClick)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("EXECUTE MISSION >", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }
            // Classification stamp
            Box(
                modifier = Modifier
                    .border(2.dp, mission.accentColor, RoundedCornerShape(2.dp))
                    .background(mission.accentColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .graphicsLayer { rotationZ = 0f }
            ) {
                Text(mission.classification, fontSize = 7.sp, color = Color.Black, fontWeight = FontWeight.Black, fontFamily = LEDFontFamily, letterSpacing = 0.5.sp)
            }
        }
    }
}

@Composable
private fun TargetingCorners(modifier: Modifier, color: Color) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.align(Alignment.TopStart).size(20.dp).padding(4.dp)) {
            drawLine(color.copy(alpha = 0.5f), Offset(0f, size.height), Offset(0f, 0f), 2f)
            drawLine(color.copy(alpha = 0.5f), Offset(0f, 0f), Offset(size.width, 0f), 2f)
        }
        Canvas(modifier = Modifier.align(Alignment.TopEnd).size(20.dp).padding(4.dp)) {
            drawLine(color.copy(alpha = 0.5f), Offset(0f, 0f), Offset(size.width, 0f), 2f)
            drawLine(color.copy(alpha = 0.5f), Offset(size.width, 0f), Offset(size.width, size.height), 2f)
        }
        Canvas(modifier = Modifier.align(Alignment.BottomStart).size(20.dp).padding(4.dp)) {
            drawLine(color.copy(alpha = 0.5f), Offset(0f, 0f), Offset(0f, size.height), 2f)
            drawLine(color.copy(alpha = 0.5f), Offset(0f, size.height), Offset(size.width, size.height), 2f)
        }
        Canvas(modifier = Modifier.align(Alignment.BottomEnd).size(20.dp).padding(4.dp)) {
            drawLine(color.copy(alpha = 0.5f), Offset(0f, size.height), Offset(size.width, size.height), 2f)
            drawLine(color.copy(alpha = 0.5f), Offset(size.width, size.height), Offset(size.width, 0f), 2f)
        }
    }
}

private fun Modifier.border(start: BorderStroke): Modifier = this.drawWithCache {
    onDrawBehind {
        drawLine(
            brush = start.brush ?: SolidColor(Color.Transparent),
            start = Offset(0f, 0f), end = Offset(0f, size.height),
            strokeWidth = start.width.toPx()
        )
    }
}
