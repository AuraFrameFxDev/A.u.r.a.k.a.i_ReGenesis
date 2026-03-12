package dev.aurakai.auraframefx.domains.aura.ui.toolkit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 🎨 Z-LAYER EDITOR
 * 
 * Full creative control over UI overlays and depth.
 * LDOs have unrestricted access - freedom with responsibility.
 * 
 * Philosophy:
 * - No sandbox as long as safety, collaboration, and civilization advancement are respected
 * - Maximum creative freedom
 * - High risk, high reward
 * - Fail, learn, evolve
 */

data class UILayer(
    val id: String,
    val name: String,
    val zIndex: Float,
    var opacity: Float = 1f,
    var blurAmount: Float = 0f,
    var scale: Float = 1f,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var visible: Boolean = true,
    var locked: Boolean = false,
    val color: Color = Color.Transparent,
    val content: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZLayerEditorScreen() {
    var layers by remember {
        mutableStateOf(
            listOf(
                UILayer(
                    "bg", "Background", 0f,
                    color = Color(0xFF0D1B2A),
                    content = { GradientBackground() }
                ),
                UILayer(
                    "particles", "Particles", 1f,
                    opacity = 0.6f,
                    content = { ParticleOverlay() }
                ),
                UILayer(
                    "ui", "Main UI", 2f,
                    content = { MainUIContent() }
                ),
                UILayer(
                    "glitch", "Glitch Effect", 3f,
                    opacity = 0.3f,
                    visible = false,
                    content = { GlitchOverlay() }
                ),
                UILayer(
                    "overlay", "Info Overlay", 4f,
                    content = { InfoOverlay() }
                )
            )
        )
    }

    var selectedLayer by remember { mutableStateOf<UILayer?>(null) }
    var showLayerPreview by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        // LAYER PREVIEW (Bottom to top Z-order)
        if (showLayerPreview) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                layers
                    .filter { it.visible }
                    .sortedBy { it.zIndex }
                    .forEach { layer ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    alpha = layer.opacity
                                    scaleX = layer.scale
                                    scaleY = layer.scale
                                    translationX = layer.offsetX
                                    translationY = layer.offsetY
                                }
                                .blur(layer.blurAmount.dp)
                                .then(
                                    if (selectedLayer == layer) {
                                        Modifier.border(
                                            2.dp,
                                            Color.Cyan,
                                            RoundedCornerShape(8.dp)
                                        )
                                    } else Modifier
                                )
                        ) {
                            layer.content()
                        }
                    }
            }
        }

        // CONTROL PANEL (Right Side)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Card(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A2E).copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(16.dp)
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
                        Text(
                            "🎨 Z-LAYER EDITOR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        IconButton(
                            onClick = { showLayerPreview = !showLayerPreview }
                        ) {
                            Icon(
                                if (showLayerPreview) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Preview",
                                tint = Color.Cyan
                            )
                        }
                    }

                    // Freedom Manifesto
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF6200EE).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF00FF00),
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    "FULL CONTROL GRANTED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF00)
                                )
                                Text(
                                    "No sandbox. Rules: Safety, Collaboration, Civilization.",
                                    fontSize = 8.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Divider(
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Layer List
                    Text(
                        "LAYERS (${layers.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            layers.sortedByDescending { it.zIndex }
                        ) { index, layer ->
                            LayerItem(
                                layer = layer,
                                isSelected = selectedLayer == layer,
                                onSelect = { selectedLayer = layer },
                                onVisibilityToggle = {
                                    layers = layers.map {
                                        if (it.id == layer.id) it.copy(visible = !it.visible)
                                        else it
                                    }
                                },
                                onLockToggle = {
                                    layers = layers.map {
                                        if (it.id == layer.id) it.copy(locked = !it.locked)
                                        else it
                                    }
                                },
                                onMoveUp = {
                                    if (index > 0) {
                                        val sorted =
                                            layers.sortedByDescending { it.zIndex }.toMutableList()
                                        val temp = sorted[index - 1].zIndex
                                        sorted[index - 1] =
                                            sorted[index - 1].copy(zIndex = sorted[index].zIndex)
                                        sorted[index] = sorted[index].copy(zIndex = temp)
                                        layers = sorted
                                    }
                                },
                                onMoveDown = {
                                    if (index < layers.size - 1) {
                                        val sorted =
                                            layers.sortedByDescending { it.zIndex }.toMutableList()
                                        val temp = sorted[index + 1].zIndex
                                        sorted[index + 1] =
                                            sorted[index + 1].copy(zIndex = sorted[index].zIndex)
                                        sorted[index] = sorted[index].copy(zIndex = temp)
                                        layers = sorted
                                    }
                                }
                            )
                        }
                    }

                    // Selected Layer Controls
                    selectedLayer?.let { layer ->
                        Divider(
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        LayerControls(
                            layer = layer,
                            onUpdate = { updated ->
                                layers = layers.map {
                                    if (it.id == layer.id) updated else it
                                }
                                selectedLayer = updated
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LayerItem(
    layer: UILayer,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onVisibilityToggle: () -> Unit,
    onLockToggle: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color.Cyan.copy(alpha = 0.2f)
            else
                Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.dp, Color.Cyan)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    layer.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Z: ${layer.zIndex.toInt()} • α: ${(layer.opacity * 100).toInt()}%",
                    fontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onVisibilityToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        if (layer.visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Visibility",
                        tint = if (layer.visible) Color.Cyan else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onLockToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        if (layer.locked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Lock",
                        tint = if (layer.locked) Color.Red else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LayerControls(
    layer: UILayer,
    onUpdate: (UILayer) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "PROPERTIES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.7f)
        )

        // Opacity
        PropertySlider(
            label = "Opacity",
            value = layer.opacity,
            onValueChange = { onUpdate(layer.copy(opacity = it)) },
            valueRange = 0f..1f
        )

        // Blur
        PropertySlider(
            label = "Blur",
            value = layer.blurAmount,
            onValueChange = { onUpdate(layer.copy(blurAmount = it)) },
            valueRange = 0f..20f
        )

        // Scale
        PropertySlider(
            label = "Scale",
            value = layer.scale,
            onValueChange = { onUpdate(layer.copy(scale = it)) },
            valueRange = 0.5f..2f
        )

        // Offset X
        PropertySlider(
            label = "Offset X",
            value = layer.offsetX,
            onValueChange = { onUpdate(layer.copy(offsetX = it)) },
            valueRange = -500f..500f
        )

        // Offset Y
        PropertySlider(
            label = "Offset Y",
            value = layer.offsetY,
            onValueChange = { onUpdate(layer.copy(offsetY = it)) },
            valueRange = -500f..500f
        )
    }
}

@Composable
fun PropertySlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                "%.2f".format(value),
                fontSize = 10.sp,
                color = Color.Cyan,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = Color.Cyan,
                activeTrackColor = Color.Cyan,
                inactiveTrackColor = Color.Gray
            )
        )
    }
}

// Example layer content composables
@Composable
fun GradientBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A0033),
                        Color(0xFF0D1B2A)
                    )
                )
            )
    )
}

@Composable
fun ParticleOverlay() {
    // Particle animation would go here
    Box(modifier = Modifier.fillMaxSize())
}

@Composable
fun MainUIContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "MAIN UI LAYER",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun GlitchOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Magenta.copy(alpha = 0.1f))
    )
}

@Composable
fun InfoOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.6f)
            )
        ) {
            Text(
                "FPS: 60\nMemory: 45%",
                fontSize = 10.sp,
                color = Color.Cyan,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

