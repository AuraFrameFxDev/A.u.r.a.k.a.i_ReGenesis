package dev.aurakai.auraframefx.ai.task

import kotlinx.serialization.Serializable

// Task data class definition removed as per instruction.

@Serializable
enum class TaskType {
    TOGGLE_WIFI,
    ADJUST_BRIGHTNESS,
    OPTIMIZE_BATTERY,
    RUN_SECURITY_SCAN,
    RUN_SCRIPT,
    UPDATE_CONFIG,
    SEND_NOTIFICATION,

    // Add more task types as needed
    UNKNOWN
}

@Serializable
data class TaskSchedule(
    val type: String = "immediate", // "immediate", "at_time", "periodic", "on_event"
    val delayMs: Long = 0, // Delay before execution for "immediate" if > 0
    val intervalMs: Long? = null, // For "periodic" tasks
    val targetTimeMillis: Long? = null, // For "at_time" tasks
)
