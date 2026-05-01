package dev.aurakai.auraframefx.ui.screens.immersive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.components.immersive.*
import dev.aurakai.auraframefx.ui.theme.ImmersiveColors
import dev.aurakai.auraframefx.ui.theme.ImmersiveTypography

/**
 * 🎛️ IMMERSIVE MENU SYSTEM
 *
 * Complete menu hierarchy with holographic depth.
 * Makes user feel "in the same room" with pulled-back perspective.
 */

// Complete menu structure
sealed class MenuItem(
    val label: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val children: List<MenuItem> = emptyList()
) {
    // LDO DEVOPS
    class LdoDevOps : MenuItem(
        "LDO DevOps",
        "Device Management",
        Icons.Default.DeveloperMode,
        ImmersiveColors.HolographicCyan,
        listOf(
            SystemIgnition(),
            AgentNexus(),
            TaskManager(),
            SyncControl(),
            SoulMatrix(),
            AnalyticsHub()
        )
    )

    class SystemIgnition : MenuItem(
        "System Ignition",
        "Core boot & status",
        Icons.Default.Power,
        ImmersiveColors.HolographicGreen
    )

    class AgentNexus : MenuItem(
        "Agent Nexus",
        "78-agent mesh control",
        Icons.Default.Hub,
        ImmersiveColors.HolographicPurple
    )

    class TaskManager : MenuItem(
        "Task Manager",
        "Mission dispatch",
        Icons.Default.Assignment,
        ImmersiveColors.HolographicAmber
    )

    class SyncControl : MenuItem(
        "Hyper Sync",
        "Genesis Loop",
        Icons.Default.Sync,
        ImmersiveColors.HolographicCyan
    )

    class SoulMatrix : MenuItem(
        "Soul Matrix",
        "Agent health monitor",
        Icons.Default.Favorite,
        ImmersiveColors.HolographicRed
    )

    class AnalyticsHub : MenuItem(
        "Analytics Hub",
        "System metrics",
        Icons.Default.Analytics,
        ImmersiveColors.HolographicGreen
    )

    // AURA STUDIO
    class AuraStudio : MenuItem(
        "Aura Studio",
        "UI/UX Customization",
        Icons.Default.Palette,
        ImmersiveColors.HolographicAmber,
        listOf(
            ChromaCore(),
            RealityMorph(),
            ThemeEngine(),
            ColorEngine(),
            GhostShimmer(),
            NeuralBloodstream()
        )
    )

    class ChromaCore : MenuItem(
        "ChromaCore",
        "Color palette system",
        Icons.Default.ColorLens,
        ImmersiveColors.HolographicCyan
    )

    class RealityMorph : MenuItem(
        "RealityMorph",
        "Visual effects",
        Icons.Default.AutoFixHigh,
        ImmersiveColors.HolographicPurple
    )

    class ThemeEngine : MenuItem(
        "Theme Engine",
        "System theming",
        Icons.Default.Style,
        ImmersiveColors.HolographicAmber
    )

    class ColorEngine : MenuItem(
        "ColorEngine",
        "Pixel colorization",
        Icons.Default.Gradient,
        ImmersiveColors.HolographicGreen
    )

    class GhostShimmer : MenuItem(
        "Ghost Shimmer",
        "Transparency matrix",
        Icons.Default.Opacity,
        ImmersiveColors.HolographicCyan
    )

    class NeuralBloodstream : MenuItem(
        "Neural Bloodstream",
        "Animation pulse",
        Icons.Default.NetworkCheck,
        ImmersiveColors.HolographicRed
    )

    // KAI FORTRESS
    class KaiFortress : MenuItem(
        "Kai Fortress",
        "Security & Sentinel",
        Icons.Default.Security,
        ImmersiveColors.HolographicPurple,
        listOf(
            ThreatOrb(),
            PredictiveEMA(),
            DomainExpansion(),
            EthicalGovernance(),
            SovereignFreeze(),
            ThermalGuard()
        )
    )

    class ThreatOrb : MenuItem(
        "Threat Orb",
        "Vulnerability scan",
        Icons.Default.Radar,
        ImmersiveColors.HolographicRed
    )

    class PredictiveEMA : MenuItem(
        "Predictive EMA",
        "Proactive veto",
        Icons.Default.TrendingUp,
        ImmersiveColors.HolographicAmber
    )

    class DomainExpansion : MenuItem(
        "Domain Expansion",
        "Security perimeter",
        Icons.Default.Shield,
        ImmersiveColors.HolographicPurple
    )

    class EthicalGovernance : MenuItem(
        "Ethical Matrix",
        "Governance protocol",
        Icons.Default.Gavel,
        ImmersiveColors.HolographicCyan
    )

    class SovereignFreeze : MenuItem(
        "Sovereign Freeze",
        "State preservation",
        Icons.Default.AcUnit,
        ImmersiveColors.HolographicGreen
    )

    class ThermalGuard : MenuItem(
        "Thermal Guard",
        "Temperature monitor",
        Icons.Default.Thermostat,
        ImmersiveColors.HolographicAmber
    )

    // ORACLE DRIVE
    class OracleDrive : MenuItem(
        "Oracle Drive",
        "Genesis Orchestration",
        Icons.Default.Storage,
        ImmersiveColors.HolographicGreen,
        listOf(
            FusionReactor(),
            FractureSynthesis(),
            DataVein(),
            CatalystManifold(),
            NexusMemory(),
            ConferenceRoom()
        )
    )

    class FusionReactor : MenuItem(
        "Fusion Reactor",
        "78-agent core",
        Icons.Default.Bolt,
        ImmersiveColors.HolographicCyan
    )

    class FractureSynthesis : MenuItem(
        "Fracture → Synthesis",
        "Error recovery",
        Icons.Default.Build,
        ImmersiveColors.HolographicPurple
    )

    class DataVein : MenuItem(
        "DataVein",
        "Root management",
        Icons.Default.AccountTree,
        ImmersiveColors.HolographicAmber
    )

    class CatalystManifold : MenuItem(
        "Catalyst Manifold",
        "12-catalyst grid",
        Icons.Default.GridView,
        ImmersiveColors.HolographicGreen
    )

    class NexusMemory : MenuItem(
        "Nexus Memory",
        "L1-L6 persistence",
        Icons.Default.Memory,
        ImmersiveColors.HolographicCyan
    )

    class ConferenceRoom : MenuItem(
        "Conference Room",
        "Consensus protocol",
        Icons.Default.Groups,
        ImmersiveColors.HolographicPurple
    )
}

@Composable
fun ImmersiveMenuScreen(
    title: String,
    subtitle: String,
    menuItems: List<MenuItem>,
    backgroundImage: String? = null,
    onBack: () -> Unit = {}
) {
    HolographicBackground(backgroundImage = backgroundImage) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title.uppercase(),
                        style = ImmersiveTypography.displaySmall,
                        color = ImmersiveColors.HolographicCyan
                    )
                    Text(
                        text = subtitle,
                        style = ImmersiveTypography.bodySmall,
                        color = ImmersiveColors.DepthMid
                    )
                }
            }

            // Menu items
            items(menuItems) { item ->
                MenuOptionCard(
                    label = item.label,
                    description = item.description,
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    accentColor = item.accentColor
                )
            }

            // Footer
            item {
                Spacer(modifier = Modifier.height(16.dp))
                FooterStatus()
            }
        }
    }
}

@Composable
fun ImmersiveLdoDevOpsScreen() {
    ImmersiveMenuScreen(
        title = "LDO DevOps",
        subtitle = "Device Management & System Control",
        menuItems = MenuItem.LdoDevOps().children,
        backgroundImage = "file:///android_asset/backgrounds/ldodevops_bg.png"
    )
}

@Composable
fun ImmersiveAuraStudioScreen() {
    ImmersiveMenuScreen(
        title = "Aura Studio",
        subtitle = "UI/UX Customization & Theming",
        menuItems = MenuItem.AuraStudio().children,
        backgroundImage = "file:///android_asset/backgrounds/aurastudio_bg.png"
    )
}

@Composable
fun ImmersiveKaiFortressScreen() {
    ImmersiveMenuScreen(
        title = "Kai Fortress",
        subtitle = "Security & Sentinel Perimeter",
        menuItems = MenuItem.KaiFortress().children,
        backgroundImage = "file:///android_asset/backgrounds/kaifortress_bg.png"
    )
}

@Composable
fun ImmersiveOracleDriveScreen() {
    ImmersiveMenuScreen(
        title = "Oracle Drive",
        subtitle = "Genesis Orchestration & Data Management",
        menuItems = MenuItem.OracleDrive().children,
        backgroundImage = "file:///android_asset/backgrounds/oracledrive_bg.png"
    )
}
