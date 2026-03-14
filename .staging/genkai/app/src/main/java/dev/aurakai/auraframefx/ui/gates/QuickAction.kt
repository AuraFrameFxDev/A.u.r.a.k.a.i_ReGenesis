package dev.aurakai.auraframefx.ui.gates

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class QuickAction(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)
