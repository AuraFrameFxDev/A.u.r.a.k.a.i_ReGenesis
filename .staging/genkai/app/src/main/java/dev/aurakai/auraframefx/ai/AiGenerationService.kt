package dev.aurakai.auraframefx.ai

// import dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateImageDescriptionResponse // Not available in new API
// import kotlinx.coroutines.CoroutineScope // Not needed if generateImageDescription is removed
import dev.aurakai.auraframefx.ai.model.GenerateTextRequest
import dev.aurakai.auraframefx.ai.model.GenerateTextResponse
import dev.aurakai.auraframefx.api.AiContentApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiGenerationService(
    private val api: AiContentApi, // Updated type
) {
    suspend fun generateText(
        prompt: String,
        maxTokens: Int = 500,
        temperature: Float = 0.7f,
    ): Result<GenerateTextResponse> = withContext(Dispatchers.IO) {
        try {
            val request = GenerateTextRequest( // This will now use the new model
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

    // suspend fun generateImageDescription(
    //     imageUrl: String,
    //     context: String? = null,
    //     prompt: String? = null,
    //     maxTokens: Int? = null,
    //     model: String? = null,
    //     imageData: ByteArray? = null,
    // ): Result<GenerateImageDescriptionResponse> = withContext(Dispatchers.IO) {
    //     try {
    //         // The request model dev.aurakai.auraframefx.generated.model.auraframefxai.GenerateImageDescriptionRequest
    //         // is also from the old generated source and would need to be handled if this method were kept.
    //         // val request = GenerateImageDescriptionRequest(
    //         //     imageUrl = imageUrl,
    //         //     context = context,
    //         //     imageData = imageData,
    //         //     prompt = prompt,
    //         //     maxTokens = maxTokens,
    //         //     model = model
    //         // )
    //         // val response = api.generateImageDescription(request) // This method doesn't exist on AiContentApi
    //         // Result.success(response)
    //         Result.failure(UnsupportedOperationException("generateImageDescription is not supported in the current API"))
    //     } catch (e: Exception) {
    //         Result.failure(e)
    //     }
    // }

    // private fun CoroutineScope.GenerateImageDescriptionRequest(
    //     imageUrl: String,
    //     context: String?,
    //     imageData: ByteArray?,
    //     prompt: String?,
    //     maxTokens: Int?,
    //     model: String?,
    // ) {
    //     TODO("Not yet implemented")
    // }
}
