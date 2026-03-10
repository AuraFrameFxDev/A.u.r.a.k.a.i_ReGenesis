package collabcanvas.util

import android.graphics.Path
import android.graphics.RectF
import androidx.compose.ui.geometry.Rect

/**
 * A wrapper around Android's RectF to work with Compose's Path
 */
class PathBounds : RectF() {
    fun set(path: Path) {
        path.computeBounds(this, true)
    }
}

/**
 * Extension function to get bounds of a Path
 */
fun Path.getBounds(): Rect {
    val bounds = PathBounds()
    bounds.set(this)
    return Rect(bounds.left, bounds.top, bounds.right, bounds.bottom)
}
