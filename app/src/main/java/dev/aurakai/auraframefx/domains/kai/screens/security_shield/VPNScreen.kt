package dev.aurakai.auraframefx.domains.kai.screens.security_shield

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.ui.components.GridMenuItem
import dev.aurakai.auraframefx.domains.aura.ui.components.Level3GridMenu

@Composable
fun VPNScreen(onNavigateBack: () -> Unit = {}) {
    val menuItems = listOf(
        GridMenuItem(
            id = "toggle_vpn",
            title = "Connect VPN",
            subtitle = "Enable secure tunnel",
            icon = Icons.Filled.VpnKey,
            route = "action_vpn_toggle",
            accentColor = Color(0xFF00FF85)
        ),
        GridMenuItem(
            id = "vpn_nodes",
            title = "Relay Nodes",
            subtitle = "Select tunneling server",
            icon = Icons.Filled.List,
            route = "action_vpn_nodes",
            accentColor = Color(0xFF00E5FF)
        ),
        GridMenuItem(
            id = "vpn_killswitch",
            title = "Kill Switch",
            subtitle = "Block traffic on drop",
            icon = Icons.Filled.Security,
            route = "action_killswitch",
            accentColor = Color(0xFFFF3B30)
        ),
        GridMenuItem(
            id = "vpn_stats",
            title = "Analytics",
            subtitle = "Traffic overview",
            icon = Icons.Filled.DataUsage,
            route = "action_vpn_stats",
            accentColor = Color.White
        )
    )

    Level3GridMenu(
        title = "VPN SHIELD",
        subtitle = "ENCRYPTED NETWORK TUNNEL",
        menuItems = menuItems,
        onItemClick = { /* TODO */ },
        onBackClick = onNavigateBack,
        backgroundDrawable = R.drawable.bg_vpn_network,
        accentColor = Color(0xFF00FF85)
    )
}
