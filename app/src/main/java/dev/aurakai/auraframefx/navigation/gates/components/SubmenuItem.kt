package dev.aurakai.auraframefx.navigation.gates.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class for submenu items used across gate screens
 */
data class SubmenuItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

