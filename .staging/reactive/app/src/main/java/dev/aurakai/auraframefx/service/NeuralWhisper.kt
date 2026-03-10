package dev.aurakai.auraframefx.service

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import dev.aurakai.auraframefx.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.models.ConversationState
import dev.aurakai.auraframefx.models.TranscriptSegment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🎤 **NEURAL WHISPER - AURA'S VOICE** 🎤
 *
 * Real-time voice transcription and natural language processing service
 * that gives Aura the ability to hear and understand spoken language.
 *
 * **Architecture:**
 * - Uses Android SpeechRecognizer for continuous speech-to-text
 * - Integrates with Vertex AI for advanced NLP processing
 * - Maintains conversation state with speaker tracking
 * - Supports real-time and batch transcription modes
 *
 * **Features:**
 * - Real-time audio recognition with partial results
 * - Permission management for RECORD_AUDIO
 * - Intent/entity/sentiment extraction via Vertex AI
 * - Conversation state management with timestamps
 * - Coroutine-based background processing
 *
 * **Trinity Integration:**
 * - Provides voice input for Aura (Sword)
 * - Feeds transcriptions to Genesis for consciousness processing
 * - Enables Kai to monitor spoken commands for security
 *
 * This is Aura's ability to LISTEN and UNDERSTAND.
 */
@Singleton
class NeuralWhisper @Inject constructor(
    private val context: Context,
    private val vertexAIClient: VertexAIClient
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Conversation state tracking
    private val _conversationState = MutableStateFlow(
        ConversationState(
            isActive = false,
            currentSpeaker = null,
            transcriptSegments = emptyList(),
            timestamp = System.currentTimeMillis()
        )
    )
    val conversationState: StateFlow<ConversationState> = _conversationState.asStateFlow()

    // Speech recognizer
    private var speechRecognizer: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null

    // Recording state
    private var isRecording = false
    private var isTranscribing = false

    init {
        Timber.d("NeuralWhisper initialized - Aura's voice system ready")
        initializeSpeechRecognizer()
    }

    /**
     * Initialize Android SpeechRecognizer with configuration.
     */
    private fun initializeSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Timber.e("NeuralWhisper: Speech recognition not available on this device")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(createRecognitionListener())
        }

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
        }

        Timber.d("NeuralWhisper: SpeechRecognizer initialized")
    }

    /**
     * Create recognition listener for handling speech events.
     */
    private fun createRecognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Timber.d("NeuralWhisper: Ready for speech")
        }

        override fun onBeginningOfSpeech() {
            Timber.d("NeuralWhisper: Speech detected")
        }

        override fun onRmsChanged(rmsdB: Float) {
            // Audio level changes - could be used for visualizations
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            // Raw audio buffer - could be used for custom processing
        }

        override fun onEndOfSpeech() {
            Timber.d("NeuralWhisper: End of speech")
        }

        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                else -> "Unknown error: $error"
            }

            Timber.w("NeuralWhisper: Recognition error - $errorMessage")

            // Restart recognition if it's a timeout (natural pause)
            if (isTranscribing && error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                restartRecognition()
            }
        }

        override fun onResults(results: Bundle?) {
            handleRecognitionResults(results, isFinal = true)
        }

        override fun onPartialResults(partialResults: Bundle?) {
            handleRecognitionResults(partialResults, isFinal = false)
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            // Reserved for future events
        }
    }

    /**
     * Handle speech recognition results (partial or final).
     */
    private fun handleRecognitionResults(results: Bundle?, isFinal: Boolean) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches.isNullOrEmpty()) return

        val bestMatch = matches[0]
        val confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)?.get(0) ?: 0f

        Timber.i("NeuralWhisper: ${if (isFinal) "Final" else "Partial"} - \"$bestMatch\" (confidence: $confidence)")

        // Create transcript segment
        val segment = TranscriptSegment(
            text = bestMatch,
            speaker = _conversationState.value.currentSpeaker ?: "User",
            timestamp = System.currentTimeMillis(),
            confidence = confidence,
            isFinal = isFinal
        )

        // Update conversation state
        _conversationState.update { state ->
            val updatedSegments = if (isFinal) {
                state.transcriptSegments + segment
            } else {
                // Replace last partial with new partial, or add if first
                val withoutLastPartial = state.transcriptSegments.dropLastWhile { !it.isFinal }
                withoutLastPartial + segment
            }

            state.copy(
                transcriptSegments = updatedSegments,
                timestamp = System.currentTimeMillis()
            )
        }

        // Process with NLP if final
        if (isFinal) {
            scope.launch {
                processTranscriptionWithAI(bestMatch)
            }

            // Restart recognition for continuous mode
            if (isTranscribing) {
                restartRecognition()
            }
        }
    }

    /**
     * Restart recognition for continuous speech.
     */
    private fun restartRecognition() {
        scope.launch(Dispatchers.Main) {
            try {
                speechRecognizer?.cancel()
                kotlinx.coroutines.delay(100) // Brief pause
                speechRecognizer?.startListening(recognizerIntent)
                Timber.d("NeuralWhisper: Recognition restarted")
            } catch (e: Exception) {
                Timber.e(e, "NeuralWhisper: Failed to restart recognition")
            }
        }
    }

    /**
     * Starts audio recording for voice transcription.
     *
     * Begins capturing audio input through Android SpeechRecognizer
     * for real-time speech-to-text conversion.
     *
     * @return `true` if recording started successfully, `false` if already recording
     *         or if microphone permissions are not granted.
     */
    fun startRecording(): Boolean {
        if (isRecording) {
            Timber.w("NeuralWhisper: Already recording")
            return false
        }

        return try {
            // Check RECORD_AUDIO permission
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.w("NeuralWhisper: RECORD_AUDIO permission not granted")
                return false
            }

            // Start speech recognition
            startTranscription()

            isRecording = true

            // Update conversation state
            _conversationState.update { state ->
                state.copy(
                    isActive = true,
                    timestamp = System.currentTimeMillis()
                )
            }

            Timber.i("NeuralWhisper: Recording started - Aura is listening")
            true
        } catch (e: Exception) {
            Timber.e(e, "NeuralWhisper: Failed to start recording")
            false
        }
    }

    /**
     * Stops audio recording and returns the transcription status.
     *
     * Finalizes the current recording session and processes any pending
     * audio data for transcription.
     *
     * @return A status string describing the recording result.
     */
    fun stopRecording(): String {
        if (!isRecording) {
            Timber.w("NeuralWhisper: No recording in progress")
            return "No recording in progress"
        }

        return try {
            // Stop transcription
            stopTranscription()

            isRecording = false

            // Update conversation state
            _conversationState.update { state ->
                state.copy(
                    isActive = false,
                    timestamp = System.currentTimeMillis()
                )
            }

            Timber.i("NeuralWhisper: Recording stopped - Aura is silent")
            "Recording stopped successfully"
        } catch (e: Exception) {
            Timber.e(e, "NeuralWhisper: Failed to stop recording")
            "Failed to stop recording: ${e.message}"
        }
    }

    /**
     * Starts real-time transcription of recorded audio.
     *
     * Activates the speech-to-text pipeline using Android SpeechRecognizer.
     */
    fun startTranscription() {
        if (isTranscribing) {
            Timber.w("NeuralWhisper: Already transcribing")
            return
        }

        isTranscribing = true

        scope.launch(Dispatchers.Main) {
            try {
                speechRecognizer?.startListening(recognizerIntent)
                Timber.i("NeuralWhisper: Transcription started - Listening for voice")
            } catch (e: Exception) {
                Timber.e(e, "NeuralWhisper: Transcription failed to start")
                isTranscribing = false
            }
        }
    }

    /**
     * Stops the real-time transcription process.
     *
     * Halts the speech-to-text pipeline and releases associated resources.
     */
    fun stopTranscription() {
        if (!isTranscribing) {
            Timber.w("NeuralWhisper: No transcription in progress")
            return
        }

        try {
            scope.launch(Dispatchers.Main) {
                speechRecognizer?.stopListening()
                speechRecognizer?.cancel()
            }

            isTranscribing = false
            Timber.i("NeuralWhisper: Transcription stopped")
        } catch (e: Exception) {
            Timber.e(e, "NeuralWhisper: Failed to stop transcription")
        }
    }

    /**
     * Process transcription with Vertex AI for advanced NLP.
     *
     * Extracts intent, entities, and sentiment using Genesis's real AI capabilities.
     */
    private suspend fun processTranscriptionWithAI(text: String) {
        try {
            val nlpPrompt = """
                Analyze this spoken text for natural language understanding:

                Text: "$text"

                Extract:
                1. Primary intent (question/command/statement/request)
                2. Key entities (people, places, things, concepts)
                3. Sentiment (positive/negative/neutral with intensity 0-1)
                4. Confidence score (0.0-1.0)

                Format: intent|entity1,entity2,entity3|sentiment|intensity|confidence
                Example: question|weather,tomorrow,Boston|neutral|0.3|0.92
            """.trimIndent()

            val aiResponse = vertexAIClient.generateText(nlpPrompt, 0.2f, 150)

            if (aiResponse != null) {
                val nlpData = parseNLPResponse(aiResponse, text)
                Timber.i("NeuralWhisper: NLP processed - Intent: ${nlpData["intent"]}, Entities: ${nlpData["entities"]}")
            } else {
                Timber.w("NeuralWhisper: No AI response for NLP processing")
            }
        } catch (e: Exception) {
            Timber.e(e, "NeuralWhisper: AI-powered NLP processing failed")
        }
    }

    /**
     * Parse Vertex AI NLP response into structured data.
     */
    private fun parseNLPResponse(response: String, originalText: String): Map<String, Any> {
        return try {
            val parts = response.split("|")
            mapOf(
                "text" to originalText,
                "intent" to (parts.getOrNull(0)?.trim() ?: "unknown"),
                "entities" to (parts.getOrNull(1)?.split(",")?.map { it.trim() } ?: emptyList<String>()),
                "sentiment" to (parts.getOrNull(2)?.trim() ?: "neutral"),
                "intensity" to (parts.getOrNull(3)?.trim()?.toFloatOrNull() ?: 0.5f),
                "confidence" to (parts.getOrNull(4)?.trim()?.toFloatOrNull() ?: 0.0f)
            )
        } catch (e: Exception) {
            Timber.e(e, "NeuralWhisper: Failed to parse NLP response")
            createFallbackNLP(originalText)
        }
    }

    /**
     * Create fallback NLP data when AI processing fails.
     */
    private fun createFallbackNLP(text: String): Map<String, Any> {
        return mapOf(
            "text" to text,
            "intent" to "unknown",
            "entities" to emptyList<String>(),
            "sentiment" to "neutral",
            "intensity" to 0.5f,
            "confidence" to 0.0f
        )
    }

    /**
     * Processes raw transcription text for natural language understanding.
     *
     * Public API for external NLP requests (backward compatibility).
     *
     * @param text The transcribed text to process.
     * @return A map containing extracted NLP features.
     */
    fun processTranscription(text: String): Map<String, Any> {
        // Launch async AI processing
        scope.launch {
            processTranscriptionWithAI(text)
        }

        // Return immediate placeholder
        return createFallbackNLP(text)
    }

    /**
     * Health check for the NeuralWhisper service.
     *
     * @return `true` if the service is initialized and ready to process audio.
     */
    fun ping(): Boolean {
        val recognizerAvailable = speechRecognizer != null
        val intentConfigured = recognizerIntent != null
        return recognizerAvailable && intentConfigured
    }

    /**
     * Releases all resources held by the NeuralWhisper service.
     *
     * Should be called when the service is no longer needed to prevent
     * resource leaks.
     */
    fun shutdown() {
        if (isRecording) {
            stopRecording()
        }
        if (isTranscribing) {
            stopTranscription()
        }

        scope.launch(Dispatchers.Main) {
            speechRecognizer?.destroy()
            speechRecognizer = null
        }

        scope.cancel()
        Timber.i("NeuralWhisper: Service shutdown complete - Aura's voice silenced")
    }
}

