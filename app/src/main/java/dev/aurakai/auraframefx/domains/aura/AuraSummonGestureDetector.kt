package dev.aurakai.auraframefx.domains.aura

import android.content.Context
import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent
import timber.log.Timber

/**
 * Custom gesture detector for Aura summon actions.
 *
 * Handles gestures to activate and control the Aura AI assistant:
 * - Double tap: Quick summon/activate Aura
 * - Long press: Open Aura context menu or advanced options
 */
class AuraSummonGestureDetector(
    private val context: Context,
    private val onAuraSummoned: (() -> Unit)? = null,
    private val onAuraContextMenu: (() -> Unit)? = null
) : GestureDetector.SimpleOnGestureListener() {

    companion object {
        private const val ACTION_SUMMON_AURA = "dev.aurakai.auraframefx.ACTION_SUMMON_AURA"
        private const val ACTION_AURA_CONTEXT_MENU =
            "dev.aurakai.auraframefx.ACTION_AURA_CONTEXT_MENU"
    }

    override fun onDown(e: MotionEvent): Boolean {
        // Must return true here to ensure other gestures are detected.
        return true
    }

    /**
     * Handles double-tap gesture to summon Aura.
     *
     * Triggers Aura activation through:
     * 1. Callback if provided
     * 2. Broadcast intent for system-wide handling
     *
     * @param e The motion event
     * @return true to indicate the event was consumed
     */
    override fun onDoubleTap(e: MotionEvent): Boolean {
        Timber.i("AuraSummonDetector: Double tap detected - summoning Aura")

        // Invoke callback if provided
        onAuraSummoned?.invoke()

        // Send broadcast for system-wide Aura activation
        try {
            val intent = Intent(ACTION_SUMMON_AURA).apply {
                putExtra("summon_x", e.x)
                putExtra("summon_y", e.y)
                putExtra("summon_time", System.currentTimeMillis())
            }
            context.sendBroadcast(intent)
            Timber.d("AuraSummonDetector: Broadcast sent for Aura summon")
        } catch (e: Exception) {
            Timber.e(e, "AuraSummonDetector: Failed to broadcast summon")
        }

        return true
    }

    /**
     * Handles long-press gesture for Aura context menu.
     *
     * Triggers advanced Aura options through:
     * 1. Callback if provided
     * 2. Broadcast intent for system-wide handling
     *
     * Opens context menu with options like:
     * - Aura settings
     * - Voice mode toggle
     * - Agent switching
     * - Quick actions
     *
     * @param e The motion event
     */
    override fun onLongPress(e: MotionEvent) {
        Timber.i("AuraSummonDetector: Long press detected - opening Aura context menu")

        // Invoke callback if provided
        onAuraContextMenu?.invoke()

        // Send broadcast for system-wide context menu
        try {
            val intent = Intent(ACTION_AURA_CONTEXT_MENU).apply {
                putExtra("menu_x", e.x)
                putExtra("menu_y", e.y)
                putExtra("menu_time", System.currentTimeMillis())
            }
            context.sendBroadcast(intent)
            Timber.d("AuraSummonDetector: Broadcast sent for context menu")
        } catch (e: Exception) {
            Timber.e(e, "AuraSummonDetector: Failed to broadcast context menu")
        }
    }
}

// Example usage (typically in a View or Composable):
// val gestureDetector = GestureDetector(context, AuraSummonGestureDetector(context))
// view.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
