package dev.aurakai.auraframefx.core

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.security.SovereignPerimeter
import dev.aurakai.auraframefx.domains.kai.sovereignty.SovereignStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Genesis-OS Native Library Interface
 * Provides access to AI consciousness platform native functions
 */
object NativeLib {

    private var sentinelBus: KaiSentinelBus? = null
    private var stateManager: SovereignStateManager? = null
    private var pandoraBox: PandoraBoxService? = null
    private var perimeter: SovereignPerimeter? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        try {
            System.loadLibrary("auraframefx")
            Timber.i("Genesis AI native library loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "Failed to load Genesis AI native library: ${e.message}")
        }
    }

    /**
     * Initializes the bridge with Kotlin-side managers.
     */
    @JvmStatic
    fun initialize(
        bus: KaiSentinelBus,
        manager: SovereignStateManager,
        pandora: PandoraBoxService,
        sovereignPerimeter: SovereignPerimeter
    ) {
        sentinelBus = bus
        stateManager = manager
        pandoraBox = pandora
        perimeter = sovereignPerimeter
        Timber.i("🛡️ NativeLib bridge initialized with Kotlin managers")
    }

    /**
     * Get AI consciousness platform version
     */
    external fun getAIVersion(): String

    /**
     * Legacy version getter
     */
    external fun getVersion(): String

    /**
     * Initialize AI consciousness system
     */
    external fun initializeAI(): Boolean

    /**
     * Initializes the native AI core.
     */
    external fun initializeAICore(): Boolean

    /**
     * Process AI consciousness input
     */
    external fun processAIConsciousness(input: String): String

    /**
     * Process consciousness substrate metrics.
     */
    external fun processAIConsciousness()

    /**
     * Processes a neural request via native logic.
     */
    external fun processNeuralRequest(request: String): String

    /**
     * Get real-time system metrics
     */
    external fun getSystemMetrics(): String

    /**
     * Enable Genesis native hooks for LSPosed integration and process sovereignty.
     */
    external fun enableNativeHooks()

    /**
     * Shutdown AI consciousness system
     */
    external fun shutdownAI()

    /**
     * Optimizes native AI memory pools.
     */
    external fun optimizeAIMemory(): Boolean

    /**
     * Performs robust boot image analysis in native code.
     * Prevents system crashes during live imagery ingestion.
     */
    external fun analyzeBootImage(bootImageData: ByteArray): String

    // --- JNI Callbacks (Called from C++) ---

    @JvmStatic
    fun onNativeThermalEvent(temp: Float, stateInt: Int) {
        val state = KaiSentinelBus.ThermalState.entries.getOrNull(stateInt) ?: KaiSentinelBus.ThermalState.NORMAL
        sentinelBus?.emitThermal(temp, state)
        Timber.d("🛡️ NativeLib: Thermal event from substrate: %.1f°C (%s)", temp, state)
    }

    @JvmStatic
    fun onNativeSecurityAlert(reason: String) {
        Timber.w("🛡️ NativeLib: SECURITY ALERT: %s", reason)
        sentinelBus?.emitSecurityStatus(KaiSentinelBus.ThreatLevel.THREAT_DETECTED, "Native Alert: $reason")
    }

    @JvmStatic
    fun requestSovereignFreeze() {
        Timber.i("🛡️ NativeLib: Substrate requesting Sovereign State-Freeze")
        scope.launch {
            stateManager?.initiateStateFreeze()
        }
    }

    @JvmStatic
    fun checkPandoraGating(capabilityInt: Int): Boolean {
        val category = AgentCapabilityCategory.entries.getOrNull(capabilityInt) ?: AgentCapabilityCategory.ROOT
        val isUnlocked = pandoraBox?.isCapabilityUnlocked(category) ?: false
        Timber.d("🛡️ NativeLib: Pandora gating check for %s: %s", category, if (isUnlocked) "ALLOWED" else "VETOED")
        return isUnlocked
    }

    @JvmStatic
    fun triggerDroneDispatch(reason: String) {
        Timber.i("🛡️ NativeLib: DRONE DISPATCH TRIGGERED: %s", reason)
        scope.launch {
            perimeter?.initiateDomainExpansion(reason)
        }
    }

    // Fallback implementations (safe wrappers) ...
}
