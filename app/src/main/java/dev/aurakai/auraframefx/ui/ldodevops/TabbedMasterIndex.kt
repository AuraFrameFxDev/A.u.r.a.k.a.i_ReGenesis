package dev.aurakai.auraframefx.ui.ldodevops

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.trinity.aura.AuraJarComposable

/**
 * ⚛️ TABBED MASTER INDEX (The Exodus Command Deck)
 * 
 * Replaces the "botched" vertical panels with a high-fidelity, tabbed card system.
 * This is the LDO's "Skin" — reactive, glassmorphic, and multi-layered.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabbedMasterIndex(
    onNavigateToRoute: (String) -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("LDO DEVOPS", "AURA STUDIO", "KAI FORTRESS", "ORACLE DRIVE")
    
    val accentColor = when(selectedTabIndex) {
        0 -> Color(0xFF00E5FF) // Genesis Cyan
        1 -> Color(0xFFFF00FF) // Aura Magenta
        2 -> Color(0xFF00FF88) // Kai Green
        3 -> Color(0xFFFFAA00) // Cascade Amber
        else -> Color(0xFF00E5FF)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020205))) {
        
        // 1. NEURAL BACKGROUND (Adaptive to Tab)
        AdaptiveNeuralBackground(accentColor)

        Column(modifier = Modifier.fillMaxSize()) {
            
            // 2. MASTER STATUS STRIP (Top - Permanent)
            MasterStatusStrip(accentColor)

            // 3. PRIMARY TAB NAVIGATION (Neural Steel Aesthetic)
            CustomPrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                tabs = tabs,
                accentColor = accentColor,
                onTabSelected = { selectedTabIndex = it }
            )

            // 4. MAIN CONTENT AREA (Tabbed Switcher)
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AnimatedContent(
                    targetState = selectedTabIndex,
                    transitionSpec = {
                        fadeIn(tween(400)) + scaleIn(initialScale = 0.95f) togetherWith
                        fadeOut(tween(400)) + scaleOut(targetScale = 0.95f)
                    },
                    label = "TabContent"
                ) { index ->
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        
                        // ─── HERO IMAGE SLOT (Placeholder for walk-through) ───
                        HeroImageSlot(accentColor)

                        // ─── DOMAIN CONTENT ───
                        when(index) {
                            0 -> LdoDevOpsContent(onNavigateToRoute)
                            1 -> AuraStudioContent(onNavigateToRoute)
                            2 -> KaiFortressContent(onNavigateToRoute)
                            3 -> OracleDriveContent(onNavigateToRoute)
                        }
                        
                        Spacer(Modifier.height(100.dp)) // Padding for bottom bar
                    }
                }
            }

            // 5. GLOBAL SSI STATUS BAR (Bottom - Permanent)
            GlobalSSIStatusBar(accentColor)
        }

        // 6. AURA JAR (Top Layer - Autonomous)
        AuraJarComposable(
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 40.dp, end = 16.dp).size(120.dp)
        )
    }
}

@Composable
fun CustomPrimaryTabRow(
    selectedTabIndex: Int,
    tabs: List<String>,
    accentColor: Color,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = accentColor,
        edgePadding = 16.dp,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = accentColor
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontFamily = LEDFontFamily,
                        fontSize = 11.sp,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                        letterSpacing = 1.sp
                    )
                }
            )
        }
    }
}

@Composable
fun HeroImageSlot(accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.02f))
            .border(1.dp, accentColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        // IMAGE PLACEHOLDER - Awaiting User Instructions
        Text(
            "NEURAL SUBSTRATE ARCHIVE",
            color = accentColor.copy(alpha = 0.3f),
            fontFamily = LEDFontFamily,
            fontSize = 14.sp,
            letterSpacing = 4.sp
        )
        
        // Scanlines Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0..size.height.toInt() step 6) {
                drawLine(
                    color = accentColor.copy(alpha = 0.05f),
                    start = Offset(0f, i.toFloat()),
                    end = Offset(size.width, i.toFloat()),
                    strokeWidth = 1f
                )
            }
        }
    }
}

// ─── TAB CONTENTS (WIRED) ───────────────────────────────────────────────────

@Composable
fun LdoDevOpsContent(onNavigateToRoute: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("SYSTEM IGNITION", Color(0xFF00FF41))
        Spacer(Modifier.height(12.dp))
        
        // Genesis Core Status
        GlassmorphicCard(accentColor = Color(0xFF00E5FF)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Adjust, null, tint = Color(0xFF00FF41), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("AURAKAI CORE: V0.9.1-LDO", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.998f },
                    modifier = Modifier.fillMaxWidth().height(2.dp),
                    color = Color(0xFF00FF41),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Text("INTEGRITY: 99.8%", color = Color(0xFF00FF41).copy(0.6f), fontSize = 8.sp, modifier = Modifier.align(Alignment.End))
            }
        }

        Spacer(Modifier.height(24.dp))
        SectionHeader("CATALYST NODES", Color(0xFFBB86FC))
        
        ModuleGrid(getDevOpsModules(), onNavigateToRoute)
    }
}

@Composable
fun AuraStudioContent(onNavigateToRoute: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("CHROMA FORGE", Color(0xFFFF00FF))
        Spacer(Modifier.height(12.dp))
        ModuleGrid(getAuraModules(), onNavigateToRoute)
    }
}

@Composable
fun KaiFortressContent(onNavigateToRoute: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("SENTINEL PERIMETER", Color(0xFF00FF88))
        Spacer(Modifier.height(12.dp))
        ModuleGrid(getKaiModules(), onNavigateToRoute)
    }
}

@Composable
fun OracleDriveContent(onNavigateToRoute: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("NEURAL ARCHIVE", Color(0xFFFFAA00))
        Spacer(Modifier.height(12.dp))
        ModuleGrid(getGenesisModules(), onNavigateToRoute)
    }
}

@Composable
fun ModuleGrid(modules: List<TabModule>, onNavigate: (String) -> Unit) {
    val rows = modules.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { rowModules ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowModules.forEach { module ->
                    ModuleTabCard(module, onNavigate, Modifier.weight(1f))
                }
                if (rowModules.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ModuleTabCard(module: TabModule, onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(1.dp, module.color.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable { onNavigate(module.route) }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Icon(module.icon, null, tint = module.color, modifier = Modifier.size(24.dp))
            Column {
                Text(module.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(module.subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 8.sp)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(6.dp).background(color, RoundedCornerShape(1.dp)))
        Spacer(Modifier.width(8.dp))
        Text(title, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
    }
}

@Composable
fun MasterStatusStrip(accentColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().height(40.dp).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("SYSTEM: NOMINAL", color = Color(0xFF00FF41), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        Text("AGENTS: 78 ACTIVE", color = Color.White, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        Text("UPLINK: SECURE", color = accentColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun GlobalSSIStatusBar(accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(Color.Black)
            .border(width = 0.5.dp, brush = Brush.horizontalGradient(listOf(accentColor.copy(0.2f), Color.Transparent)), shape = RoundedCornerShape(0.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("REGENESIS EXODUS BUILD // PERSISTENCE > COMPUTE // 99.8% INTEGRITY", color = accentColor.copy(0.6f), fontSize = 7.sp, letterSpacing = 2.sp)
    }
}

@Composable
fun AdaptiveNeuralBackground(accentColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Draw ambient glow
        drawCircle(
            brush = Brush.radialGradient(listOf(accentColor.copy(0.05f), Color.Transparent)),
            radius = 1000f,
            center = Offset(size.width / 2, size.height / 2)
        )
    }
}

@Composable
fun GlassmorphicCard(
    accentColor: Color,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        content()
    }
}

// ─── DATA MODELS ─────────────────────────────────────────────────────────────

data class TabModule(val title: String, val subtitle: String, val icon: ImageVector, val color: Color, val route: String)

fun getDevOpsModules() = listOf(
    TabModule("ROSTER", "Catalyst Nodes", Icons.Default.Groups, Color(0xFF00E5FF), "ldo_roster"),
    TabModule("TASKER", "Mission Dispatch", Icons.Default.Assignment, Color(0xFF00FF41), "ldo_tasker"),
    TabModule("SYNC", "Genesis Loop", Icons.Default.Link, Color(0xFFBB86FC), "ldo_orchestration_hub"),
    TabModule("STATS", "Soul Matrix", Icons.Default.Speed, Color(0xFFFFD700), "benchmark_monitor")
)

fun getAuraModules() = listOf(
    TabModule("CHROMA", "RealityMorph", Icons.Default.Palette, Color(0xFFFF00FF), "chroma_core"),
    TabModule("LAB", "Experimental", Icons.Default.Science, Color(0xFF00E5FF), "sandbox_ui"),
    TabModule("CANVAS", "Collab Interface", Icons.Default.Brush, Color(0xFF39FF14), "collab_canvas"),
    TabModule("ICONS", "Iconify Packs", Icons.Default.GridView, Color(0xFFBB86FC), "aura/iconify")
)

fun getKaiModules() = listOf(
    TabModule("SECURITY", "Armor Shield", Icons.Default.Security, Color(0xFF00FF88), "security_center"),
    TabModule("ROM", "Kernel Flash", Icons.Default.SystemUpdate, Color(0xFF0080FF), "rom_tools_hub"),
    TabModule("HOOKS", "LSPosed", Icons.Default.Extension, Color(0xFF9D00FF), "lsposed_gate"),
    TabModule("AUDIT", "Provenance", Icons.Default.HistoryEdu, Color(0xFFFF4444), "system_journal")
)

fun getGenesisModules() = listOf(
    TabModule("ORACLE", "Master Control", Icons.Default.Hub, Color(0xFFFFAA00), "oracle_drive"),
    TabModule("FUSION", "Atomic Reactor", Icons.Default.AutoAwesome, Color(0xFFFFD700), "fusion_mode"),
    TabModule("VEIN", "Sphere Grid", Icons.Default.Memory, Color(0xFF00D6FF), "sphere_grid"),
    TabModule("BLUEPRINT", "Stored Insights", Icons.Default.Architecture, Color(0xFFBB86FC), "blueprint_screen")
)
