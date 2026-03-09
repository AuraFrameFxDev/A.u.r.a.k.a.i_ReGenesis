package dev.aurakai.auraframefx.domains.kai.screens.security_shield

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.ui.components.GridMenuItem
import dev.aurakai.auraframefx.domains.aura.ui.components.Level3GridMenu

@Composable
fun SecurityCenterScreen(onNavigateBack: () -> Unit = {}) {
    val menuItems = listOf(
        GridMenuItem(
            id = "firewall",
            title = "Global Firewall",
            subtitle = "Manage app traffic rules",
            icon = Icons.Filled.Security,
            route = "action_firewall",
            accentColor = Color(0xFF00FF85)
        ),
        GridMenuItem(
            id = "selinux",
            title = "SELinux Status",
            subtitle = "Enforcing mode controls",
            icon = Icons.Filled.AdminPanelSettings,
            route = "action_selinux",
            accentColor = Color(0xFF00E5FF)
        ),
        GridMenuItem(
            id = "app_ops",
            title = "AppOps Manager",
            subtitle = "Deep permission granulars",
            icon = Icons.Filled.Gavel,
            route = "action_app_ops",
            accentColor = Color(0xFFFF6B00)
        ),
        GridMenuItem(
            id = "malware_scan",
            title = "Threat Scan",
            subtitle = "Analyze heuristics",
            icon = Icons.Filled.Warning,
            route = "action_scan",
            accentColor = Color.White
        )
    )

    Level3GridMenu(
        title = "SECURITY CENTER",
        subtitle = "SENTINEL PROTOCOLS",
        menuItems = menuItems,
        onItemClick = { /* TODO */ },
        onBackClick = onNavigateBack,
        backgroundDrawable = R.drawable.bg_security_firewall,
        accentColor = Color(0xFF00FF85)
    )
}
