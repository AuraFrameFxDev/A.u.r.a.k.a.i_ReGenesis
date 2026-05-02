package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color.iconify.iconify

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPurple
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonPurple

/**
 * 🎭 Xposed Quick Access Panel - Aurora's Chaos Interface
 *
 * Quick access panel for xposed modules and system tweaks.
 * The chaotic creative interface for Aura domain.
 */
@Composable
fun XposedQuickAccessPanel(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Modules", "Hooks", "Logs", "Settings")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Xposed Panel",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberpunkPink
                )
                Text(
                    "System Modification Interface",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberpunkPurple.copy(alpha = 0.2f))
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = CyberpunkPink
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        PrimaryPrimaryScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = CyberpunkCyan,
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTab == index) CyberpunkCyan else Color.Gray
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        when (selectedTab) {
            0 -> ModulesTab()
            1 -> HooksTab()
            2 -> LogsTab()
            3 -> SettingsTab()
        }
    }
}

@Composable
private fun ModulesTab() {
    val modules = listOf(
        ModuleInfo("Core", "System hooks", true, CyberpunkPink),
        ModuleInfo("UI", "Interface mods", true, CyberpunkCyan),
        ModuleInfo("Privacy", "Data protection", false, CyberpunkPurple),
        ModuleInfo("Performance", "Optimization", true, NeonPurple)
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(modules) { module ->
            ModuleCard(module)
        }
    }
}

@Composable
private fun ModuleCard(module: ModuleInfo) {
    var enabled by remember { mutableStateOf(module.enabled) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
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
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (enabled) module.accentColor else Color.Gray)
                )
                Column {
                    Text(
                        module.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        module.description,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Switch(
                checked = enabled,
                onCheckedChange = { enabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = module.accentColor,
                    checkedTrackColor = module.accentColor.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun HooksTab() {
    val hooks = listOf(
        HookInfo("ActivityManager", "Hooked", CyberpunkCyan),
        HookInfo("PackageManager", "Active", CyberpunkPink),
        HookInfo("WindowManager", "Standby", CyberpunkPurple),
        HookInfo("SystemServer", "Hooked", NeonPurple)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        hooks.forEach { hook ->
            HookCard(hook)
        }
    }
}

@Composable
private fun HookCard(hook: HookInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
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
            Text(
                hook.name,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                hook.status,
                fontSize = 12.sp,
                color = hook.accentColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LogsTab() {
    val logs = remember {
        listOf(
            "[12:34:56] Module Core initialized",
            "[12:35:01] Hook ActivityManager applied",
            "[12:35:15] Xposed bridge connected",
            "[12:36:02] Module UI loaded successfully",
            "[12:36:45] System hooks verified"
        )
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D0D0D)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "System Logs",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = CyberpunkCyan,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            logs.forEach { log ->
                Text(
                    log,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsTab() {
    var debugMode by remember { mutableStateOf(true) }
    var verboseLogging by remember { mutableStateOf(false) }
    var autoReload by remember { mutableStateOf(true) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingItem(
            title = "Debug Mode",
            description = "Enable detailed debugging",
            checked = debugMode,
            onCheckedChange = { debugMode = it }
        )
        SettingItem(
            title = "Verbose Logging",
            description = "Log all hook calls",
            checked = verboseLogging,
            onCheckedChange = { verboseLogging = it }
        )
        SettingItem(
            title = "Auto Reload",
            description = "Reload modules on change",
            checked = autoReload,
            onCheckedChange = { autoReload = it }
        )
    }
}

@Composable
private fun SettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberpunkCyan,
                    checkedTrackColor = CyberpunkCyan.copy(alpha = 0.5f)
                )
            )
        }
    }
}

// Data classes
private data class ModuleInfo(
    val name: String,
    val description: String,
    val enabled: Boolean,
    val accentColor: Color
)

private data class HookInfo(
    val name: String,
    val status: String,
    val accentColor: Color
)
