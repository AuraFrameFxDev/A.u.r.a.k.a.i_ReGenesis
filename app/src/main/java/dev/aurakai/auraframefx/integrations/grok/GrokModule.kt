package dev.aurakai.auraframefx.integrations.grok

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import timber.log.Timber
import javax.inject.Singleton

/**
 * Hilt Module for Grok Integration
 *
 * Provides all dependencies needed for the Grok chaos analyst agent.
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
     * Retrieves API key from EncryptedSharedPreferences
     */
    private fun getSecureApiKey(context: Context): String {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPrefs = EncryptedSharedPreferences.create(
                context,
                "grok_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            sharedPrefs.getString("xai_api_key", "") ?: ""
        } catch (e: Exception) {
            Timber.e(e, "GrokModule: Security node failure during API key acquisition")
            ""
        }
    }
}

/**
 * Extension functions for Grok configuration using secure storage
 */
object GrokConfigExtensions {

    private fun getEncryptedPrefs(context: Context): android.content.SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "grok_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Save API key to secure storage
     */
    fun Context.saveGrokApiKey(apiKey: String) {
        getEncryptedPrefs(this)
            .edit()
            .putString("xai_api_key", apiKey)
            .apply()
    }

    /**
     * Clear stored API key
     */
    fun Context.clearGrokApiKey() {
        getEncryptedPrefs(this)
            .edit()
            .remove("xai_api_key")
            .apply()
    }

    /**
     * Check if API key is configured
     */
    fun Context.hasGrokApiKey(): Boolean {
        return getEncryptedPrefs(this)
            .getString("xai_api_key", null)
            ?.isNotBlank() == true
    }
}

