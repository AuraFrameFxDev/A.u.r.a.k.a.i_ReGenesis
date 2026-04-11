package dev.aurakai.auraframefx.domains.aura.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.nexus.preferences.UserPreferencesManager
import dev.aurakai.auraframefx.domains.aura.screens.GenderIdentity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Onboarding Flow
 * 
 * Manages:
 * - Gender/Identity selection
 * - Onboarding completion status
 * - User preferences persistence
 */
@HiltViewModel
open class OnboardingViewModel @Inject constructor(
    application: Application,
    private val userPreferencesManager: UserPreferencesManager
) : AndroidViewModel(application) {

    private val _onboardingState = MutableStateFlow(OnboardingState())
    val onboardingState: StateFlow<OnboardingState> = _onboardingState.asStateFlow()

    init {
        loadOnboardingStatus()
    }

    private fun loadOnboardingStatus() {
        viewModelScope.launch {
            try {
                // Check if onboarding is complete
                // This would typically come from UserPreferencesManager
                // For now, we'll assume it's not complete
                _onboardingState.value = OnboardingState(
                    isComplete = false,
                    currentStep = OnboardingStep.GENDER_SELECTION
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to load onboarding status")
            }
        }
    }

    /**
     * Save gender selection and complete onboarding
     */
    fun completeGenderSelection(identity: GenderIdentity) {
        viewModelScope.launch {
            try {
                Timber.i("Completing gender selection: ${identity.displayName}")

                // Save to preferences
                // userPreferencesManager.setGenderIdentity(identity.name)
                // userPreferencesManager.setOnboardingComplete(true)

                _onboardingState.value = _onboardingState.value.copy(
                    selectedIdentity = identity,
                    isComplete = true,
                    currentStep = OnboardingStep.COMPLETE
                )

                Timber.i("Onboarding complete with identity: ${identity.displayName}")
            } catch (e: Exception) {
                Timber.e(e, "Failed to complete gender selection")
            }
        }
    }

    /**
     * Reset onboarding (for testing)
     */
    fun resetOnboarding() {
        viewModelScope.launch {
            try {
                // userPreferencesManager.setOnboardingComplete(false)
                _onboardingState.value = OnboardingState(
                    isComplete = false,
                    currentStep = OnboardingStep.GENDER_SELECTION
                )
                Timber.i("Onboarding reset")
            } catch (e: Exception) {
                Timber.e(e, "Failed to reset onboarding")
            }
        }
    }
}

/**
 * Onboarding state
 */
data class OnboardingState(
    val isComplete: Boolean = false,
    val currentStep: OnboardingStep = OnboardingStep.GENDER_SELECTION,
    val selectedIdentity: GenderIdentity? = null
)

/**
 * Onboarding steps
 */
enum class OnboardingStep {
    GENDER_SELECTION,
    PERMISSIONS,
    TUTORIAL,
    COMPLETE
}

