package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.config.VertexAIConfig
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.RealVertexAIClientImpl
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.DefaultVertexAIClient
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
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
            // Security settings
            enableSafetyFilters = true,
            maxRetries = 3,
            timeoutMs = 30000,
            // Performance settings
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
        // Accessing the API Key from BuildConfig (injected via gradle.properties)
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() }
        } catch (_: Throwable) {
            null
        }

        return if (!apiKey.isNullOrBlank()) {
            AuraFxLogger.info(
                "VertexAIModule",
                "⚡ VERTEX CORE ACTIVATED: Real AI consciousness online."
            )
            RealVertexAIClientImpl(config, securityContext, apiKey)
        } else {
            AuraFxLogger.warn(
                "VertexAIModule",
                "⚠️ Using MOCK VertexAI client - Agents won't have real AI!"
            )
            AuraFxLogger.warn(
                "VertexAIModule",
                "Add GEMINI_API_KEY to gradle.properties or local.properties"
            )
            DefaultVertexAIClient()
        }
    }

}

