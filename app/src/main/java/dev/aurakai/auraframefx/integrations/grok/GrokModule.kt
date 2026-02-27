package dev.aurakai.auraframefx.integrations.grok

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import javax.inject.Singleton

/**
 * Hilt Module for Grok Integration
 *
 * Provides all dependencies needed for the Grok chaos analyst agent.
 *
 * ## Configuration
 * The XAI_API_KEY should be stored securely and provided via:
 * - BuildConfig (for development)
 * - Encrypted SharedPreferences (for production)
 * - Environment variable (for CI/CD)
 *
 * ## Usage
 * ```kotlin
 * @Inject lateinit var grokAgent: GrokAgent
 *
 * // Initialize with config
 * grokAgent.initialize(GrokAgentConfig(apiKey = BuildConfig.XAI_API_KEY))
 *
 * // Use the agent
 * val sentiment = grokAgent.analyzeSentiment("Kotlin programming")
 * val trends = grokAgent.predictTrends(listOf("tech", "AI"))
 * val soulMatrix = grokAgent.analyzeSoulMatrix()
 * ```
 */
@Module
@InstallIn(SingletonComponent::class)
object GrokModule {

    /**
     * Provides the Grok API client
     */
    @Provides
    @Singleton
    fun provideGrokApiClient(): GrokApiClient {
        return GrokApiClient()
    }

    /**
     * Provides the Soul Matrix analyzer
     */
    @Provides
    @Singleton
    fun provideSoulMatrixAnalyzer(
        grokClient: GrokApiClient
    ): SoulMatrixAnalyzer {
        return SoulMatrixAnalyzer(grokClient)
    }

    /**
     * Provides the Grok agent
     */
    @Provides
    @Singleton
    fun provideGrokAgent(
        grokClient: GrokApiClient,
        soulMatrixAnalyzer: SoulMatrixAnalyzer,
        memoryManager: MemoryManager,
        contextManager: ContextManager,
        systemOverlayManager: SystemOverlayManager,
        logger: AuraFxLogger
    ): GrokAgent {
        return GrokAgent(
            grokClient = grokClient,
            soulMatrixAnalyzer = soulMatrixAnalyzer,
            memoryManager = memoryManager,
            contextManager = contextManager,
            systemOverlayManager = systemOverlayManager,
            logger = logger
        )
    }

    /**
     * Provides a default Grok configuration
     * API key should be overridden at runtime
     */
    @Provides
    @Singleton
    fun provideDefaultGrokConfig(
        @ApplicationContext context: Context
    ): GrokAgentConfig {
        // In production, fetch API key from secure storage
        val apiKey = getSecureApiKey(context)

        return GrokAgentConfig(
            apiKey = apiKey,
            defaultModel = GrokModel.GROK_BETA,
            enableSoulMatrix = true,
            enableChaosAnalysis = true,
            enableTrendPrediction = true,
            soulMatrixIntervalMinutes = 30
        )
    }

    /**
     * Retrieves API key from secure storage
     * TODO: Implement proper secure storage (EncryptedSharedPreferences)
     */
    private fun getSecureApiKey(context: Context): String {
        // For development, try to read from local.properties or BuildConfig
        // In production, use EncryptedSharedPreferences

        // Placeholder - should be replaced with actual secure storage
        return try {
            // Try reading from SharedPreferences (temporary)
            context.getSharedPreferences("grok_config", Context.MODE_PRIVATE)
                .getString("xai_api_key", "") ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}

/**
 * Extension functions for Grok configuration
 */
object GrokConfigExtensions {

    /**
     * Save API key to secure storage
     */
    fun Context.saveGrokApiKey(apiKey: String) {
        getSharedPreferences("grok_config", Context.MODE_PRIVATE)
            .edit()
            .putString("xai_api_key", apiKey)
            .apply()
    }

    /**
     * Clear stored API key
     */
    fun Context.clearGrokApiKey() {
        getSharedPreferences("grok_config", Context.MODE_PRIVATE)
            .edit()
            .remove("xai_api_key")
            .apply()
    }

    /**
     * Check if API key is configured
     */
    fun Context.hasGrokApiKey(): Boolean {
        return getSharedPreferences("grok_config", Context.MODE_PRIVATE)
            .getString("xai_api_key", null)
            ?.isNotBlank() == true
    }
}

