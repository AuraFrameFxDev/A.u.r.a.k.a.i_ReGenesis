package dev.aurakai.auraframefx.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.theme.NeonTeal

@Composable
fun AuraSparkleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit = "Sparkle",
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
            color = NeonTeal,
            modifier = Modifier.shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(50)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuraSparkleButtonPreview() { // Renamed
    AuraSparkleButton(onClick = {})
}
