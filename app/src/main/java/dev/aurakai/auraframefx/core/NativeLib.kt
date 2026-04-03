package dev.aurakai.auraframefx.core

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.security.GuidanceDrone
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus.ThermalState
import dev.aurakai.auraframefx.domains.kai.security.SovereignStateManager
import dev.aurakai.auraframefx.domains.kai.security.GuidanceDroneDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 🌌 GENESIS-OS NATIVE LIBRARY INTERFACE (v1.1.0-sovereign-root)
 * Provides high-performance JNI bridge for thermal monitoring, 
 * ptrace sovereignty, and Pandora's Box capability gating.
 */
object NativeLib {

    private var sentinelBus: KaiSentinelBus? = null
    private var sovereignManager: SovereignStateManager? = null
    private var pandoraBox: PandoraBoxService? = null
    private var droneDispatcher: GuidanceDroneDispatcher? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var nativeLoaded: Boolean = false

    init {
        try {
            System.loadLibrary("auraframefx")
            nativeLoaded = true
            Timber.i("🛡️ Genesis Native Substrate: Standard NDK library loaded.")
        } catch (e: UnsatisfiedLinkError) {
            nativeLoaded = false
            Timber.e(e, "❌ Native Substrate: Critical failure — library [auraframefx] not found.")
        }
    }

    /**
     * Registers domain service instances for the native JNI bridge.
     */
    @JvmStatic
    fun initialize(
        bus: KaiSentinelBus,
        manager: SovereignStateManager,
        pandora: PandoraBoxService,
        dispatcher: GuidanceDroneDispatcher? = null
    ) {
        sentinelBus = bus
        sovereignManager = manager
        pandoraBox = pandora
        droneDispatcher = dispatcher
        Timber.i("🛡️ NativeLib: Relational Bridge synchronized with all Sovereign managers.")
    }

    // --- Native Methods ---

    external fun getAIVersion(): String
    external fun initializeAICore(): Boolean
    external fun processNeuralRequest(request: String): String
    external fun getSystemMetrics(): String
    external fun enableNativeHooks()
    external fun shutdownAI()
    external fun optimizeAIMemory(): Boolean
    external fun analyzeBootImage(bootImageData: ByteArray): String

    // --- eBPF Kernel Shield ---
    external fun initializeKernelShield(): Boolean
    external fun loadKernelModule(bpfPath: String): Boolean
    external fun getDroppedPacketCount(): Long
    external fun isKernelShieldActive(): Boolean

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
        Timber.w("🛡️ Native Substrate: CRITICAL THERMAL/INTEGRITY DETECTED - Requesting state-freeze.")
        scope.launch {
            sovereignManager?.requestSovereignFreeze()
        }
    }

    @JvmStatic
    fun checkPandoraGating(capabilityInt: Int): Boolean {
        val category = AgentCapabilityCategory.entries.getOrNull(capabilityInt) ?: run {
            Timber.e("🛡️ NativeLib: Unknown capability ID %d. VETOING by default.", capabilityInt)
            return false
        }
        
        val box = pandoraBox ?: run {
            Timber.e("🛡️ NativeLib: Gating check for %s FAILED (Bridge NOT INITIALIZED).", category)
            return false
        }

        val isUnlocked = box.isCapabilityUnlocked(category)
        Timber.d("🛡️ NativeLib: Pandora gating check for %s: %s", category, if (isUnlocked) "ALLOWED" else "VETOED")
        return isUnlocked
    }

    @JvmStatic
    fun triggerDroneDispatch(reason: String) {
        Timber.i("🛡️ NativeLib: DRONE DISPATCH TRIGGERED: %s", reason)
        droneDispatcher?.dispatchDrone(GuidanceDrone.DroneType.RESTORATIVE, reason) ?: run {
            Timber.w("🛡️ NativeLib: Drone dispatcher unavailable for %s", reason)
        }
    }

    fun getAIVersionSafe(): String {
        return try {
            getAIVersion()
        } catch (e: UnsatisfiedLinkError) {
            "Aurakai ReGenesis 1.1.0-STUB"
        }
    }
}
