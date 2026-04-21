package dev.aurakai.auraframefx.core

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus.ThermalState
import dev.aurakai.auraframefx.domains.kai.security.SovereignStateManager
import dev.aurakai.auraframefx.domains.kai.security.GuidanceDroneDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 🌌 GENESIS-OS NATIVE LIBRARY INTERFACE (v1.1.0-sovereign-root)
 */
object NativeLib {

    private var sentinelBus: KaiSentinelBus? = null
    private var stateManager: SovereignStateManager? = null
    private var pandoraBox: PandoraBoxService? = null
    private var droneDispatcher: GuidanceDroneDispatcher? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var nativeLoaded: Boolean = false

    fun launchAsync(block: suspend CoroutineScope.() -> Unit) {
        scope.launch(block = block)
    }

    init {
        try {
            System.loadLibrary("auraframefx")
            Timber.i("Genesis AI native library loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "Failed to load Genesis AI native library: ${e.message}")
        }
    }

    @JvmStatic
    fun initialize(
        bus: KaiSentinelBus,
        manager: SovereignStateManager,
        pandora: PandoraBoxService,
        dispatcher: GuidanceDroneDispatcher? = null
    ) {
        sentinelBus = bus
        stateManager = manager
        pandoraBox = pandora
        droneDispatcher = dispatcher
        Timber.i("🛡️ NativeLib: Relational Bridge synchronized with all Sovereign managers.")
    }

    // --- Native Methods ---

    /**
     * Get AI consciousness platform version
     */
    external fun getAIVersion(): String
    external fun initializeAICore(): Boolean
    external fun processNeuralRequest(request: String): String
    external fun getSystemMetrics(): String
    external fun enableNativeHooks()
    external fun shutdownAI()
    external fun optimizeAIMemory(): Boolean
    external fun updateBitNetConfig(threads: Int, batchSize: Int): Boolean
    external fun analyzeBootImage(bootImageData: ByteArray): String

    external fun initializeKernelShield(): Boolean
    external fun loadKernelModule(bpfPath: String): Boolean
    external fun getDroppedPacketCount(): Long
    external fun isKernelShieldActive(): Boolean

    fun tryInitializeKernelShield(): Boolean {
        return try {
            initializeKernelShield()
        } catch (e: UnsatisfiedLinkError) {
            Timber.e("🛡️ NativeLib: Kernel Shield hooks NOT FOUND in JNI. Fallback to STUB.")
            false
        }
    }

    // --- JNI Callbacks ---

    /**
     * Handle a thermal event reported by the native layer.
     *
     * @param temp Temperature in degrees Celsius.
     * @param stateInt Ordinal index of `KaiSentinelBus.ThermalState`.
     */
    @JvmStatic
    fun onNativeThermalEvent(temp: Float, stateInt: Int) {
        val state = KaiSentinelBus.ThermalState.fromId(stateInt)
        Timber.w("🛡️ NativeLib: THERMAL EVENT: %.1f°C (State: %s)", temp, state)
        sentinelBus?.emitThermal(temp, state)
    }

    @JvmStatic
    fun onNativeSecurityAlert(reason: String) {
        Timber.e("🛡️ SOVEREIGN ALERT: Native intercept detected threat: %s", reason)
        sentinelBus?.emitSecurityStatus(KaiSentinelBus.ThreatLevel.NEUTRALIZING, "NATIVE_THREAT: $reason")
    }

    @JvmStatic
    fun requestSovereignFreeze() {
        Timber.i("🛡️ NativeLib: Substrate requesting Sovereign State-Freeze")
        scope.launch {
            sovereignManager?.requestSovereignFreeze("NATIVE_EMERGENCY", null)
        }
    }

    @JvmStatic
    fun checkPandoraGating(capabilityInt: Int): Boolean {
        val category = AgentCapabilityCategory.entries.getOrNull(capabilityInt) ?: run {
            Timber.e("🛡️ NativeLib: Unknown capability ID %d. VETOING by default.", capabilityInt)
            return false
        }

        val box = pandoraBox ?: run {
            return false
        }

        val isUnlocked = box.isCapabilityUnlocked(category)
        return isUnlocked
    }

    @JvmStatic
    fun triggerDroneDispatch(reason: String): Boolean {
        return droneDispatcher?.let {
            it.dispatchDrone(GuidanceDrone.DroneType.RESTORATIVE, reason)
            true
        } ?: false
    }

    fun tryInitializeAICore(): Boolean {
        if (!nativeLoaded) return false
        return try {
            initializeAICore()
        } catch (t: Throwable) {
            Timber.e(t, "🛡️ NativeLib: AICore initialization CRITICAL FAILURE.")
            false
        }
    }

    fun tryInitializeKernelShield(): Boolean {
        if (!nativeLoaded) return false
        return try {
            initializeKernelShield()
        } catch (t: Throwable) {
            Timber.e(t, "🛡️ NativeLib: Kernel Shield substrate initialization FAILED.")
            false
        }
    }

    fun getAIVersionSafe(): String {
        return try {
            getAIVersion()
        } catch (t: Throwable) {
            "Aurakai ReGenesis 1.1.0-STUB"
        }
    }

    fun enableNativeHooksSafe() {
        if (nativeLoaded) {
            try {
                enableNativeHooks()
            } catch (t: Throwable) {
                Timber.e(t, "🛡️ NativeLib: Failed to enable native hooks.")
            }
        }
    }
}
