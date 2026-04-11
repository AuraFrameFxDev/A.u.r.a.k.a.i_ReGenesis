package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonTeal

@Composable
fun AuraSparkleButton(
    text: String = "Sparkle",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) { // Renamed to auraSparkleButton
    // TODO: Implement the actual Aura Sparkle Button with custom animation/effects
    Button(
        onClick = onClick,
        modifier = modifier.shadow(
            elevation = 24.dp,
            shape = RoundedCornerShape(50)
        ),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            color = NeonTeal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuraSparkleButtonPreview() { // Renamed
    AuraSparkleButton(onClick = {})
}

