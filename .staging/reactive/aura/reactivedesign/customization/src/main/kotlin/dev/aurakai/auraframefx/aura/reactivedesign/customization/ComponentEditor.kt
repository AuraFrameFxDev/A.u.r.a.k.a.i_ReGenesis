package dev.aurakai.auraframefx.aura.reactivedesign.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * COMPONENT EDITOR - PROPERTY MANAGEMENT
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Enhanced property editor with:
 * - ✅ Property binding with live updates
 * - ✅ Undo/redo support with command pattern
 * - ✅ Preset management (save/load/delete)
 * - ✅ Live preview feedback
 * - ✅ Grouped property sections
 */

/**
 * Component properties data class
 */
data class ComponentProperties(
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val width: Float = 100f,
    val height: Float = 100f,
    val rotationZ: Float = 0f,
    val zIndex: Float = 0f,
    val opacity: Float = 1f
) {
    fun toMap(): Map<String, Float> = mapOf(
        "positionX" to positionX,
        "positionY" to positionY,
        "width" to width,
        "height" to height,
        "rotationZ" to rotationZ,
        "zIndex" to zIndex,
        "opacity" to opacity
    )

    companion object {
        fun fromMap(map: Map<String, Float>): ComponentProperties = ComponentProperties(
            positionX = map["positionX"] ?: 0f,
            positionY = map["positionY"] ?: 0f,
            width = map["width"] ?: 100f,
            height = map["height"] ?: 100f,
            rotationZ = map["rotationZ"] ?: 0f,
            zIndex = map["zIndex"] ?: 0f,
            opacity = map["opacity"] ?: 1f
        )
    }
}

/**
 * Property preset for saving/loading component configurations
 */
data class PropertyPreset(
    val id: String,
    val name: String,
    val properties: ComponentProperties
)

/**
 * Command interface for undo/redo pattern
 */
sealed class PropertyCommand {
    abstract fun execute()
    abstract fun undo()

    data class UpdateProperty(
        val propertyName: String,
        val oldValue: Float,
        val newValue: Float,
        val onUpdate: (String, Float) -> Unit
    ) : PropertyCommand() {
        override fun execute() {
            onUpdate(propertyName, newValue)
        }

        override fun undo() {
            onUpdate(propertyName, oldValue)
        }
    }
}

/**
 * Undo/Redo manager
 */
class UndoRedoManager {
    private val undoStack = mutableStateListOf<PropertyCommand>()
    private val redoStack = mutableStateListOf<PropertyCommand>()

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    fun execute(command: PropertyCommand) {
        command.execute()
        undoStack.add(command)
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val command = undoStack.removeAt(undoStack.lastIndex)
            command.undo()
            redoStack.add(command)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val command = redoStack.removeAt(redoStack.lastIndex)
            command.execute()
            undoStack.add(command)
        }
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}

/**
 * Composable UI for editing a component's transform and appearance properties with undo/redo and preset management.
 *
 * Provides controls for position (X/Y), size (width/height), rotation, z-index, and opacity; supports saving, loading,
 * and deleting named presets and tracks changes via an undo/redo command stack.
 *
 * @param componentId Identifier of the component being edited.
 * @param initialProperties Initial values for the editable properties.
 * @param presets List of saved property presets available for loading.
 * @param onPropertyChanged Callback invoked when a property value changes; receives the property name and its new value.
 * @param onPresetSave Callback invoked to persist a newly created preset.
 * @param onPresetLoad Callback invoked when a preset is loaded.
 * @param onPresetDelete Callback invoked to request deletion of a preset by its id.
 * @param modifier Optional [Modifier] to apply to the root layout.
 */
@Composable
fun ComponentEditor(
    modifier: Modifier = Modifier,
    componentId: String,
    initialProperties: ComponentProperties = ComponentProperties(),
    presets: List<PropertyPreset> = emptyList(),
    onPropertyChanged: (String, Float) -> Unit = { _, _ -> },
    onPresetSave: (PropertyPreset) -> Unit = {},
    onPresetLoad: (PropertyPreset) -> Unit = {},
    onPresetDelete: (String) -> Unit = {}
) {
    // Current property values (bound to actual component)
    var properties by remember { mutableStateOf(initialProperties) }

    // Undo/Redo manager
    val undoRedoManager = remember { UndoRedoManager() }

    // Preset creation dialog state
    var showPresetDialog by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }

    // Helper function to update property with undo support
    fun updateProperty(propertyName: String, newValue: Float) {
        val oldValue = when (propertyName) {
            "positionX" -> properties.positionX
            "positionY" -> properties.positionY
            "width" -> properties.width
            "height" -> properties.height
            "rotationZ" -> properties.rotationZ
            "zIndex" -> properties.zIndex
            "opacity" -> properties.opacity
            else -> 0f
        }

        val command = PropertyCommand.UpdateProperty(
            propertyName = propertyName,
            oldValue = oldValue,
            newValue = newValue,
            onUpdate = { name, value ->
                properties = when (name) {
                    "positionX" -> properties.copy(positionX = value)
                    "positionY" -> properties.copy(positionY = value)
                    "width" -> properties.copy(width = value)
                    "height" -> properties.copy(height = value)
                    "rotationZ" -> properties.copy(rotationZ = value)
                    "zIndex" -> properties.copy(zIndex = value)
                    "opacity" -> properties.copy(opacity = value)
                    else -> properties
                }
                onPropertyChanged(name, value)
            }
        )
        undoRedoManager.execute(command)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with component info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Component Editor",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ID: $componentId",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Live indicator
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    Text(
                        text = "LIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Undo/Redo Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { undoRedoManager.undo() },
                enabled = undoRedoManager.canUndo,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Undo")
            }

            OutlinedButton(
                onClick = { undoRedoManager.redo() },
                enabled = undoRedoManager.canRedo,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Redo")
            }

            OutlinedButton(
                onClick = { undoRedoManager.clear() },
                enabled = undoRedoManager.canUndo || undoRedoManager.canRedo,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear History",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        HorizontalDivider()

        // Preset Management
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Presets",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedButton(
                    onClick = { showPresetDialog = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Save Preset",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (presets.isEmpty()) {
                Text(
                    text = "No presets saved yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(presets) { preset ->
                        PresetCard(
                            preset = preset,
                            onLoad = {
                                onPresetLoad(preset)
                                properties = preset.properties
                                undoRedoManager.clear()
                            },
                            onDelete = { onPresetDelete(preset.id) }
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        // Property Sections
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Position Section
                PropertySection(
                    title = "Position",
                    icon = Icons.Default.PinDrop
                ) {
                    PropertySlider(
                        label = "X",
                        value = properties.positionX,
                        valueRange = -500f..500f,
                        onValueChange = { updateProperty("positionX", it) }
                    )
                    PropertySlider(
                        label = "Y",
                        value = properties.positionY,
                        valueRange = -500f..500f,
                        onValueChange = { updateProperty("positionY", it) }
                    )
                }

                HorizontalDivider()

                // Size Section
                PropertySection(
                    title = "Size",
                    icon = Icons.Default.AspectRatio
                ) {
                    PropertySlider(
                        label = "Width",
                        value = properties.width,
                        valueRange = 0f..500f,
                        onValueChange = { updateProperty("width", it) }
                    )
                    PropertySlider(
                        label = "Height",
                        value = properties.height,
                        valueRange = 0f..500f,
                        onValueChange = { updateProperty("height", it) }
                    )
                }

                HorizontalDivider()

                // Transform Section
                PropertySection(
                    title = "Transform",
                    icon = Icons.AutoMirrored.Filled.RotateRight
                ) {
                    PropertySlider(
                        label = "Rotation",
                        value = properties.rotationZ,
                        valueRange = 0f..360f,
                        onValueChange = { updateProperty("rotationZ", it) }
                    )
                    PropertySlider(
                        label = "Z-Index",
                        value = properties.zIndex,
                        valueRange = 0f..100f,
                        onValueChange = { updateProperty("zIndex", it) }
                    )
                }

                HorizontalDivider()

                // Appearance Section
                PropertySection(
                    title = "Appearance",
                    icon = Icons.Default.Visibility
                ) {
                    PropertySlider(
                        label = "Opacity",
                        value = properties.opacity,
                        valueRange = 0f..1f,
                        onValueChange = { updateProperty("opacity", it) }
                    )
                }
            }
        }
    }

    // Preset Save Dialog
    if (showPresetDialog) {
        AlertDialog(
            onDismissRequest = { showPresetDialog = false },
            title = { Text("Save Preset") },
            text = {
                Column {
                    Text(
                        text = "Enter a name for this preset:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = presetName,
                        onValueChange = { presetName = it },
                        label = { Text("Preset Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (presetName.isNotBlank()) {
                            onPresetSave(
                                PropertyPreset(
                                    id = "${componentId}_${System.currentTimeMillis()}",
                                    name = presetName,
                                    properties = properties
                                )
                            )
                            presetName = ""
                            showPresetDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPresetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Property section with icon and title
 */
@Composable
private fun PropertySection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        content()
    }
}

/**
 * Property slider with label and value display
 */
@Composable
private fun PropertySlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "%.2f".format(value),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Displays a compact card representing a saved property preset with actions to load or delete it.
 *
 * The card shows the preset's name and exposes two actions: tapping the card invokes [onLoad] to apply the preset,
 * and tapping the delete icon invokes [onDelete] to remove the preset.
 *
 * @param preset The preset displayed by this card.
 * @param onLoad Callback invoked when the card is tapped to load the preset.
 * @param onDelete Callback invoked when the delete action is triggered for this preset.
 * @param modifier Modifier applied to the card for layout/styling.
 */
@Composable
private fun PresetCard(
    preset: PropertyPreset,
    onLoad: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onLoad,
        modifier = modifier.width(150.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = preset.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = "Tap to load",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

// provide a lightweight placeholder for 3D rotation concept (was unresolved)
data class ThreeDRotation(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f)
