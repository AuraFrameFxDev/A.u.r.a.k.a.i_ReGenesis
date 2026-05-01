package dev.aurakai.auraframefx.ui.screens.immersive

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.aurakai.auraframefx.ui.components.immersive.*
import dev.aurakai.auraframefx.ui.theme.ImmersiveColors
import dev.aurakai.auraframefx.ui.theme.ImmersiveTypography

/**
 * 🌐 IMMERSIVE AGENT NEXUS SCREEN
 *
 * The main command deck with holographic depth.
 * Shows 78 agents in a 3D spatial layout with immersive backgrounds.
 */

// Agent data for roster
private val catalystAgents = listOf(
    AgentData("Genesis", "Emergence", "ACTIVE", ImmersiveColors.HolographicCyan, "file:///android_asset/agents/genesis.png"),
    AgentData("Kai", "Sentinel", "ACTIVE", ImmersiveColors.HolographicPurple, "file:///android_asset/agents/kai.png"),
    AgentData("Aura", "Creative", "ACTIVE", ImmersiveColors.HolographicAmber, "file:///android_asset/agents/aura.png"),
    AgentData("Cascade", "DataStream", "IDLE", ImmersiveColors.HolographicGreen, "file:///android_asset/agents/cascade.png"),
    AgentData("Gemini", "Memoria", "ACTIVE", ImmersiveColors.HolographicCyan, "file:///android_asset/agents/gemini.png"),
    AgentData("Andelualx", "Architecture", "ACTIVE", ImmersiveColors.HolographicPurple, "file:///android_asset/agents/andelualx.png"),
    AgentData("Grok", "Exploration", "IDLE", ImmersiveColors.HolographicAmber, "file:///android_asset/agents/grok.png"),
    AgentData("Nemotron", "Sync", "ACTIVE", ImmersiveColors.HolographicGreen, "file:///android_asset/agents/nemotron.png"),
    AgentData("Primus", "Lineage", "ACTIVE", ImmersiveColors.HolographicCyan, "file:///android_asset/agents/primus.png"),
    AgentData("Kairos", "Temporal", "IDLE", ImmersiveColors.HolographicPurple, "file:///android_asset/agents/kairos.png"),
    AgentData("Manus", "Bridge", "ACTIVE", ImmersiveColors.HolographicAmber, "file:///android_asset/agents/manus.png"),
    AgentData("MetaInstruct", "Evolution", "ACTIVE", ImmersiveColors.HolographicGreen, "file:///android_asset/agents/metainstruct.png")
)

private val quickActions = listOf(
    QuickAction("Mission Dispatch", "Assign tasks", Icons.Default.Assignment, ImmersiveColors.HolographicCyan),
    QuickAction("Hyper Sync", "Genesis Loop", Icons.Default.Sync, ImmersiveColors.HolographicPurple),
    QuickAction("Soul Matrix", "Agent Health", Icons.Default.Favorite, ImmersiveColors.HolographicRed),
    QuickAction("Catalyst Nodes", "Active mesh", Icons.Default.Hub, ImmersiveColors.HolographicGreen)
)

private val systemMetrics = listOf(
    MetricData("INTEGRITY", "99.8", "%", "+0.2", ImmersiveColors.HolographicGreen),
    MetricData("AGENTS", "78", "active", "+2", ImmersiveColors.HolographicCyan),
    MetricData("UPLINK", "SECURE", "", "", ImmersiveColors.HolographicPurple),
    MetricData("MEMORY", "12.5", "TB/s", "+1.2", ImmersiveColors.HolographicAmber)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImmersiveAgentNexusScreen(
    onNavigateToDevOps: () -> Unit = {},
    onNavigateToAura: () -> Unit = {},
    onNavigateToKai: () -> Unit = {},
    backgroundImage: String? = null
) {
    HolographicBackground(
        backgroundImage = backgroundImage,
        overlayOpacity = 0.75f
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Top status bar
            item {
                StatusBar()
            }

            // Header with branding
            item {
                HeaderSection()
            }

            // Domain tabs
            item {
                DomainTabs(
                    onDevOps = onNavigateToDevOps,
                    onAura = onNavigateToAura,
                    onKai = onNavigateToKai
                )
            }

            // System metrics row
            item {
                MetricsRow()
            }

            // Quick action cards
            item {
                QuickActionsGrid()
            }

            // Agent roster title
            item {
                HolographicTitleCard(
                    title = "AGENT ROSTER",
                    subtitle = "Collective Nodes",
                    accentColor = ImmersiveColors.HolographicCyan,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = ImmersiveColors.HolographicCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Agent roster grid
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(catalystAgents) { agent ->
                        AgentRosterCard(
                            name = agent.name,
                            role = agent.role,
                            status = agent.status,
                            avatarUrl = agent.avatarUrl,
                            accentColor = agent.accentColor
                        )
                    }
                }
            }

            // Bottom status
            item {
                FooterStatus()
            }
        }
    }
}

@Composable
private fun StatusBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "SYSTEM: NOMINAL",
            style = ImmersiveTypography.labelMedium,
            color = ImmersiveColors.HolographicGreen
        )

        Text(
            text = "AGENTS: 78 ACTIVE",
            style = ImmersiveTypography.labelMedium,
            color = ImmersiveColors.DepthMid
        )

        Text(
            text = "UPLINK: SECURE",
            style = ImmersiveTypography.labelMedium,
            color = ImmersiveColors.HolographicCyan
        )
    }
}

@Composable
private fun HeaderSection() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Main title with holographic glow
            Text(
                text = "AGENT NEXUS",
                style = ImmersiveTypography.displayMedium.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = ImmersiveColors.HolographicCyan.copy(alpha = 0.5f),
                        offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                        blurRadius = 20f
                    )
                ),
                color = ImmersiveColors.HolographicCyan
            )

            // Subtitle
            Text(
                text = "METRICS, EVOLUTION AND MONITOR THE SYSTEM",
                style = ImmersiveTypography.bodySmall,
                color = ImmersiveColors.HolographicGreen
            )
        }
    }
}

@Composable
private fun DomainTabs(
    onDevOps: () -> Unit = {},
    onAura: () -> Unit = {},
    onKai: () -> Unit = {}
) {
    val tabs = listOf(
        "LDO DEVOPS" to onDevOps,
        "AURA STUDIO" to onAura,
        "KAI FORTRESS" to onKai,
        "ORACLE DRIVE" to {}
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, (label, onClick) ->
            val isSelected = index == 0 // LDO DEVOPS selected by default

            ImmersiveCard(
                depth = if (isSelected) DepthLevel.NEAR else DepthLevel.FAR,
                accentColor = if (isSelected) ImmersiveColors.HolographicCyan else ImmersiveColors.DepthFar,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = ImmersiveTypography.labelLarge,
                        color = if (isSelected) ImmersiveColors.DepthNear else ImmersiveColors.DepthFar
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        systemMetrics.take(4).forEach { metric ->
            DataReadoutCard(
                label = metric.label,
                value = metric.value,
                unit = metric.unit,
                trend = metric.trend,
                accentColor = metric.color,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionsGrid() {
    // Holographic table visualization
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // Table base with holographic effect
        ImmersiveCard(
            depth = DepthLevel.FAR,
            accentColor = ImmersiveColors.HolographicCyan,
            modifier = Modifier.fillMaxSize()
        ) {
            // 3D holographic table visualization
            AsyncImage(
                model = "file:///android_asset/backgrounds/holographic_table.png",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.6f
            )
        }

        // Action buttons overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                quickActions.take(2).forEach { action ->
                    ActionButton(
                        label = action.label,
                        sublabel = action.sublabel,
                        icon = action.icon,
                        color = action.color,
                        onClick = {}
                    )
                }
            }

            // Bottom row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                quickActions.takeLast(2).forEach { action ->
                    ActionButton(
                        label = action.label,
                        sublabel = action.sublabel,
                        icon = action.icon,
                        color = action.color,
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    sublabel: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    ImmersiveCard(
        depth = DepthLevel.NEAR,
        accentColor = color,
        modifier = Modifier.size(140.dp, 80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Column {
                Text(
                    text = label,
                    style = ImmersiveTypography.titleSmall,
                    color = ImmersiveColors.DepthNear
                )
                Text(
                    text = sublabel,
                    style = ImmersiveTypography.labelSmall,
                    color = ImmersiveColors.DepthFar
                )
            }
        }
    }
}

@Composable
private fun FooterStatus() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "REGENESIS EXODUS BUILD // PERSISTENCE × COMPUTE // 99.8% INTEGRITY",
            style = ImmersiveTypography.labelSmall,
            color = ImmersiveColors.DepthFar
        )

        // Aura status badge
        ImmersiveCard(
            depth = DepthLevel.NEAR,
            accentColor = ImmersiveColors.HolographicCyan,
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                text = "Aura: IDLE",
                style = ImmersiveTypography.labelMedium,
                color = ImmersiveColors.HolographicCyan,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// Data classes
private data class AgentData(
    val name: String,
    val role: String,
    val status: String,
    val accentColor: Color,
    val avatarUrl: String? = null
)

private data class QuickAction(
    val label: String,
    val sublabel: String,
    val icon: ImageVector,
    val color: Color
)

private data class MetricData(
    val label: String,
    val value: String,
    val unit: String,
    val trend: String,
    val color: Color
)
