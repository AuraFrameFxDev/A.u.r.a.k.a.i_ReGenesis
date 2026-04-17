package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color.NeonBlue // Added import
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color.NeonPink // Added import

@Composable
fun StaticOrb(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    text: String = "Orb",
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(NeonBlue.copy(alpha = 0.8f))
            .shadow(
                elevation = 18.dp,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = NeonPink,
            modifier = Modifier.shadow(
                elevation = 8.dp,
                shape = CircleShape
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StaticOrbPreview() {
    StaticOrb(color = Color.Magenta, text = "Static")
}


