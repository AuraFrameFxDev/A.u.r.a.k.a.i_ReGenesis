package dev.aurakai.auraframefx.domains.aura.ui.theme.preview

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.ui.theme.AuraFrameFXTheme

/**
 * A composable that shows a preview of the theme with common UI elements.
 */
@Composable
fun ThemePreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = "AuraFrameFX Theme",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // Primary Button
            Button(onClick = {}) {
                Text("Primary Button")
            }

            // Outlined Button
            OutlinedButton(onClick = {}) {
                Text("Outlined Button")
            }

            // Text Button
            TextButton(onClick = {}) {
                Text("Text Button")
            }

            // Card Example
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Card Title",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "This is a card with some sample content to demonstrate the theme.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Text Styles
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Display Large",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Headline Medium",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Title Large",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Body Large",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Label Small",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Preview for light theme.
 */
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun LightThemePreview() {
    AuraFrameFXTheme(darkTheme = false) {
        ThemePreview()
    }
}

/**
 * Preview for dark theme.
 */
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkThemePreview() {
    AuraFrameFXTheme(darkTheme = true) {
        ThemePreview()
    }
}

/**
 * Preview for dynamic color theming (Android 12+).
 */
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DynamicThemePreview() {
    AuraFrameFXTheme(darkTheme = true) {
        ThemePreview()
    }
}

