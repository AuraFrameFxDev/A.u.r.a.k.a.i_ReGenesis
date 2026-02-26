package dev.aurakai.auraframefx.ui.gates

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * LSPosed Gate Screen - Xposed Module Manager & Quick Access
 *
 * Provides comprehensive Xposed/LSPosed integration including:
 * - Module status monitoring
 * - Hook management and logs
 * - Quick actions for common tasks
 * - Module configuration
 */
/**
 * Displays the LSPosed control screen with framework status, quick actions, and a list of active modules.
 *
 * The UI shows a top app bar with back navigation, a status card reflecting framework activity and stats,
 * a set of quick action cards, and expandable module cards. The screen simulates dynamic updates to the
 * total hook count while visible.
 *
 * @param onNavigateBack Callback invoked when the back navigation icon is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
/**
 * Displays the LSPosed control screen containing framework status, quick actions, and active modules.
 *
 * The UI shows a top app bar with back navigation, a status card for framework state and statistics,
 * a list of quick action cards, and a list of expandable module cards. The displayed total hook
 * count is periodically updated to simulate dynamic changes.
 *
 * @param onNavigateBack Called when the top app bar back button is pressed. Defaults to a no-op.
 */
@Composable
fun LSPosedGateScreen(
    onNavigateBack: () -> Unit = {}
) {
    var isLSPosedActive by remember { mutableStateOf(true) } // Simulate LSPosed detection
    var activeModules by remember { mutableIntStateOf(3) }
    var totalHooks by remember { mutableIntStateOf(147) }

    // Simulate dynamic updates
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            totalHooks += (kotlin.random.Random.nextInt(-5, 10))
            totalHooks = totalHooks.coerceIn(100, 300)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("LSPosed Control", fontWeight = FontWeight.Bold)
                        Text(
                            "Xposed Framework Manager",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A0E27)
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0E27))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LSPosed Status Card
            item {
                StatusCard(
                    title = "Framework Status",
                    isActive = isLSPosedActive,
                    activeText = "LSPosed Active",
                    inactiveText = "LSPosed Inactive",
                    stats = listOf(
                        Stat("Active Modules", "$activeModules"),
                        Stat("Total Hooks", "$totalHooks"),
                        Stat("Framework", "v1.9.2")
                    )
                )
            }

            // Quick Actions
            item {
                Text(
                    "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            items(getQuickActions()) { action ->
                QuickActionCard(action)
            }

            // Module Management
            item {
                Text(
                    "Active Modules",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(getActiveModules()) { module ->
                ModuleCard(module)
            }
        }
    }
}

/**
 * Displays a colored status card that presents a title, an icon and text reflecting active/inactive state, and a row of statistical items.
 *
 * @param title The heading displayed at the top of the card.
 * @param isActive Whether the status is active; controls the displayed icon and which descriptive text is shown.
 * @param activeText Text to show when `isActive` is true.
 * @param inactiveText Text to show when `isActive` is false.
 * @param stats A list of `Stat` items to render as value/label pairs in the card's stats row.
 */
@Composable
private fun StatusCard(
    title: String,
    isActive: Boolean,
    activeText: String,
    inactiveText: String,
    stats: List<Stat>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFF1B5E20) else Color(0xFF8B0000)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Icon(
                    if (isActive) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                if (isActive) activeText else inactiveText,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                stats.forEach { stat ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stat.value,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            stat.label,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Display a quick-action card showing an icon, title, description, and trailing chevron.
 *
 * The card is populated from the provided QuickAction and styled for high-contrast readability.
 *
 * @param action QuickAction data used to populate the card (title, description, icon, and color).
 */
@Composable
private fun QuickActionCard(action: GateQuickAction) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = action.color
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    action.icon,
                    contentDescription = null,
                    tint = Color.White
                )
                Column {
                    Text(
                        action.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        action.description,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

/**
 * Renders a collapsible card representing an Xposed module.
 *
 * The collapsed card shows the module name, package name, and an ACTIVE/DISABLED badge. When expanded it also shows Version, Hooks, Scope, and action buttons to toggle enable state and view logs.
 *
 * @param module Module metadata to display (name, packageName, version, enabled, hookCount, scope).
 */
@Composable
private fun ModuleCard(module: GateXposedModule) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1F3A)
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = { !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        module.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        module.packageName,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Badge(
                    containerColor = if (module.enabled) Color.Green else Color.Gray
                ) {
                    Text(
                        if (module.enabled) "ACTIVE" else "DISABLED",
                        fontSize = 10.sp
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))

                ModuleDetailRow("Version", value = module.version)
                ModuleDetailRow("Hooks", value = module.hookCount.toString())
                ModuleDetailRow("Scope", module.scope)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* Toggle module */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (module.enabled) Color(0xFF8B0000) else Color(0xFF1B5E20)
                        )
                    ) {
                        Text(if (module.enabled) "Disable" else "Enable")
                    }

                    OutlinedButton(
                        onClick = { /* View logs */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Logs")
                    }
                }
            }
        }
    }
}


/**
 * Displays a single horizontal detail row with a left-aligned label and a right-aligned value.
 *
 * The label is rendered in small gray text and the value in small white text with medium weight.
 *
 * @param label The descriptive label shown on the left.
 * @param value The corresponding value shown on the right.
 */
@Composable
private fun ModuleDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            value,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

// Data Classes
private data class Stat(val label: String, val value: String)

private data class GateQuickAction(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val category: String = ""
)

private data class GateXposedModule(
    val name: String,
    val packageName: String,
    val version: String,
    val enabled: Boolean,
    val hookCount: Int,
    val scope: String
)

/**
 * Provides a list of sample quick actions used by the UI.
 *
 * @return A list of `QuickAction` instances representing example actions (title, description, icon, and color) shown in the Quick Actions section.
 */
private fun getQuickActions() = listOf(
    GateQuickAction(
        "View Hook Logs",
        "Real-time monitoring of all active hooks",
        Icons.Default.Description,
        Color(0xFF1976D2),
        "Battery"
    ),
    GateQuickAction(
        "Manage Scope",
        "Configure which apps are hooked",
        Icons.Default.Apps,
        Color(0xFF7B1FA2),
        "Battery"
    ),
    GateQuickAction(
        "Reboot System",
        "Soft reboot to apply changes",
        Icons.Default.Refresh,
        Color(0xFFD32F2F),
        "Battery"
    ),
    GateQuickAction(
        "Module Repository",
        "Browse and install Xposed modules",
        Icons.Default.Store,
        Color(0xFF388E3C),
        "Battery"
    )
)

/**
 * Provides a list of sample active Xposed/LSPosed modules used by the UI.
 *
 * @return A list of `XposedModule` instances representing mock module metadata (name, package, version, enabled state, hook count, and scope).
 */
private fun getActiveModules() = listOf(
    GateXposedModule(
        name = "Genesis Protocol",
        packageName = "dev.aurakai.auraframefx",
        version = "0.1.0",
        enabled = true,
        hookCount = 147,
        scope = "SystemUI, Settings, Launcher"
    ),
    GateXposedModule(
        name = "GravityBox",
        packageName = "com.ceco.gravitybox",
        version = "13.0.0",
        enabled = true,
        hookCount = 89,
        scope = "System Framework"
    ),
    GateXposedModule(
        name = "MinMinGuard",
        packageName = "tw.fatminmin.xposed.minminguard",
        version = "7.0.1",
        enabled = false,
        hookCount = 0,
        scope = "All Apps"
    )
)
