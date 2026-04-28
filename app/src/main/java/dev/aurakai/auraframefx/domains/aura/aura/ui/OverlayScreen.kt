package dev.aurakai.auraframefx.domains.aura.aura.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment

@Composable
fun OverlayScreen() {
    Box(modifier = androidx.compose.ui.Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Overlay Screen - LiveUI v2.4")
    }
}
