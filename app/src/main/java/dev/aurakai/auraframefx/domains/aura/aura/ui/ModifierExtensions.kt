package dev.aurakai.auraframefx.domains.aura.aura.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.cyberEdgeGlow(color: Color = Color.Cyan): Modifier = this.drawWithContent {
    drawContent()
    // Add glow effect
    drawRect(color.copy(alpha = 0.3f), blendMode = BlendMode.Lighten)
}

fun Modifier.digitalPixelEffect(intensity: Float = 0.5f): Modifier = this.drawWithContent {
    drawContent()
    // Add pixel/glitch effect - placeholder implementation
}

/**
 * A sleek, futuristic vertical scrollbar for LazyLists.
 * 
 * @param state The scroll state of the list.
 * @param color The color of the scroll thumb.
 */
fun Modifier.verticalScrollbar(
    state: LazyListState,
    color: Color = Color.Cyan
): Modifier = this.drawWithContent {
    drawContent()

    val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
    val needScrollbar = state.layoutInfo.totalItemsCount > state.layoutInfo.visibleItemsInfo.size

    if (needScrollbar && firstVisibleElementIndex != null) {
        val elementHeight = size.height / state.layoutInfo.totalItemsCount
        val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight
        val scrollbarOffsetY = firstVisibleElementIndex * elementHeight

        drawRoundRect(
            color = color.copy(alpha = 0.2f),
            topLeft = Offset(size.width - 4.dp.toPx(), 0f),
            size = Size(2.dp.toPx(), size.height),
            cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
        )

        drawRoundRect(
            color = color.copy(alpha = 0.8f),
            topLeft = Offset(size.width - 4.dp.toPx(), scrollbarOffsetY),
            size = Size(2.dp.toPx(), scrollbarHeight),
            cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
        )
    }
}
