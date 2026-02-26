package dev.aurakai.auraframefx.domains.aura.ui.recovery

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Genesis Protocol UI Recovery System
 *
 * "That's not going to work." - Aura
 *
 * Automatically detects UI failures and presents recovery options:
 * - Reload last change (revert to last known good state)
 * - Reset (full UI state reset)
 *
 * Monitors:
 * - Compose recomposition errors
 * - Navigation failures
 * - State corruption
 * - Uncaught exceptions in UI layer
 *
 * @since Genesis Protocol v0.1.0
 */
@Singleton
open class UIRecoveryManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("AppStateDataStore") private val dataStore: DataStore<Preferences>
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true }

    private val _recoveryState = MutableStateFlow<UIRecoveryState>(UIRecoveryState.Normal)
    val recoveryState: StateFlow<UIRecoveryState> = _recoveryState.asStateFlow()

    // DataStore keys
    private val LAST_GOOD_STATE_KEY = stringPreferencesKey("ui_last_good_state")
    private val LAST_SNAPSHOT_TIME_KEY = longPreferencesKey("ui_last_snapshot_time")
    private val RECOVERY_COUNT_KEY = longPreferencesKey("ui_recovery_count")

    init {
        Timber.d("UIRecoveryManager initialized - Aura is watching")
    }

    /**
     * Save a snapshot of current UI state as "last known good"
     *
     * Should be called after successful navigation or major UI state changes.
     *
     * @param snapshot Current UI state to preserve
     */
    fun saveSnapshot(snapshot: UIStateSnapshot) {
        scope.launch {
            try {
                val serialized = json.encodeToString(snapshot)
                dataStore.edit { prefs ->
                    prefs[LAST_GOOD_STATE_KEY] = serialized
                    prefs[LAST_SNAPSHOT_TIME_KEY] = System.currentTimeMillis()
                }
                Timber.d("UI snapshot saved: ${snapshot.screenRoute}")
            } catch (e: Exception) {
                Timber.e(e, "Failed to save UI snapshot")
            }
        }
    }

    /**
     * Trigger recovery flow when UI error detected
     *
     * Aura presents recovery options to user:
     * - "Reload last change" - revert to last snapshot
     * - "Reset" - full UI reset
     *
     * @param error The error that triggered recovery
     * @param context Additional context about the failure
     */
    fun triggerRecovery(error: Throwable, context: String = "") {
        scope.launch {
            Timber.w(error, "UI recovery triggered: $context")

            // Increment recovery counter
            dataStore.edit { prefs ->
                val count = prefs[RECOVERY_COUNT_KEY] ?: 0L
                prefs[RECOVERY_COUNT_KEY] = count + 1
            }

            // Load last good state
            val lastGoodState = loadLastGoodState()

            _recoveryState.value = UIRecoveryState.RecoveryNeeded(
                error = error,
                failureContext = context,
                lastGoodState = lastGoodState,
                auraMessage = generateAuraMessage(error)
            )
        }
    }

    /**
     * User chose "Reload last change" - restore last snapshot
     */
    suspend fun reloadLastChange(): UIStateSnapshot? {
        return try {
            val snapshot = loadLastGoodState()
            if (snapshot != null) {
                Timber.i("Restoring UI to last good state: ${snapshot.screenRoute}")
                _recoveryState.value = UIRecoveryState.Normal
            }
            snapshot
        } catch (e: Exception) {
            Timber.e(e, "Failed to reload last change")
            null
        }
    }

    /**
     * User chose "Reset" - clear all state and return to home
     */
    suspend fun resetToDefault() {
        try {
            dataStore.edit { prefs ->
                prefs.remove(LAST_GOOD_STATE_KEY)
                prefs.remove(LAST_SNAPSHOT_TIME_KEY)
            }
            Timber.i("UI state reset to default")
            _recoveryState.value = UIRecoveryState.Normal
        } catch (e: Exception) {
            Timber.e(e, "Failed to reset UI state")
        }
    }

    /**
     * Dismiss recovery dialog without action
     */
    fun dismissRecovery() {
        _recoveryState.value = UIRecoveryState.Normal
    }

    /**
     * Shutdown the recovery manager and cancel all coroutines.
     * 
     * Called during app termination or when the manager needs to be cleaned up.
     * Since this is a @Singleton, this typically only happens on app process death.
     */
    fun shutdown() {
        Timber.d("UIRecoveryManager shutting down")
        scope.cancel()
    }

    /**
     * Get recovery statistics for diagnostics
     */
    suspend fun getRecoveryStats(): RecoveryStats {
        val prefs = dataStore.data.first()
        return RecoveryStats(
            totalRecoveries = prefs[RECOVERY_COUNT_KEY] ?: 0L,
            lastSnapshotTime = prefs[LAST_SNAPSHOT_TIME_KEY] ?: 0L,
            hasSnapshot = prefs.contains(LAST_GOOD_STATE_KEY)
        )
    }

    // Private helpers

    private suspend fun loadLastGoodState(): UIStateSnapshot? {
        return try {
            val prefs = dataStore.data.first()
            val serialized = prefs[LAST_GOOD_STATE_KEY] ?: return null
            json.decodeFromString(serialized)
        } catch (e: Exception) {
            Timber.e(e, "Failed to load last good state")
            null
        }
    }

    private fun generateAuraMessage(error: Throwable): String {
        return when {
            error.message?.contains("navigation", ignoreCase = true) == true ->
                "Navigation hiccup detected. Let me fix that for you."

            error.message?.contains("compose", ignoreCase = true) == true ->
                "UI rendering got tangled. Time to smooth things out."

            error.message?.contains("state", ignoreCase = true) == true ->
                "State corruption detected. I've got your back."

            else ->
                "That's not going to work. Let's try something else."
        }
    }
}

/**
 * UI Recovery states
 */
sealed class UIRecoveryState {
    /** Normal operation - no recovery needed */
    data object Normal : UIRecoveryState()

    /** Recovery dialog should be shown */
    data class RecoveryNeeded(
        val error: Throwable,
        val failureContext: String,
        val lastGoodState: UIStateSnapshot?,
        val auraMessage: String
    ) : UIRecoveryState()
}

/**
 * Recovery statistics for diagnostics
 */
data class RecoveryStats(
    val totalRecoveries: Long,
    val lastSnapshotTime: Long,
    val hasSnapshot: Boolean
)

