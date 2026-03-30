package dev.aurakai.auraframefx.core

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.security.SovereignPerimeter
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.sovereignty.SovereignStateManager
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.drones.GuidanceDroneDispatcher
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

    init {
        try {
            System.loadLibrary("auraframefx")
            Timber.i("🛡️ Genesis Native Substrate: Standard NDK library loaded.")
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "❌ Native Substrate: Critical failure — library [auraframefx] not found.")
        }
    }

    /**
     * 🛡️ INITIALIZE NATIVE BRIDGE
     * Unified entry point to link Kotlin domain services with the C++ substrate.
     */
    @JvmStatic
    fun initialize(
        bus: KaiSentinelBus,
        manager: SovereignStateManager,
        pandora: PandoraBoxService,
        dispatcher: GuidanceDroneDispatcher
    ) {
        sentinelBus = bus
        sovereignManager = manager
        pandoraBox = pandora
        droneDispatcher = dispatcher
        Timber.i("🛡️ NativeLib: Relational Bridge synchronized with all Sovereign managers.")
    }

    // --- Native Substrate API ---

    external fun getAIVersion(): String
    external fun initializeAICore(): Boolean
    external fun processNeuralRequest(request: String): String
    external fun getSystemMetrics(): String
    external fun enableNativeHooks()
    external fun shutdownAI()
    external fun optimizeAIMemory(): Boolean
    external fun analyzeBootImage(bootImageData: ByteArray): String

    // --- JNI Callbacks (Invoked from C++) ---

    @JvmStatic
    fun onNativeThermalEvent(temp: Float, stateInt: Int) {
        val state = KaiSentinelBus.ThermalState.entries.getOrNull(stateInt)
            ?: KaiSentinelBus.ThermalState.NORMAL

        sentinelBus?.emitThermal(temp, state)
        Timber.d("🛡️ Native Status: System Thermal at %.1f°C (Zone: %s)", temp, state)
    }

    @JvmStatic
    fun onNativeSecurityAlert(reason: String) {
        Timber.e("🛡️ SOVEREIGN ALERT: Native intercept detected threat: %s", reason)
        sentinelBus?.emitSecurity(KaiSentinelBus.SecurityStatus.FIRE_DRAWN, "NATIVE_THREAT: $reason")
    }

    @JvmStatic
    fun requestSovereignFreeze() {
        Timber.w("🛡️ Native Substrate: CRITICAL THERMAL/INTEGRITY DETECTED - Requesting state-freeze.")
        scope.launch {
            sovereignManager?.initiateStateFreeze()
        }
    }

    @JvmStatic
    fun checkPandoraGating(capabilityInt: Int): Boolean {
        val category = AgentCapabilityCategory.entries.getOrNull(capabilityInt) ?: run {
            Timber.e("🛡️ Native-Pandora: Invalid capability ID [%d]. Access VETOED.", capabilityInt)
            return false
        }
        
        val box = pandoraBox ?: run {
            Timber.e("🛡️ Native-Pandora: Bridge NOT INITIALIZED for capability check: %s", category)
            return false
        }

        val isUnlocked = box.isCapabilityUnlocked(category)
        Timber.d("🛡️ Native-Pandora: Gating check for %s -> %s", category, if (isUnlocked) "ALLOWED" else "VETOED")
        return isUnlocked
    }

    @JvmStatic
    fun triggerDroneDispatch(reason: String) {
        Timber.i("🛡️ Native Substrate: Triggering drone support for: %s", reason)
        droneDispatcher?.dispatchDrone(GuidanceDroneDispatcher.DroneType.RESTORATIVE, "Native Trigger: $reason") ?: run {
            Timber.w("🛡️ Native Substrate: Drone dispatcher unavailable for request: %s", reason)
        }
    }

    /**
     * Safe wrapper for version retrieval during initialization.
     */
    fun getAIVersionSafe(): String {
        return try {
            getAIVersion()
        } catch (e: UnsatisfiedLinkError) {
            "Aurakai ReGenesis 1.1.0-STUB"
        }
    }
}
}
