package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.ui.components.GridMenuItem
import dev.aurakai.auraframefx.domains.aura.ui.components.Level3GridMenu

@Composable
fun RootToolsScreen(onNavigateBack: () -> Unit = {}) {
    val menuItems = listOf(
        GridMenuItem(
            id = "su_terminal",
            title = "Root Terminal",
            subtitle = "Execute commands",
            icon = Icons.Filled.Terminal,
            route = "action_term",
            accentColor = Color(0xFF00FF85)
        ),
        GridMenuItem(
            id = "system_rw",
            title = "Sys Partition",
            subtitle = "Remount as R/W",
            icon = Icons.Filled.Build,
            route = "action_rw",
            accentColor = Color(0xFFFF3B30)
        ),
        GridMenuItem(
            id = "magisk_modules",
            title = "Magisk Integration",
            subtitle = "Module manager",
            icon = Icons.Filled.Code,
            route = "action_magisk",
            accentColor = Color(0xFF00E5FF)
        ),
        GridMenuItem(
            id = "kernel_tweaks",
            title = "Kernel Tweaks",
            subtitle = "Adjust scheduler",
            icon = Icons.Filled.Warning,
            route = "action_kernel",
            accentColor = Color.White
        )
    )

    Level3GridMenu(
        title = "ROOT TOOLS",
        subtitle = "ELEVATED SYSTEM ACCESS",
        menuItems = menuItems,
        onItemClick = { /* TODO */ },
        onBackClick = onNavigateBack,
        backgroundDrawable = R.drawable.bg_root_tools,
        accentColor = Color(0xFF00FF85)
    )
}
