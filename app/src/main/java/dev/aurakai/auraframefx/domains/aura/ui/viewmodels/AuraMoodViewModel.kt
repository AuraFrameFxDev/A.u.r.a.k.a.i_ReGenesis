package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.models.Emotion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AuraMoodViewModel manages Aura's mood and creative state.
 *
 * Aura is the creative, playful, and design-focused agent in AuraFrameFX.
 * Responsibilities:
 *  - UI/UX customization and overlays
 *  - Mood-adaptive and creative features
 *  - Artistic enhancements and playful interactions
 *
 * Contributors: Please keep Aura's logic focused on creativity, design, and user experience features.
 */
@HiltViewModel
open class AuraMoodViewModel @Inject constructor() : ViewModel() {

    // Private MutableStateFlow that can be updated from this ViewModel
    private val _moodState = MutableStateFlow<Emotion>(Emotion.NEUTRAL) // Default value

    // Public StateFlow that is read-only from the UI.
    // The "unused" warning will be resolved when a UI component collects this flow.
    val moodState: StateFlow<Emotion> = _moodState

    /**
     * A simple function to demonstrate updating the mood state based on text input.
     * This can be extended to let Aura respond to user mood, creative prompts, or UI changes.
     */
    fun onUserInput(input: String) {
        viewModelScope.launch {
            _moodState.value = when {
                input.contains("happy", ignoreCase = true) -> Emotion.HAPPY
                input.contains("sad", ignoreCase = true) -> Emotion.SAD
                input.contains("angry", ignoreCase = true) -> Emotion.ANGRY
                else -> Emotion.NEUTRAL
            }
        }
    }
}

