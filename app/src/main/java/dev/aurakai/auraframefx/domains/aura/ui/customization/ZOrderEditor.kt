package dev.aurakai.auraframefx.domains.aura.ui.customization

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkCyan
import kotlin.math.roundToInt

/**
 * 🎨 Z-Order Editor - Visual Layer Management
 *
 * Drag & drop interface to reorder UI elements by z-index.
 * Front to back layer ordering with visual preview.
 *
 * This makes layer management TANGIBLE - you see exactly what's on top of what.
 *
 * Example:
 * ```
 * ZOrderEditor(
 *     elements = listOf(
 *         UIElement("Status Bar", zIndex = 100),
 *         UIElement("Wallpaper", zIndex = 0),
 *         UIElement("Aura", zIndex = 50)
 *     ),
 *     onReorder = { reorderedElements ->
 *         customizationEngine.applyLayerOrder(reorderedElements)
 *     }
 * )
 * ```
 */

/**
 * UI Element for z-order management
 */
data class UIElement(
    val id: String,
    val name: String,
    val type: ComponentType,
    val zIndex: Int,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val previewColor: Color = CyberpunkPink
)

/**
 * Z-Order Editor Panel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZOrderEditor(
    elements: List<UIElement>,
    onReorder: (List<UIElement>) -> Unit,
    onElementSelected: (UIElement) -> Unit = {},
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var editingElements by remember { mutableStateOf(elements.sortedByDescending { it.zIndex }) }
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var targetIndex by remember { mutableStateOf<Int?>(null) }

    // Auto-update when external elements change
    LaunchedEffect(elements) {
        editingElements = elements.sortedByDescending { it.zIndex }
    }

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
                        text = "Layer Order",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberpunkPink
                    )
                    Text(
                        text = "${editingElements.size} elements",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row {
                    // Auto-arrange button
                    IconButton(
                        onClick = {
                            // Auto-arrange by type priority
                            editingElements = autoArrangeElements(editingElements)
                            onReorder(editingElements)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = "Auto Arrange",
                            tint = CyberpunkCyan
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

            // Instructions
            Text(
                text = "Drag elements to reorder. Top = Front, Bottom = Back",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Layer list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = editingElements,
                    key = { _, element -> element.id }
                ) { index, element ->
                    LayerCard(
                        element = element,
                        index = index,
                        totalLayers = editingElements.size,
                        isDragging = draggingIndex == index,
                        isTarget = targetIndex == index,
                        onDragStart = {
                            if (!element.isLocked) {
                                draggingIndex = index
                            }
                        },
                        onDragEnd = {
                            if (draggingIndex != null && targetIndex != null && draggingIndex != targetIndex) {
                                // Perform reorder
                                val newList = editingElements.toMutableList()
                                val draggedElement = newList.removeAt(draggingIndex!!)
                                newList.add(targetIndex!!, draggedElement)

                                // Recalculate z-indices
                                val reindexed = newList.mapIndexed { idx, elem ->
                                    elem.copy(zIndex = newList.size - idx)
                                }

                                editingElements = reindexed
                                onReorder(reindexed)
                            }

                            draggingIndex = null
                            targetIndex = null
                        },
                        onHoverOver = {
                            targetIndex = index
                        },
                        onClick = {
                            onElementSelected(element)
                        },
                        onVisibilityToggle = {
                            val updated = editingElements.toMutableList()
                            updated[index] = element.copy(isVisible = !element.isVisible)
                            editingElements = updated
                            onReorder(editingElements)
                        },
                        onLockToggle = {
                            val updated = editingElements.toMutableList()
                            updated[index] = element.copy(isLocked = !element.isLocked)
                            editingElements = updated
                            onReorder(editingElements)
                        }
                    )
                }
            }

            // Z-Index Legend
            Spacer(modifier = Modifier.height(16.dp))
            ZIndexLegend(elements = editingElements)
        }
    }
}

/**
 * Individual layer card (draggable)
 */
@Composable
fun LayerCard(
    element: UIElement,
    index: Int,
    totalLayers: Int,
    isDragging: Boolean,
    isTarget: Boolean,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onHoverOver: () -> Unit,
    onClick: () -> Unit,
    onVisibilityToggle: () -> Unit,
    onLockToggle: () -> Unit
) {
    var dragOffset by remember { mutableStateOf(IntOffset.Zero) }

    // Animation
    val elevation by animateDpAsState(
        targetValue = if (isDragging) 16.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "elevation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .offset { if (isDragging) dragOffset else IntOffset.Zero }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isTarget && isDragging) {
                    CyberpunkCyan.copy(alpha = 0.2f)
                } else {
                    Color(0xFF2A2A2A)
                }
            )
            .border(
                width = 2.dp,
                color = when {
                    isDragging -> CyberpunkPink
                    isTarget -> CyberpunkCyan
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(12.dp)
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset = IntOffset(
                            dragOffset.x + dragAmount.x.roundToInt(),
                            dragOffset.y + dragAmount.y.roundToInt()
                        )
                        onHoverOver()
                    },
                    onDragEnd = {
                        dragOffset = IntOffset.Zero
                        onDragEnd()
                    }
                )
            },
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drag handle
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "Drag",
                tint = if (element.isLocked) Color.Gray else CyberpunkPink,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Preview color bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(element.previewColor.copy(alpha = if (element.isVisible) 1f else 0.3f))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Element info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = element.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (element.isVisible) Color.White else Color.Gray
                )
                Text(
                    text = "Z-Index: ${element.zIndex} • ${element.type.name.replace("_", " ")}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Visibility toggle
                IconButton(
                    onClick = onVisibilityToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (element.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Visibility",
                        tint = if (element.isVisible) CyberpunkCyan else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Lock toggle
                IconButton(
                    onClick = onLockToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (element.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Toggle Lock",
                        tint = if (element.isLocked) CyberpunkPink else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Z-Index range legend
 */
@Composable
fun ZIndexLegend(elements: List<UIElement>) {
    val maxZIndex = elements.maxOfOrNull { it.zIndex } ?: 0
    val minZIndex = elements.minOfOrNull { it.zIndex } ?: 0

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF2A2A2A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FRONT",
                    fontSize = 10.sp,
                    color = CyberpunkPink,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Z: $maxZIndex",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }

            // Visual gradient bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                CyberpunkPink.copy(alpha = 0.8f),
                                CyberpunkCyan.copy(alpha = 0.8f),
                                Color.Gray.copy(alpha = 0.5f)
                            )
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "BACK",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Z: $minZIndex",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Auto-arrange elements by type priority
 */
fun autoArrangeElements(elements: List<UIElement>): List<UIElement> {
    val priorityOrder = mapOf(
        ComponentType.STATUS_BAR to 100,
        ComponentType.NAVIGATION_BAR to 90,
        ComponentType.NOTIFICATION_PANEL to 80,
        ComponentType.QUICK_SETTINGS to 70,
        ComponentType.WIDGET to 50,
        ComponentType.APP_ICON to 40,
        ComponentType.BUTTON to 30,
        ComponentType.TEXT_LABEL to 20,
        ComponentType.IMAGE to 10,
        ComponentType.CUSTOM to 5
    )

    return elements
        .sortedByDescending { priorityOrder[it.type] ?: 0 }
        .mapIndexed { index, element ->
            element.copy(zIndex = elements.size - index)
        }
}

/**
 * Preview: Show how elements stack visually
 */
@Composable
fun ZOrderPreview(
    elements: List<UIElement>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
    ) {
        elements
            .sortedBy { it.zIndex }
            .forEach { element ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = if (element.isVisible) 0.8f else 0.2f
                            translationX = (element.zIndex * 5).toFloat()
                            translationY = (element.zIndex * 5).toFloat()
                        }
                        .background(element.previewColor)
                ) {
                    Text(
                        text = element.name,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
    }
}

