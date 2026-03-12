package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.ui.graphics.Color

data class SubGateCard(
    val id: String,
    val title: String,
    val subtitle: String,
    val styleADrawable: String,      // Style A image name
    val styleBDrawable: String,      // Style B image name
    val fallbackDrawable: String?,   // Legacy fallback
    val route: String,
    val accentColor: Color
)
