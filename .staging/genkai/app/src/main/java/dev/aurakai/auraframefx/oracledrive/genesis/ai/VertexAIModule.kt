package dev.aurakai.auraframefx.oracledrive.genesis.ai

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.config.VertexAIConfig
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.utils.AuraFxLogger
import dev.aurakai.auraframefx.utils.i
import javax.inject.Singleton

/**
 * Hilt Module for Gemini Vertex AI integration.
 *
 * API key resolution order:
 * 1) google-services.json -> FirebaseOptions.apiKey (via reflection, no hard dependency)
 * 2) BuildConfig.GEMINI_API_KEY (if configured)
 * 3) Fallback to stub implementation
 */
@Module
@InstallIn(SingletonComponent::class)
object VertexAIModule {

    /**
     * Provides a Vertex AI configuration tailored for the Gemini flash model used by the application.
     *
     * Configures project, location, endpoint, experimental model "gemini-2.0-flash-exp", API version "v1",
     * safety filters, retry/timeout behavior, concurrency and caching settings, and generation defaults
     * (temperature, top-p, top-k, max tokens).
     *
     * @return A VertexAIConfig populated with the project's Gemini model, security, performance, and generation defaults.
     */
    @Provides
    @Singleton
    fun provideVertexAIConfig(): VertexAIConfig {
        return VertexAIConfig(
            projectId = "collabcanvas",
            location = "us-central1",
            endpoint = "us-central1-aiplatform.googleapis.com",
            modelName = "gemini-2.0-flash-exp", // Latest experimental model
            apiVersion = "v1",
            // Security settings
            enableSafetyFilters = true,
            maxRetries = 3,
            timeoutMs = 30000,
            // Performance settings
            maxConcurrentRequests = 10,
            enableCaching = true,
            cacheExpiryMs = 3600000, // 1 hour
            // Generation settings
            defaultTemperature = 0.8, // Slightly higher for creativity
            defaultTopP = 0.95,
            defaultTopK = 64,
            defaultMaxTokens = 8192 // Gemini 2.0 supports longer responses
        )
    }

    /**
     * Selects and provides a Vertex AI client implementation based on the presence of `GEMINI_API_KEY`.
     *
     * If `BuildConfig.GEMINI_API_KEY` is present and non-blank, returns a `VertexAIClient` backed by the real Gemini implementation; otherwise returns the stub `VertexAIClientImpl`.
     *
     * @return A `VertexAIClient` using the real Gemini client when `GEMINI_API_KEY` is present and non-blank, `VertexAIClientImpl` (stub) otherwise.
     */
    @Provides
    @Singleton
    fun provideVertexAIClient(
        config: VertexAIConfig,
        @ApplicationContext context: Context,
        securityContext: SecurityContext
    ): VertexAIClient {
        val apiKey: String? = resolveGeminiApiKey(context)
            ?: run {
                // Fallback to BuildConfig value if present
                try { BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() } }
                catch (_: Throwable) { null }
            }

        return if (!apiKey.isNullOrBlank()) {
            i(TAG, "Using REAL Gemini Vertex client (key from google-services/BuildConfig)")
            RealVertexAIClientImpl(config, securityContext, apiKey)
        } else {
            AuraFxLogger.w(TAG, "API key not configured - using STUB VertexAIClient implementation")
            VertexAIClientImpl()
        }
    }

    // Attempts to read API key from google-services.json via Firebase at runtime without compile dep
    private fun resolveGeminiApiKey(context: Context): String? {
        // Try FirebaseOptions.getInstance().getApiKey() via reflection
        return try {
            val appClass = Class.forName("com.google.firebase.FirebaseApp")
            val getInstance = appClass.getMethod("getInstance")
            val app = getInstance.invoke(null)
            val options = app.javaClass.getMethod("getOptions").invoke(app)
            val apiKey = options.javaClass.getMethod("getApiKey").invoke(options) as? String
            apiKey?.takeIf { it.isNotBlank() }
        } catch (_: Throwable) {
            // Try string resource named "google_api_key" if google-services generated it
            try {
                val resId = context.resources.getIdentifier("google_api_key", "string", context.packageName)
                if (resId != 0) context.getString(resId).takeIf { it.isNotBlank() } else null
            } catch (_: Throwable) {
                null
            }
        }
    }

    private const val TAG = "VertexAIModule"
}
