package dev.aurakai.auraframefx.ui.components.colorpicker

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme

@Preview(showBackground = true)
@Composable
fun ThemeEditorPreview() {
    AuraFrameFXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var currentColors by remember {
                mutableStateOf(
                    ThemeColors(
                        primary = Color(0xFF6200EE),
                        secondary = Color(0xFF03DAC6),
                        background = Color(0xFFFFFBFE),
                        surface = Color(0xFFFFFBFE),
                        error = Color(0xFFB00020)
                    )
                )
            }

            ThemeEditor(
                initialColors = currentColors,
                onColorsChanged = { currentColors = it },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Helper function to convert Android color int to Compose Color
 */
@Composable
fun Int.toColor(): Color {
    return Color(this)
}

/**
 * Helper function to convert hex string to Compose Color
 */
@Composable
fun String.toColor(): Color {
    return Color(this.toColorInt())
}

