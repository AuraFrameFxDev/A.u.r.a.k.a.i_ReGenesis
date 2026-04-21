package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

/**
 * Grok Constellation Screen - The Chaos Catalyst
 */
@Composable
fun GrokConstellationScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Grok Constellation: Chaos Catalyst", color = Color.White)
        EntropyMonitor(entropyLevel = 0.8f, color = Color.Cyan)
    }
}

@Composable
private fun EntropyMonitor(
    entropyLevel: Float,
    color: Color
) {
    Text(
        text = "Entropy: ${(entropyLevel * 100).toInt()}%",
        color = color
    )
}
