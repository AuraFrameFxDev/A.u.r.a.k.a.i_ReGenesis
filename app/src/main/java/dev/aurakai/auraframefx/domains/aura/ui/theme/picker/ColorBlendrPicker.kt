package dev.aurakai.auraframefx.domains.aura.ui.theme.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.aurakai.colorblendr.ChromaCore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorBlendrPicker(
    initialColor: Color = Color.Cyan,
    onColorChanged: (Color) -> Unit = {},
    onColorSelected: (Color) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(initialColor) }

    // Button to open the color picker
    Surface(
        modifier = modifier
            .size(48.dp)
            .clickable { showColorPicker = true },
        shape = MaterialTheme.shapes.medium,
        color = selectedColor,
        shadowElevation = 4.dp
    ) {}

    // Color picker dialog
    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text("Select Color") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ChromaCore color picker
                    ChromaCoreColorPicker(
                        color = selectedColor,
                        onColorChange = { color ->
                            selectedColor = color
                            onColorChanged(color)
                            onColorSelected(color)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showColorPicker = false }
                ) {
                    Text("Done")
                }
            }
        )
    }
}

/**
 * ChromaCore color picker component using REAL ChromaCore utilities
 */
@Composable
fun ChromaCoreColorPicker(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var brightness by remember { mutableFloatStateOf(1f) }

    // Initialize HSV from color if needed
    // In a real implementation, we'd extract HSV from the incoming color

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Color preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color, MaterialTheme.shapes.medium)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
        )

        // Hue slider
        Text("Hue", style = MaterialTheme.typography.labelMedium)
        Slider(
            value = hue,
            onValueChange = { newHue ->
                hue = newHue
                // Rotate hue using ChromaCore
                val baseColor = Color.Red // Reference point for 0 degrees
                val newColor = ChromaCore.rotateHue(baseColor, newHue * 360f)
                onColorChange(
                    ChromaCore.adjustSaturation(
                        ChromaCore.adjustBrightness(
                            newColor,
                            brightness
                        ), saturation
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Red, Color.Yellow, Color.Green,
                            Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                        )
                    ),
                    MaterialTheme.shapes.small
                )
        )

        // Saturation slider
        Text("Saturation", style = MaterialTheme.typography.labelMedium)
        Slider(
            value = saturation,
            onValueChange = { newSaturation ->
                saturation = newSaturation
                onColorChange(ChromaCore.adjustSaturation(color, newSaturation))
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Brightness slider
        Text("Brightness", style = MaterialTheme.typography.labelMedium)
        Slider(
            value = brightness,
            onValueChange = { newBrightness ->
                brightness = newBrightness
                onColorChange(ChromaCore.adjustBrightness(color, newBrightness))
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Quick color presets using ChromaCore
        Text("Presets", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val presetColors = listOf(
                Color.Red, Color.Green, Color.Blue,
                Color.Cyan, Color.Magenta, Color.Yellow
            )
            presetColors.forEach { presetColor ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(presetColor, MaterialTheme.shapes.small)
                        .clickable {
                            onColorChange(presetColor)
                        }
                        .border(
                            1.dp,
                            if (color == presetColor) MaterialTheme.colorScheme.primary else Color.Transparent,
                            MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}

@Composable
fun rememberColorBlendrState(initialColor: Color): MutableState<Color> = remember {
    mutableStateOf(initialColor)
}

