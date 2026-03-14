package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.navigation.GenesisRoutes
import dev.aurakai.auraframefx.ui.components.SubmenuScaffold

/**
 * UI/UX Design Gate Submenu (ChromaCore)
 * Central hub for all visual customization features
 */
@Composable
fun UIUXGateSubmenuScreen(
    navController: NavController
) {
    val menuItems = listOf(
        SubmenuItem(
            title = "Theme Engine",
            description = "Customize system colors, fonts, and styles",
            icon = Icons.Default.Palette,
            route = GenesisRoutes.THEME_ENGINE,
            color = Color(0xFFFF00FF) // Magenta
        ),
        SubmenuItem(
            title = "Notch Bar",
            description = "Adjust notch height, style, and visibility",
            icon = Icons.Default.Smartphone,
            route = GenesisRoutes.NOTCH_BAR,
            color = Color(0xFF00FFFF) // Cyan
        ),
        SubmenuItem(
            title = "Status Bar",
            description = "Configure icons, clock, and battery styles",
            icon = Icons.Default.Wifi,
            route = GenesisRoutes.STATUS_BAR,
            color = Color(0xFF00FF00) // Green
        ),
        SubmenuItem(
            title = "Quick Settings",
            description = "Modify quick settings tiles and layout",
            icon = Icons.Default.SettingsInputComponent,
            route = GenesisRoutes.QUICK_SETTINGS,
            color = Color(0xFFFFD700) // Gold
        ),
        SubmenuItem(
            title = "Overlay Menus",
            description = "Manage floating bubbles and sidebars",
            icon = Icons.Default.Layers,
            route = GenesisRoutes.OVERLAY_MENUS,
            color = Color(0xFFFF4500) // Orange Red
        ),
        SubmenuItem(
            title = "3D Customization Lab",
            description = "Gyroscope-controlled 3D component editor",
            icon = Icons.Default.ViewInAr,
            route = GenesisRoutes.GYROSCOPE_CUSTOMIZATION,
            color = Color(0xFF00B4D8) // Futuristic Blue
        )
    )

    SubmenuScaffold(
        title = "UI/UX Design Gate",
        subtitle = "ChromaCore System",
        color = Color(0xFF00FFFF), // Cyan theme
        onNavigateBack = { navController.popBackStack() },
        menuItems = menuItems,
        onItemClick = { item ->
            navController.navigate(item.route)
        }
    )
}

