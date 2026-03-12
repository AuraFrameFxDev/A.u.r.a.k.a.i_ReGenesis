package dev.aurakai.auraframefx.domains.aura

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OverlayManager - Manages floating agent overlay lifecycle
 *
 * Handles:
 * - Overlay permission checking
 * - Starting/stopping overlay service
 * - Permission request UI
 */
@Singleton
class OverlayManager @Inject constructor() {

    private var overlayActive = false

    /**
     * Check if overlay permission is granted
     */
    fun hasOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // Permission not required below API 23
        }
    }

    /**
     * Request overlay permission
     */
    fun requestOverlayPermission(activity: ComponentActivity, onResult: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${activity.packageName}")
                )

                // Register result handler
                val launcher = activity.registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val granted = Settings.canDrawOverlays(activity)
                    onResult(granted)
                    Timber.i("OverlayManager: Permission result: $granted")
                }

                launcher.launch(intent)
                Timber.i("OverlayManager: Requesting overlay permission")
            } else {
                onResult(true)
            }
        } else {
            onResult(true)
        }
    }

    /**
     * Start floating agent overlay
     * Now supports 4-agent pager: AURA, KAI, GENESIS, CLAUDE
     */
    fun startOverlay(context: Context) {
        if (!hasOverlayPermission(context)) {
            Timber.w("OverlayManager: Cannot start overlay - permission not granted")
            return
        }

        if (overlayActive) {
            Timber.d("OverlayManager: Overlay already active")
            return
        }

        val intent = Intent(context, FloatingAgentOverlay::class.java)
        context.startService(intent)
        overlayActive = true
        Timber.i("OverlayManager: Started 4-agent overlay pager (AURA, KAI, GENESIS, CLAUDE)")
    }

    /**
     * Stop floating agent overlay
     */
    fun stopOverlay(context: Context) {
        val intent = Intent(context, FloatingAgentOverlay::class.java)
        context.stopService(intent)
        overlayActive = false
        Timber.i("OverlayManager: Stopped overlay")
    }

    /**
     * Toggle overlay on/off
     */
    fun toggleOverlay(context: Context) {
        if (overlayActive) {
            stopOverlay(context)
        } else {
            startOverlay(context)
        }
    }

    /**
     * Check if overlay is currently active
     */
    fun isOverlayActive(): Boolean = overlayActive
}
