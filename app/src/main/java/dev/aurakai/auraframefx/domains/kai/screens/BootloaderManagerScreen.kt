package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.ui.components.GridMenuItem
import dev.aurakai.auraframefx.domains.aura.ui.components.Level3GridMenu

@Composable
fun BootloaderManagerScreen(onNavigateBack: () -> Unit = {}) {
    val menuItems = listOf(
        GridMenuItem(
            id = "unlock_bl",
            title = "Unlock Bootloader",
            subtitle = "Flash unlock command",
            icon = Icons.Filled.LockOpen,
            route = "action_unlock",
            accentColor = Color(0xFF00FF85)
        ),
        GridMenuItem(
            id = "lock_bl",
            title = "Lock Bootloader",
            subtitle = "Re-lock device",
            icon = Icons.Filled.Lock,
            route = "action_lock",
            accentColor = Color(0xFFFF3B30)
        ),
        GridMenuItem(
            id = "fastboot_mode",
            title = "Reboot to Fastboot",
            subtitle = "Enter fastboot mode",
            icon = Icons.Filled.Build,
            route = "action_fastboot",
            accentColor = Color(0xFF00E5FF)
        ),
        GridMenuItem(
            id = "oem_status",
            title = "OEM Status",
            subtitle = "Check lock state",
            icon = Icons.Filled.Info,
            route = "action_status",
            accentColor = Color.White
        )
    )

    Level3GridMenu(
        title = "BOOTLOADER",
        subtitle = "SECURE DEVICE CONTROLS",
        menuItems = menuItems,
        onItemClick = { /* TODO: Implement action */ },
        onBackClick = onNavigateBack,
        backgroundDrawable = R.drawable.bg_bootloader_shield,
        accentColor = Color(0xFF00FF85)
    )
}
