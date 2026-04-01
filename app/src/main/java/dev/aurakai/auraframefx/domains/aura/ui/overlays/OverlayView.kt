package dev.aurakai.auraframefx.domains.aura.ui.overlays

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/**
 * 🖼️ OVERLAY VIEW
 *
 * Provides a draggable container for widgets and UI components that float
 * above the system UI.
 */
@Composable
fun DraggableOverlay(
    initialOffset: IntOffset = IntOffset(0, 0),
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(initialOffset.x.toFloat()) }
    var offsetY by remember { mutableStateOf(initialOffset.y.toFloat()) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        content()
    }
}
