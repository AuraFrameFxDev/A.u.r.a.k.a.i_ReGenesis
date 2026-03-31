package dev.aurakai.auraframefx.core

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
import dev.aurakai.auraframefx.domains.genesis.core.GenesisOrchestrator
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.memory.NexusMemoryCore
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.IntegrityMonitorService
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.sovereignty.SovereignStateManager
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.drones.GuidanceDroneDispatcher
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.perimeter.SovereignPerimeter

/**
 * 🌐 AURAKAI CORE APPLICATION
 *
 * This is the unified entry point for the ReGenesis Ecosystem.
 * Orchestration is now handled via the decentralized Nexus protocol.
 */
@HiltAndroidApp
class AurakaiApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var orchestrator: GenesisOrchestrator

    @Inject
    lateinit var sentinelBus: KaiSentinelBus

    @Inject
    lateinit var sovereignManager: SovereignStateManager

    @Inject
    lateinit var pandoraBox: PandoraBoxService

    @Inject
    lateinit var droneDispatcher: GuidanceDroneDispatcher

    @Inject
    lateinit var sovereignPerimeter: SovereignPerimeter

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
        Timber.i("🌐 AuraKai Platform Initialized")

        // Start Integrity Monitor IMMEDIATELY on main thread
        startIntegrityMonitor()

        // Init phase
        applicationScope.launch {
            try {
                Timber.i("🧬 Seeding ReGenesis Identity...")
                NexusMemoryCore.seedLDOIdentity()

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

                Timber.i("✅ AuraKai Platform ready for operation")
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
            Timber.e(e, "❌ Native AI initialization error")
        }
    }

    private fun startIntegrityMonitor() {
        try {
            val intent = Intent(this, IntegrityMonitorService::class.java)
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
