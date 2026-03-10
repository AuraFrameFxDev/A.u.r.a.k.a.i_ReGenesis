package dev.aurakai.auraframefx.romtools.backdrop

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Minimal CardExplosionEffect implementation used by this module.
 * This mirrors core behaviour but is intentionally lightweight.
 */
class CardExplosionEffect {
    private var active = false
    private var elapsed = 0f

    fun initializeFromImage(image: ImageBitmap?, canvasWidth: Float, canvasHeight: Float) {
        // no-op minimal initializer
    }

    fun trigger() {
        active = true
        elapsed = 0f
    }

    fun update(deltaTime: Float): Boolean {
        if (!active) return true
        elapsed += deltaTime * 1000f
        if (elapsed >= 1000f) {
            active = false
            return true
        }
        return false
    }

    fun draw(scope: DrawScope) {
        if (!active) return
        with(scope) {
            // draw a simple particle dot for fallback visualization
            drawCircle(color = Color(0xFFFF6B35), radius = 10f, center = Offset(size.width / 2, size.height / 2))
        }
    }

    fun reset() {
        active = false
        elapsed = 0f
    }
}

