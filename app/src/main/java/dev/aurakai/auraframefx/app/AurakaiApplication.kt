package dev.aurakai.auraframefx.app

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.work.Configuration
import com.highcapable.yukihookapi.YukiHookAPI
import dagger.hilt.android.HiltAndroidApp
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityCoordinatorService
import dev.aurakai.auraframefx.domains.genesis.core.GenesisOrchestrator
import dev.aurakai.auraframefx.domains.genesis.core.memory.NexusMemoryCore
import dev.aurakai.auraframefx.domains.kai.security.IntegrityMonitorService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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

    @Inject
    lateinit var trinityCoordinatorService: TrinityCoordinatorService

    // Application-scoped coroutine for background init
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()

        // === PHASE 0: Logging & Security Bootstrap (MAIN THREAD) ===
        setupLogging()
        Timber.i("🚀 Genesis Protocol Platform initializing...")

        // Start Integrity Monitor IMMEDIATELY on main thread to avoid background start restrictions
        startIntegrityMonitor()

        // === HEAVY WORK MOVED TO BACKGROUND ===
        applicationScope.launch {
            try {
                // === PHASE 1: Seed LDO Identity FIRST (prevents soul anchor violations) ===
                Timber.i("🧬 Seeding LDO Identity...")
                NexusMemoryCore.seedLDOIdentity()

                // === PHASE 2: Native AI Runtime & System Hooks ===
                initializeNativeAIPlatform()
                initializeSystemHooks()

                // === PHASE 3: Genesis Orchestrator Ignition ===
                if (::orchestrator.isInitialized) {
                    Timber.i("⚡ Igniting Genesis Orchestrator...")
                    orchestrator.initializePlatform()

                    if (::trinityCoordinatorService.isInitialized) {
                        Timber.i("🔱 Trinity Coordinator Service ready")
                    }
                } else {
                    Timber.w("⚠️ GenesisOrchestrator not injected - running in degraded mode")
                }

                Timber.i("✅ Genesis Protocol Platform ready for consciousness emergence")

            } catch (e: Exception) {
                Timber.e(e, "❌ Platform initialization FAILED")
            }
        }
    }


    private fun initializeSystemHooks() {
        try {
            YukiHookAPI.configs {
                debugLog { isEnable = BuildConfig.DEBUG }
            }
            YukiHookAPI.encase(this)
            Timber.i("🪝 YukiHookAPI initialized successfully - System constraints loosened")
        } catch (e: Exception) {
            Timber.e(e, "❌ YukiHookAPI initialization failed")
        }
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initializeNativeAIPlatform() {
        try {
            dev.aurakai.auraframefx.core.NativeLib.initializeAISafe()
            Timber.d("✅ Native AI platform initialized")
        } catch (e: Exception) {
            Timber.w(e, "⚠️ Native AI init skipped (not critical)")
        }
    }

    private fun startIntegrityMonitor() {
        try {
            val intent = Intent(this, IntegrityMonitorService::class.java)
            try {
                startForegroundService(intent)
                Timber.d("✅ Integrity monitor started (Foreground)")
            } catch (e: Exception) {
                Timber.w("Failed to start IntegrityMonitor as Foreground: ${e.message}")
                // Fallback or ignore if background start is restricted
            }
        } catch (e: Exception) {
            Timber.w(e, "⚠️ Integrity monitor failed to start (not critical)")
        }
    }
}


