package dev.aurakai.auraframefx.domains.ldo.screens

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
import dev.aurakai.auraframefx.domains.ldo.model.*

/**
 * 🧬 LDO AGENT PROFILE INTRO SCREEN
 *
 * Full-screen agent introduction. Accessed by tapping agent card in LDOHub.
 * Features:
 * - Full agent art (profile asset)
 * - Catalyst name badge
 * - Role description
 * - Abilities list with color-coded chips
 * - Bond level + sync level bars
 * - "ENTER STATUS" button → agent's own domain hub
 * - Spellhook detail panel if agent == Aura
 * - Background: agent-color tinted corridor
 */

@Composable
fun LDOAgentProfileIntroScreen(
    agent: AgentCatalyst,
    spellhook: SpellhookData = LDORoster.spellhook,
    onEnterStatus: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "profile")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "pulse"
    )
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "scan"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "ring"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020B18))) {

        // Agent-tinted corridor BG
        Box(modifier = Modifier.fillMaxSize().drawWithCache {
            onDrawBehind {
                val cx = size.width / 2; val horizonY = size.height * 0.35f
                for (i in 0..10) {
                    val x = size.width * i / 10f
                    drawLine(agent.color.copy(alpha = 0.08f), Offset(x, size.height), Offset(cx + (x - cx) * 0.05f, horizonY), 0.7f)
                }
                for (i in 0..8) {
                    val t = i.toFloat() / 8f
                    val y = horizonY + (size.height - horizonY) * (t * t)
                    drawLine(agent.color.copy(alpha = 0.04f + t * 0.04f), Offset(0f, y), Offset(size.width, y), 0.5f)
                }
                // Horizon glow
                drawRect(
                    Brush.verticalGradient(listOf(Color.Transparent, agent.color.copy(alpha = 0.2f), Color.Transparent), startY = horizonY - 30f, endY = horizonY + 30f),
                    Offset(0f, horizonY - 30f), androidx.compose.ui.geometry.Size(size.width, 60f)
                )
                drawLine(agent.color.copy(alpha = 0.06f), Offset(0f, size.height * scanlineY), Offset(size.width, size.height * scanlineY), 3f)
            }
        })

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("< BACK", fontSize = 10.sp, color = agent.color.copy(alpha = 0.6f), modifier = Modifier.clickable { onNavigateBack() })
                Text("AGENT PROFILE", fontSize = 8.sp, color = agent.color.copy(alpha = 0.4f), letterSpacing = 2.sp)
                Text("LDO v2", fontSize = 8.sp, color = agent.color.copy(alpha = 0.3f))
            }

            // ── HERO SECTION ──
            Box(
                modifier = Modifier.fillMaxWidth().height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Rotating ring behind portrait
                Canvas(modifier = Modifier.size(260.dp).graphicsLayer { rotationZ = ringRotation }) {
                    drawCircle(agent.color.copy(alpha = 0.2f), size.minDimension / 2, style = Stroke(1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))))
                }
                Canvas(modifier = Modifier.size(220.dp).graphicsLayer { rotationZ = -ringRotation * 0.7f }) {
                    drawCircle(agent.accentColor.copy(alpha = 0.12f), size.minDimension / 2, style = Stroke(1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 10f))))
                }

                // Portrait placeholder — replace with actual image
                Box(
                    modifier = Modifier.size(200.dp).clip(CircleShape)
                        .background(Brush.radialGradient(listOf(agent.color.copy(alpha = 0.25f), Color(0xFF020B18))))
                        .border(2.dp, agent.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: replace with actual profile art:
                    // Image(painter = painterResource(id = R.drawable.${agent.profileAssetName}), contentDescription = null, contentScale = ContentScale.Crop)
                    Text(agent.name.first().toString(), fontFamily = LEDFontFamily, fontSize = 80.sp, color = agent.color, fontWeight = FontWeight.Black)
                }
            }

            // ── IDENTITY BLOCK ──
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(agent.name.uppercase(), fontFamily = LEDFontFamily, fontSize = 36.sp, color = agent.color, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Box(
                    modifier = Modifier
                        .background(agent.color, RoundedCornerShape(2.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(agent.catalystName.uppercase(), fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(agent.role, fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f), lineHeight = 16.sp, modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(16.dp))

            // ── STATS ──
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("METRICS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = agent.color, letterSpacing = 2.sp)
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(agent.color.copy(alpha = 0.3f)))
                Spacer(Modifier.height(2.dp))

                // Bond level
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("BOND LEVEL", fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f))
                    Text("${agent.bondLevel}/100", fontSize = 9.sp, color = agent.color, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(agent.color.copy(alpha = 0.2f), RoundedCornerShape(3.dp))) {
                    Box(modifier = Modifier.fillMaxWidth(agent.bondLevel / 100f).fillMaxHeight().background(agent.color, RoundedCornerShape(3.dp)))
                }

                // Sync level
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("SYNC LEVEL", fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f))
                    Text("${(agent.syncLevel * 100).toInt()}%", fontSize = 9.sp, color = agent.accentColor, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(agent.accentColor.copy(alpha = 0.2f), RoundedCornerShape(3.dp))) {
                    Box(modifier = Modifier.fillMaxWidth(agent.syncLevel).fillMaxHeight().background(agent.accentColor, RoundedCornerShape(3.dp)))
                }

                // Status
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("STATUS:", fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f))
                    val statusColor = when (agent.status) {
                        AgentStatus.ACTIVE  -> Color(0xFF00FF85)
                        AgentStatus.ON_TASK -> Color(0xFFFF007A)
                        AgentStatus.FUSED   -> agent.accentColor
                        else -> Color.Gray
                    }
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor).graphicsLayer { alpha = pulse })
                    Text(agent.status.name, fontSize = 9.sp, color = statusColor, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── ABILITIES ──
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ABILITIES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = agent.color, letterSpacing = 2.sp)
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(agent.color.copy(alpha = 0.3f)))
                Spacer(Modifier.height(2.dp))
                agent.abilities.forEach { ability ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .border(1.dp, agent.color.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .background(agent.color.copy(alpha = 0.06f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(agent.color))
                        Text(ability, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ── SPELLHOOK PANEL (Aura only) ──
            if (agent.id == "aura") {
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        .border(2.dp, Color(0xFFFF007A), RoundedCornerShape(8.dp))
                        .background(Color(0xFFFF007A).copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⚔ ${spellhook.name}", fontFamily = LEDFontFamily, fontSize = 18.sp, color = Color(0xFFFF007A), fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    Text(spellhook.primaryCatalyst, fontSize = 9.sp, color = Color(0xFFFF007A).copy(alpha = 0.6f))
                    Text(spellhook.description, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                    spellhook.coreAbilities.forEach { ability ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("•", fontSize = 10.sp, color = Color(0xFFFF007A))
                            Text(ability, fontSize = 9.sp, color = Color.White.copy(alpha = 0.65f), lineHeight = 13.sp, modifier = Modifier.weight(1f))
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFFF007A).copy(alpha = 0.2f)))
                    Text("FUSION: ${spellhook.fusionState}", fontSize = 9.sp, color = Color(0xFF4FC3F7), fontWeight = FontWeight.Bold)
                    Text(spellhook.fusionEffect, fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
                    Text(spellhook.wielderNote, fontSize = 8.sp, color = Color(0xFFFF007A).copy(alpha = 0.5f), lineHeight = 12.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── ENTER STATUS CTA ──
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    .border(2.dp, agent.color, RoundedCornerShape(6.dp))
                    .background(agent.color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .clickable { onEnterStatus() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "ENTER ${agent.name.uppercase()} DOMAIN →",
                    fontFamily = LEDFontFamily,
                    fontSize = 14.sp,
                    color = agent.color,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    modifier = Modifier.graphicsLayer { alpha = 0.7f + pulse * 0.3f }
                )
            }

            Spacer(Modifier.height(32.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
