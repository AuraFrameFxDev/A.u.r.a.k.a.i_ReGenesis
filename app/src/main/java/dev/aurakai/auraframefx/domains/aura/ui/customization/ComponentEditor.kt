package dev.aurakai.auraframefx.domains.aura.ui.customization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify.IconPicker
import dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify.IconifyService
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPink

/**
 * Position model for UI components
 */
data class Position(
    val x: Float = 0f,
    val y: Float = 0f
)

/**
 * 🎨 Component Editor - Individual UI Component Property Editor
 *
 * This is what users see when they select a UI component in the 3D workbench.
 * Edit EVERYTHING: position, size, rotation, z-index, opacity, color, animations.
 *
 * Example:
 * ```
 * ComponentEditor(
 *     component = statusBarComponent,
 *     onUpdate = { updatedComponent ->
 *         customizationEngine.updateComponent(updatedComponent)
 *     }
 * )
 * ```
 */

/**
 * UI Component data model
 */
data class UIComponent(
    val id: String,
    val name: String,
    val type: ComponentType,

    // Position & Transform
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 100f,
    val height: Float = 100f,
    val rotation: Float = 0f,
    val scale: Float = 1f,

    // Visual Properties
    val zIndex: Int = 0,
    val opacity: Float = 1f,
    val backgroundColor: Color = Color.Transparent,
    val borderColor: Color = Color.Transparent,
    val borderWidth: Float = 0f,
    val cornerRadius: Float = 0f,

    // Animation
    val animationType: AnimationType = AnimationType.NONE,
    val animationDuration: Float = 1f, // seconds

    // Icon
    val iconId: String? = null, // Iconify icon ID (e.g., "mdi:heart", "fa:user")

    // Behavior
    val isVisible: Boolean = true,
    val isInteractive: Boolean = true,
    val isLocked: Boolean = false,
    val position: Position? = null
)

enum class ComponentType {
    STATUS_BAR,
    NAVIGATION_BAR,
    NOTIFICATION_PANEL,
    QUICK_SETTINGS,
    APP_ICON,
    WIDGET,
    TEXT_LABEL,
    BUTTON,
    IMAGE,
    CUSTOM
}

enum class AnimationType {
    NONE,
    FADE_IN_OUT,
    SLIDE_LEFT_RIGHT,
    SLIDE_UP_DOWN,
    ROTATE,
    PULSE,
    GLITCH,
    HOLOGRAM,
    DECONSTRUCT
}

/**
 * Component Editor Panel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentEditor(
    component: UIComponent,
    onUpdate: (UIComponent) -> Unit,
    onClose: () -> Unit = {},
    iconifyService: IconifyService? = null,
    modifier: Modifier = Modifier
) {
    var editingComponent by remember { mutableStateOf(component) }

    // Expanded sections tracking
    var transformExpanded by remember { mutableStateOf(true) }
    var visualExpanded by remember { mutableStateOf(false) }
    var animationExpanded by remember { mutableStateOf(false) }
    var behaviorExpanded by remember { mutableStateOf(false) }

    // Dialog states
    var showIconPicker by remember { mutableStateOf(false) }
    var showBackgroundColorPicker by remember { mutableStateOf(false) }
    var showBorderColorPicker by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(360.dp),
        color = Color(0xFF1A1A1A),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = editingComponent.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberpunkPink
                    )
                    Text(
                        text = editingComponent.type.name.replace("_", " "),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row {
                    // Lock/Unlock
                    IconButton(
                        onClick = {
                            editingComponent = editingComponent.copy(
                                isLocked = !editingComponent.isLocked
                            )
                            onUpdate(editingComponent)
                        }
                    ) {
                        Icon(
                            imageVector = if (editingComponent.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock/Unlock",
                            tint = if (editingComponent.isLocked) CyberpunkPink else Color.Gray
                        )
                    }

                    // Close
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // TRANSFORM SECTION
                EditorSection(
                    title = "Transform",
                    icon = Icons.Default.Transform,
                    expanded = transformExpanded,
                    onToggle = { transformExpanded = !transformExpanded }
                ) {
                    // Position
                    PropertySlider(
                        label = "X Position",
                        value = editingComponent.x,
                        valueRange = -500f..1500f,
                        onValueChange = {
                            editingComponent = editingComponent.copy(x = it)
                            onUpdate(editingComponent)
                        }
                    )

                    PropertySlider(
                        label = "Y Position",
                        value = editingComponent.y,
                        valueRange = -500f..2500f,
                        onValueChange = {
                            editingComponent = editingComponent.copy(y = it)
                            onUpdate(editingComponent)
                        }
                    )

                    // Size
                    PropertySlider(
                        label = "Width",
                        value = editingComponent.width,
                        valueRange = 10f..1000f,
                        onValueChange = {
                            editingComponent = editingComponent.copy(width = it)
                            onUpdate(editingComponent)
                        }
                    )

                    PropertySlider(
                        label = "Height",
                        value = editingComponent.height,
                        valueRange = 10f..500f,
                        onValueChange = {
                            editingComponent = editingComponent.copy(height = it)
                            onUpdate(editingComponent)
                        }
                    )

                    // Rotation & Scale
                    PropertySlider(
                        label = "Rotation",
                        value = editingComponent.rotation,
                        valueRange = 0f..360f,
                        unit = "°",
                        onValueChange = {
                            editingComponent = editingComponent.copy(rotation = it)
                            onUpdate(editingComponent)
                        }
                    )

                    PropertySlider(
                        label = "Scale",
                        value = editingComponent.scale,
                        valueRange = 0.1f..3f,
                        onValueChange = {
                            editingComponent = editingComponent.copy(scale = it)
                            onUpdate(editingComponent)
                        }
                    )
                }

                // VISUAL SECTION
                EditorSection(
                    title = "Visual",
                    icon = Icons.Default.Palette,
                    expanded = visualExpanded,
                    onToggle = { visualExpanded = !visualExpanded }
                ) {
                    // Z-Index
                    PropertySlider(
                        label = "Z-Index (Layer)",
                        value = editingComponent.zIndex.toFloat(),
                        valueRange = -10f..100f,
                        onValueChange = {
                            editingComponent = editingComponent.copy(zIndex = it.toInt())
                            onUpdate(editingComponent)
                        }
                    )

                    // Opacity
                    PropertySlider(
                        label = "Opacity",
                        value = editingComponent.opacity,
                        valueRange = 0f..1f,
                        onValueChange = {
                            editingComponent = editingComponent.copy(opacity = it)
                            onUpdate(editingComponent)
                        }
                    )

                    // Corner Radius
                    PropertySlider(
                        label = "Corner Radius",
                        value = editingComponent.cornerRadius,
                        valueRange = 0f..50f,
                        unit = "dp",
                        onValueChange = {
                            editingComponent = editingComponent.copy(cornerRadius = it)
                            onUpdate(editingComponent)
                        }
                    )

                    // Border Width
                    PropertySlider(
                        label = "Border Width",
                        value = editingComponent.borderWidth,
                        valueRange = 0f..10f,
                        unit = "dp",
                        onValueChange = {
                            editingComponent = editingComponent.copy(borderWidth = it)
                            onUpdate(editingComponent)
                        }
                    )

                    // Icon Selector
                    Text(
                        text = "Icon",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    Button(
                        onClick = { showIconPicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2A2A2A)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.InsertEmoticon,
                            contentDescription = "Select Icon",
                            tint = CyberpunkCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = editingComponent.iconId ?: "Select Icon (250K+ available)",
                            maxLines = 1
                        )
                    }

                    if (editingComponent.iconId != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    editingComponent = editingComponent.copy(iconId = null)
                                    onUpdate(editingComponent)
                                }
                            ) {
                                Text("Remove Icon", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }

                    // Background Color Picker
                    Text(
                        text = "Background Color",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(editingComponent.backgroundColor)
                                .border(2.dp, CyberpunkCyan, CircleShape)
                                .clickable { showBackgroundColorPicker = true }
                        )
                        Text(
                            text = "#${
                                editingComponent.backgroundColor.toArgb().toUInt().toString(16)
                                    .uppercase().takeLast(6)
                            }",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    // Border Color Picker
                    Text(
                        text = "Border Color",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(editingComponent.borderColor)
                                .border(2.dp, CyberpunkCyan, CircleShape)
                                .clickable { showBorderColorPicker = true }
                        )
                        Text(
                            text = "#${
                                editingComponent.borderColor.toArgb().toUInt().toString(16)
                                    .uppercase().takeLast(6)
                            }",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // ANIMATION SECTION
                EditorSection(
                    title = "Animation",
                    icon = Icons.Default.Animation,
                    expanded = animationExpanded,
                    onToggle = { animationExpanded = !animationExpanded }
                ) {
                    // Animation Type Selector
                    Text(
                        text = "Animation Type",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    AnimationType.values().forEach { type ->
                        AnimationTypeChip(
                            type = type,
                            isSelected = editingComponent.animationType == type,
                            onClick = {
                                editingComponent = editingComponent.copy(animationType = type)
                                onUpdate(editingComponent)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animation Duration
                    PropertySlider(
                        label = "Duration",
                        value = editingComponent.animationDuration,
                        valueRange = 0.1f..5f,
                        unit = "s",
                        onValueChange = {
                            editingComponent = editingComponent.copy(animationDuration = it)
                            onUpdate(editingComponent)
                        }
                    )
                }

                // BEHAVIOR SECTION
                EditorSection(
                    title = "Behavior",
                    icon = Icons.Default.Settings,
                    expanded = behaviorExpanded,
                    onToggle = { behaviorExpanded = !behaviorExpanded }
                ) {
                    // Visibility
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Visible",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Switch(
                            checked = editingComponent.isVisible,
                            onCheckedChange = {
                                editingComponent = editingComponent.copy(isVisible = it)
                                onUpdate(editingComponent)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberpunkPink,
                                checkedTrackColor = CyberpunkPink.copy(alpha = 0.5f)
                            )
                        )
                    }

                    // Interactive
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interactive",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Switch(
                            checked = editingComponent.isInteractive,
                            onCheckedChange = {
                                editingComponent = editingComponent.copy(isInteractive = it)
                                onUpdate(editingComponent)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberpunkCyan,
                                checkedTrackColor = CyberpunkCyan.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        editingComponent = component // Reset
                        onUpdate(component)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }

                Button(
                    onClick = { onUpdate(editingComponent) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberpunkPink
                    )
                ) {
                    Text("Apply")
                }
            }
        }
    }

    // Icon Picker Dialog
    if (showIconPicker && iconifyService != null) {
        Dialog(onDismissRequest = { showIconPicker = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.9f),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1A1A1A)
            ) {
                IconPicker(
                    iconifyService = iconifyService,
                    currentIcon = editingComponent.iconId,
                    onIconSelected = { iconId ->
                        editingComponent = editingComponent.copy(iconId = iconId)
                        onUpdate(editingComponent)
                        showIconPicker = false
                    },
                    onDismiss = { showIconPicker = false }
                )
            }
        }
    }

    // Background Color Picker Dialog
    if (showBackgroundColorPicker) {
        ColorPickerDialog(
            currentColor = editingComponent.backgroundColor,
            onColorSelected = { color ->
                editingComponent = editingComponent.copy(backgroundColor = color)
                onUpdate(editingComponent)
                showBackgroundColorPicker = false
            },
            onDismiss = { showBackgroundColorPicker = false }
        )
    }

    // Border Color Picker Dialog
    if (showBorderColorPicker) {
        ColorPickerDialog(
            currentColor = editingComponent.borderColor,
            onColorSelected = { color ->
                editingComponent = editingComponent.copy(borderColor = color)
                onUpdate(editingComponent)
                showBorderColorPicker = false
            },
            onDismiss = { showBorderColorPicker = false }
        )
    }
}

/**
 * Collapsible section
 */
@Composable
fun EditorSection(
    title: String,
    icon: ImageVector,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onToggle)
                .background(Color(0xFF2A2A2A))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = CyberpunkPink,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = Color.Gray
            )
        }

        // Content
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * Property slider with label and value
 */
@Composable
fun PropertySlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    unit: String = "",
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = "${value.toInt()}$unit",
                fontSize = 14.sp,
                color = CyberpunkCyan,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = CyberpunkPink,
                activeTrackColor = CyberpunkPink,
                inactiveTrackColor = Color.Gray
            )
        )
    }
}

/**
 * Animation type chip
 */
@Composable
fun AnimationTypeChip(
    type: AnimationType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) CyberpunkPink else Color(0xFF2A2A2A)
    ) {
        Text(
            text = type.name.replace("_", " "),
            fontSize = 12.sp,
            color = if (isSelected) Color.Black else Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/**
 * Simple Color Picker Dialog
 */
@Composable
fun ColorPickerDialog(
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var red by remember { mutableFloatStateOf(currentColor.red) }
    var green by remember { mutableFloatStateOf(currentColor.green) }
    var blue by remember { mutableFloatStateOf(currentColor.blue) }
    var alpha by remember { mutableFloatStateOf(currentColor.alpha) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Select Color",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberpunkPink
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(red, green, blue, alpha))
                        .border(2.dp, CyberpunkCyan, RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))

                // RGB Sliders
                ColorSlider("Red", red) { red = it }
                ColorSlider("Green", green) { green = it }
                ColorSlider("Blue", blue) { blue = it }
                ColorSlider("Alpha", alpha) { alpha = it }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onColorSelected(Color(red, green, blue, alpha)) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberpunkPink
                        )
                    ) {
                        Text("Select")
                    }
                }
            }
        }
    }
}

/**
 * Color Slider Component
 */
@Composable
fun ColorSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = "${(value * 255).toInt()}",
                fontSize = 14.sp,
                color = CyberpunkCyan
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = CyberpunkPink,
                activeTrackColor = CyberpunkCyan,
                inactiveTrackColor = Color.Gray
            )
        )
    }
}

