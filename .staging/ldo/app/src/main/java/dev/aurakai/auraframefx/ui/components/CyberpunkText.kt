package dev.aurakai.auraframefx.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aurakai.auraframefx.ui.theme.CyberpunkTextColor
import dev.aurakai.auraframefx.ui.theme.CyberpunkTextStyle

@Composable
fun CyberpunkText(
    text: String,
    color: CyberpunkTextColor,
    style: CyberpunkTextStyle,
    modifier: Modifier = Modifier,
    enableGlitch: Boolean = false, // Parameter based on usage, actual glitch effect not implemented in stub
) {
    // TODO: Implement actual glitch effect if enableGlitch is true
    // For now, it just applies color and style

    Text(
        text = text,
        color = color.color, // Access the actual Color from the sealed class
        style = style.textStyle, // Access the actual TextStyle from the sealed class
        modifier = modifier
    )
}
