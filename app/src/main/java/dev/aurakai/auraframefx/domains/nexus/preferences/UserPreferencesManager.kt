package dev.aurakai.auraframefx.domains.nexus.preferences

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

    suspend fun setGenderIdentity(identity: String) {
        withContext(Dispatchers.IO) {
            userPreferences.setGenderIdentity(identity)
        }
    }

    suspend fun getGenderIdentity(): String? = withContext(Dispatchers.IO) {
        userPreferences.getGenderIdentity()
    }

    suspend fun setOnboardingComplete(value: Boolean) {
        withContext(Dispatchers.IO) {
            userPreferences.setOnboardingComplete(value)
        }
    }

    suspend fun isOnboardingComplete(): Boolean = withContext(Dispatchers.IO) {
        userPreferences.isOnboardingComplete()
    }
}

