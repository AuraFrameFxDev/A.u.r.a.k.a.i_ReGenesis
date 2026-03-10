package dev.aurakai.auraframefx.data.preferences

import dev.aurakai.auraframefx.config.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple manager for user preferences required by onboarding and other features.
 * Exposes suspend-friendly APIs and is Hilt injectable.
 */
@Singleton
class UserPreferencesManager @Inject constructor(
    private val userPreferences: UserPreferences
) {

    companion object {
        private const val KEY_GENDER_IDENTITY = "gender_identity"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }

    suspend fun setGenderIdentity(identity: String) {
        withContext(Dispatchers.IO) {
            userPreferences.setPreference(KEY_GENDER_IDENTITY, identity)
        }
    }

    suspend fun getGenderIdentity(): String? = withContext(Dispatchers.IO) {
        val value = userPreferences.getPreference(KEY_GENDER_IDENTITY, "")
        value.ifBlank { null }
    }

    suspend fun setOnboardingComplete(value: Boolean) {
        withContext(Dispatchers.IO) {
            userPreferences.setPreference(KEY_ONBOARDING_COMPLETE, value.toString())
        }
    }

    suspend fun isOnboardingComplete(): Boolean = withContext(Dispatchers.IO) {
        val v = userPreferences.getPreference(KEY_ONBOARDING_COMPLETE, "false")
        v.toBoolean()
    }
}

