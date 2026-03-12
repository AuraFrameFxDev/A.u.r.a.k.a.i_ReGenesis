package dev.aurakai.auraframefx.aura.ui

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import dev.aurakai.auraframefx.core.GenesisOrchestrator
import dev.aurakai.auraframefx.core.NativeLib
import dev.aurakai.auraframefx.services.security.IntegrityMonitorService
import timber.log.Timber
import javax.inject.Inject

/**
 * AurakaiApplication - Genesis-OS Root Manager
 *
 * ⚠️ CRITICAL: Must have @HiltAndroidApp for dependency injection to work!
 */
@HiltAndroidApp
open class AurakaiApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var orchestrator: GenesisOrchestrator

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()

        try {
            // === PHASE 0: Logging Bootstrap ===
            setupLogging()
            Timber.i("🚀 Genesis-OS Platform initializing...")

            // === PHASE 1: Native AI Runtime ===
            initializeNativeAIPlatform()

            // === PHASE 2: Agent Domain Initialization via GenesisOrchestrator ===
            if (::orchestrator.isInitialized) {
                Timber.i("⚡ Igniting Genesis Orchestrator...")
                orchestrator.initializePlatform()
            } else {
                Timber.w("⚠️ GenesisOrchestrator not injected - running in degraded mode")
            }

            Timber.i("✅ Genesis-OS Platform ready for operation")

        } catch (e: Exception) {
            Timber.e(e, "❌ CRITICAL: Genesis-OS initialization failed")
            // Graceful degradation - app continues but with limited functionality
        }
        // Start background integrity monitor (kept from AuraFrameApplication)
        try {
            startService(Intent(this, IntegrityMonitorService::class.java))
        } catch (t: Throwable) {
            // Be defensive: service may not be available in test contexts
            Timber.tag("AurakaiApplication").w(t, "Failed to start IntegrityMonitorService")
        }
    }

    private fun setupLogging() {
        Timber.plant(Timber.DebugTree())
        Timber.d("🐛 Debug logging enabled")
    }

    private fun initializeNativeAIPlatform() {
        try {
            val aiInitialized = NativeLib.initializeAISafe()
            val aiVersion = NativeLib.getAIVersionSafe()

            Timber.i("🤖 Native AI Platform v%s", aiVersion)
            Timber.i("🧠 AI Runtime: %s", if (aiInitialized) "ONLINE" else "OFFLINE")

            if (!aiInitialized) {
                Timber.w("⚠️  Native AI initialization returned false - degraded mode")
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Native AI Platform initialization failed")
        }
    }

    override fun onTerminate() {
        super.onTerminate()

        try {
            Timber.i("🔄 Genesis-OS Platform shutting down...")

            if (::orchestrator.isInitialized) {
                orchestrator.shutdownPlatform()
            }

            try {
                NativeLib.shutdownAISafe()
                Timber.i("✓ Native AI Platform shut down")
            } catch (e: Exception) {
                Timber.e(e, "Failed to shut down native AI platform")
            }

            Timber.i("👋 Genesis-OS terminated gracefully")

        } catch (e: Exception) {
            Timber.e(e, "⚠️  Error during platform shutdown")
        }
    }
}
