package dev.aurakai.auraframefx.domains.aura.ui.components.common

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * 🔘 AURA BUTTON
 *
 * A standardized button component for the Aura design system.
 */
@Composable
fun AuraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AuraButtonStyle = AuraButtonStyle.Primary,
    enabled: Boolean = true
) {
    val containerColor = when (style) {
        AuraButtonStyle.Primary -> MaterialTheme.colorScheme.primary
        AuraButtonStyle.Secondary -> MaterialTheme.colorScheme.secondary
        AuraButtonStyle.Outline -> Color.Transparent
    }
    
    val contentColor = when (style) {
        AuraButtonStyle.Outline -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onPrimary
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(text = text)
    }
}

enum class AuraButtonStyle {
    Primary,
    Secondary,
    Outline
}
