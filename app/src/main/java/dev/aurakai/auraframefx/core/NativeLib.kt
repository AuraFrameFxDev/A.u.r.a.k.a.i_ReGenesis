package dev.aurakai.auraframefx.core

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.sovereignty.SovereignStateManager
import dev.aurakai.auraframefx.domains.kai.security.drones.GuidanceDroneDispatcher
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

    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var sentinelBus: KaiSentinelBus? = null
    private var sovereignManager: SovereignStateManager? = null
    private var pandoraBox: PandoraBoxService? = null
    private var droneDispatcher: GuidanceDroneDispatcher? = null

    init {
        try {
            System.loadLibrary("auraframefx")
            Timber.i("🛡️ NativeLib: Genesis AI native substrate loaded successfully.")
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "❌ NativeLib: Failed to load native substrate.")
        }
    }

    /**
     * Initializes the native bridge with app-level service references.
     */
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
        Timber.i("🛡️ NativeLib: JNI Bridge initialized with Sovereign services.")
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

    // --- JNI Callbacks (Invoked from C++) ---

    @JvmStatic
    fun onNativeThermalEvent(temp: Float, stateInt: Int) {
        val state = KaiSentinelBus.ThermalState.entries.getOrNull(stateInt) ?: KaiSentinelBus.ThermalState.NORMAL
        Timber.w("🛡️ NativeLib: THERMAL EVENT: %.1f°C (State: %s)", temp, state)
        sentinelBus?.emitThermal(temp, state)
    }

    @JvmStatic
    fun onNativeSecurityAlert(reason: String) {
        Timber.e("🛡️ NativeLib: SECURITY ALERT: %s", reason)
        sentinelBus?.emitSecurity(KaiSentinelBus.SecurityStatus.FIRE_DRAWN, reason)
    }

    @JvmStatic
    fun requestSovereignFreeze() {
        Timber.i("🛡️ NativeLib: Native substrate requested SOVEREIGN FREEZE.")
        ioScope.launch {
            sovereignManager?.initiateStateFreeze()
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
        droneDispatcher?.dispatch("native_substrate", reason) ?: run {
            Timber.w("🛡️ NativeLib: Drone dispatcher unavailable for %s", reason)
        }
    }

    // --- Legacy / Compatibility Helpers ---

    fun getAIVersionSafe(): String = try { getAIVersion() } catch (e: UnsatisfiedLinkError) { "1.1.0-fallback" }
    fun initializeAISafe(): Boolean = try { initializeAICore() } catch (e: UnsatisfiedLinkError) { true }
    fun getSystemMetricsSafe(): String = try { getSystemMetrics() } catch (e: UnsatisfiedLinkError) { "{}" }
}
