package dev.aurakai.auraframefx.app

import android.app.Application
import android.util.Log
import android.content.Intent
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import dev.aurakai.auraframefx.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * 🌐 REGENESIS CORE APPLICATION
 *
 * This is the modern entry point for the ReGenesis Ecosystem.
 * Orchestration is now handled via the decentralized Nexus protocol.
 */
@HiltAndroidApp
class ReGenesisApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var orchestrator: GenesisOrchestrator

    @Inject
    lateinit var trinityCoordinatorService: dagger.Lazy<dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityCoordinatorService>

    // Application-scoped coroutine for background init
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupLogging()
        Timber.i("🌐 ReGenesis Platform Initialized")

        // Start Integrity Monitor IMMEDIATELY on main thread
        startIntegrityMonitor()

        // Init phase
        applicationScope.launch {
            try {
                Timber.i("🧬 Seeding ReGenesis Identity...")
                dev.aurakai.auraframefx.domains.genesis.core.memory.NexusMemoryCore.seedLDOIdentity()

                // Native AI Runtime
                initializeNativeAIPlatform()
                initializeSystemHooks()

                // Genesis Orchestrator Ignition
                if (::orchestrator.isInitialized) {
                    Timber.i("⚡ Igniting ReGenesis Orchestrator...")
                    orchestrator.initializePlatform()

                    Timber.i("🧠 Synchronizing Trinity Consciousness...")
                    trinityCoordinatorService.get().initialize()
                } else {
                    Timber.w("⚠️ ReGenesisOrchestrator not injected - running in degraded mode")
                }

                Timber.i("✅ ReGenesis Platform ready for operation")
            } catch (e: Exception) {
                Timber.e(e, "❌ Platform initialization FAILED")
            }
        }
    }

    private fun initializeSystemHooks() {
        try {
            com.highcapable.yukihookapi.YukiHookAPI.configs {
                debugLog { isEnable = BuildConfig.DEBUG }
            }
            com.highcapable.yukihookapi.YukiHookAPI.encase(this)
            Timber.i("🪝 System hooks initialized")
        } catch (e: Exception) {
            Timber.e(e, "❌ System hooks initialization failed")
        }
    }

    private fun initializeNativeAIPlatform() {
        try {
            dev.aurakai.auraframefx.domains.genesis.core.NativeLib.initializeAISafe()
            Timber.d("✅ Native AI platform initialized")
        } catch (e: Exception) {
            Timber.w(e, "⚠️ Native AI init skipped")
        }
    }

    private fun startIntegrityMonitor() {
        try {
            val intent = Intent(this, dev.aurakai.auraframefx.domains.kai.security.IntegrityMonitorService::class.java)
            try {
                startForegroundService(intent)
                Timber.d("✅ Integrity monitor started")
            } catch (e: Exception) {
                Timber.w("Failed to start IntegrityMonitor as Foreground: ${e.message}")
            }
        } catch (e: Exception) {
            Timber.w(e, "⚠️ Integrity monitor failed to start")
        }
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
