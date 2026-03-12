package dev.aurakai.auraframefx.domains.cascade.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class VoiceCommandManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _voiceState = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    fun startListening() {
        if (isListening) return

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _voiceState.value = VoiceState.Error("Speech recognition not available")
            return
        }

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(recognitionListener)
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }

            speechRecognizer?.startListening(intent)
            isListening = true
            _voiceState.value = VoiceState.Listening
        } catch (e: Exception) {
            Timber.e(e, "Failed to start speech recognition")
            _voiceState.value = VoiceState.Error(e.message ?: "Unknown error")
            isListening = false
        }
    }

    fun stopListening() {
        if (!isListening) return
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
            isListening = false
            _voiceState.value = VoiceState.Idle
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop speech recognition")
        }
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Timber.d("Voice: Ready for speech")
        }

        override fun onBeginningOfSpeech() {
            Timber.d("Voice: Speech started")
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Timber.d("Voice: Speech ended")
            _voiceState.value = VoiceState.Processing
        }

        override fun onError(error: Int) {
            val errorMessage = getErrorText(error)
            Timber.e("Voice Error: $errorMessage")
            _voiceState.value = VoiceState.Error(errorMessage)
            isListening = false
            // Reset to idle after error
            speechRecognizer?.destroy()
            speechRecognizer = null
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = matches?.firstOrNull()
            if (text != null) {
                Timber.d("Voice Result: $text")
                _voiceState.value = VoiceState.Result(text)
            } else {
                _voiceState.value = VoiceState.Error("No speech detected")
            }
            isListening = false
            speechRecognizer?.destroy()
            speechRecognizer = null
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = matches?.firstOrNull()
            if (text != null) {
                _voiceState.value = VoiceState.PartialResult(text)
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun getErrorText(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "Error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error"
        }
    }
}

sealed class VoiceState {
    object Idle : VoiceState()
    object Listening : VoiceState()
    object Processing : VoiceState()
    data class PartialResult(val text: String) : VoiceState()
    data class Result(val text: String) : VoiceState()
    data class Error(val message: String) : VoiceState()
}

