package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.aurakai.auraframefx.core.theme.CyberpunkTextColor
import dev.aurakai.auraframefx.core.theme.CyberpunkTextStyle

@Composable
fun CyberpunkText(
    text: String,
    color: CyberpunkTextColor,
    style: CyberpunkTextStyle,
    modifier: Modifier = Modifier,
    enableGlitch: Boolean = false,
) {
    Text(
        text = text,
        color = color.color,
        style = style.textStyle,
        modifier = modifier
    )
}
