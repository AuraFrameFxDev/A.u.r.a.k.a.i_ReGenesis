package dev.aurakai.auraframefx.domains.nexus.preferences

import javax.inject.Inject
import javax.inject.Singleton

/**
 * User preferences storage wrapper.
 */
@Singleton
class UserPreferences @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    suspend fun setPreference(key: String, value: String) {
        dataStoreManager.storeString(key, value)
    }

    suspend fun getPreference(key: String, default: String): String {
        return dataStoreManager.getString(key, default)
    }
}

/**
 * User preference model state.
 */
data class UserPreferencesState(
    val theme: String = "dark_aura",
    val agentPersonality: String = "balanced",
    val thermalWarningEnabled: Boolean = true,
    val hapticFeedback: Boolean = true,
    val reducedMotion: Boolean = false,
    val defaultGateId: String = "home",
    val llmTemperatureOverride: Float = -1f
)
