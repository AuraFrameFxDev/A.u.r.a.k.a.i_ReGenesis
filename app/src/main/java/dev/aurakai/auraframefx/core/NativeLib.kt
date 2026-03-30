package dev.aurakai.auraframefx.core

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.sovereignty.SovereignStateManager
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.drones.GuidanceDroneDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 🌌 GENESIS-OS NATIVE LIBRARY INTERFACE (v1.1.0-sovereign-root)
 * High-performance JNI bridge for thermal monitoring,
 * ptrace sovereignty checks, Pandora's Box gating,
 * and Guidance Drone dispatch.
 */
object NativeLib {

    private var sentinelBus: KaiSentinelBus? = null
    private var sovereignManager: SovereignStateManager? = null
    private var pandoraBox: PandoraBoxService? = null
    private var droneDispatcher: GuidanceDroneDispatcher? = null

    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        try {
            System.loadLibrary("auraframefx")
            Timber.i("🛡️ NativeLib: Genesis AI native substrate loaded successfully.")
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "❌ NativeLib: Failed to load native substrate.")
        }
    }

    /**
     * Initialize the JNI bridge with all required managers.
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
        Timber.i("🛡️ NativeLib: JNI Bridge fully initialized with Sovereign services.")
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

    // --- JNI Callbacks (Called from C++) ---

    @JvmStatic
    fun onNativeThermalEvent(temp: Float, stateInt: Int) {
        val state = KaiSentinelBus.ThermalState.entries.getOrNull(stateInt)
            ?: KaiSentinelBus.ThermalState.NORMAL

        sentinelBus?.emitThermal(temp, state)
        Timber.d("🛡️ Native thermal event: %.1f°C (State: %s)", temp, state)
    }

    @JvmStatic
    fun onNativeSecurityAlert(reason: String) {
        Timber.w("🛡️ NativeLib: SECURITY ALERT: %s", reason)
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
            Timber.e("🛡️ NativeLib: Unknown capability ID %d → VETO (fail-closed)", capabilityInt)
            return false
        }

        val box = pandoraBox ?: run {
            Timber.e("🛡️ NativeLib: PandoraBox not initialized → VETO")
            return false
        }

        val isUnlocked = box.isCapabilityUnlocked(category)
        Timber.d("🛡️ Pandora gating for %s: %s", category, if (isUnlocked) "ALLOWED" else "VETOED")
        return isUnlocked
    }

    @JvmStatic
    fun triggerDroneDispatch(reason: String) {
        Timber.i("🛡️ NativeLib: DRONE DISPATCH TRIGGERED: %s", reason)
        droneDispatcher?.dispatch("native_substrate", reason) ?: run {
            Timber.w("🛡️ DroneDispatcher unavailable for trigger: %s", reason)
        }
    }

    // --- Legacy / Compatibility Helpers ---

    fun getAIVersionSafe(): String = try { getAIVersion() } catch (e: UnsatisfiedLinkError) { "1.1.0-fallback" }
    fun initializeAISafe(): Boolean = try { initializeAICore() } catch (e: UnsatisfiedLinkError) { true }
    fun getSystemMetricsSafe(): String = try { getSystemMetrics() } catch (e: UnsatisfiedLinkError) { "{}" }
    fun enableNativeHooksSafe() { try { enableNativeHooks() } catch (e: UnsatisfiedLinkError) { Timber.w("Native hooks unavailable") } }
}
