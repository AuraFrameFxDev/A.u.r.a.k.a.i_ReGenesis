package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.ui.components.GridMenuItem
import dev.aurakai.auraframefx.domains.aura.ui.components.Level3GridMenu

@Composable
fun ROMFlasherScreen(onNavigateBack: () -> Unit = {}) {
    val menuItems = listOf(
        GridMenuItem(
            id = "flash_zip",
            title = "Flash ZIP/IMG",
            subtitle = "Install update package",
            icon = Icons.Filled.Download,
            route = "action_flash",
            accentColor = Color(0xFF00FF85)
        ),
        GridMenuItem(
            id = "wipe_data",
            title = "Wipe Partitions",
            subtitle = "Format data/cache",
            icon = Icons.Filled.Warning,
            route = "action_wipe",
            accentColor = Color(0xFFFF3B30)
        ),
        GridMenuItem(
            id = "backup_rom",
            title = "Nandroid Backup",
            subtitle = "Create system image",
            icon = Icons.Filled.SettingsBackupRestore,
            route = "action_backup",
            accentColor = Color(0xFF00E5FF)
        ),
        GridMenuItem(
            id = "mount_sys",
            title = "Mount Storage",
            subtitle = "R/W partition access",
            icon = Icons.Filled.SdStorage,
            route = "action_mount",
            accentColor = Color.White
        )
    )

    Level3GridMenu(
        title = "ROM FLASHER",
        subtitle = "FIRMWARE MANAGEMENT",
        menuItems = menuItems,
        onItemClick = { /* TODO */ },
        onBackClick = onNavigateBack,
        backgroundDrawable = R.drawable.bg_rom_flasher,
        accentColor = Color(0xFF00FF85)
    )
}
