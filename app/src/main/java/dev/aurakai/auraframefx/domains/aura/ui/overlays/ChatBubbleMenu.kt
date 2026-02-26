package dev.aurakai.auraframefx.domains.aura.ui.overlays

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * ChatBubbleMenu
 * Draggable floating bubble that expands into quick chat/voice controls.
 */
@Composable
fun ChatBubbleMenu(
    modifier: Modifier = Modifier,
    onOpenChat: () -> Unit = {},
    onToggleVoice: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val offsetXDp by remember(offsetX) { mutableStateOf(with(density) { offsetX.toDp() }) }
    val offsetYDp by remember(offsetY) { mutableStateOf(with(density) { offsetY.toDp() }) }
    Box(
        modifier = modifier
            .offset(x = offsetXDp, y = offsetYDp)
            .size(if (expanded) 160.dp else 56.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00FFFF), Color(0xFF001A1A))
                )
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .pointerInput(expanded) {
                detectDragGestures(onDragStart = { expanded = !expanded }) { _, _ -> }
            }
    ) {
        if (!expanded) {
            Text(
                text = "Chat",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Agent Chat", color = Color.Cyan)
                Button(onClick = onOpenChat, modifier = Modifier.padding(top = 4.dp)) {
                    Text("Open")
                }
                Button(onClick = onToggleVoice, modifier = Modifier.padding(top = 4.dp)) {
                    Text("Voice")
                }
            }
        }
    }
}

