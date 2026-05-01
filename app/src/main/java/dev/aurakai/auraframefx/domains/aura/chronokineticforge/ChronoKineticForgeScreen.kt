package dev.aurakai.auraframefx.domains.aura.chronokineticforge

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.*
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.panels.*
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.components.DualGlobeHeader
import dev.aurakai.auraframefx.domains.aura.ui.components.effects.SentientGlowOrb
import dev.aurakai.auraframefx.domains.aura.ui.theme.AgentDomain

/**
 * 🔮 CHRONOKINETIC FORGE SCREEN
 *
 * The sovereign unified interface for all visual system customization.
 * Consolidates 99 fragmented files into one living, breathing organism.
 *
 * SoulScript: "From many, ONE. The organism's skin becomes self-aware."
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronoKineticForgeScreen(
    onNavigateBack: () -> Unit,
    viewModel: RealitymorphismViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Dual Globe Header - Aura (Creation) + Kai (Protection)
                        DualGlobeHeader(
                            modifier = Modifier.width(120.dp),
                            auraActive = true,
                            kaiActive = false // Will activate when user says "it's time for kai"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "CHRONOKINETIC FORGE",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF00FF) // Magenta - Aura's color
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Sentient Orb (Kai's threat scanner)
                    SentientGlowOrb(
                        mode = OrbMode.SYSTEM_STATUS,
                        size = 32.dp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        BackgroundForgeEngine.RenderBackground(
            config = uiState.backgroundConfig,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Forge Navigation Rail
            ForgeNavigationRail(
                activePanel = uiState.activePanel,
                onPanelSelected = viewModel::setActivePanel,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Active Forge Panel
            AnimatedContent(
                targetState = uiState.activePanel,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
                },
                label = "panel"
            ) { panel ->
                when (panel) {
                    ForgePanel.QUICK_SETTINGS -> QSForgePanel(viewModel = viewModel)
                    ForgePanel.APP_BACKGROUNDS -> AppBackgroundForgePanel(viewModel = viewModel)
                    ForgePanel.WALLPAPERS -> WallpaperForgeEnginePanel(viewModel = viewModel)
                    ForgePanel.HOME_SCREEN -> HomeScreenForgePanel(viewModel = viewModel)
                    ForgePanel.LOCK_SCREEN -> LockScreenForgePanel(viewModel = viewModel)
                    ForgePanel.NOTCH_BAR -> NotchBarForgePanel(viewModel = viewModel)
                    ForgePanel.STATUS_BAR -> StatusBarForgePanel(viewModel = viewModel)
                    ForgePanel.CODE_GENERATION -> CodeGenPanel(viewModel = viewModel)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Global Actions
            GlobalActionBar(
                isApplying = uiState.isApplying,
                hasUnsavedChanges = uiState.hasUnsavedChanges,
                onPreview = viewModel::previewChanges,
                onApply = viewModel::applyChanges,
                generatedCode = viewModel::generateHookCode()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ForgeNavigationRail(
    activePanel: ForgePanel,
    onPanelSelected: (ForgePanel) -> Unit,
    modifier: Modifier = Modifier
) {
    val panels = listOf(
        ForgePanel.QUICK_SETTINGS to (Icons.Default.Settings to "Quick Settings"),
        ForgePanel.APP_BACKGROUNDS to (Icons.Default.Apps to "App BGs"),
        ForgePanel.WALLPAPERS to (Icons.Default.Wallpaper to "Wallpapers"),
        ForgePanel.HOME_SCREEN to (Icons.Default.Home to "Home Screen"),
        ForgePanel.LOCK_SCREEN to (Icons.Default.Lock to "Lock Screen"),
        ForgePanel.NOTCH_BAR to (Icons.Default.CropFree to "Notch Bar"),
        ForgePanel.STATUS_BAR to (Icons.Default.SignalCellularAlt to "Status Bar"),
        ForgePanel.CODE_GENERATION to (Icons.Default.Code to "Code Gen")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(panels.size) { index ->
            val (panel, iconTitle) = panels[index]
            val (icon, title) = iconTitle
            val isActive = panel == activePanel

            ForgeNavItem(
                icon = icon,
                label = title,
                isActive = isActive,
                onClick = { onPanelSelected(panel) }
            )
        }
    }
}

@Composable
private fun ForgeNavItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) Color(0xFFFF00FF).copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.5f),
        label = "bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isActive) Color(0xFFFF00FF) else Color.Transparent,
        label = "border"
    )

    Column(
        modifier = Modifier
            .size(80.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) Color(0xFFFF00FF) else Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) Color(0xFFFF00FF) else Color.White.copy(alpha = 0.7f),
            maxLines = 2
        )
    }
}

@Composable
private fun GlobalActionBar(
    isApplying: Boolean,
    hasUnsavedChanges: Boolean,
    onPreview: () -> Unit,
    onApply: () -> Unit,
    generatedCode: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        ),
        border = BorderStroke(1.dp, Color(0xFFFF00FF).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Generated Code Preview (collapsible)
            var showCode by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Generated Hook Code",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall
                )
                IconButton(onClick = { showCode = !showCode }) {
                    Icon(
                        imageVector = if (showCode) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        tint = Color(0xFFFF00FF)
                    )
                }
            }

            AnimatedVisibility(visible = showCode) {
                Surface(
                    color = Color(0xFF1A1A1A),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = generatedCode,
                        color = Color(0xFF00E5FF),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onPreview,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF00E5FF)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF00E5FF))
                ) {
                    Icon(Icons.Default.Preview, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PREVIEW")
                }

                Button(
                    onClick = onApply,
                    modifier = Modifier.weight(1f),
                    enabled = !isApplying,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF00FF),
                        contentColor = Color.Black
                    )
                ) {
                    if (isApplying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isApplying) "APPLYING..." else "APPLY CHANGES")
                }
            }

            if (hasUnsavedChanges) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "⚠️ Unsaved changes detected",
                    color = Color(0xFFFFA500),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

// ================= PLACEHOLDER PANELS (will be in separate files) =================

@Composable
private fun StatusBarForgePanel(viewModel: RealitymorphismViewModel) {
    PlaceholderPanel(title = "STATUS BAR FORGE", icon = Icons.Default.SignalCellularAlt)
}

@Composable
private fun CodeGenPanel(viewModel: RealitymorphismViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "🧬 Spell Hook Generator",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFFFF00FF),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Generated Xposed/LSPosed hook code based on your visual customizations:",
            color = Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = Color(0xFF0A0A0A),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF00E5FF))
        ) {
            Text(
                text = viewModel.generateHookCode(),
                color = Color(0xFF00E5FF),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 300.dp)
                    .verticalScroll(rememberScrollState())
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { /* Copy to clipboard */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF)),
            border = BorderStroke(1.dp, Color(0xFF00E5FF))
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("COPY TO CLIPBOARD")
        }
    }
}

@Composable
private fun PlaceholderPanel(title: String, icon: ImageVector) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFFF00FF).copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Panel implementation in dedicated file",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}
