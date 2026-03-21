package dev.aurakai.auraframefx.ai

import dev.aurakai.auraframefx.ai.model.GenerateTextRequest
import dev.aurakai.auraframefx.ai.model.GenerateTextResponse
import dev.aurakai.auraframefx.api.AiContentApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiGenerationService(
    private val api: AiContentApi,
) {
    suspend fun generateText(
        prompt: String,
        maxTokens: Int = 500,
        temperature: Float = 0.7f,
    ): Result<GenerateTextResponse> = withContext(Dispatchers.IO) {
        try {
            val request = GenerateTextRequest(
                prompt = prompt,
                maxTokens = maxTokens,
                temperature = temperature
            )
            val response = api.generateText(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
