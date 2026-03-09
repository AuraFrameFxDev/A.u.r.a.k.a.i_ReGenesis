package dev.aurakai.auraframefx.domains.aura.ui.recovery

import kotlinx.serialization.Serializable

/**
 * Snapshot of UI state for recovery purposes
 *
 * Captures enough information to restore the UI to a known good state.
 *
 * @property screenRoute Current navigation route (e.g., "HOME", "AGENT_NEXUS")
 * @property timestamp When this snapshot was taken
 * @property navigationStack Complete navigation stack for restoration
 * @property stateData Additional state data (JSON serialized)
 */
@Serializable
data class UIStateSnapshot(
    val screenRoute: String,
    val timestamp: Long = System.currentTimeMillis(),
    val navigationStack: List<String> = emptyList(),
    val stateData: Map<String, String> = emptyMap()
) {
    companion object {
        /**
         * Create a default snapshot (HOME screen)
         */
        fun default(): UIStateSnapshot {
            return UIStateSnapshot(
                screenRoute = "HOME",
                timestamp = System.currentTimeMillis(),
                navigationStack = listOf("HOME")
            )
        }

        /**
         * Create a snapshot for a specific screen
         */
        fun forScreen(
            route: String,
            additionalData: Map<String, String> = emptyMap()
        ): UIStateSnapshot {
            return UIStateSnapshot(
                screenRoute = route,
                navigationStack = listOf(route),
                stateData = additionalData
            )
        }
    }

    /**
     * Check if snapshot is recent (< 5 minutes old)
     */
    fun isRecent(): Boolean {
        val fiveMinutesMs = 5 * 60 * 1000
        return (System.currentTimeMillis() - timestamp) < fiveMinutesMs
    }

    /**
     * Get human-readable age of snapshot
     */
    fun getAge(): String {
        val ageMs = System.currentTimeMillis() - timestamp
        val ageMinutes = ageMs / (60 * 1000)
        return when {
            ageMinutes < 1 -> "just now"
            ageMinutes < 60 -> "${ageMinutes}m ago"
            else -> "${ageMinutes / 60}h ago"
        }
    }
}

