package dev.aurakai.auraframefx.domains.aura

import dev.aurakai.auraframefx.domains.cascade.network.apis.AIContentApi
import dev.aurakai.auraframefx.domains.cascade.utils.memory.models.GenerateTextRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Client wrapper for the AuraFrameFx AI Content API.
 * Provides clean methods to access text and image description generation capabilities.
 */
@Singleton
class AuraFxContentApiClient @Inject constructor(
    private val aiContentApi: AIContentApi,
) {
    /**
     * Generates AI-powered text asynchronously based on the given prompt.
     *
     * @param prompt The text prompt to guide the AI-generated output.
     * @param maxTokens The maximum number of tokens for the generated text. If null, defaults to 500.
     * @param temperature Controls the randomness of the output. If null, defaults to 0.7.
     * @return The raw API response containing the generated text.
     */
    suspend fun generateText(
        prompt: String,
        maxTokens: Int? = null,
        temperature: Float? = null,
    ): Any = withContext(Dispatchers.IO) { // Temporary: Use Any instead of missing ResponseType

        aiContentApi.aiGenerateTextPost(
            GenerateTextRequest(
                prompt = prompt,
                maxTokens = maxTokens ?: 500,
                temperature = temperature ?: 0.7f
            )
        )
    }

    /**
     * Asynchronously generates an AI description for the specified image URL, optionally using additional context.
     *
     * @param imageUrl The URL of the image to be described.
     * @param context Optional text providing additional context or guidance for the description.
     * @return The raw API response containing the generated image description.
     */
    suspend fun generateImageDescription(
        imageUrl: String,
        context: String? = null,
    ): Any =
        withContext(Dispatchers.IO) { // Temporary: Use Any instead of missing GenerateImageDescriptionResponse

            aiContentApi.aiGenerateImageDescriptionPost(
                GenerateImageDescriptionRequest(
                    imageUrl = imageUrl,
                    context = context
                )
            )
        }
}
