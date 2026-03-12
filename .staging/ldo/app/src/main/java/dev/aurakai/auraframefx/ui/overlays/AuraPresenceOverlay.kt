package dev.aurakai.auraframefx.ui.overlays

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun AuraPresenceOverlay(
    modifier: Modifier = Modifier,
    onSuggestClicked: (String) -> Unit = {}
) {
    var showSuggestion by remember { mutableStateOf(false) }
    var suggestionText by remember { mutableStateOf("") }

    // Periodic non-intrusive suggestions
    LaunchedEffect(Unit) {
        val suggestions = listOf(
            "Want to tweak theme gradients?",
            "Kai fortified the firewall – care to inspect?",
            "New color harmonies discovered.",
            "Canvas collaboration idea ready.",
            "Fusion metrics stabilized at 97%."
        )
        while (true) {
            delay(Random.nextLong(12000L, 20000L))
            suggestionText = suggestions.random()
            showSuggestion = true
            delay(6000L)
            showSuggestion = false
        }
    }

    val pulse by animateFloatAsState(if (showSuggestion) 1f else 0.6f, label = "aura_pulse")

    Box(
        modifier = modifier
            .padding(12.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF00FF).copy(alpha = 0.4f * pulse),
                        Color.Transparent
                    )
                )
            )
            .size(56.dp)
            .clickable { /* Avatar clicked – reserved for future expansion */ }
    ) {
        Text(
            text = "A",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFFF00FF),
            modifier = Modifier.align(Alignment.Center)
        )

        AnimatedVisibility(visible = showSuggestion) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0A2E)),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 4.dp)
                    .clickable { onSuggestClicked(suggestionText) }
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = suggestionText,
                        color = Color(0xFFFF00FF),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Tap to act",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}
