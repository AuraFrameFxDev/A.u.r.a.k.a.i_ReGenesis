package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Quick Actions Screen
 * Common operations and shortcuts for LSPosed
 */
/**
 * Renders the Quick Actions screen showing categorized action cards, recent actions, execution status, and system status.
 *
 * Triggering an action marks it as executing, adds its title to the recent-actions list (keeps up to three most recent),
 * and simulates execution for two seconds during which the corresponding card shows a running indicator and other actions
 * are non-clickable.
 */
/**
 * Renders the Quick Actions screen: a header, execution status, recent actions, categorized action grid, and system status.
 *
 * The UI displays a set of predefined quick actions grouped by their category. Tapping an action marks it as executing,
 * adds its title to the recent actions list (keeps up to three most recent unique entries), disables interaction for the
 * currently executing action, and simulates execution by clearing the executing state after a two-second delay.
 */
/**
 * Renders the Quick Actions screen UI, presenting categorized action tiles, recent actions, execution status, and system status.
 *
 * The composable manages its own UI state: a list of up to three recent action titles, the currently executing action, and a flag that disables interactions while an action is executing. Tapping an action marks it as executing, adds its title to the recent-actions list (unique, most-recent-first, max 3), and simulates completion after a short delay.
 */
@Composable
fun QuickActionsScreen(onNavigateBack: () -> Boolean) {
    val actions = listOf(
        ScreenQuickAction(
            "Reboot System",
            "Soft reboot device",
            Icons.Default.RestartAlt,
            Color(0xFFFF6B35),
            "Reboot"
        ),
        ScreenQuickAction(
            "Clear Module Cache",
            "Clear cached module data",
            Icons.Default.CleaningServices,
            Color(0xFF4ECDC4),
            "Cache"
        ),
        ScreenQuickAction(
            "Force Stop SystemUI",
            "Restart system interface",
            Icons.Default.Stop,
            Color(0xFFDC143C),
            "System"
        ),
        ScreenQuickAction(
            "Backup Modules",
            "Save current module configuration",
            Icons.Default.Backup,
            Color(0xFFFFD93D),
            "Backup"
        ),
        ScreenQuickAction(
            "Restore Modules",
            "Load saved module configuration",
            Icons.Default.Restore,
            Color(0xFF9370DB),
            "Restore"
        ),
        ScreenQuickAction(
            "Check Updates",
            "Check for module updates",
            Icons.Default.Update,
            Color(0xFF32CD32),
            "Update"
        ),
        ScreenQuickAction(
            "Safe Mode",
            "Boot with all modules disabled",
            Icons.Default.Security,
            Color(0xFFFF69B4),
            "Safe"
        ),
        ScreenQuickAction(
            "Performance Mode",
            "Optimize for performance",
            Icons.Default.Speed,
            Color(0xFF00CED1),
            "Performance"
        ),
        ScreenQuickAction(
            "Battery Mode",
            "Optimize for battery life",
            Icons.Default.BatteryFull,
            Color(0xFF98FB98),
            "Battery"
        ),
        ScreenQuickAction(
            "Developer Options",
            "Advanced debugging tools",
            Icons.Default.DeveloperMode,
            Color(0xFFFF6347),
            "Developer"
        ),
        ScreenQuickAction(
            "Logcat Capture",
            "Start system log capture",
            Icons.Default.BugReport,
            Color(0xFFDEB887),
            "Logs"
        ),
        ScreenQuickAction(
            "Module Tester",
            "Test module compatibility",
            Icons.Default.Science,
            Color(0xFF40E0D0),
            "Test"
        )
    )

    val recentActions = remember { mutableStateListOf<String>() }
    val isExecuting = remember { mutableStateOf(false) }
    val currentAction = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Text(
            text = "⚡ QUICK ACTIONS",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF6C5CE7),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Common operations and shortcuts",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF8E7CC3).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Execution Status
        if (isExecuting.value && currentAction.value != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                ),
                border = BorderStroke(1.dp, Color(0xFF6C5CE7))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF6C5CE7),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Executing: ${currentAction.value}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF6C5CE7)
                        )
                        Text(
                            text = "Please wait...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Recent Actions
        if (recentActions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recent Actions",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        recentActions.take(3).forEach { action ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF6C5CE7).copy(alpha = 0.2f)
                                )
                            ) {
                                Text(
                                    text = action,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF6C5CE7),
                                    modifier = Modifier.padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Action Categories
        val categories = actions.groupBy { it.category }

        categories.forEach { (categoryName, categoryActions) ->
            Text(
                text = categoryName.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp),
                fontWeight = FontWeight.Bold
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categoryActions) { action ->
                    ActionCard(
                        action = action,
                        isExecuting = isExecuting.value && currentAction.value == action.title,
                        onExecute = {
                            isExecuting.value = true
                            currentAction.value = action.title

                            // Add to recent actions
                            if (!recentActions.contains(action.title)) {
                                recentActions.add(0, action.title)
                                if (recentActions.size > 3) {
                                    recentActions.removeAt(3)
                                }
                            }

                            // Simulate execution
                            coroutineScope.launch {
                                delay(2000)
                                isExecuting.value = false
                                currentAction.value = null
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // System Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "System Status",
                    tint = Color(0xFF6C5CE7),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "System Ready",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "All quick actions are available and system is stable.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF32CD32).copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Ready",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF32CD32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}


/**
 * Action card component
 */
@Composable
private fun ActionCard(
    action: ScreenQuickAction,
    isExecuting: Boolean,
    onExecute: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = !isExecuting, onClick = onExecute),
        colors = CardDefaults.cardColors(
            containerColor = if (isExecuting)
                action.color.copy(alpha = 0.3f)
            else
                Color.Black.copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            1.dp,
            if (isExecuting) action.color else action.color.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            if (isExecuting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = action.color,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    tint = action.color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = action.title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}

/**
 * Data class for quick actions
 */
data class ScreenQuickAction(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val category: String
)
