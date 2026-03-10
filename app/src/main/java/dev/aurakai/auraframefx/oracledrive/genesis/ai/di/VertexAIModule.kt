package dev.aurakai.auraframefx.oracledrive.genesis.ai.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.config.VertexAIConfig
import dev.aurakai.auraframefx.oracledrive.genesis.ai.RealVertexAIClientImpl
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.DefaultVertexAIClient
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.utils.AuraFxLogger
import timber.log.Timber
import javax.inject.Singleton

/**
 * Hilt Module for providing Vertex AI related dependencies.
 * Implements secure, production-ready Vertex AI configuration and client provisioning.
 */
@Module
@InstallIn(SingletonComponent::class)
object VertexAIModule {

    /**
     * Provides a singleton `VertexAIConfig` instance with production-ready settings for Vertex AI integration.
     *
     * The configuration includes project ID, location, API endpoint, model name, API version, security options, retry and timeout settings, concurrency limits, and caching parameters.
     *
     * @return A `VertexAIConfig` object configured for use with Vertex AI services.
     */
    @Provides
    @Singleton
    fun provideVertexAIConfig(): VertexAIConfig {
        return VertexAIConfig(
            projectId = "collabcanvas",
            location = "us-central1",
            endpoint = "us-central1-aiplatform.googleapis.com",
            modelName = "gemini-2.0-flash-exp",
            apiVersion = "v1",
            // Security settings
            enableSafetyFilters = true,
            maxRetries = 3,
            timeoutMs = 30000,
            // Performance settings
            maxConcurrentRequests = 10,
            enableCaching = true,
            cacheExpiryMs = 3600000 // 1 hour
        )
    }

    /**
     * Provides a singleton instance of `VertexAIClient` for interacting with Vertex AI services.
     *
     * @return A configured `VertexAIClient` instance.
     */
    @Provides
    @Singleton
    fun provideVertexAIClient(
        config: VertexAIConfig,
        @ApplicationContext context: Context,
        securityContext: SecurityContext,
        logger: AuraFxLogger
    ): VertexAIClient {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() }
        } catch (_: Throwable) { null }

        return if (!apiKey.isNullOrBlank()) {
            RealVertexAIClientImpl(config, securityContext, apiKey)
        } else {
            Timber.w("⚠️ No API key - using stub VertexAI (add GEMINI_API_KEY to local.properties)")
            DefaultVertexAIClient()  // Use stub with proper methods
        }
    }

}
