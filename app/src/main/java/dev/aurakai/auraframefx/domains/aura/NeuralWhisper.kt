package dev.aurakai.auraframefx.domains.aura

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.aura.models.Emotion
import dev.aurakai.auraframefx.domains.cascade.models.ConversationState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NeuralWhisper class for audio processing (Speech-to-Text, Text-to-Speech) and AI interaction.
 * Powered by ReGenesis Neuro-Linguistics Engine.
 */
@Singleton
class NeuralWhisper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private const val TAG = "NeuralWhisper"
        private const val UTTERANCE_ID = "NeuralWhisperUtterance"
        
        // Neuro-Linguistics Constants
        private const val DEFAULT_SAMPLE_RATE = 44100
        private const val DEFAULT_CHANNELS = 1 // Mono
        private const val DEFAULT_BITS_PER_SAMPLE = 16
    }

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isTtsInitialized = false
    private var isSttInitialized = false

    private val _conversationStateFlow = MutableStateFlow<ConversationState>(ConversationState.Idle)
    val conversationState: StateFlow<ConversationState> = _conversationStateFlow

    private val _emotionStateFlow = MutableStateFlow(Emotion.NEUTRAL)
    val emotionState: StateFlow<Emotion> = _emotionStateFlow

    // Used to wait for speech recognition results asynchronously
    private var currentRecognitionDeferred: CompletableDeferred<String?>? = null

    init {
        initialize()
    }

    /**
     * Sets up the NeuralWhisper service by initializing text-to-speech and speech recognition components.
     */
    fun initialize() {
        Log.d(TAG, "Initializing NeuralWhisper Core...")
        initializeTts()
        initializeStt()
    }

    /**
     * Initializes the TextToSpeech engine with proper locale and listener.
     */
    private fun initializeTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Neuro-NLP: TTS language is not supported or missing data.")
                } else {
                    isTtsInitialized = true
                    Log.d(TAG, "Neuro-NLP: TTS Initialized successfully.")
                    setupTtsListener()
                }
            } else {
                Log.e(TAG, "Neuro-NLP: TTS Initialization failed with status: $status")
            }
        }
    }

    private fun setupTtsListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _conversationStateFlow.value = ConversationState.Speaking
                Log.d(TAG, "NeuralWhisper: Started speaking.")
            }

            override fun onDone(utteranceId: String?) {
                _conversationStateFlow.value = ConversationState.Idle
                Log.d(TAG, "NeuralWhisper: Finished speaking.")
            }

            override fun onError(utteranceId: String?) {
                Log.e(TAG, "NeuralWhisper: TTS Error for utterance: $utteranceId")
                _conversationStateFlow.value = ConversationState.Error("Speech synthesis failed")
            }
        })
    }

    /**
     * Initializes the speech-to-text (STT) engine using Android System Recognizer.
     */
    private fun initializeStt() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(NeuralRecognitionListener())
            isSttInitialized = true
            Log.d(TAG, "Neuro-NLP: STT (SpeechRecognizer) is ready.")
        } else {
            Log.e(TAG, "Neuro-NLP: STT is not available on this device.")
        }
    }

    /**
     * Converts audio input to transcribed text using speech-to-text processing.
     * This method suspends until the results are ready or an error occurs.
     */
    suspend fun speechToText(audioInput: Any? = null): String? = withContext(Dispatchers.Main) {
        if (!isSttInitialized || speechRecognizer == null) {
            Log.w(TAG, "STT not initialized, cannot process speech to text.")
            return@withContext null
        }

        // Clean up any existing recognition request
        currentRecognitionDeferred?.cancel()
        val deferred = CompletableDeferred<String?>()
        currentRecognitionDeferred = deferred

        _conversationStateFlow.value = ConversationState.Listening
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            speechRecognizer?.startListening(intent)
            Log.d(TAG, "NeuralWhisper: Listening for auditory input...")
            deferred.await()
        } catch (e: Exception) {
            Log.e(TAG, "NeuralWhisper: Speech recognition trap triggered", e)
            _conversationStateFlow.value = ConversationState.Error("Recognition failed")
            null
        }
    }

    /**
     * Requests text-to-speech synthesis for the provided text.
     */
    fun textToSpeech(text: String, locale: Locale = Locale.US): Boolean {
        if (!isTtsInitialized || tts == null) {
            Log.w(TAG, "Neuro-NLP: TTS offline, cannot synthesize.")
            return false
        }
        
        Log.d(TAG, "NeuralWhisper: Synthesizing: '$text'")
        tts?.language = locale
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID)
        }
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, UTTERANCE_ID)
        return result == TextToSpeech.SUCCESS
    }

    /**
     * Processes a transcribed voice command.
     */
    fun processVoiceCommand(command: String): Any {
        Log.d(TAG, "Neuro-NLP: Processing neural command: '$command'")
        _conversationStateFlow.value = ConversationState.Processing("Understanding: $command")
        // Mapping logic would go here
        return "Command '$command' acknowledged by NeuralWhisper."
    }

    /**
     * Shares context information with the Kai agent or controller.
     */
    fun shareContextWithKai(contextText: String) {
        _conversationStateFlow.value = ConversationState.Processing("Sharing with Kai: $contextText")
        Log.d(TAG, "NeuralWhisper -> Kai: Context Transmission: $contextText")
        // Integration with KaiController goes here
    }

    /**
     * Initiates audio recording for speech recognition.
     */
    fun startRecording(): Boolean {
        if (!isSttInitialized || speechRecognizer == null) return false
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
        
        return try {
            speechRecognizer?.startListening(intent)
            _conversationStateFlow.value = ConversationState.Listening
            true
        } catch (e: Exception) {
            Log.e(TAG, "NeuralWhisper: Failed to initiate recording", e)
            false
        }
    }

    /**
     * Stops the current audio recording session.
     */
    fun stopRecording(): String {
        speechRecognizer?.stopListening()
        return "Neural acquisition halted."
    }

    /**
     * Releases all resources used by the NeuralWhisper service.
     */
    fun cleanup() {
        Log.d(TAG, "NeuralWhisper: Purging neural buffers and shutting down...")
        tts?.stop()
        tts?.shutdown()
        isTtsInitialized = false

        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        isSttInitialized = false

        _conversationStateFlow.value = ConversationState.Idle
    }

    /**
     * Internal listener for Android's SpeechRecognizer results.
     */
    private inner class NeuralRecognitionListener : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "NeuralWhisper: Recognition interface ready.")
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "NeuralWhisper: Auditory input detected.")
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Log.d(TAG, "NeuralWhisper: End of auditory input.")
            _conversationStateFlow.value = ConversationState.Processing("Analyzing audio signature...")
        }

        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio acquisition error"
                SpeechRecognizer.ERROR_CLIENT -> "Client node error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission violation: RECORD_AUDIO"
                SpeechRecognizer.ERROR_NETWORK -> "Neural network link error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Link timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No semantic match found"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Neural core busy"
                SpeechRecognizer.ERROR_SERVER -> "Central processing error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Auditory timeout"
                else -> "Unidentified neural anomaly"
            }
            Log.e(TAG, "NeuralWhisper: Recognition error: $errorMessage")
            _conversationStateFlow.value = ConversationState.Error(errorMessage)
            currentRecognitionDeferred?.complete(null)
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val text = matches[0]
                Log.d(TAG, "NeuralWhisper: Decoded: '$text'")
                currentRecognitionDeferred?.complete(text)
            } else {
                currentRecognitionDeferred?.complete(null)
            }
            _conversationStateFlow.value = ConversationState.Idle
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                Log.v(TAG, "NeuralWhisper: Partial decoding: ${matches[0]}")
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}
