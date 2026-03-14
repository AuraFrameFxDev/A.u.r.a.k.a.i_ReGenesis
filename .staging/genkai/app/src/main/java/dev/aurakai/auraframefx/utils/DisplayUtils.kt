package dev.aurakai.auraframefx.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager

/**
 * Utility object for display-related functions.
 */
object DisplayUtils {

    /**
     * Gets the display metrics of the current device.
     *
     * @param context The application context.
     * @return DisplayMetrics object containing screen information.
     */
    @Suppress("DEPRECATION")
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = DisplayMetrics()
            windowManager.currentWindowMetrics.bounds.let {
                metrics.widthPixels = it.width()
                metrics.heightPixels = it.height()
            }
            metrics
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics
        }
    }

    /**
     * Gets the status bar height.
     *
     * @param context The application context.
     * @return The height of the status bar in pixels, or 0 if it cannot be determined.
     */
    fun getStatusBarHeight(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val windowInsets = windowManager.currentWindowMetrics.windowInsets
            windowInsets.getInsets(WindowInsets.Type.statusBars())?.top ?: 0
        } else {
            var result = 0
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            result
        }
    }

    /**
     * Converts density-independent pixels (dp) to pixels (px).
     *
     * @param dp The value in dp.
     * @param context The application context.
     * @return The value in pixels.
     */
    fun dpToPx(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    /**
     * Converts pixels (px) to density-independent pixels (dp).
     *
     * @param px The value in pixels.
     * @param context The application context.
     * @return The value in dp.
     */
    fun pxToDp(px: Float, context: Context): Float {
        return px / context.resources.displayMetrics.density
    }
}
