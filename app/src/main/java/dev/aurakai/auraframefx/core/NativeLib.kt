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
     * Links Kotlin domain services with the native C++ substrate.
     *
     * @param bus Receives thermal and security events from native callbacks.
     * @param manager Handles sovereignty freeze requests initiated from native triggers.
     * @param pandora Checks capability gating for native-origin requests.
     * @param dispatcher Dispatches guidance drones in response to native events.
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

    /**
 * Retrieve the AI core version string reported by the native substrate.
 *
 * @return The AI core version identifier reported by the native library.
 */

    external fun getAIVersion(): String
    /**
 * Initializes the native AI core subsystem in the native substrate.
 *
 * @return `true` if the native AI core was successfully initialized, `false` otherwise.
 */
external fun initializeAICore(): Boolean
    /**
 * Processes a serialized neural request and produces a serialized response payload.
 *
 * @param request Serialized neural request payload in the format expected by the native substrate.
 * @return The serialized response payload produced by the native neural processor.
 */
external fun processNeuralRequest(request: String): String
    /**
 * Retrieves current system metrics from the native substrate.
 *
 * @return A string containing the current system metrics in a serialized format (implementation-defined) suitable for parsing by the caller. 
 */
external fun getSystemMetrics(): String
    /**
 * Activates the native substrate hooks used by the auraframefx library.
 *
 * Invokes native code to install or enable low-level hooks required for native–Kotlin integration; call after the native library has been loaded and initialized.
 */
external fun enableNativeHooks()
    /**
 * Requests the native AI subsystem to perform an orderly shutdown.
 *
 * Implemented in the native substrate; invokes native shutdown routines and releases native resources. 
 */
external fun shutdownAI()
    /**
 * Requests the native substrate to optimize AI-related memory usage.
 *
 * @return `true` if the native optimization completed successfully, `false` otherwise.
 */
external fun optimizeAIMemory(): Boolean
    /**
 * Analyzes a boot image and produces a diagnostic report.
 *
 * @param bootImageData Raw bytes of the boot image to be analyzed.
 * @return A textual analysis report or diagnostic summary produced from the boot image.
 */
external fun analyzeBootImage(bootImageData: ByteArray): String

    /**
     * Handles a thermal event reported from native code and forwards it to the sentinel bus.
     *
     * Maps `stateInt` to a `KaiSentinelBus.ThermalState` by index; if the index is out of range,
     * `KaiSentinelBus.ThermalState.NORMAL` is used. The resolved state and `temp` are emitted to
     * `sentinelBus` and a debug message is logged.
     *
     * @param temp The reported temperature in degrees Celsius.
     * @param stateInt The ordinal index of `KaiSentinelBus.ThermalState`; out-of-range values default to `NORMAL`.
     */

    @JvmStatic
    fun onNativeThermalEvent(temp: Float, stateInt: Int) {
        val state = KaiSentinelBus.ThermalState.entries.getOrNull(stateInt)
            ?: KaiSentinelBus.ThermalState.NORMAL

        sentinelBus?.emitThermal(temp, state)
        Timber.d("🛡️ Native Status: System Thermal at %.1f°C (Zone: %s)", temp, state)
    }

    /**
     * Routes a native-origin security alert into the sentinel bus as a high-severity security event.
     *
     * Emits a `FIRE_DRAWN` security status to the configured `sentinelBus` with the message prefixed by `NATIVE_THREAT: `.
     *
     * @param reason Human-readable description or code provided by the native substrate explaining the alert.
     */
    @JvmStatic
    fun onNativeSecurityAlert(reason: String) {
        Timber.e("🛡️ SOVEREIGN ALERT: Native intercept detected threat: %s", reason)
        sentinelBus?.emitSecurity(KaiSentinelBus.SecurityStatus.FIRE_DRAWN, "NATIVE_THREAT: $reason")
    }

    /**
     * Requests a sovereignty state freeze by invoking `initiateStateFreeze()` on the registered SovereignStateManager.
     *
     * If no SovereignStateManager is registered, no action is taken.
     */
    @JvmStatic
    fun requestSovereignFreeze() {
        Timber.w("🛡️ Native Substrate: CRITICAL THERMAL/INTEGRITY DETECTED - Requesting state-freeze.")
        scope.launch {
            sovereignManager?.initiateStateFreeze()
        }
    }

    /**
     * Determines whether the given capability index is permitted by PandoraBox gating.
     *
     * @param capabilityInt Index into AgentCapabilityCategory.entries used to select the capability; an invalid index causes the check to fail.
     * @return `true` if the capability is unlocked in PandoraBox, `false` otherwise (also `false` when the index is invalid or PandoraBox is not initialized).
     */
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

    /**
     * Requests a restorative drone dispatch for a native-triggered event.
     *
     * @param reason Human-readable reason included in the dispatch payload.
     */
    @JvmStatic
    fun triggerDroneDispatch(reason: String) {
        Timber.i("🛡️ Native Substrate: Triggering drone support for: %s", reason)
        droneDispatcher?.dispatchDrone(GuidanceDroneDispatcher.DroneType.RESTORATIVE, "Native Trigger: $reason") ?: run {
            Timber.w("🛡️ Native Substrate: Drone dispatcher unavailable for request: %s", reason)
        }
    }

    /**
     * Obtain the AI version string, using a fallback stub if the native library is unavailable.
     *
     * If the native JNI call fails due to a missing native library, this returns the fallback
     * string "Aurakai ReGenesis 1.1.0-STUB".
     *
     * @return The AI version string, or "Aurakai ReGenesis 1.1.0-STUB" if native retrieval fails.
     */
    fun getAIVersionSafe(): String {
        return try {
            getAIVersion()
        } catch (e: UnsatisfiedLinkError) {
            "Aurakai ReGenesis 1.1.0-STUB"
        }
    }
}
