package dev.aurakai.auraframefx.domains.aura.screens.themes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.picker.ThemeColors
import dev.aurakai.auraframefx.domains.aura.ui.theme.picker.ThemeEditor
import dev.aurakai.auraframefx.domains.aura.ui.theme.picker.ColorBlendrPicker
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberGlow
import dev.aurakai.auraframefx.domains.aura.ui.overlays.OverlayPrefs
import kotlinx.coroutines.launch
import kotlin.collections.forEachIndexed
import kotlin.collections.toMutableList


/**
 * Theme Engine Screen
 * Integrates ChromaCore and ThemeEditor for full system customization
 */
@Composable
fun ThemeEngineScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedColor by remember { mutableStateOf(Color.Magenta) }
    val scrollState = rememberScrollState()

    // Persisted states from DataStore
    val persistedEnabled by OverlayPrefs.enabledFlow(context).collectAsState(initial = true)
    val persistedStyle by OverlayPrefs.transitionStyleFlow(context).collectAsState(initial = "fade")
    val persistedSpeed by OverlayPrefs.transitionSpeedFlow(context).collectAsState(initial = 3)
    val persistedOrder by OverlayPrefs.orderFlow(context)
        .collectAsState(initial = listOf("StatusBar", "NavBar", "Widget"))

    // Remove LocalOverlaySettings access that causes crash
    var overlaysEnabled by remember { mutableStateOf(true) }

    // Overlay settings state
    data class OverlaySettings(
        var transitionSpeed: Int = 3,
        var overlayZOrder: List<String> = listOf()
    )

    var overlaySettings by remember { mutableStateOf(OverlaySettings()) }
    var editMode by remember { mutableStateOf(false) }
    val scaleAnim = remember { Animatable(1f) }
    val wigglePhase = remember { Animatable(0f) }

    var transitionStyle by remember { mutableStateOf("fade") }

    // Synchronize local state with persisted state
    LaunchedEffect(persistedEnabled) { overlaysEnabled = persistedEnabled }
    LaunchedEffect(persistedStyle) { transitionStyle = persistedStyle }
    LaunchedEffect(persistedSpeed) {
        overlaySettings = overlaySettings.copy(transitionSpeed = persistedSpeed)
    }
    LaunchedEffect(persistedOrder) {
        overlaySettings = overlaySettings.copy(overlayZOrder = persistedOrder)
    }

    LaunchedEffect(editMode) {
        if (editMode) {
            scaleAnim.animateTo(0.92f, animationSpec = tween(200))
            // loop wiggle
            while (true) {
                wigglePhase.animateTo(1f, animationSpec = tween(600))
                wigglePhase.snapTo(0f)
            }
        } else {
            scaleAnim.animateTo(1f, animationSpec = tween(200))
            wigglePhase.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberGlow.verticalGradient)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "THEME ENGINE",
                        style = MaterialTheme.typography.headlineSmall,
                        color = CyberGlow.Electric,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Advanced Color & Style Control",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Color Picker Section
            Text(
                text = "Primary Color",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = CyberGlow.cardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ColorBlendrPicker(
                        initialColor = selectedColor,
                        onColorChanged = { selectedColor = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Theme Editor Section
            Text(
                text = "System Theme",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Embedding ThemeEditor with CyberGlow colors
            ThemeEditor(
                initialColors = ThemeColors(
                    primary = CyberGlow.Electric,
                    secondary = CyberGlow.Neon,
                    background = CyberGlow.DeepPurple,
                    surface = CyberGlow.cardBackground
                ),
                onColorsChanged = { /* Handle theme updates */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Overlay Managers & Z-Order
            Text(
                text = "Overlay Managers",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { editMode = true },
                            onTap = { if (editMode) editMode = false }
                        )
                    }
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = CyberGlow.cardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = scaleAnim.value
                        scaleY = scaleAnim.value
                    }) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            val newValue = !overlaysEnabled
                            overlaysEnabled = newValue
                            coroutineScope.launch {
                                OverlayPrefs.saveEnabled(context, newValue)
                            }
                        }) {
                            Icon(
                                imageVector = if (overlaysEnabled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (overlaysEnabled) "Disable overlays" else "Enable overlays",
                                tint = if (overlaysEnabled) Color(0xFF00FF41) else Color(0xFFFF4500)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (overlaysEnabled) "Overlays Enabled" else "Overlays Disabled",
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (editMode) {
                            Button(
                                onClick = { editMode = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberGlow.Neon,
                                    contentColor = Color.White
                                )
                            ) { Text("Done") }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Transition Style",
                        color = CyberGlow.Electric,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("lens", "fade", "swipe_left", "swipe_right").forEach { style ->
                            FilterChip(
                                selected = transitionStyle == style,
                                onClick = {
                                    transitionStyle = style
                                    coroutineScope.launch {
                                        OverlayPrefs.saveTransitionStyle(context, style)
                                    }
                                },
                                label = { Text(style.replace("_", " ").uppercase()) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberGlow.Neon,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Transition Speed",
                        color = CyberGlow.Electric,
                        style = MaterialTheme.typography.titleSmall
                    )
                    val transitionSpeed = overlaySettings.transitionSpeed.toFloat()
                    Slider(
                        value = transitionSpeed,
                        onValueChange = {
                            overlaySettings = overlaySettings.copy(transitionSpeed = it.toInt())
                        },
                        onValueChangeFinished = {
                            coroutineScope.launch {
                                OverlayPrefs.saveTransitionSpeed(
                                    context,
                                    overlaySettings.transitionSpeed
                                )
                            }
                        },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberGlow.Electric,
                            activeTrackColor = CyberGlow.Neon
                        )
                    )
                    Text(
                        "Speed: ${transitionSpeed.toInt()}",
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Z‑Order (Top → Bottom)", color = Color.White.copy(alpha = 0.8f))
                    val overlayZOrder = overlaySettings.overlayZOrder
                    overlayZOrder.forEachIndexed { index: Int, name: String ->
                        val phase = wigglePhase.value
                        val rotation = if (editMode) (kotlin.math.sin(phase * 2f * Math.PI)
                            .toFloat() * 2.0f) else 0f
                        val translateY =
                            if (editMode) (kotlin.math.sin((phase + index * 0.1f) * 2f * Math.PI)
                                .toFloat() * 2f) else 0f
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .graphicsLayer {
                                    rotationZ = rotation
                                    translationY = translateY
                                }
                                .pointerInput(index, overlayZOrder, editMode) {
                                    if (editMode) {
                                        detectDragGestures(
                                            onDragEnd = {
                                                coroutineScope.launch {
                                                    OverlayPrefs.saveOrder(
                                                        context,
                                                        overlaySettings.overlayZOrder
                                                    )
                                                }
                                            }
                                        ) { _: PointerInputChange, dragAmount: Offset ->
                                            // Calculate target index based on drag direction
                                            val targetIndex = when {
                                                dragAmount.y < -4f -> (index - 1).coerceAtLeast(0)
                                                dragAmount.y > 4f -> (index + 1).coerceAtMost(
                                                    overlayZOrder.size - 1
                                                )

                                                else -> index
                                            }
                                            if (targetIndex != index) {
                                                val m: MutableList<String> =
                                                    overlayZOrder.toMutableList()
                                                val item: String = m.removeAt(index)
                                                m.add(targetIndex, item)
                                                overlaySettings =
                                                    overlaySettings.copy(overlayZOrder = m)
                                            }
                                        }
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${index + 1}. $name", color = Color.White)
                            Spacer(modifier = Modifier.weight(1f))
                            if (!editMode) {
                                OutlinedButton(onClick = {
                                    if (index > 0) {
                                        val m: MutableList<String> = overlayZOrder.toMutableList()
                                        val tmp: String = m[index - 1]; m[index - 1] =
                                            m[index]; m[index] = tmp
                                        overlaySettings = overlaySettings.copy(overlayZOrder = m)
                                        coroutineScope.launch {
                                            OverlayPrefs.saveOrder(context, m)
                                        }
                                    }
                                }) { Text("Up") }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(onClick = {
                                    if (index < overlayZOrder.size - 1) {
                                        val m: MutableList<String> = overlayZOrder.toMutableList()
                                        val tmp: String = m[index + 1]; m[index + 1] =
                                            m[index]; m[index] = tmp
                                        overlaySettings = overlaySettings.copy(overlayZOrder = m)
                                        coroutineScope.launch {
                                            OverlayPrefs.saveOrder(context, m)
                                        }
                                    }
                                }) { Text("Down") }
                            } else {
                                AssistChip(
                                    onClick = { /* no-op in edit mode */ },
                                    label = { Text("Drag") })
                            }
                        }
                    }
                    Text(
                        text = "Note: Changes apply immediately to overlay layering.",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
        }
    }
}


