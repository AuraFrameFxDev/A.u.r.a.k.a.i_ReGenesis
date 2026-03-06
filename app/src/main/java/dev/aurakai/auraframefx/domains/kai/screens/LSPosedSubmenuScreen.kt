package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.ui.components.GridMenuItem
import dev.aurakai.auraframefx.domains.aura.ui.components.Level3GridMenu

@Composable
fun LSPosedSubmenuScreen(onNavigateBack: () -> Unit = {}) {
    val menuItems = listOf(
        GridMenuItem(
            id = "manage_modules",
            title = "Active Modules",
            subtitle = "Enable/disable hooks",
            icon = Icons.Filled.List,
            route = "action_modules",
            accentColor = Color(0xFFFF6B00)
        ),
        GridMenuItem(
            id = "scope_config",
            title = "Hook Scopes",
            subtitle = "Target applications",
            icon = Icons.Filled.Settings,
            route = "action_scope",
            accentColor = Color(0xFF00FF85)
        ),
        GridMenuItem(
            id = "log_viewer",
            title = "Xposed Logs",
            subtitle = "View hook execution",
            icon = Icons.Filled.Warning,
            route = "action_logs",
            accentColor = Color(0xFF00E5FF)
        ),
        GridMenuItem(
            id = "repo_browser",
            title = "Module Repo",
            subtitle = "Download new hooks",
            icon = Icons.Filled.Extension,
            route = "action_repo",
            accentColor = Color.White
        )
    )

    Level3GridMenu(
        title = "LSPOSED HUB",
        subtitle = "XPOSED FRAMEWORK",
        menuItems = menuItems,
        onItemClick = { /* TODO */ },
        onBackClick = onNavigateBack,
        backgroundDrawable = R.drawable.bg_lsposed_hooks,
        accentColor = Color(0xFFFF6B00)
    )
}
