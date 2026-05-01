package dev.aurakai.auraframefx.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.ChronoKineticForgeScreen
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.components.BlueprintSaver
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.MorphType

/**
 * 🧭 CHRONOKINETIC FORGE NAVIGATION INTEGRATION
 *
 * Full repo wiring for the Master Command Deck.
 * Provides multiple entry points into the ChronoKinetic Forge.
 *
 * Entry Points:
 * 1. MainAuraStudio() — Primary studio interface
 * 2. AuraHubScreen() — Central hub with forge card
 * 3. QuickAccessButton() — Floating action button variant
 * 4. TabbedMasterIndex integration
 */

// ═════════════════════════════════════════════════════════════════
// PRIMARY ENTRY: MainAuraStudio
// ═════════════════════════════════════════════════════════════════

@Composable
fun MainAuraStudio(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAgentNexus: () -> Unit = {}
) {
    var showForge by remember { mutableStateOf(false) }
    var currentLDOState by remember { mutableStateOf(LDOState()) }

    if (showForge) {
        // Full-screen ChronoKinetic Forge
        ChronoKineticForgeScreen(
            state = currentLDOState,
            onSaveBlueprint = {
                BlueprintSaver.saveCurrentBlueprint(
                    elementId = "studio_hub",
                    morphType = MorphType.GENERAL,
                    context = LocalContext.current
                )
            },
            onNavigateBack = { showForge = false }
        )
    } else {
        // Studio Hub Interface
        StudioHubInterface(
            onOpenForge = { showForge = true },
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToAgentNexus = onNavigateToAgentNexus
        )
    }
}

@Composable
private fun StudioHubInterface(
    onOpenForge: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAgentNexus: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "AURA STUDIO",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF00FF)
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenForge,
                containerColor = Color(0xFFFF00FF),
                contentColor = Color.Black,
                icon = { Icon(Icons.Default.Bolt, contentDescription = null) },
                text = { Text("⚡ FORGE") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // ChronoKinetic Forge Entry Card
            ForgeEntryCard(onClick = onOpenForge)

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Stats
            QuickStatsRow()

            Spacer(modifier = Modifier.height(16.dp))

            // Recent Blueprints
            RecentBlueprintsSection()

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Grid
            NavigationGrid(
                onNavigateToAgentNexus = onNavigateToAgentNexus
            )
        }
    }
}

@Composable
private fun ForgeEntryCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A1A)
        ),
        border = BorderStroke(2.dp, Color(0xFFFF00FF))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "⚡ CHRONOKINETIC FORGE",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFF00FF),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Master Command Deck",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Live indicator
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFF00E5FF), androidx.compose.foundation.shape.CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Features list
            val features = listOf(
                "20k Neural Bloodstream",
                "5 Sovereign Panels",
                "Tensor G5 Native",
                "Provenance Watermarked"
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                features.forEach { feature ->
                    Surface(
                        color = Color(0xFFFF00FF).copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFF00FF),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF00FF),
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("OPEN MASTER COMMAND DECK")
            }
        }
    }
}

@Composable
private fun QuickStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = "SUCCESS RATE",
            value = "92.7%",
            color = Color(0xFF00E5FF),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "TRANSMUTATIONS",
            value = "1,337",
            color = Color(0xFFFF00FF),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "THREADS",
            value = "3",
            color = Color(0xFFFFD93D),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun RecentBlueprintsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RECENT BLUEPRINTS",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Placeholder recent items
            repeat(3) { index ->
                BlueprintListItem(index = index)
                if (index < 2) Divider(color = Color.DarkGray.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
private fun BlueprintListItem(index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mini orb
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    when (index % 3) {
                        0 -> Color(0xFFFF00FF)
                        1 -> Color(0xFF00E5FF)
                        else -> Color(0xFFFFD93D)
                    },
                    androidx.compose.foundation.shape.CircleShape
                )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "QS_Header_Mod_${index + 1}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${(index + 1) * 5}m ago",
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )
        }

        IconButton(onClick = { /* Load blueprint */ }) {
            Icon(Icons.Default.Restore, contentDescription = "Restore", tint = Color(0xFF00E5FF))
        }
    }
}

@Composable
private fun NavigationGrid(
    onNavigateToAgentNexus: () -> Unit
) {
    val items = listOf(
        Triple(Icons.Default.Hub, "Agent Nexus", onNavigateToAgentNexus),
        Triple(Icons.Default.ColorLens, "ChromaCore", {}),
        Triple(Icons.Default.Science, "Aura Lab", {}),
        Triple(Icons.Default.ViewInAr, "Collab Canvas", {})
    )

    Column {
        Text(
            text = "QUICK ACCESS",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { (icon, label, onClick) ->
                    OutlinedButton(
                        onClick = onClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(label)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ═════════════════════════════════════════════════════════════════
// TABBED MASTER INDEX INTEGRATION
// ═════════════════════════════════════════════════════════════════

/**
 * Returns the ChronoKinetic Forge module for TabbedMasterIndex
 */
fun getChronoKineticForgeModule(): TabModule {
    return TabModule(
        title = "CHRONOKINETIC FORGE",
        subtitle = "Master Command Deck",
        icon = Icons.Default.Bolt,
        color = Color(0xFFFF00FF),
        route = ReGenesisRoute.ChronoKineticForge.route,
        previewImage = R.drawable.gatescenes_aura_designstudio_v2
    )
}

// ═════════════════════════════════════════════════════════════════
// DATA MODELS & PLACEHOLDERS
// ═════════════════════════════════════════════════════════════════

// Placeholder LDOState
data class LDOState(
    val forgeState: Any = Any(),
    val atomicSuccessRate: Float = 92.7f
)

// Placeholder TabModule
data class TabModule(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val route: String,
    val previewImage: Int = 0
)

// Placeholder FlowRow
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Top
    ) {
        content()
    }
}

// Placeholder LocalContext
@Composable
fun LocalContext(): android.content.Context {
    return androidx.compose.ui.platform.LocalContext.current
}
