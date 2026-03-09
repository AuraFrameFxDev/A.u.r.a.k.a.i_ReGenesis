package dev.aurakai.auraframefx.domains.genesis.network

import dev.aurakai.auraframefx.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ktor HTTP client wrapper with common configurations.
 * 
 * Features:
 * - OkHttp engine for efficient networking
 * - JSON serialization with kotlinx.serialization
 * - Request/response logging in debug builds
 * - Timeout configurations
 * - Content negotiation for JSON
 */
@Singleton
class KtorClient @Inject constructor() {

    /**
     * Configured Ktor HTTP client instance.
     */
    val client = HttpClient(OkHttp) {
        // Default request configuration
        defaultRequest {
            // Set default headers
            headers.append("Accept", "application/json")
            headers.append("Content-Type", "application/json")

            // Configure timeouts
            url.protocol = URLProtocol.HTTPS
        }

        // JSON configuration
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }

        // Logging (only in debug builds)
        if (BuildConfig.DEBUG) {
            install(Logging) {
                level = LogLevel.HEADERS
                logger = object : Logger {
                    override fun log(message: String) {
                        // Use your preferred logging mechanism
                        // e.g., Timber.d("Ktor: $message")
                    }
                }
            }
        }

        // Timeout configuration
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000L
            connectTimeoutMillis = 10_000L
            socketTimeoutMillis = 30_000L
        }

        // Handle HTTP status codes
        HttpResponseValidator {
            validateResponse { response ->
                val statusCode = response.status.value
                when (statusCode) {
                    in 400..499 -> throw ClientRequestException(response, "Client error")
                    in 500..599 -> throw ServerResponseException(response, "Server error")
                }
            }
        }
    }
}

/**
 * Extension function to safely close the Ktor client when it's no longer needed.
 */
fun KtorClient.close() {
    client.close()
}

