package dev.aurakai.auraframefx.utils

import androidx.compose.ui.unit.dp

/**
 * Application-wide constants for AuraFrameFx
 */
object AppConstants {
    // Animation constants
    const val ROTATION_DELAY_MILLIS = 16L // ~60 FPS
    const val TASK_PROCESSING_DELAY_MILLIS = 5000L // 5 seconds

    // UI dimension constants
    val NODE_RADIUS_DP = 24.dp
    val HALO_RADIUS_OFFSET_DP = 32.dp
    val AGENT_NODE_RADIUS_OFFSET_DP = 64.dp
    val AGENT_TOUCH_AREA_RADIUS_DP = 24.dp
    val STATUS_OFFSET_X_DP = 30.dp
    val STATUS_OFFSET_Y_DP = -10.dp

    // Status constants
    const val STATUS_IDLE = "idle"
    const val STATUS_PROCESSING = "processing"
    const val STATUS_ERROR = "error"
}
