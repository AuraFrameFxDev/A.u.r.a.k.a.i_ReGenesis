package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * XposedQuickAccessPanel — Quick settings panel for Xposed modules
 */
@Composable
fun XposedQuickAccessPanel(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Xposed Quick Access — Coming Soon",
            color = Color.White
        )
    }
}
