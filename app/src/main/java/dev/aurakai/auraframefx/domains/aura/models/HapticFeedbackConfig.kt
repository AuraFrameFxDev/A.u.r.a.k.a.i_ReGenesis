package dev.aurakai.auraframefx.domains.aura.models

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Configuration for haptic feedback in the application.
 * @property enabled Whether haptic feedback is enabled
 * @property intensity The intensity of the haptic feedback (0.0 - 1.0)
 * @property duration The duration of the haptic feedback in milliseconds
 * @property vibrationPattern Array of millisecond timings for vibration pattern (alternating on/off)
 * @property amplitude Array of vibration amplitudes corresponding to vibrationPattern (if supported)
 */
data class HapticFeedbackConfig(
    val enabled: Boolean = true,
    val intensity: Float = 0.7f,
    val duration: Int = 10,
    val vibrationPattern: LongArray = longArrayOf(0, 50, 100, 50),
    val amplitude: IntArray = intArrayOf(0, 150, 0, 150)
) {
    /**
     * @return A copy with the intensity clamped between 0.0 and 1.0
     */
    fun withClampedIntensity(): HapticFeedbackConfig {
        return copy(intensity = intensity.coerceIn(0f, 1f))
    }

    /**
     * @return A copy with haptic feedback disabled
     */
    fun disabled() = copy(enabled = false)

    /**
     * @return A copy with haptic feedback enabled
     */
    fun enabled() = copy(enabled = true)

    /**
     * @return The vibration pattern with intensity applied to amplitudes
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getScaledVibrationAmplitude(): IntArray {
        return amplitude.map { (it * intensity).toInt() }.toIntArray()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HapticFeedbackConfig

        if (enabled != other.enabled) return false
        if (intensity != other.intensity) return false
        if (duration != other.duration) return false
        if (!vibrationPattern.contentEquals(other.vibrationPattern)) return false
        if (!amplitude.contentEquals(other.amplitude)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + intensity.hashCode()
        result = 31 * result + duration
        result = 31 * result + vibrationPattern.contentHashCode()
        result = 31 * result + amplitude.contentHashCode()
        return result
    }

    companion object {
        /**
         * Default haptic feedback configuration
         */
        val Default = HapticFeedbackConfig()

        /**
         * Haptic feedback configuration with haptics disabled
         */
        val Disabled = HapticFeedbackConfig(enabled = false)

        /**
         * Creates a haptic feedback configuration with a simple click effect
         */
        fun createClickConfig(intensity: Float = 0.7f) = HapticFeedbackConfig(
            enabled = true,
            intensity = intensity,
            duration = 10,
            vibrationPattern = longArrayOf(0, 30),
            amplitude = intArrayOf(0, (255 * intensity).toInt())
        )

        /**
         * Creates a haptic feedback configuration with a double click effect
         */
        fun createDoubleClickConfig(intensity: Float = 0.8f) = HapticFeedbackConfig(
            enabled = true,
            intensity = intensity,
            duration = 10,
            vibrationPattern = longArrayOf(0, 20, 40, 20),
            amplitude = intArrayOf(0, 200, 0, 200)
        )
    }
}
