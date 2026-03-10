package dev.aurakai.auraframefx.screens

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom navigation model used by the app's NavigationBar.
 */

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
