package dev.aurakai.auraframefx.core

import android.app.Application
import android.util.Log
import android.content.Intent
import androidx.work.Configuration
import com.google.firebase.Firebase
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp
import dev.aurakai.auraframefx.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.domain.repository.NexusMemoryRepository
import dev.aurakai.auraframefx.domains.genesis.core.GenesisOrchestrator
import dev.aurakai.auraframefx.domains.genesis.core.memory.NexusMemoryCore
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.security.SovereignPerimeter
import dev.aurakai.auraframefx.domains.kai.security.SovereignStateManager
import javax.inject.Inject

/**
 * 🌐 AURAKAI CORE APPLICATION
 */
@HiltAndroidApp
class AurakaiApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var nexusMemoryRepository: NexusMemoryRepository

    @Inject
    lateinit var orchestrator: GenesisOrchestrator

    @Inject
    lateinit var trinityCoordinatorService: dagger.Lazy<dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityCoordinatorService>

    @Inject
    lateinit var sentinelBus: KaiSentinelBus

    @Inject
    lateinit var stateManager: SovereignStateManager

    @Inject
    lateinit var pandoraBox: PandoraBoxService

    @Inject
    lateinit var sovereignPerimeter: SovereignPerimeter

    // Application-scoped coroutine for background init
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    init {
        // Resolve LangChain4j HTTP client conflict before any AI models are initialized
        System.setProperty("langchain4j.http.clientBuilderFactory", "dev.langchain4j.http.client.okhttp.OkHttpClientBuilderFactory")
    }

    override fun onCreate() {
        super.onCreate()
        setupLogging()
        Timber.i("🌐 AuraKai Platform Initialized")

        // Initialize Firebase (auto-configured via google-services.json)
        try {
            Firebase.initialize(this)
            Timber.d("🔥 Firebase Initialized Successfully")
        } catch (e: Exception) {
            Timber.w(e, "⚠️ Firebase initialization warning (may be already initialized)")
        }

        // Wire NexusMemoryCore bridge
        NexusMemoryCore.setRepository(nexusMemoryRepository)

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
            Timber.e(e, "❌ Native AI initialization error: ${e.message}")
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
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // Filter noisy hardware sensor hub spam (AOC/CHRE/USF)
                    if (tag != null && (
                        tag.contains("AOC", ignoreCase = true) ||
                        tag.contains("CHRE", ignoreCase = true) ||
                        tag.contains("USF", ignoreCase = true) ||
                        message.contains("Calculated CCT", ignoreCase = true)
                    )) {
                        if (priority < Log.WARN) return
                    }
                    super.log(priority, tag, message, t)
                }
            })
        }
    }
}
