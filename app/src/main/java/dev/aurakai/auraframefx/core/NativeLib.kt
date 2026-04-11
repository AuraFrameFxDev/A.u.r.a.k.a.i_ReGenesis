package dev.aurakai.auraframefx.core

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.sovereignty.SovereignStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 🌌 GENESIS-OS NATIVE LIBRARY INTERFACE (v1.1.0-sovereign-root)
 * Provides high-performance JNI bridge for thermal monitoring, 
 * ptrace sovereignty, and Pandora's Box capability gating.
 */
object NativeLib {

    private var sentinelBus: KaiSentinelBus? = null
    private var stateManager: SovereignStateManager? = null
    private var pandoraBox: PandoraBoxService? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        try {
            System.loadLibrary("auraframefx")
            Timber.i("🛡️ NativeLib: Genesis AI native substrate loaded successfully.")
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "❌ NativeLib: Failed to load native substrate.")
        }
    }

    /**
     * Configure the NativeLib bridge with Kotlin-side services used by native callbacks.
     *
     * @param bus Bus used to emit thermal and security events.
     * @param manager Manager used to initiate sovereignty state changes.
     * @param pandora Service used to evaluate capability gating.
     */
    @JvmStatic
    fun initialize(
        bus: KaiSentinelBus,
        manager: SovereignStateManager,
        pandora: PandoraBoxService
    ) {
        sentinelBus = bus
        stateManager = manager
        pandoraBox = pandora
        Timber.i("🛡️ NativeLib bridge initialized with Kotlin managers")
    }

    /**
     * Initializes NativeLib with runtime service delegates used by native callbacks.
     *
     * @param bus Kotlin bus for emitting thermal and security events.
     * @param manager Manager used to initiate sovereignty/state changes.
     * @param pandora Service used to evaluate capability gating.
     * @param dispatcher Dispatcher responsible for outbound drone dispatch requests.
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
    /**
 * Requests the native substrate to optimize the AI runtime's memory usage.
 *
 * @return `true` if the native optimization succeeded, `false` otherwise.
 */
external fun optimizeAIMemory(): Boolean
    /**
 * Analyze a boot image and produce a diagnostic report.
 *
 * @param bootImageData Raw bytes of the boot image to analyze.
 * @return A string containing analysis results or diagnostics.
external fun analyzeBootImage(bootImageData: ByteArray): String

    /**
     * Handles a thermal event from the native substrate and forwards it to the sentinel bus.
     *
     * Maps `stateInt` to `KaiSentinelBus.ThermalState` by ordinal; if the ordinal is invalid, `ThermalState.NORMAL`
     * is used. The temperature and resolved state are emitted to the configured `sentinelBus`.
     *
     * @param temp The measured temperature in degrees Celsius.
     * @param stateInt Ordinal of the `KaiSentinelBus.ThermalState`.
     */

    @JvmStatic
    fun onNativeThermalEvent(temp: Float, stateInt: Int) {
        val state = KaiSentinelBus.ThermalState.entries.getOrNull(stateInt) ?: KaiSentinelBus.ThermalState.NORMAL
        sentinelBus?.emitThermal(temp, state)
        Timber.d("🛡️ NativeLib: Thermal event from substrate: %.1f°C (%s)", temp, state)
    }

    /**
     * Handles security alerts sent from the native substrate.
     *
     * Logs the provided reason at warning level.
     *
     * @param reason Human-readable reason or code describing the security alert.
     */
    @JvmStatic
    fun onNativeSecurityAlert(reason: String) {
        Timber.w("🛡️ NativeLib: SECURITY ALERT: %s", reason)
        // Hardening: Could trigger immediate lock or notify bus
    }

    /**
     * Requests that the system enter a sovereign state freeze.
     *
     * Schedules an asynchronous invocation of the configured SovereignStateManager's
     * state-freeze initiation. If no manager is configured, this call has no effect.
     */
    @JvmStatic
    fun requestSovereignFreeze() {
        Timber.i("🛡️ NativeLib: Substrate requesting Sovereign State-Freeze")
        scope.launch {
            stateManager?.initiateStateFreeze()
        }
    }

    /**
     * Checks whether a native-substrate capability code is permitted by the Pandora gating service.
     *
     * Maps `capabilityInt` to an `AgentCapabilityCategory` (unknown values map to `ROOT`) and queries the configured `PandoraBoxService` for unlock status.
     *
     * @param capabilityInt Integer index into `AgentCapabilityCategory.entries` representing the capability to check.
     * @return `true` if the mapped capability is unlocked by the `PandoraBoxService`, `false` otherwise (including when the service is unavailable).
     */
    @JvmStatic
    fun checkPandoraGating(capabilityInt: Int): Boolean {
        val category = AgentCapabilityCategory.entries.getOrNull(capabilityInt) ?: AgentCapabilityCategory.ROOT
        val isUnlocked = pandoraBox?.isCapabilityUnlocked(category) ?: false
        Timber.d("🛡️ NativeLib: Pandora gating check for %s: %s", category, if (isUnlocked) "ALLOWED" else "VETOED")
        return isUnlocked
    }

    /**
     * Notify the system that the native substrate requested a drone dispatch for the provided reason.
     *
     * This implementation currently records the request via logging; actual dispatch execution is not performed here.
     *
     * @param reason Human-readable rationale for the requested dispatch.
     */
    @JvmStatic
    fun triggerDroneDispatch(reason: String) {
        Timber.i("🛡️ NativeLib: DRONE DISPATCH TRIGGERED: %s", reason)
        // Future Phase 2 implementation point
    }

    /**
     * Obtain the AI platform version string, falling back to a stable identifier if the native library is unavailable.
     *
     * @return The AI platform version string; if the native library cannot be loaded (`UnsatisfiedLinkError`), returns
     *         "Genesis-OS AI Platform 1.0 (Native library not available)".
     */
    fun getAIVersionSafe(): String {
        return try {
            getAIVersion()
        } catch (e: UnsatisfiedLinkError) {
            "Genesis-OS AI Platform 1.0 (Native library not available)"
        }
    }

    /**
     * Called from native code when the substrate reports a thermal event.
     *
     * Maps `stateInt` to `KaiSentinelBus.ThermalState` by index (defaults to `NORMAL` if invalid)
     * and emits the temperature and resolved state to the configured `sentinelBus` if present.
     *
     * @param temp Temperature in degrees Celsius.
     * @param stateInt Integer index into `KaiSentinelBus.ThermalState` entries. Defaults to `NORMAL` when out of range.
     */
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
        // [FIX] CodeRabbit: Deny unknown capability IDs (Fail-Closed)
        val category = AgentCapabilityCategory.entries.getOrNull(capabilityInt) ?: run {
            Timber.e("🛡️ NativeLib: Unknown capability ID %d. VETOING by default.", capabilityInt)
            return false
        }
        
        // [FIX] Qodo: Log if bridge not initialized
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
        // [FIX] CodeRabbit: Implement actual dispatch instead of just logging
        Timber.i("🛡️ NativeLib: DRONE DISPATCH TRIGGERED: %s", reason)
        droneDispatcher?.dispatch("native_substrate", reason) ?: run {
            Timber.w("🛡️ NativeLib: Drone dispatcher unavailable for %s", reason)
        }
    }
}
