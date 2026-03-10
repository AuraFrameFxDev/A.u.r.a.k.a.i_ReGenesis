package dev.aurakai.auraframefx.ui.components.colorpicker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mahmud.colorblendr.ColorBlendr
import com.mahmud.colorblendr.rememberColorBlendrState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorBlendrPicker(
    initialColor: Color = Color.Cyan,
    onColorSelected: (Color) -> Unit,
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
                    // ColorBlendr component
                    ColorBlendr(
                        color = selectedColor,
                        onColorChange = { color ->
                            selectedColor = color
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

@Composable
fun rememberColorBlendrState(initialColor: Color) = remember {
    com.mahmud.colorblendr.rememberColorBlendrState(initialColor)
}
