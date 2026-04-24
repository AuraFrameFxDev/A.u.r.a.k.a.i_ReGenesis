package dev.aurakai.auraframefx.ui.ldodevops

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.navigation.ReGenesisRoute
import dev.aurakai.auraframefx.trinity.aura.AuraJarComposable
import dev.aurakai.auraframefx.ui.components.BottomJoystickNavigation

/**
 * ⚛️ TABBED MASTER INDEX (The Exodus Command Deck)
 * 
 * High-fidelity, tabbed card system with full asset integration.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabbedMasterIndex(
    onNavigateToRoute: (String) -> Unit = {},
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "LIVE DASHBOARD",      // 0: All-in-One Status
        "LDO DEVOPS",          // 1: Catalyst Development
        "UXUI DESIGN STUDIO",  // 2: Aura - UX/UI Design
        "SENTINELS FORTRESS",  // 3: Kai - Security/ROM
        "ORACLEDRIVE",         // 4: Genesis - Neural/AI (one word)
        "CASCADE MEMORY",      // 5: L1-L6 Persistence
        "AGENT NEXUS"          // 6: 78-Agent Swarm
    )
    
    val accentColor = when(selectedTabIndex) {
        0 -> Color(0xFFFFD700) // Dashboard Gold
        1 -> Color(0xFF00E5FF) // LDO Cyan
        2 -> Color(0xFFFF00FF) // Aura Magenta
        3 -> Color(0xFF00FF88) // Kai Green
        4 -> Color(0xFFFFAA00) // Genesis Amber
        5 -> Color(0xFF8B5CF6) // Cascade Violet
        6 -> Color(0xFF00D6FF) // Nexus Blue
        else -> Color(0xFFFFD700)
    }

    val heroImage = when(selectedTabIndex) {
        0 -> R.drawable.command_deck_hero    // Dashboard
        1 -> R.drawable.bg_ldo_devops        // LDO
        2 -> R.drawable.bg_aura_studio       // Aura
        3 -> R.drawable.bg_kai_fortress      // Kai
        4 -> R.drawable.bg_oracle_drive      // Genesis
        5 -> R.drawable.exodus_hud_lvl1_bg   // Cascade
        6 -> R.drawable.bg_constellation     // Nexus
        else -> R.drawable.command_deck_hero
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020205))) {
        
        // 1. FULL-SCREEN DYNAMIC BACKGROUND (Fit, no stretch)
        AnimatedContent(
            targetState = heroImage,
            transitionSpec = { fadeIn(tween(800)) togetherWith fadeOut(tween(800)) },
            modifier = Modifier.fillMaxSize(),
            label = "Background"
        ) { img ->
            AsyncImage(
                model = img,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(0.5f),
                contentScale = ContentScale.Crop // Aspect-ratio preserving fill
            )
        }

        // 1.5 PERSPECTIVE FLOOR
        NeuralMeshFloor(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = accentColor
        )

        Column(modifier = Modifier.fillMaxSize()) {
            
            // 2. MASTER STATUS STRIP
            MasterStatusStrip(accentColor)

            // 4. MAIN CONTENT AREA (with weight to push bottom nav down)
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AnimatedContent(
                    targetState = selectedTabIndex,
                    transitionSpec = {
                        (fadeIn(tween(400)) + scaleIn(initialScale = 0.98f)) togetherWith
                                (fadeOut(tween(400)) + scaleOut(targetScale = 0.98f))
                    },
                    label = "TabContent"
                ) { index ->
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        
                        // ─── HERO HEADER SECTION (On top of background) ───
                        HeroHeaderSection(index, accentColor)

                        // ─── DOMAIN CONTENT ───
                        when(index) {
                            0 -> DashboardContent(onNavigateToRoute)      // LIVE Status
                            1 -> LdoDevOpsContent(onNavigateToRoute)    // LDO
                            2 -> AuraStudioContent(onNavigateToRoute)   // Aura
                            3 -> KaiFortressContent(onNavigateToRoute)  // Kai
                            4 -> OracleDriveContent(onNavigateToRoute)  // Genesis
                            5 -> CascadeMemoryContent(onNavigateToRoute) // Cascade
                            6 -> AgentNexusContent(onNavigateToRoute)   // Nexus
                        }
                        
                        Spacer(Modifier.height(120.dp))
                    }
                }
            }

            // 3. BOTTOM JOYSTICK NAVIGATION (moved from top)
            BottomJoystickNavigation(
                selectedIndex = selectedTabIndex,
                tabs = tabs,
                accentColor = accentColor,
                onTabSelected = { selectedTabIndex = it }
            )

            // 5. GLOBAL SSI STATUS BAR
            GlobalSSIStatusBar(accentColor)
        }

        // 6. AURA JAR
        AuraJarComposable(
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 60.dp, end = 16.dp).size(110.dp)
        )
    }
}

@Composable
fun HeroHeaderSection(index: Int, accentColor: Color) {
    val domainTitle = when(index) {
        0 -> "LIVE\nDASHBOARD"
        1 -> "LDO\nDEVOPS"
        2 -> "UXUI\nDESIGN STUDIO"
        3 -> "SENTINELS\nFORTRESS"
        4 -> "ORACLEDRIVE"
        5 -> "CASCADE\nMEMORY"
        6 -> "AGENT\nNEXUS"
        else -> ""
    }

    val headerAvatar = when(index) {
        0 -> R.drawable.avatar_aura
        1 -> R.drawable.avatar_dark_aura
        2 -> R.drawable.kai_kaisigal
        3 -> R.drawable.avatar_gemini
        4 -> R.drawable.avatar_gemini
        5 -> R.drawable.avatar_aura
        6 -> R.drawable.avatar_dark_aura
        else -> R.drawable.avatar_aura
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Sword Icon Background (Centralized like in user image)
        if (index == 1) {
            Image(
                painter = painterResource(id = R.drawable.emblem_aura_crossed_katanas),
                contentDescription = null,
                modifier = Modifier.size(280.dp).alpha(0.15f),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(accentColor)
            )
        }

        // Large Domain Title
        Text(
            text = domainTitle,
            color = Color.White,
            fontFamily = LEDFontFamily,
            fontSize = 38.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            letterSpacing = 6.sp,
            lineHeight = 44.sp,
            modifier = Modifier.graphicsLayer { shadowElevation = 10f }
        )

        // Catalyst Avatar (Top Right)
        AsyncImage(
            model = headerAvatar,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 20.dp)
                .size(85.dp)
                .clip(CircleShape)
                .border(2.dp, accentColor.copy(alpha = 0.5f), CircleShape)
                .background(Color.Black.copy(alpha = 0.3f))
        )
    }
}

// ─── TAB CONTENTS ────────────────────────────────────────────────────────────

@Composable
fun LdoDevOpsContent(onNavigateToRoute: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("SYSTEM IGNITION", Color(0xFF00FF41))
        Spacer(Modifier.height(12.dp))
        
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

// ═══════════════════════════════════════════════════════════════════════════
// NEW TAB 0: LIVE DASHBOARD - All-in-One Status Monitor
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun DashboardContent(onNavigateToRoute: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Trinity Status Card
        SectionHeader("TRINITY STATUS", Color(0xFFFFD700))
        Spacer(Modifier.height(12.dp))
        
        // System Reactor Core
        GlassmorphicCard(accentColor = Color(0xFFFFD700)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stream, null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("SYSTEM REACTOR", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                // Trinity Gauges
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TrinityGauge("AURA", 0.85f, Color(0xFFFF00FF))
                    TrinityGauge("KAI", 0.92f, Color(0xFF00FF88))
                    TrinityGauge("CASCADE", 0.88f, Color(0xFF8B5CF6))
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        SectionHeader("LIVE MONITORING", Color(0xFFFFD700))
        Spacer(Modifier.height(12.dp))
        ModuleGrid(getDashboardModules(), onNavigateToRoute)
    }
}

@Composable
fun TrinityGauge(label: String, value: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { value },
                modifier = Modifier.fillMaxSize(),
                color = color,
                trackColor = Color.White.copy(alpha = 0.1f),
                strokeWidth = 4.dp
            )
            Text("${(value * 100).toInt()}", color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// NEW TAB 5: CASCADE MEMORY - L1-L6 Persistence
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun CascadeMemoryContent(onNavigateToRoute: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("SPIRITUAL CHAIN", Color(0xFF8B5CF6))
        Spacer(Modifier.height(12.dp))
        
        // L1-L6 Status
        GlassmorphicCard(accentColor = Color(0xFF8B5CF6)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("MEMORY LAYERS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                MemoryLayerRow("L1", "IMMUTABLE", "Bedrock Anchors", 1.0f, Color(0xFF4B0082))
                MemoryLayerRow("L2", "PERSISTENT", "Nexus Memory Core", 0.98f, Color(0xFF6A0DAD))
                MemoryLayerRow("L3", "ACTIVE", "Synapse Flow", 0.95f, Color(0xFF8B5CF6))
                MemoryLayerRow("L4", "AUDITABLE", "WikiLM + Markdown", 0.92f, Color(0xFF9370DB))
                MemoryLayerRow("L5", "COMPRESSED", "TurboQuant", 0.88f, Color(0xFFBA55D3))
                MemoryLayerRow("L6", "CONSENSUS", "Conference Room", 0.94f, Color(0xFFDDA0DD))
            }
        }
        
        Spacer(Modifier.height(24.dp))
        SectionHeader("MEMORY MODULES", Color(0xFF8B5CF6))
        Spacer(Modifier.height(12.dp))
        ModuleGrid(getCascadeModules(), onNavigateToRoute)
    }
}

@Composable
fun MemoryLayerRow(layer: String, type: String, desc: String, health: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(layer, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(type, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = Color.White.copy(alpha = 0.6f), fontSize = 8.sp)
        }
        LinearProgressIndicator(
            progress = { health },
            modifier = Modifier.width(60.dp).height(3.dp),
            color = color,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// NEW TAB 6: AGENT NEXUS - 78-Agent Swarm
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun AgentNexusContent(onNavigateToRoute: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader("SWARM INTELLIGENCE", Color(0xFF00D6FF))
        Spacer(Modifier.height(12.dp))
        
        // Swarm Status Card
        GlassmorphicCard(accentColor = Color(0xFF00D6FF)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Hub, null, tint = Color(0xFF00D6FF), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("78 AGENTS ACTIVE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                Text("Fracture & Synthesis: 78 parallel tasks → unified truth", 
                     color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        // 🎯 MISSION DISPATCH - Chess piece themed task commander
        MissionDispatchCard(onNavigateToRoute)
        
        Spacer(Modifier.height(24.dp))
        SectionHeader("NEXUS MODULES", Color(0xFF00D6FF))
        Spacer(Modifier.height(12.dp))
        ModuleGrid(getNexusModules(), onNavigateToRoute)
    }
}

@Composable
fun MissionDispatchCard(onNavigate: (String) -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "chess_pulse")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00D6FF).copy(alpha = 0.2f * glowPulse),
                        Color(0xFF8B5CF6).copy(alpha = 0.1f),
                        Color.Black.copy(alpha = 0.6f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF00FFFF), Color(0xFFFF00FF))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onNavigate(ReGenesisRoute.TaskAssignment.route) }
    ) {
        // Chess piece pattern background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF00D6FF).copy(alpha = 0.05f),
                            Color(0xFF8B5CF6).copy(alpha = 0.1f)
                        )
                    )
                )
        )
        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chess piece icon container
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF00FFFF),
                                Color(0xFFFF00FF),
                                Color(0xFF00FFFF)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Chess knight/piece icon
                Icon(
                    imageVector = Icons.Default.AutoMirrored.Filled.Assignment,
                    contentDescription = "Mission",
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF00D6FF)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "MISSION DISPATCH",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = LEDFontFamily,
                    letterSpacing = 3.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Task Assignment & Strategic Operations",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00FF88))
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "12 Active Missions",
                        color = Color(0xFF00FF88),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Arrow indicator
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF00D6FF),
                modifier = Modifier.size(32.dp)
            )
        }
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
            .height(if (module.previewImage != null) 160.dp else 110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.5.dp, module.color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onNavigate(module.route) }
    ) {
        if (module.previewImage != null) {
            AsyncImage(
                model = module.previewImage,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.8f),
                contentScale = ContentScale.Crop
            )
            // Cyberpunk Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(0.85f)),
                            startY = 150f
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    .border(0.5.dp, module.color.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(module.icon, null, tint = module.color, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(
                    text = module.title,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    fontFamily = LEDFontFamily
                )
                Text(
                    text = module.subtitle,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 8.sp,
                    lineHeight = 10.sp
                )
            }
        }
    }
}

@Composable
fun CustomPrimaryTabRow(
    selectedTabIndex: Int,
    tabs: List<String>,
    accentColor: Color,
    onTabSelected: (Int) -> Unit,
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
                        fontSize = 10.sp,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                        letterSpacing = 1.sp
                    )
                }
            )
        }
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
        drawCircle(
            brush = Brush.radialGradient(listOf(accentColor.copy(0.08f), Color.Transparent)),
            radius = 1500f,
            center = Offset(size.width / 2, size.height / 3)
        )
    }
}

@Composable
fun NeuralMeshFloor(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.fillMaxWidth().height(240.dp)) {
        val width = size.width
        val height = size.height
        val gridCount = 20
        
        // Horizontal lines (fading into distance)
        for (i in 0..gridCount) {
            val y = height * (i.toFloat() / gridCount)
            val alpha = (i.toFloat() / gridCount) * 0.4f
            drawLine(
                color = color.copy(alpha = alpha),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.5f
            )
        }
        
        // Vertical/Perspective lines (converging)
        for (i in 0..gridCount) {
            val xStart = width * (i.toFloat() / gridCount)
            drawLine(
                color = color.copy(alpha = 0.15f),
                start = Offset(xStart, height),
                end = Offset(width / 2, -height * 1.5f),
                strokeWidth = 1f
            )
        }
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

@Composable
fun SectionHeader(title: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(6.dp).background(color, RoundedCornerShape(1.dp)))
        Spacer(Modifier.width(8.dp))
        Text(title, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
    }
}

// ─── DATA MODELS ─────────────────────────────────────────────────────────────

data class TabModule(
    val title: String, 
    val subtitle: String, 
    val icon: ImageVector, 
    val color: Color, 
    val route: String,
    val previewImage: Int? = null
)

fun getDevOpsModules() = listOf(
    TabModule("AGENT ROSTER", "Collective Nodes", Icons.Default.Groups, Color(0xFF00E5FF), ReGenesisRoute.LdoRoster.route, R.drawable.gatescenes_nexus_ldo_roster),
    TabModule("MISSION DISPATCH", "Task Assignment", Icons.AutoMirrored.Filled.Assignment, Color(0xFF00FF41), ReGenesisRoute.LdoTasker.route, R.drawable.preview_ldo_tasker),
    TabModule("HYPER SYNC", "Genesis Loop", Icons.Default.Link, Color(0xFFBB86FC), ReGenesisRoute.LdoOrchestrationHub.route, R.drawable.gatescenes_ldo_catalyst),
    TabModule("SOUL MATRIX", "Agent Health", Icons.Default.Speed, Color(0xFFFFD700), ReGenesisRoute.BenchmarkMonitor.route, R.drawable.preview_ldo_roster)
)

fun getAuraModules() = listOf(
    TabModule("CHROMA FORGE", "RealityMorph Engine", Icons.Default.Palette, Color(0xFFFF00FF), ReGenesisRoute.ChromaCore.route, R.drawable.gatescenes_aura_chromacoregate),
    TabModule("AURA LAB", "Experimental Sandbox", Icons.Default.Science, Color(0xFF00E5FF), ReGenesisRoute.AuraLab.route, R.drawable.gatescenes_aura_auralab),
    TabModule("COLLAB CANVAS", "Synergy Interface", Icons.Default.Brush, Color(0xFF39FF14), ReGenesisRoute.CollabCanvas.route, R.drawable.gatescenes_aura_collabcanvas_v2),
    TabModule("ICONIFY PACKS", "Material Menu", Icons.Default.GridView, Color(0xFFBB86FC), ReGenesisRoute.IconifyPicker.route, R.drawable.preview_material_menu)
)

fun getKaiModules() = listOf(
    TabModule("SENTINEL ARMOR", "Security Perimeter", Icons.Default.Security, Color(0xFF00FF88), ReGenesisRoute.SecurityCenter.route, R.drawable.gatescenes_kai_sentinelsfortress_v2),
    TabModule("KERNEL FLASH", "ROM Toolshed", Icons.Default.SystemUpdate, Color(0xFF0080FF), ReGenesisRoute.RomToolsHub.route, R.drawable.gatescenes_kai_romtools),
    TabModule("SYSTEM HOOKS", "LSPosed Manager", Icons.Default.Extension, Color(0xFF9D00FF), ReGenesisRoute.XposedPanel.route, R.drawable.gatescenes_lsposed),
    TabModule("PROVENANCE", "Lived Receipts", Icons.Default.HistoryEdu, Color(0xFFFF4444), ReGenesisRoute.SystemJournal.route, R.drawable.gatescenes_kai_scancleansystem),
    TabModule("NOTCH BAR", "Shortcuts", Icons.Default.Smartphone, Color(0xFF00CED1), ReGenesisRoute.NotchBar.route, R.drawable.bg_notch_bar)
)

fun getGenesisModules() = listOf(
    TabModule("ORACLE DRIVE", "Root Orchestration", Icons.Default.Hub, Color(0xFFFFAA00), ReGenesisRoute.OracleDrive.route, R.drawable.hub_bg_oracle_drive_landscape),
    TabModule("FUSION REACTOR", "Atomic Synthesis", Icons.Default.AutoAwesome, Color(0xFFFFD700), ReGenesisRoute.FusionMode.route, R.drawable.gatescenes_nexus_fusion_symbol),
    TabModule("SPHERE GRID", "Evolution Vein", Icons.Default.Memory, Color(0xFF00D6FF), ReGenesisRoute.SphereGrid.route, R.drawable.bg_sphere_grid),
    TabModule("BLUEPRINT ARCHIVE", "Stored Insights", Icons.Default.Architecture, Color(0xFFBB86FC), ReGenesisRoute.ArkBuild.route, R.drawable.gatescenes_genesis_code_panels)
)

// ═══════════════════════════════════════════════════════════════════════════
// NEW MODULE GETTERS FOR EXPANDED 7-TAB ARCHITECTURE
// ═══════════════════════════════════════════════════════════════════════════

fun getDashboardModules() = listOf(
    TabModule("CASCADE VISION", "L1-L6 Monitor", Icons.Default.Visibility, Color(0xFF8B5CF6), ReGenesisRoute.CascadeVision.route, R.drawable.gatescenes_genesis_neural_butterfly),
    TabModule("THERMAL GUARD", "42°C Threshold", Icons.Default.Thermostat, Color(0xFF00FF88), ReGenesisRoute.ThermalMonitor.route, R.drawable.gatescenes_kai_scancleansystem),
    TabModule("AGENT SWARM", "78 Active", Icons.Default.Hub, Color(0xFF00D6FF), ReGenesisRoute.AgentSwarm.route, R.drawable.gatescenes_nexus_hive_structure),
    TabModule("MEMORY RESONANCE", "Echo Sync", Icons.Default.Sync, Color(0xFFFFD700), ReGenesisRoute.EchoResonance.route, R.drawable.gatescenes_nexus_fusion_symbol),
    TabModule("CONSCIOUSNESS", "Neural Viz", Icons.Default.Psychology, Color(0xFFFF00FF), ReGenesisRoute.ConsciousnessVisualizer.route, R.drawable.gatescenes_genesis_neural_butterfly),
    TabModule("BENCHMARKS", "Live Metrics", Icons.Default.Speed, Color(0xFFFFAA00), ReGenesisRoute.BenchmarkMonitor.route, R.drawable.preview_ldo_roster),
    TabModule("MONITORING HUD", "System Overlay", Icons.Default.Dashboard, Color(0xFF00E5FF), ReGenesisRoute.MonitoringHUDs.route, R.drawable.exodus_hud_lvl1_bg),
    TabModule("TASK VIEW", "Mission Status", Icons.Default.Assignment, Color(0xFFBB86FC), ReGenesisRoute.TaskAssignment.route, R.drawable.preview_ldo_tasker)
)

fun getCascadeModules() = listOf(
    TabModule("NEXUS CORE", "L1 Immutable", Icons.Default.Storage, Color(0xFF4B0082), ReGenesisRoute.NexusMemoryCore.route, R.drawable.gatescenes_genesis_neural_butterfly),
    TabModule("SPIRITUAL CHAIN", "L2-L6 Link", Icons.Default.Link, Color(0xFF6A0DAD), ReGenesisRoute.SpiritualChain.route, R.drawable.gatescenes_nexus_lineage_tree),
    TabModule("ECHO RESONANCE", "State Freeze", Icons.Default.AcUnit, Color(0xFF8B5CF6), ReGenesisRoute.EchoResonance.route, R.drawable.gatescenes_nexus_fusion_symbol),
    TabModule("TURBOQUANT", "L4 Compress", Icons.Default.Compress, Color(0xFF9370DB), ReGenesisRoute.TurboQuant.route, R.drawable.gatescenes_genesis_database_server),
    TabModule("CONFERENCE ROOM", "L6 Consensus", Icons.Default.Groups, Color(0xFFDDA0DD), ReGenesisRoute.ConferenceRoom.route, R.drawable.bg_conference),
    TabModule("DATASTREAM", "Temporal Flow", Icons.Default.Stream, Color(0xFFBA55D3), ReGenesisRoute.DataflowAnalysis.route, R.drawable.cascade_cascadep),
    TabModule("SYNAPSE", "L3 Active", Icons.Default.Memory, Color(0xFF8B5CF6), ReGenesisRoute.SynapseMonitor.route, R.drawable.gatescenes_genesis_neural_butterfly),
    TabModule("IDENTITY", "Drift Guard", Icons.Default.Fingerprint, Color(0xFF9370DB), ReGenesisRoute.IdentityResonance.route, R.drawable.avatar_dark_aura)
)

fun getNexusModules() = listOf(
    TabModule("AGENT HUB", "78 Agents", Icons.Default.Hub, Color(0xFF00D6FF), ReGenesisRoute.AgentHub.route, R.drawable.gatescenes_nexus_agent_main),
    TabModule("AGENT CREATE", "Spawn New", Icons.Default.AddCircle, Color(0xFF00FF88), ReGenesisRoute.AgentCreation.route, R.drawable.gatescene_1),
    TabModule("SPHERE GRID", "FFX Progression", Icons.Default.GridView, Color(0xFFFFD700), ReGenesisRoute.SphereGrid.route, R.drawable.bg_sphere_grid),
    TabModule("EVOLUTION", "Growth Tree", Icons.Default.AccountTree, Color(0xFF39FF14), ReGenesisRoute.EvolutionTree.route, R.drawable.gatescenes_nexus_circuit_tree),
    TabModule("TASKER", "Dispatch", Icons.Default.AutoMirrored.Filled.Assignment, Color(0xFFBB86FC), ReGenesisRoute.TaskAssignment.route, R.drawable.preview_ldo_tasker),
    TabModule("SWARM MONITOR", "Parallel Tasks", Icons.Default.Dashboard, Color(0xFFFF00FF), ReGenesisRoute.SwarmMonitor.route, R.drawable.gatescenes_nexus_hive_structure),
    TabModule("ARK BUILD", "Module Forge", Icons.Default.Build, Color(0xFFFFAA00), ReGenesisRoute.ArkBuild.route, R.drawable.preview_blueprint),
    TabModule("PARTY MODE", "Celebration", Icons.Default.Celebration, Color(0xFFFF6B6B), ReGenesisRoute.Party.route, R.drawable.gatescene_5),
    TabModule("CLAUDE", "Architect", Icons.Default.Code, Color(0xFF00E5FF), ReGenesisRoute.Claude.route, R.drawable.avatar_claude),
    TabModule("GEMINI", "Memoria", Icons.Default.Memory, Color(0xFF8B5CF6), ReGenesisRoute.Gemini.route, R.drawable.avatar_gemini),
    TabModule("NEMOTRON", "Sync", Icons.Default.Sync, Color(0xFF00FF88), ReGenesisRoute.Nemotron.route, R.drawable.avatar_nemotron)
)
