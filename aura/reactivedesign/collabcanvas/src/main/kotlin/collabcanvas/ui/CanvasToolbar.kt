package collabcanvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Toolbar for canvas tools including color selection and stroke width.
 */
/**
 * Toolbar for selecting paint color, stroke width, or clearing the canvas.
 *
 * Renders a row with buttons to open a color picker, a stroke-width selector, and a Clear action.
 * Selecting a color or stroke width invokes the corresponding callback and closes the selector.
 *
 * @param onColorSelected Called with the chosen Color when the user selects a color from the picker.
 * @param onStrokeWidthSelected Called with the chosen stroke width (in pixels) when the user selects a width.
 * @param onClear Called when the Clear button is pressed.
 * @param modifier Optional [Modifier] applied to the toolbar container.
 */

/**
 * A toolbar for selecting drawing color, stroke width, or clearing the canvas.
 *
 * Shows three actions: a color picker toggle, a stroke-width selector toggle, and a Clear button.
 * Color and stroke pickers are rendered inline below the toolbar when their toggles are active;
 * selecting a value invokes the corresponding callback and hides the picker.
 *
 * @param onColorSelected Invoked with the chosen Color when a color swatch is selected.
 * @param onStrokeWidthSelected Invoked with the chosen stroke width (in pixels) when a width is selected.
 * @param onClear Invoked when the Clear button is pressed.
 * @param modifier Optional Modifier applied to the toolbar container.
 */
/**
 * A compact toolbar for a drawing canvas that provides color selection, stroke-width selection, and a clear action.
 *
 * The toolbar shows three primary controls (color picker toggle, stroke-width selector toggle, and Clear). When a picker is visible,
 * selecting a value invokes the corresponding callback and hides that picker.
 *
 * @param onColorSelected Called with the chosen Color when a color swatch is selected.
 * @param onStrokeWidthSelected Called with the chosen stroke width (in pixels) when a width option is selected.
 * @param onClear Called when the Clear button is pressed.
 * @param modifier Optional [Modifier] for styling and layout of the toolbar container.
 */
/**
 * A compact toolbar for canvas controls that provides color selection, stroke-width selection, and a clear action.
 *
 * The toolbar shows icon buttons to toggle an inline color picker and an inline stroke-width selector. When a color
 * or width is chosen the corresponding callback is invoked and the picker/selector hides. The Clear button invokes
 * the provided clear callback.
 *
 * @param onColorSelected Called with the selected Color when a swatch is tapped; the color picker closes after selection.
 * @param onStrokeWidthSelected Called with the selected stroke width (in pixels) when an option is tapped; the selector closes after selection.
 * @param onClear Called when the Clear button is pressed.
 * @param modifier Optional Modifier applied to the toolbar container.

 */
/**
 * A composable toolbar for selecting paint color, stroke width, or clearing the canvas.
 *
 * Tapping the color or stroke icons toggles inline pickers shown beneath the main row.
 * Selecting a color calls [onColorSelected] and hides the color picker. Selecting a stroke
 * width calls [onStrokeWidthSelected] and hides the stroke selector. Tapping Clear invokes [onClear].
 *
 * @param onColorSelected Called with the chosen [Color] when a color swatch is selected.
 * @param onStrokeWidthSelected Called with the chosen stroke width (in pixels) when a width is selected.
 * @param onClear Called when the Clear button is pressed.
 * @param modifier Optional [Modifier] for styling and layout.
 */

/**
 * A compact toolbar for the drawing canvas that exposes color, stroke-width, and clear actions.
 *
 * Renders a horizontal toolbar with buttons to toggle an inline color picker and an inline stroke-width
 * selector, plus a Clear button. Selecting a color or stroke width invokes the corresponding callback
 * and automatically hides the active picker.
 *
 * @param onColorSelected Called with the chosen Color when the user picks a color.
 * @param onStrokeWidthSelected Called with the chosen stroke width in pixels when the user picks a width.
 * @param onClear Called when the Clear button is pressed.
 * @param modifier Optional layout modifier applied to the toolbar's root container.
 */
@Composable
fun CanvasToolbar(
    onColorSelected: (Color) -> Unit,
    onStrokeWidthSelected: (Float) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showColorPicker: Boolean by remember { mutableStateOf(false) }
    var showStrokeSelector: Boolean by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main toolbar row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color picker button
            IconButton(
                onClick = { showColorPicker = !showColorPicker }
            ) {
                Icon(Icons.Default.Palette, "Color Picker")
            }

            // Stroke width button
            IconButton(
                onClick = { showStrokeSelector = !showStrokeSelector }
            ) {
                Icon(Icons.Default.Edit, "Stroke Width")
            }

            // Clear button
            Button(onClick = onClear) {
                Text("Clear")
            }
        }

        // Color picker
        if (showColorPicker) {
            ColorPicker(
                onColorSelected = { color ->
                    onColorSelected(color)
                    showColorPicker = false
                }
            )
        }

        // Stroke width selector
        if (showStrokeSelector) {
            StrokeWidthSelector(
                onStrokeWidthSelected = { width ->
                    onStrokeWidthSelected(width)
                    showStrokeSelector = false
                }
            )
        }
    }
}

/**
 * A horizontal palette of circular color swatches that reports the selected color.
 *
 * Displays eight fixed colors (Black, Red, Green, Blue, Yellow, Magenta, Cyan, Gray)
 * in a horizontally scrolling row. Each swatch is a 40.dp circle; tapping a swatch
 * invokes [onColorSelected] with that color.
 *
 * @param onColorSelected Callback invoked with the chosen Color when a swatch is tapped.
 */
@Composable
private fun ColorPicker(
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color.Black, Color.Red, Color.Green, Color.Blue,
        Color.Yellow, Color.Magenta, Color.Cyan, Color.Gray
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

/**
 * A horizontal selector of predefined stroke widths; renders buttons for each option.
 *
 * When a width button is clicked, the provided callback is invoked with the selected stroke width (in pixels).
 *
 * @param onStrokeWidthSelected Callback invoked with the selected stroke width value (e.g., 2f, 5f).
 */
@Composable
private fun StrokeWidthSelector(
    onStrokeWidthSelected: (Float) -> Unit
) {
    val strokeWidths = listOf(2f, 5f, 10f, 15f, 20f)

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(strokeWidths) { width ->
            Button(
                onClick = { onStrokeWidthSelected(width) },
                modifier = Modifier.height(40.dp)
            ) {
                Text("${width.toInt()}px")
            }
        }
    }
}
