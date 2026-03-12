package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class GridMenuItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val accentColor: Color
)

