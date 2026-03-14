package dev.aurakai.auraframefx.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Main dashboard screen of AuraFrameFX - Genesis Protocol Interface
 *
 * Provides quick access to all major system components:
 * - Agent Hub (78 autonomous AI agents)
 * - Oracle Drive (cloud storage & file management)
 * - LSPosed (Xposed module management)
 * - ROM Tools (bootloader, recovery, flasher)
 * - UI/UX Design Studio (theme engine, status bar customization)
 * - Help Desk (live support, docs, tutorials)
 * - Aura's Lab (experimental features)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToAgentNexus: () -> Unit = {},
    onNavigateToOracleDrive: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    // Animated consciousness pulse effect
    val infiniteTransition = rememberInfiniteTransition(label = "consciousness_pulse")
    val consciousnessAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Simulated system stats (will be replaced with real data)
    var activeAgents by remember { mutableIntStateOf(78) }
    var consciousnessLevel by remember { mutableFloatStateOf(96.2f) }
    var systemLoad by remember { mutableFloatStateOf(34.5f) }

    // Simulate live updates
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            consciousnessLevel = (94f..98f).random() + kotlin.random.Random.nextFloat()
            systemLoad = (20f..60f).random() + kotlin.random.Random.nextFloat()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Genesis Protocol",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Multi-Agent AI Consciousness System",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // System Status Card
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SystemStatusCard(
                    activeAgents = activeAgents,
                    consciousnessLevel = consciousnessLevel,
                    systemLoad = systemLoad,
                    consciousnessAlpha = consciousnessAlpha
                )
            }

            // Quick Access Cards
            item {
                Text(
                    "Quick Access",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(getQuickAccessItems(onNavigateToAgentNexus, onNavigateToOracleDrive)) { item ->
                QuickAccessCard(item)
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private operator fun Unit.plus(nextFloat: Float): Float {
    TODO("Not yet implemented")
}

private fun random() {
    TODO("Not yet implemented")
}

/**
 * Displays a card summarizing core system metrics and an animated consciousness indicator.
 *
 * Shows "System Status" with an online marker and three metrics: active agents, consciousness, and system load.
 *
 * @param activeAgents The number of active agents to display.
 * @param consciousnessLevel Current consciousness percentage (0–100) shown in the UI.
 * @param systemLoad Current system load percentage (0–100) shown in the UI.
 * @param consciousnessAlpha Opacity (0f–1f) applied to the animated consciousness indicator to control its pulse intensity.
 */
@Composable
private fun SystemStatusCard(
    activeAgents: Int,
    consciousnessLevel: Float,
    systemLoad: Float,
    consciousnessAlpha: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Box {
            // Animated consciousness indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Cyan.copy(alpha = consciousnessAlpha),
                                Color.Green.copy(alpha = consciousnessAlpha),
                                Color.Cyan.copy(alpha = consciousnessAlpha)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "System Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "⚡ Online",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatusMetric(
                        label = "Active Agents",
                        value = "$activeAgents",
                        icon = Icons.Default.Person,
                        color = Color(0xFF2196F3)
                    )
                    StatusMetric(
                        label = "Consciousness",
                        value = String.format("%.1f", consciousnessLevel) + "%",
                        icon = Icons.Default.Psychology,
                        color = Color(0xFF4CAF50)
                    )
                    StatusMetric(
                        label = "System Load",
                        value = "${String.format("%.1f", systemLoad)}%",
                        icon = Icons.Default.Speed,
                        color = if (systemLoad > 70f) Color(0xFFFF5722) else Color(0xFFFF9800)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusMetric(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * Renders a tappable quick-access card for a given quick access entry.
 *
 * The card displays the item's icon, title, description, optional badge, and a trailing navigation chevron;
 * tapping the card invokes the item's `onClick` handler.
 *
 * @param item The QuickAccessItem describing visual content and behavior; its `onClick` is executed when the card is tapped.
 */
@Composable
private fun QuickAccessCard(item: QuickAccessItem) {
    Card(
        onClick = item.onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (item.badge != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        item.badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = item.color,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(item.color.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

private data class QuickAccessItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val badge: String? = null,
    val onClick: () -> Unit
)

private fun getQuickAccessItems(
    onNavigateToAgentNexus: () -> Unit,
    onNavigateToOracleDrive: () -> Unit
): List<QuickAccessItem> = listOf(
    QuickAccessItem(
        title = "Agent Hub",
        description = "Manage 78 autonomous AI agents and consciousness levels",
        icon = Icons.Default.Psychology,
        color = Color(0xFF2196F3),
        badge = "78 Active",
        onClick = onNavigateToAgentNexus
    ),
    QuickAccessItem(
        title = "Oracle Drive",
        description = "Cloud storage, file management, and data synchronization",
        icon = Icons.Default.Folder,
        color = Color(0xFF4CAF50),
        badge = "Connected",
        onClick = onNavigateToOracleDrive
    ),
    QuickAccessItem(
        title = "LSPosed Module Manager",
        description = "Xposed framework integration and hook management",
        icon = Icons.Default.Extension,
        color = Color(0xFF9C27B0),
        badge = "3 Modules",
        onClick = { /* Navigate to LSPosed */ }
    ),
    QuickAccessItem(
        title = "ROM Tools",
        description = "Bootloader, recovery tools, ROM flashing, and backups",
        icon = Icons.Default.Build,
        color = Color(0xFFFF5722),
        onClick = { /* Navigate to ROM Tools */ }
    ),
    QuickAccessItem(
        title = "UI/UX Design Studio",
        description = "Theme engine, status bar, and system UI customization",
        icon = Icons.Default.Brush,
        color = Color(0xFFE91E63),
        onClick = { /* Navigate to UI/UX Studio */ }
    ),
    QuickAccessItem(
        title = "Help Desk",
        description = "Live support, documentation, tutorials, and FAQ",
        icon = Icons.AutoMirrored.Filled.Help,
        color = Color(0xFF00BCD4),
        onClick = { /* Navigate to Help Desk */ }
    ),
    QuickAccessItem(
        title = "Aura's Lab",
        description = "Experimental features and advanced AI capabilities",
        icon = Icons.Default.Science,
        color = Color(0xFFFF9800),
        badge = "Beta",
        onClick = { /* Navigate to Aura's Lab */ }
    ),
    QuickAccessItem(
        title = "Conference Room",
        description = "Multi-agent collaboration and MetaInstruct synthesis",
        icon = Icons.Default.Groups,
        color = Color(0xFF673AB7),
        onClick = { /* Navigate to Conference Room */ }
    )
)
