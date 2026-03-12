package dev.aurakai.auraframefx.domains.aura.ui.theme.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import dev.aurakai.auraframefx.domains.genesis.network.model.Theme
import dev.aurakai.auraframefx.domains.genesis.network.model.ThemeColors
import dev.aurakai.auraframefx.domains.aura.ui.theme.AuraFrameFXTheme
import dev.aurakai.auraframefx.domains.aura.ui.theme.picker.SpectraCodeReGen

@Preview(showBackground = true, widthDp = 412, heightDp = 800)
@Composable
fun SpectraCodeReGenPreview() {
    val cyberpunkTheme = Theme(
        id = "cyberpunk_theme",
        name = "Cyberpunk",
        isActive = true,
        colors = ThemeColors(
            primary = "#FF2A6FDB",
            secondary = "#FF03DAC6",
            background = "#FF121212",
            surface = "#FF1E1E1E",
            error = "#FFCF6679",
            onPrimary = "#FF000000",
            onSecondary = "#FF000000",
            onBackground = "#FFFFFFFF",
            onSurface = "#FFE0E0E0",
            onError = "#FF000000"
        )
    )

    var currentTheme by remember { mutableStateOf(cyberpunkTheme) }

    AuraFrameFXTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "SPECTRACODE REGEN",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Advanced Theme Customization",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Theme Preview Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .clip(MaterialTheme.shapes.large),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Theme Preview",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Color preview blocks
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ColorBlock(
                                "Primary",
                                currentTheme.colors?.primary?.toColor() ?: Color.Blue
                            )
                            ColorBlock(
                                "Secondary",
                                currentTheme.colors?.secondary?.toColor() ?: Color.Cyan
                            )
                            ColorBlock(
                                "Surface",
                                currentTheme.colors?.surface?.toColor() ?: Color.DarkGray
                            )
                        }
                    }
                }

                // Color Picker Section
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    SpectraCodeReGen(
                        currentTheme = currentTheme,
                        onThemeUpdated = { updatedTheme ->
                            currentTheme = updatedTheme
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorBlock(label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = color,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .size(60.dp)
                .padding(bottom = 4.dp)
        ) {}

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Helper function to convert hex string to Compose Color
 */
fun String.toColor(): Color {
    return Color(this.toColorInt())
}

