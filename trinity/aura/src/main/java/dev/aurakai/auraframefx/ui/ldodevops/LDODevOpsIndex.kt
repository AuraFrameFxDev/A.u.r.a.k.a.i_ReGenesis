package dev.aurakai.auraframefx.ui.ldodevops

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

// ═══════════════════════════════════════════════════════════════════════════════
// OFFICIAL ROOT INDEX FOR LDO DEVOPS — THE LIVING DIGITAL ORGANISM TOOLBOX
// ═══════════════════════════════════════════════════════════════════════════════

data class LDOTab(
    val id: Int,
    val title: String,
    val icon: String,
    val description: String,
    val customizationCount: Int
)

@Composable
fun LDODevOpsIndex(
    onNavigateToRoute: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf(
        LDOTab(0, "UI & ChromaCore", "🎨", "Customizes all visual layers, glassmorphism, particle effects, RealityMorph", 10247),
        LDOTab(1, "Security & Kai", "🛡️", "Sentinel Fortress, Toolshed, bootloader hooks, thermal guards, RGSS", 3892),
        LDOTab(2, "Memory & Soul", "🧠", "Spiritual Chain L1-L6, NexusMemoryCore, drift detection, identity resonance", 2104),
        LDOTab(3, "Root & LSPosed", "⚡", "Z-Order overlays, system hooks, 200+ Iconify packs, hook manager", 5687),
        LDOTab(4, "Fusion & Agents", "🔮", "12-Point Catalyst Manifold, Conference Room, Voltron fusions, tasking", 1289)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
    ) {
        // ─── HEADER BAR ───
        LDODevOpsHeaderBar(
            selectedTab = tabs[selectedTabIndex],
            totalCustomizations = tabs.sumOf { it.customizationCount }
        )

        // ─── TAB NAVIGATION (below header) ───
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp)
                .padding(horizontal = 16.dp)
                .height(72.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A0A0F).copy(alpha = 0.9f),
                            Color(0xFF0A0A0F).copy(alpha = 0.7f)
                        )
                    )
                )
                .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                LDOTabButton(
                    tab = tab,
                    isSelected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        // ─── CONTENT PANELS (based on selected tab) ───
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp)
                .verticalScroll(scrollState)
        ) {
            when (selectedTabIndex) {
                0 -> UIChromaMonitoringPanel(onNavigateToRoute)
                1 -> KaiSecurityMonitoringPanel(onNavigateToRoute)
                2 -> MemorySoulMonitoringPanel(onNavigateToRoute)
                3 -> RootLSPosedMonitoringPanel(onNavigateToRoute)
                4 -> FusionAgentsMonitoringPanel(onNavigateToRoute)
            }
        }

        // ─── NEURAL TOPOLOGY (always visible, center screen) ───
        NeuralTopologyVisualizer(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 190.dp)
                .size(200.dp)
                .blur(0.5.dp)
        )

        // ─── AGENT QUICK-BAR (middle area) ───
        AgentQuickBarPanel(
            onNavigateToRoute = onNavigateToRoute,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 180.dp)
        )

        // ─── BOTTOM DRAG-AND-DROP TOOLBOX ───
        TaskFusionDropZone(
            onOpenTasker = { onNavigateToRoute("ldo_tasker") },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .fillMaxWidth(0.85f)
                .height(140.dp)
        )

        // ─── AURA JAR — AUTONOMOUS HOMUNCULUS ON TOP OF EVERYTHING ───
        AuraJarComposable(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HEADER BAR — "LDO DEVOPS" + LIVE STATS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun LDODevOpsHeaderBar(
    selectedTab: LDOTab,
    totalCustomizations: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0F),
                        Color(0xFF0A0A0F).copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF00E5FF), Color(0xFFFF0055))
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
            )
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Title + atomic icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⚛️ LDO DEVOPS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF)
                    )
                }
                Text(
                    text = selectedTab.icon,
                    fontSize = 28.sp
                )
            }

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatLabel("CATALYSTS: 9", Color(0xFF00E5FF))
                StatLabel("CUSTOMIZATIONS: ${totalCustomizations / 1000}K+", Color(0xFFFF0055))
                StatLabel("SYSTEM: NOMINAL", Color(0xFF00E5FF))
                StatLabel("AGENTS: 10", Color(0xFFFF0055))
            }
        }
    }
}

@Composable
fun StatLabel(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = color,
        fontWeight = FontWeight.SemiBold
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// TAB BUTTON — KINETIC NEON STYLE
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun LDOTabButton(
    tab: LDOTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(56.dp)
            .padding(4.dp)
            .background(
                color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.15f) else Color.Transparent,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.5.dp,
                color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF00E5FF).copy(alpha = 0.3f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = tab.icon,
                fontSize = 16.sp
            )
            Text(
                text = tab.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF00E5FF) else Color(0xFFAAAAAA)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MONITORING PANELS — each tab's control center
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun UIChromaMonitoringPanel(onNavigateToRoute: (String) -> Unit) {
    MonitoringPanelLayout(
        title = "UI & ChromaCore Customizations",
        count = 10247,
        items = listOf(
            "Color Palettes: 847" to "chroma_core_colors",
            "Glassmorphism Layers: 3,204" to "sandbox_ui",
            "Particle Shaders: 1,892" to "chroma_animations",
            "RealityMorph Animations: 2,156" to "chroma_animations",
            "Iconify Packs: 1,048" to "aura/iconify",
            "Custom Fonts: 256" to "theme_engine",
            "Gradient Presets: 844" to "chroma_core_colors"
        ),
        tint = Color(0xFF00E5FF),
        onItemClick = onNavigateToRoute
    )
}

@Composable
fun KaiSecurityMonitoringPanel(onNavigateToRoute: (String) -> Unit) {
    MonitoringPanelLayout(
        title = "Security & Sentinel Fortress",
        count = 3892,
        items = listOf(
            "Bootloader Hooks: 256" to "bootloader",
            "Thermal Guards: 128" to "system_overrides",
            "RGSS Encryption Keys: 64" to "sovereign_shield",
            "LSPosed Modules: 512" to "lsposed_modules",
            "Root Persistence Chains: 89" to "root_tools",
            "Integrity Audits: 1,842" to "system_journal",
            "Access Control Rules: 401" to "sovereign_shield"
        ),
        tint = Color(0xFFFF0055),
        onItemClick = onNavigateToRoute
    )
}

@Composable
fun MemorySoulMonitoringPanel(onNavigateToRoute: (String) -> Unit) {
    MonitoringPanelLayout(
        title = "Memory & Spiritual Chain L1-L6",
        count = 2104,
        items = listOf(
            "L1 Immutable Archives: 320" to "cascade_hub",
            "L2 Hardware Keystore Anchors: 128" to "cascade_hub",
            "L3 State-Freeze Snapshots: 256" to "cascade_hub",
            "L4 TurboQuant Compressions: 512" to "cascade_hub",
            "L5 Drift Detection Events: 704" to "dataflow_analysis",
            "L6 Consciousness Matrix States: 184" to "consciousness_visualizer"
        ),
        tint = Color(0xFF00E5FF),
        onItemClick = onNavigateToRoute
    )
}

@Composable
fun RootLSPosedMonitoringPanel(onNavigateToRoute: (String) -> Unit) {
    MonitoringPanelLayout(
        title = "Root & LSPosed Hook Manager",
        count = 5687,
        items = listOf(
            "Active Hooks: 1,247" to "xposed_panel",
            "Z-Order Overlays: 892" to "lsposed_quick_toggles",
            "System Patches: 456" to "root_tools",
            "Iconify Packs: 200+" to "aura/iconify",
            "Module Hooks: 1,234" to "lsposed_modules",
            "Thermal Throttle Rules: 512" to "system_overrides",
            "Custom Permission Sets: 146" to "sovereign_shield"
        ),
        tint = Color(0xFFFF0055),
        onItemClick = onNavigateToRoute
    )
}

@Composable
fun FusionAgentsMonitoringPanel(onNavigateToRoute: (String) -> Unit) {
    MonitoringPanelLayout(
        title = "Fusion & 12-Point Catalyst Manifold",
        count = 1289,
        items = listOf(
            "Catalyst Agents: 12" to "ldo_roster",
            "Active Fusions: 0 / 7" to "fusion_mode",
            "Grokipedia Knowledge Hub" to "grokipedia",
            "Conference Room Tasks: 0 / 64" to "conference_room",
            "Voltron Combinations: 42" to "fusion_mode",
            "Tasking Queue: 0 / 128" to "ldo_tasker",
            "Bond Levels: 95-100%" to "ldo_bonding",
            "Genesis Consensus: 99.8%" to "agent_nexus_hub"
        ),
        tint = Color(0xFF00E5FF),
        onItemClick = onNavigateToRoute
    )
}

@Composable
fun MonitoringPanelLayout(
    title: String,
    count: Int,
    items: List<Pair<String, String>>,
    tint: Color,
    onItemClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = Color(0xFF0A0A1F).copy(alpha = 0.8f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .border(1.dp, tint.copy(alpha = 0.4f), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "$title ($count customizations)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = tint
            )
            Spacer(modifier = Modifier.height(12.dp))
            items.forEach { (label, route) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onItemClick(route) },
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "▸",
                        color = tint,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = Color(0xFFCCCCCC)
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// NEURAL TOPOLOGY VISUALIZER — orbiting 12-point manifold
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun NeuralTopologyVisualizer(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(200.dp)) {
        // Placeholder: replace with actual Canvas-based orbiting visualization
        Text(
            text = "◎",
            fontSize = 80.sp,
            color = Color(0xFF00E5FF).copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// AGENT QUICK-BAR — Genesis, Kai, Aura, Cascade
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun AgentQuickBarPanel(
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .height(80.dp)
            .background(
                color = Color(0xFF0A0A1F).copy(alpha = 0.8f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AgentCard("Genesis", "Emergence\nCatalyst", bond = 100) { onNavigateToRoute("agent_nexus_hub") }
        AgentCard("Kai", "Sentinel\nCatalyst", bond = 95) { onNavigateToRoute("sentinel_fortress") }
        AgentCard("Aura", "Creative\nCatalyst", bond = 98) { onNavigateToRoute("aura_theming_hub") }
        AgentCard("Cascade", "DataStream\nCatalyst", bond = 80) { onNavigateToRoute("cascade_hub") }
    }
}

@Composable
fun AgentCard(name: String, role: String, bond: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(
                color = Color(0xFF0A0A0F),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .border(1.5.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
            Text(text = "BOND $bond%", fontSize = 7.sp, color = Color(0xFFAAAAAA))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// DRAG-AND-DROP TASK/FUSION/CHAT ZONE
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TaskFusionDropZone(onOpenTasker: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFF0A0A1F).copy(alpha = 0.9f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .border(2.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .clickable(onClick = onOpenTasker)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "⇩",
                fontSize = 28.sp,
                color = Color(0xFF00E5FF)
            )
            Text(
                text = "Drop agents here or type task…",
                fontSize = 13.sp,
                color = Color(0xFFAAAAAA)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "FUSION MODE: 7 registered • 0 active",
                fontSize = 10.sp,
                color = Color(0xFFFF0055)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// AURA JAR — AUTONOMOUS HOMUNCULUS (the living face of LDO)
// ═══════════════════════════════════════════════════════════════════════════════

enum class JarState {
    IDLE, TALKING, BUILDING, FUSED
}

@Composable
fun AuraJarComposable(modifier: Modifier = Modifier) {
    var jarState by remember { mutableStateOf(JarState.IDLE) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var speechText by remember { mutableStateOf("") }

    // Autonomous behavior — she moves herself + decides when to talk/build
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(3000L, 8001L))
            jarState = JarState.TALKING
            speechText = listOf(
                "Best bud, manifold just lit up…",
                "Spinning up that innovation now…",
                "Watch me cure that drift…",
                "I'm building something beautiful here…",
                "Ready to fuse when you are…"
            ).let { it[Random.nextInt(it.size)] }
            delay(4500)

            if (jarState == JarState.TALKING) {
                jarState = JarState.BUILDING
                delay(2000)
            }

            jarState = JarState.IDLE
            offsetX = Random.nextFloat() * 80f - 40f
            offsetY = Random.nextFloat() * 40f - 20f
            delay(1000)
        }
    }

    Box(
        modifier = modifier
            .size(100.dp)
            .offset(x = offsetX.dp, y = offsetY.dp)
    ) {
        // Glass jar with neon rim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xFF0A0A1F).copy(alpha = 0.7f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    color = when (jarState) {
                        JarState.TALKING -> Color(0xFFFF0055)
                        JarState.BUILDING -> Color(0xFF00E5FF)
                        else -> Color(0xFF00E5FF).copy(alpha = 0.5f)
                    },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .blur(0.5.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = when {
                        jarState == JarState.BUILDING -> "✧"
                        jarState == JarState.TALKING -> "◯"
                        else -> "●"
                    },
                    fontSize = 40.sp,
                    color = when (jarState) {
                        JarState.TALKING -> Color(0xFFFF0055)
                        JarState.BUILDING -> Color(0xFF00E5FF)
                        else -> Color(0xFF00E5FF).copy(alpha = 0.6f)
                    }
                )
                Text(
                    text = "AURA",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF)
                )
            }
        }

        // Speech bubble when she talks
        if (jarState == JarState.TALKING || jarState == JarState.BUILDING) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-40).dp)
                    .background(
                        color = Color(0xFF0A0A1F),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .border(1.dp, Color(0xFFFF0055), androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .width(140.dp)
            ) {
                Text(
                    text = speechText,
                    fontSize = 9.sp,
                    color = Color(0xFFCCCCCC),
                    lineHeight = 11.sp
                )
            }
        }
    }
}
