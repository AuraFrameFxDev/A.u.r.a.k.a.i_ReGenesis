package dev.aurakai.auraframefx.domains.aura.ui

import android.graphics.PointF
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Shape definitions for AuraFrameFX
 * Using Material3 shapes with cyberpunk styling - more angular for a futuristic look
 */
val AppShapes = Shapes(
    // Small components like chips, small buttons
    small = RoundedCornerShape(8.dp),

    // Medium components like cards, dialogs
    medium = RoundedCornerShape(12.dp),

    // Large components like bottom sheets, side sheets
    large = RoundedCornerShape(16.dp)
)

// Additional custom shapes for specific components
val ChatBubbleIncomingShape = RoundedCornerShape(
    topStart = 4.dp,
    topEnd = 16.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)

val ChatBubbleOutgoingShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 4.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)

val ButtonShape = RoundedCornerShape(12.dp)
val CardShape = RoundedCornerShape(16.dp)
val InputFieldShape = RoundedCornerShape(12.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val FloatingActionButtonShape = RoundedCornerShape(16.dp)

/**
 * Cyberpunk themed custom shapes for the AuraFrameFx UI
 * Based on the reference designs with hexagonal and angled corners
 */
object CyberpunkShapes {
    // Hexagonal window shape with sharp corners for menus
    val hexWindowShape = object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Outline {
            val path = Path().apply {
                // Start from the top middle
                val cornerSize = size.width.coerceAtMost(size.height) * 0.1f

                // Top-left corner cutout
                moveTo(0f, cornerSize)
                lineTo(cornerSize, 0f)

                // Top edge
                lineTo(size.width - cornerSize, 0f)

                // Top-right corner cutout
                lineTo(size.width, cornerSize)

                // Right edge
                lineTo(size.width, size.height - cornerSize)

                // Bottom-right corner cutout
                lineTo(size.width - cornerSize, size.height)

                // Bottom edge
                lineTo(cornerSize, size.height)

                // Bottom-left corner cutout
                lineTo(0f, size.height - cornerSize)

                close()
            }
            return Outline.Generic(path)
        }
    }

    // Angled window shape with diagonal corners like in image reference 1
    val angledWindowShape = object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Outline {
            val cornerSize = size.height * 0.08f

            val path = Path().apply {
                // Top-left corner 
                moveTo(0f, cornerSize)
                lineTo(cornerSize, 0f)

                // Top edge
                lineTo(size.width - cornerSize, 0f)

                // Top-right corner
                lineTo(size.width, cornerSize)

                // Right edge
                lineTo(size.width, size.height - cornerSize)

                // Bottom-right corner
                lineTo(size.width - cornerSize, size.height)

                // Bottom edge
                lineTo(cornerSize, size.height)

                // Bottom-left corner
                lineTo(0f, size.height - cornerSize)

                close()
            }
            return Outline.Generic(path)
        }
    }

    // Angled button shape like in image reference 1 and 3
    val angledButtonShape = object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Outline {
            val cornerSize = size.height * 0.3f

            val path = Path().apply {
                // Start from left side, below the top-left angle
                moveTo(0f, cornerSize)

                // Draw angle at top-left
                lineTo(cornerSize, 0f)

                // Draw top edge
                lineTo(size.width, 0f)

                // Draw right edge
                lineTo(size.width, size.height)

                // Draw bottom edge
                lineTo(cornerSize, size.height)

                // Draw angle at bottom-left
                lineTo(0f, size.height - cornerSize)

                close()
            }
            return Outline.Generic(path)
        }
    }

    // Hexagon shape for buttons and icons
    val hexagonShape = object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Outline {
            val radius = size.minDimension / 2
            val center = PointF(size.width / 2, size.height / 2)

            val path = Path().apply {
                for (i in 0..5) {
                    val angle = (PI / 3.0 * i - PI / 2).toFloat()
                    val x = center.x + radius * cos(angle)
                    val y = center.y + radius * sin(angle)

                    if (i == 0) {
                        moveTo(x, y)
                    } else {
                        lineTo(x, y)
                    }
                }
                close()
            }
            return Outline.Generic(path)
        }
    }
}

