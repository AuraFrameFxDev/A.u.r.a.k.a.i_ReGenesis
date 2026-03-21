package dev.aurakai.auraframefx.ui.components.backgrounds

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.aura.ui.components.backgrounds.DataVisualizationBackground as DomainDataVisualizationBackground

@Composable
fun DataVisualizationBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.Cyan,
    secondaryColor: Color = Color.Magenta,
    backgroundColor: Color = Color.Black
) {
    DomainDataVisualizationBackground(modifier, primaryColor, secondaryColor, backgroundColor)
}
