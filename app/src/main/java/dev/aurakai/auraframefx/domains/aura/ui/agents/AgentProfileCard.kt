package dev.aurakai.auraframefx.domains.aura.ui.agents

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * AgentProfileCard - Holographic Agent Display (No Avatar)
 *
 * Based on the agent profile mockups showing:
 * - Agent name & level
 * - Bond progress bar (300%)
 * - Trinity icon progress bar
 * - 3 ability/trait icons
 * - 4 action buttons (prompt/scan/create/fusion/etc.)
 * - Holographic projection platform aesthetic
 */
@Composable
fun AgentProfileCard(
    agentName: String,
    agentColor: Color,
    level: Int = 5,
    bondProgress: Float = 1.0f, // 0.0 to 3.0 (300%)
    trinityProgress: Float = 0.5f, // 0.0 to 1.0
    abilityIcons: List<ImageVector> = listOf(
        Icons.Default.Settings,
        Icons.Default.Favorite,
        Icons.Default.Star
    ),
    actionButtons: List<AgentAction> = emptyList(),
    onActionClick: (AgentAction) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val neonCyan = Color(0xFF00D9FF)
    val neonMagenta = Color(0xFFFF1493)
    val bondColor = Color(0xFF00FF00) // Green for bond bar

    // Pulsing animation for holographic effect
    val infiniteTransition = rememberInfiniteTransition(label = "hologram")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .width(340.dp)
            .background(Color.Black.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
            .border(2.dp, agentColor.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ═══════════════════════════════════════════════════════════════
        // HEADER: Agent Name & Level
        // ═══════════════════════════════════════════════════════════════
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = agentName.uppercase(),
                color = agentColor,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
            )
            Text(
                text = "LVL $level",
                color = neonCyan,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ═══════════════════════════════════════════════════════════════
        // BOND PROGRESS BAR (300%)
        // ═══════════════════════════════════════════════════════════════
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "bond",
                    color = bondColor.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall.copy(letterSpacing = 1.sp)
                )
                Text(
                    text = "${(bondProgress * 100).toInt()}%",
                    color = bondColor,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(Color(0xFF1A1A1A), RoundedCornerShape(2.dp))
                    .border(1.dp, bondColor.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(bondProgress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(bondColor, bondColor.copy(alpha = 0.7f))
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ═══════════════════════════════════════════════════════════════
        // ABILITY ICONS & TRINITY PROGRESS
        // ═══════════════════════════════════════════════════════════════
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3 Ability Icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                abilityIcons.take(3).forEach { icon ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF1A1A1A), CircleShape)
                            .border(2.dp, neonCyan.copy(alpha = pulseAlpha), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = neonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Trinity Icon Progress Bar
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "trinity icon",
                    color = Color(0xFFFFB000).copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(10.dp)
                        .background(Color(0xFF1A1A1A), RoundedCornerShape(2.dp))
                        .border(
                            1.dp,
                            Color(0xFFFFB000).copy(alpha = 0.5f),
                            RoundedCornerShape(2.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(trinityProgress)
                            .fillMaxHeight()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(Color(0xFFFFFF00), Color(0xFFFF4500))
                                ),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ═══════════════════════════════════════════════════════════════
        // ACTION BUTTONS (4 buttons in 2x2 grid)
        // ═══════════════════════════════════════════════════════════════
        if (actionButtons.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    actionButtons.getOrNull(0)?.let { action ->
                        AgentActionButton(
                            action = action,
                            primaryColor = neonMagenta,
                            secondaryColor = Color(0xFFFFFF00),
                            onClick = { onActionClick(action) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    actionButtons.getOrNull(1)?.let { action ->
                        AgentActionButton(
                            action = action,
                            primaryColor = neonCyan,
                            secondaryColor = Color(0xFF00FF00),
                            onClick = { onActionClick(action) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    actionButtons.getOrNull(2)?.let { action ->
                        AgentActionButton(
                            action = action,
                            primaryColor = neonMagenta,
                            secondaryColor = Color(0xFFFFFF00),
                            onClick = { onActionClick(action) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    actionButtons.getOrNull(3)?.let { action ->
                        AgentActionButton(
                            action = action,
                            primaryColor = neonCyan,
                            secondaryColor = Color(0xFFFFFF00),
                            onClick = { onActionClick(action) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Agent Action Button - Angled cyberpunk button
 */
@Composable
private fun AgentActionButton(
    action: AgentAction,
    primaryColor: Color,
    secondaryColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF1A1A1A), Color(0xFF0A0A0A))
                ),
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    listOf(primaryColor.copy(alpha = 0.8f), secondaryColor.copy(alpha = 0.6f))
                ),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = action.label.lowercase(),
            color = primaryColor,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        )
    }
}

/**
 * Agent Action - Defines a clickable action button
 */
data class AgentAction(
    val id: String,
    val label: String,
    val description: String = ""
)

/**
 * Predefined agent actions matching the mockups
 */
object AgentActions {
    // Kai's actions
    val KAI_PROMPT = AgentAction("kai_prompt", "PROMPT", "Talk to Kai")
    val KAI_RGSS = AgentAction("kai_rgss", "R.G.S.S", "Reality Gate Security System")
    val KAI_SCAN = AgentAction("kai_scan", "SCAN", "Security scan")
    val KAI_FUSION = AgentAction("kai_fusion", "FUSION", "Fusion mode")

    // Aura's actions
    val AURA_PROMPT = AgentAction("aura_prompt", "PROMPT", "Talk to Aura")
    val AURA_UXUIDS = AgentAction("aura_uxuids", "UXUIDS", "UI/UX Design Studio")
    val AURA_CREATE = AgentAction("aura_create", "CREATE", "Generate UI")
    val AURA_FUSION = AgentAction("aura_fusion", "FUSION", "Fusion mode")

    // Genesis actions
    val GENESIS_PROMPT = AgentAction("genesis_prompt", "PROMPT", "Talk to Genesis")
    val GENESIS_ORCHESTRATE =
        AgentAction("genesis_orchestrate", "ORCHESTRATE", "Multi-agent coordination")
    val GENESIS_CREATE = AgentAction("genesis_create", "CREATE", "Create agent/module")
    val GENESIS_FUSION = AgentAction("genesis_fusion", "FUSION", "Fusion mode")

    // Cascade actions
    val CASCADE_PROMPT = AgentAction("cascade_prompt", "PROMPT", "Talk to Cascade")
    val CASCADE_VISION = AgentAction("cascade_vision", "VISION", "Visual analysis")
    val CASCADE_CONSENSUS = AgentAction("cascade_consensus", "CONSENSUS", "Build consensus")
    val CASCADE_FUSION = AgentAction("cascade_fusion", "FUSION", "Fusion mode")

    // Common actions
    val SCAN = AgentAction("scan", "SCAN", "Scan/Analyze")
    val TASKS = AgentAction("tasks", "TASKS", "View tasks")
    val STATS = AgentAction("stats", "STATS", "View statistics")
}

