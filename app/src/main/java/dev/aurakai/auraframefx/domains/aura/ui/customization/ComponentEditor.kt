package dev.aurakai.auraframefx.domains.aura.ui.customization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import dev.aurakai.auraframefx.core.theme.*
import dev.aurakai.auraframefx.domains.aura.ui.components.IconPicker
import dev.aurakai.auraframefx.domains.aura.services.iconify.IconifyService

/**
 * Position model for UI components
 */
data class Position(
    val x: Float = 0f,
    val y: Float = 0f
)

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
    val animationDuration: Float = 1f,

    // Icon
    val iconId: String? = null,

    // Behavior
    val isVisible: Boolean = true,
    val isInteractive: Boolean = true,
    val isLocked: Boolean = false,
    val position: Position? = null
)

enum class ComponentType {
    STATUS_BAR, NAVIGATION_BAR, NOTIFICATION_PANEL, QUICK_SETTINGS,
    APP_ICON, WIDGET, TEXT_LABEL, BUTTON, IMAGE, CUSTOM
}

enum class AnimationType {
    NONE, FADE_IN_OUT, SLIDE_LEFT_RIGHT, SLIDE_UP_DOWN,
    ROTATE, PULSE, GLITCH, HOLOGRAM, DECONSTRUCT
}

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

    var transformExpanded by remember { mutableStateOf(true) }
    var visualExpanded by remember { mutableStateOf(false) }
    var animationExpanded by remember { mutableStateOf(false) }
    var behaviorExpanded by remember { mutableStateOf(false) }

    var showIconPicker by remember { mutableStateOf(false) }
    var showBackgroundColorPicker by remember { mutableStateOf(false) }
    var showBorderColorPicker by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxHeight().width(360.dp),
        color = Color(0xFF1A1A1A),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
                        color = Color.CyberpunkPink
                    )
                    Text(
                        text = editingComponent.type.name.replace("_", " "),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row {
                    IconButton(
                        onClick = {
                            editingComponent = editingComponent.copy(isLocked = !editingComponent.isLocked)
                            onUpdate(editingComponent)
                        }
                    ) {
                        Icon(
                            imageVector = if (editingComponent.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock/Unlock",
                            tint = if (editingComponent.isLocked) Color.CyberpunkPink else Color.Gray
                        )
                    }

                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                EditorSection(title = "Transform", icon = Icons.Default.Transform, expanded = transformExpanded, onToggle = { transformExpanded = !transformExpanded }) {
                    PropertySlider(label = "X Position", value = editingComponent.x, valueRange = -500f..1500f, onValueChange = { editingComponent = editingComponent.copy(x = it); onUpdate(editingComponent) })
                    PropertySlider(label = "Y Position", value = editingComponent.y, valueRange = -500f..2500f, onValueChange = { editingComponent = editingComponent.copy(y = it); onUpdate(editingComponent) })
                    PropertySlider(label = "Width", value = editingComponent.width, valueRange = 10f..1000f, onValueChange = { editingComponent = editingComponent.copy(width = it); onUpdate(editingComponent) })
                    PropertySlider(label = "Height", value = editingComponent.height, valueRange = 10f..500f, onValueChange = { editingComponent = editingComponent.copy(height = it); onUpdate(editingComponent) })
                    PropertySlider(label = "Rotation", value = editingComponent.rotation, valueRange = 0f..360f, unit = "°", onValueChange = { editingComponent = editingComponent.copy(rotation = it); onUpdate(editingComponent) })
                    PropertySlider(label = "Scale", value = editingComponent.scale, valueRange = 0.1f..3f, onValueChange = { editingComponent = editingComponent.copy(scale = it); onUpdate(editingComponent) })
                }

                EditorSection(title = "Visual", icon = Icons.Default.Palette, expanded = visualExpanded, onToggle = { visualExpanded = !visualExpanded }) {
                    PropertySlider(label = "Z-Index", value = editingComponent.zIndex.toFloat(), valueRange = -10f..100f, onValueChange = { editingComponent = editingComponent.copy(zIndex = it.toInt()); onUpdate(editingComponent) })
                    PropertySlider(label = "Opacity", value = editingComponent.opacity, valueRange = 0f..1f, onValueChange = { editingComponent = editingComponent.copy(opacity = it); onUpdate(editingComponent) })
                    PropertySlider(label = "Corner Radius", value = editingComponent.cornerRadius, valueRange = 0f..50f, unit = "dp", onValueChange = { editingComponent = editingComponent.copy(cornerRadius = it); onUpdate(editingComponent) })
                    PropertySlider(label = "Border Width", value = editingComponent.borderWidth, valueRange = 0f..10f, unit = "dp", onValueChange = { editingComponent = editingComponent.copy(borderWidth = it); onUpdate(editingComponent) })

                    Text(text = "Icon", fontSize = 14.sp, color = Color.White, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                    Button(onClick = { showIconPicker = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A))) {
                        Icon(imageVector = Icons.Default.InsertEmoticon, contentDescription = "Select Icon", tint = Color.CyberpunkCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = editingComponent.iconId ?: "Select Icon", maxLines = 1)
                    }

                    if (editingComponent.iconId != null) {
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { editingComponent = editingComponent.copy(iconId = null); onUpdate(editingComponent) }) {
                                Text("Remove Icon", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }

                    Text(text = "Background Color", fontSize = 14.sp, color = Color.White, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(editingComponent.backgroundColor).border(2.dp, Color.CyberpunkCyan, CircleShape).clickable { showBackgroundColorPicker = true })
                        Text(text = "#${editingComponent.backgroundColor.toArgb().toUInt().toString(16).uppercase().takeLast(6)}", fontSize = 12.sp, color = Color.Gray)
                    }

                    Text(text = "Border Color", fontSize = 14.sp, color = Color.White, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(editingComponent.borderColor).border(2.dp, Color.CyberpunkCyan, CircleShape).clickable { showBorderColorPicker = true })
                        Text(text = "#${editingComponent.borderColor.toArgb().toUInt().toString(16).uppercase().takeLast(6)}", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                EditorSection(title = "Animation", icon = Icons.Default.Animation, expanded = animationExpanded, onToggle = { animationExpanded = !animationExpanded }) {
                    Text(text = "Animation Type", fontSize = 14.sp, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
                    AnimationType.entries.forEach { type ->
                        AnimationTypeChip(type = type, isSelected = editingComponent.animationType == type, onClick = { editingComponent = editingComponent.copy(animationType = type); onUpdate(editingComponent) })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    PropertySlider(label = "Duration", value = editingComponent.animationDuration, valueRange = 0.1f..5f, unit = "s", onValueChange = { editingComponent = editingComponent.copy(animationDuration = it); onUpdate(editingComponent) })
                }

                EditorSection(title = "Behavior", icon = Icons.Default.Settings, expanded = behaviorExpanded, onToggle = { behaviorExpanded = !behaviorExpanded }) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Visible", fontSize = 14.sp, color = Color.White)
                        Switch(checked = editingComponent.isVisible, onCheckedChange = { editingComponent = editingComponent.copy(isVisible = it); onUpdate(editingComponent) }, colors = SwitchDefaults.colors(checkedThumbColor = Color.CyberpunkPink, checkedTrackColor = Color.CyberpunkPink.copy(alpha = 0.5f)))
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Interactive", fontSize = 14.sp, color = Color.White)
                        Switch(checked = editingComponent.isInteractive, onCheckedChange = { editingComponent = editingComponent.copy(isInteractive = it); onUpdate(editingComponent) }, colors = SwitchDefaults.colors(checkedThumbColor = Color.CyberpunkCyan, checkedTrackColor = Color.CyberpunkCyan.copy(alpha = 0.5f)))
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { editingComponent = component; onUpdate(component) }, modifier = Modifier.weight(1f)) { Text("Reset") }
                Button(onClick = { onUpdate(editingComponent) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.CyberpunkPink)) { Text("Apply") }
            }
        }
    }

    if (showIconPicker && iconifyService != null) {
        Dialog(onDismissRequest = { showIconPicker = false }) {
            Surface(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f), shape = RoundedCornerShape(16.dp), color = Color(0xFF1A1A1A)) {
                IconPicker(iconifyService = iconifyService, currentIcon = editingComponent.iconId, onIconSelected = { iconId -> editingComponent = editingComponent.copy(iconId = iconId); onUpdate(editingComponent); showIconPicker = false }, onDismiss = { showIconPicker = false })
            }
        }
    }

    if (showBackgroundColorPicker) {
        ColorPickerDialog(currentColor = editingComponent.backgroundColor, onColorSelected = { color -> editingComponent = editingComponent.copy(backgroundColor = color); onUpdate(editingComponent); showBackgroundColorPicker = false }, onDismiss = { showBackgroundColorPicker = false })
    }

    if (showBorderColorPicker) {
        ColorPickerDialog(currentColor = editingComponent.borderColor, onColorSelected = { color -> editingComponent = editingComponent.copy(borderColor = color); onUpdate(editingComponent); showBorderColorPicker = false }, onDismiss = { showBorderColorPicker = false })
    }
}

@Composable
fun EditorSection(title: String, icon: ImageVector, expanded: Boolean, onToggle: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable(onClick = onToggle).background(Color(0xFF2A2A2A)).padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = title, tint = Color.CyberpunkPink, modifier = Modifier.size(20.dp))
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Icon(imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = if (expanded) "Collapse" else "Expand", tint = Color.Gray)
        }
        AnimatedVisibility(visible = expanded, enter = expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)), exit = shrinkVertically()) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { content() }
        }
    }
}

@Composable
fun PropertySlider(label: String, value: Float, valueRange: ClosedFloatingPointRange<Float>, unit: String = "", onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, fontSize = 14.sp, color = Color.White)
            Text(text = "${value.toInt()}$unit", fontSize = 14.sp, color = Color.CyberpunkCyan, fontWeight = FontWeight.Bold)
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = valueRange, colors = SliderDefaults.colors(thumbColor = Color.CyberpunkPink, activeTrackColor = Color.CyberpunkPink, inactiveTrackColor = Color.Gray))
    }
}

@Composable
fun AnimationTypeChip(type: AnimationType, isSelected: Boolean, onClick: () -> Unit) {
    Surface(modifier = Modifier.padding(4.dp).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), color = if (isSelected) Color.CyberpunkPink else Color(0xFF2A2A2A)) {
        Text(text = type.name.replace("_", " "), fontSize = 12.sp, color = if (isSelected) Color.Black else Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@Composable
fun ColorPickerDialog(currentColor: Color, onColorSelected: (Color) -> Unit, onDismiss: () -> Unit) {
    var red by remember { mutableFloatStateOf(currentColor.red) }
    var green by remember { mutableFloatStateOf(currentColor.green) }
    var blue by remember { mutableFloatStateOf(currentColor.blue) }
    var alpha by remember { mutableFloatStateOf(currentColor.alpha) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF1A1A1A), modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Select Color", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.CyberpunkPink)
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(8.dp)).background(Color(red, green, blue, alpha)).border(2.dp, Color.CyberpunkCyan, RoundedCornerShape(8.dp)))
                Spacer(modifier = Modifier.height(24.dp))
                ColorSlider("Red", red) { red = it }
                ColorSlider("Green", green) { green = it }
                ColorSlider("Blue", blue) { blue = it }
                ColorSlider("Alpha", alpha) { alpha = it }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(onClick = { onColorSelected(Color(red, green, blue, alpha)) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.CyberpunkPink)) { Text("Select") }
                }
            }
        }
    }
}

@Composable
fun ColorSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, fontSize = 14.sp, color = Color.White)
            Text(text = "${(value * 255).toInt()}", fontSize = 14.sp, color = Color.CyberpunkCyan)
        }
        Slider(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), colors = SliderDefaults.colors(thumbColor = Color.CyberpunkPink, activeTrackColor = Color.CyberpunkCyan, inactiveTrackColor = Color.Gray))
    }
}
