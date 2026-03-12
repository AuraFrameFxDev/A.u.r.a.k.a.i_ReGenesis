package dev.aurakai.auraframefx.domains.aura.ui.theme.picker

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.ui.theme.preview.UIComponentsPreview

/**
 * A comprehensive theme editor that shows a live preview of UI components
 * along with color pickers for different theme attributes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeEditor(
    initialColors: ThemeColors = ThemeColors(),
    onColorsChanged: (ThemeColors) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    var colors by remember { mutableStateOf(initialColors) }
    var selectedColorType by remember { mutableStateOf<ColorType?>(null) }

    // Update parent when colors change
    LaunchedEffect(colors) {
        onColorsChanged(colors)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // UI Preview Section
        UIComponentsPreview(
            primaryColor = colors.primary,
            secondaryColor = colors.secondary,
            backgroundColor = colors.background,
            onSurfaceColor = colors.onSurface,
            modifier = Modifier.padding(8.dp)
        )

        // Color Selection Section
        Text(
            "Theme Colors",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        // Color Picker Grid
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Primary Color
            ColorSelectionItem(
                label = "Primary",
                color = colors.primary,
                onClick = { selectedColorType = ColorType.PRIMARY }
            )

            // Secondary Color
            ColorSelectionItem(
                label = "Secondary",
                color = colors.secondary,
                onClick = { selectedColorType = ColorType.SECONDARY }
            )

            // Background
            ColorSelectionItem(
                label = "Background",
                color = colors.background,
                onClick = { selectedColorType = ColorType.BACKGROUND }
            )

            // Surface
            ColorSelectionItem(
                label = "Surface",
                color = colors.surface,
                onClick = { selectedColorType = ColorType.SURFACE }
            )

            // Error
            ColorSelectionItem(
                label = "Error",
                color = colors.error,
                onClick = { selectedColorType = ColorType.ERROR }
            )
        }

        // Color Picker Dialog
        selectedColorType?.let { colorType ->
            AlertDialog(
                onDismissRequest = { selectedColorType = null },
                title = { Text("Select ${colorType.label} Color") },
                text = {
                    ChromaCoreColorPicker(
                        color = when (colorType) {
                            ColorType.PRIMARY -> colors.primary
                            ColorType.SECONDARY -> colors.secondary
                            ColorType.BACKGROUND -> colors.background
                            ColorType.SURFACE -> colors.surface
                            ColorType.ERROR -> colors.error
                        },
                        onColorChange = { newColor: Color ->
                            colors = when (colorType) {
                                ColorType.PRIMARY -> colors.copy(primary = newColor)
                                ColorType.SECONDARY -> colors.copy(secondary = newColor)
                                ColorType.BACKGROUND -> colors.copy(background = newColor)
                                ColorType.SURFACE -> colors.copy(surface = newColor)
                                ColorType.ERROR -> colors.copy(error = newColor)
                                else -> colors
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { selectedColorType = null }
                    ) {
                        Text("Done")
                    }
                }
            )
        }
    }
}


@Composable
private fun ColorSelectionItem(
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            // Color preview
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color, RoundedCornerShape(4.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

/**
 * Placeholder color picker using ChromaCore color space
 * This is a simple implementation for selecting colors in the theme editor
 */


/**
 * Data class representing all theme colors
 */
data class ThemeColors(
    val primary: Color = Color(0xFF6750A4),
    val onPrimary: Color = Color.White,
    val primaryContainer: Color = Color(0xFFEADDFF),
    val onPrimaryContainer: Color = Color(0xFF21005D),
    val secondary: Color = Color(0xFF625B71),
    val onSecondary: Color = Color.White,
    val secondaryContainer: Color = Color(0xFFE8DEF8),
    val onSecondaryContainer: Color = Color(0xFF1D192B),
    val background: Color = Color(0xFFFFFBFE),
    val onBackground: Color = Color(0xFF1C1B1F),
    val surface: Color = Color(0xFFFFFBFE),
    val onSurface: Color = Color(0xFF1C1B1F),
    val surfaceVariant: Color = Color(0xFFE7E0EC),
    val onSurfaceVariant: Color = Color(0xFF49454F),
    val error: Color = Color(0xFFB3261E),
    val onError: Color = Color.White,
    val errorContainer: Color = Color(0xFFF9DEDC),
    val onErrorContainer: Color = Color(0xFF410E0B),
    val outline: Color = Color(0xFF79747E)
)

/**
 * Represents different types of colors in the theme
 */
private enum class ColorType(val label: String) {
    PRIMARY("Primary"),
    SECONDARY("Secondary"),
    BACKGROUND("Background"),
    SURFACE("Surface"),
    ERROR("Error")
}

