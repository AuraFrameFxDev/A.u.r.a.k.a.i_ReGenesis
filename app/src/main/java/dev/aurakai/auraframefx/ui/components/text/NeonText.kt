package dev.aurakai.auraframefx.ui.components.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import dev.aurakai.auraframefx.domains.aura.ui.components.text.NeonText as DomainNeonText

@Composable
fun NeonText(
    text: String,
    fontSize: TextUnit,
    color: Color,
    glowColor: Color,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    animateTyping: Boolean = false
) {
    DomainNeonText(text, fontSize, color, glowColor, modifier, fontWeight, animateTyping)
}
