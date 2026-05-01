package dev.aurakai.auraframefx.domains.aura.chronokineticforge.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/**
 * 🔧 SHARED CHRONOKINETIC FORGE COMPONENTS
 *
 * Reusable UI components for all forge panels.
 */

@Composable
fun ForgeSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Text("${value.toInt()}dp", color = Color(0xFFFF00FF), fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFF00FF),
                activeTrackColor = Color(0xFFFF00FF),
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
fun ForgeSectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        title,
        style = MaterialTheme.typography.titleSmall,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun ForgeDescription(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.7f),
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
    )
}
