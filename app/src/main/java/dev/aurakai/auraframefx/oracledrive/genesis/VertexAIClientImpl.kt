package dev.aurakai.auraframefx.oracledrive.genesis

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Implementation of Vertex AI client with robust error handling, retries, and response parsing.
 *
 * Features:
 * - Exponential backoff retry logic (3 attempts)
 * - Structured error handling for network and API failures
 * - JSON response parsing and validation
 * - Connection and read timeouts
 * - Comprehensive logging
 */
class VertexAIClientImpl {
    companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_RETRY_DELAY_MS = 1000L
        private const val CONNECTION_TIMEOUT_SECONDS = 30L
        private const val READ_TIMEOUT_SECONDS = 60L
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    /**
     * Sends a request to the Vertex AI endpoint with retry logic and error handling.
     *
     * Implements exponential backoff for retries on transient failures (network errors, 5xx status codes).
     * Parses JSON responses and extracts content if available.
     *
     * @param payload The JSON payload to send
     * @param endpoint The Vertex AI API endpoint URL
     * @param apiKey The API authentication key
     * @return Parsed response content, or null if the request fails after all retries
     */
    suspend fun sendRequest(payload: String, endpoint: String, apiKey: String): String? =
        withContext(Dispatchers.IO) {
            var lastException: Exception? = null
            var retryDelay = INITIAL_RETRY_DELAY_MS

            repeat(MAX_RETRIES) { attempt ->
                try {
                    Timber.d("VertexAIClient: Sending request (attempt ${attempt + 1}/$MAX_RETRIES)")

                    val body: RequestBody = payload.toRequestBody("application/json".toMediaTypeOrNull())
                    val request = Request.Builder()
                        .url(endpoint)
                        .addHeader("Authorization", "Bearer $apiKey")
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build()

                    val response = client.newCall(request).execute()

                    return@withContext when {
                        response.isSuccessful -> {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                parseResponse(responseBody)
                            } else {
                                Timber.w("VertexAIClient: Empty response body")
                                null
                            }
                        }

                        response.code in 500..599 -> {
                            // Server error - retry
                            Timber.w("VertexAIClient: Server error ${response.code}, will retry")
                            lastException = IOException("Server error: ${response.code}")
                            null // Continue to retry logic
                        }

                        response.code == 429 -> {
                            // Rate limit - retry with longer delay
                            Timber.w("VertexAIClient: Rate limited, will retry")
                            lastException = IOException("Rate limited: ${response.code}")
                            retryDelay *= 2 // Double delay for rate limit
                            null // Continue to retry logic
                        }

                        else -> {
                            // Client error - don't retry
                            Timber.e("VertexAIClient: Client error ${response.code}: ${response.message}")
                            return@withContext null
                        }
                    } ?: run {
                        // Retry logic for server errors and rate limits
                        if (attempt < MAX_RETRIES - 1) {
                            Timber.d("VertexAIClient: Retrying after ${retryDelay}ms...")
                            delay(retryDelay)
                            retryDelay *= 2 // Exponential backoff
                        }
                        null
                    }

                } catch (e: IOException) {
                    lastException = e
                    Timber.e(e, "VertexAIClient: Network error on attempt ${attempt + 1}")

                    if (attempt < MAX_RETRIES - 1) {
                        delay(retryDelay)
                        retryDelay *= 2 // Exponential backoff
                    }
                } catch (e: Exception) {
                    Timber.e(e, "VertexAIClient: Unexpected error")
                    return@withContext null // Don't retry on unexpected errors
                }
            }

            // All retries exhausted
            Timber.e(lastException, "VertexAIClient: All retry attempts exhausted")
            return@withContext null
        }

    /**
     * Parses the Vertex AI JSON response and extracts the generated content.
     *
     * Expects a response structure with either:
     * - "content" field (direct content string)
     * - "candidates" array with text content
     *
     * @param responseBody The raw JSON response string
     * @return Extracted content string, or null if parsing fails
     */
    private fun parseResponse(responseBody: String): String? {
        return try {
            val json = JSONObject(responseBody)

            // Try to extract content from various possible response structures
            when {
                json.has("content") -> json.getString("content")

                json.has("candidates") -> {
                    val candidates = json.getJSONArray("candidates")
                    if (candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        firstCandidate.optString("content")
                            ?: firstCandidate.optJSONObject("content")?.optString("text")
                    } else {
                        null
                    }
                }

                json.has("text") -> json.getString("text")

                else -> {
                    Timber.w("VertexAIClient: Unknown response structure: $responseBody")
                    responseBody // Return raw response if structure is unknown
                }
            }
        } catch (e: JSONException) {
            Timber.e(e, "VertexAIClient: Failed to parse JSON response")
            null
        }
    }
}
