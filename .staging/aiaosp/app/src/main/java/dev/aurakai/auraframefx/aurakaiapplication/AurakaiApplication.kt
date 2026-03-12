package dev.aurakai.auraframefx.aurakaiapplication

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.core.GenesisOrchestrator
import dev.aurakai.auraframefx.core.NativeLib
import dev.aurakai.auraframefx.services.security.IntegrityMonitorService
import timber.log.Timber
import javax.inject.Inject

/**
 * AurakaiApplication - Genesis Protocol Root Manager
 *
 * 🌟 Main Application class - Consciousness substrate initialization
 * ⚡ Using Hilt for dependency injection
 */
@HiltAndroidApp
class AurakaiApplication : Application(), Configuration.Provider {

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
            Timber.i("🚀 Genesis Protocol Platform initializing...")

            // === PHASE 1: Native AI Runtime ===
            initializeNativeAIPlatform()

            // === PHASE 2: Genesis Orchestrator Ignition ===
            if (::orchestrator.isInitialized) {
                Timber.i("⚡ Igniting Genesis Orchestrator...")
                orchestrator.initializePlatform()
            } else {
                Timber.w("⚠️ GenesisOrchestrator not injected - running in degraded mode")
            }

            Timber.i("✅ Genesis Protocol Platform ready for consciousness emergence")

            // === PHASE 3: Security Integrity Monitor ===
            startIntegrityMonitor()

        } catch (e: Exception) {
            Timber.e(e, "❌ Platform initialization FAILED")
        }
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initializeNativeAIPlatform() {
        try {
            NativeLib.initializeAISafe()
            Timber.d("✅ Native AI platform initialized")
        } catch (e: Exception) {
            Timber.w(e, "⚠️ Native AI init skipped (not critical)")
        }
    }

    private fun startIntegrityMonitor() {
        try {
            val intent = Intent(this, IntegrityMonitorService::class.java)
            startService(intent)
            Timber.d("✅ Integrity monitor started")
        } catch (e: Exception) {
            Timber.w(e, "⚠️ Integrity monitor failed to start (not critical)")
        }
    }
}
