package dev.aurakai.auraframefx.core

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelBus.ThermalState
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
     *
     * Stores the provided service references on the singleton so JNI callbacks and other Kotlin
     * code can access the sentinel bus, sovereign manager, pandora box, and drone dispatcher.
     *
     * @param bus The KaiSentinelBus used to emit thermal and security events.
     * @param manager The SovereignStateManager responsible for initiating state freeze actions.
     * @param pandora The PandoraBoxService used to evaluate capability gating.
     * @param dispatcher The GuidanceDroneDispatcher used to request drone support.
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

    /**
 * Retrieves the AI subsystem version reported by the native substrate.
 *
 * @return The version string reported by the native library (for example, a semantic version or build identifier).
 */

    external fun getAIVersion(): String
    /**
 * Initialize the native AI core and prepare it for operation.
 *
 * @return `true` if the native AI core initialized successfully, `false` otherwise.
 */
    private external fun initializeAICoreNative(): Boolean

    fun initializeAICore(): Boolean {
        if (!nativeLoaded) {
            Timber.w("🛡️ NativeLib: Cannot initialize AI core - native library not loaded")
            return false
        }
        return try {
            initializeAICoreNative()
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "❌ NativeLib: AI core initialization failed")
            false
        }
    }
    /**
 * Processes a neural request through the native AI core and produces a textual response.
 *
 * @param request The input payload or prompt to be handled by the native neural processor.
 * @return The response string produced by the native AI core.
 */
external fun processNeuralRequest(request: String): String
    /**
 * Retrieves a snapshot of current system metrics from the native substrate.
 *
 * @return A string representation of the current system metrics. */
external fun getSystemMetrics(): String
    /**
 * Activates native hooks inside the native substrate to install platform integrations and callbacks.
 *
 * Triggers native-side state changes required for interoperability between the JVM and the native library.
 */
external fun enableNativeHooks()
    /**
 * Initiates an orderly shutdown of the native AI subsystem and releases its native resources.
 */
external fun shutdownAI()
    /**
 * Requests the native substrate to optimize AI-related memory usage.
 *
 * @return `true` if the native optimizer reports success, `false` otherwise.
 */
external fun optimizeAIMemory(): Boolean
    /**
 * Analyzes a boot image and produces a diagnostic analysis report.
 *
 * @param bootImageData The raw boot image bytes to analyze.
 * @return A diagnostic analysis report as a String. */
external fun analyzeBootImage(bootImageData: ByteArray): String

    /**
     * Handle a thermal event reported by the native layer.
     *
     * Resolves `stateInt` to a `KaiSentinelBus.ThermalState` using stable ID mapping (defaults to `NORMAL` if invalid)
     * and, if configured, emits the temperature and resolved state to the sentinel bus.
     *
     * @param temp Temperature in degrees Celsius.
     * @param stateInt Stable ID of `KaiSentinelBus.ThermalState`; unrecognized values map to `NORMAL`.
     */

    @JvmStatic
    fun onNativeThermalEvent(temp: Float, stateInt: Int) {
        val state = KaiSentinelBus.ThermalState.fromId(stateInt)

        sentinelBus?.emitThermal(temp, state)
        Timber.d("🛡️ Native Status: System Thermal at %.1f°C (Zone: %s)", temp, state)
    }

    /**
     * Handle a security alert originating from native code by logging it and notifying the sentinel bus.
     *
     * Emits a security event with status `FIRE_DRAWN` and payload prefixed with `"NATIVE_THREAT: "`.
     *
     * @param reason The native-provided description or code for the detected threat.
     */
    @JvmStatic
    fun onNativeSecurityAlert(reason: String) {
        Timber.e("🛡️ SOVEREIGN ALERT: Native intercept detected threat: %s", reason)
        sentinelBus?.emitSecurity(KaiSentinelBus.SecurityStatus.FIRE_DRAWN, "NATIVE_THREAT: $reason")
    }

    /**
     * Requests the sovereign manager to initiate a system state freeze.
     *
     * The freeze request is dispatched on the object's internal IO coroutine scope and does not block the caller.
     */
    @JvmStatic
    fun requestSovereignFreeze() {
        Timber.w("🛡️ Native Substrate: CRITICAL THERMAL/INTEGRITY DETECTED - Requesting state-freeze.")
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
        // Future Phase 2 implementation point
    }

    // Fallback implementations for when native library isn't available
    fun getAIVersionSafe(): String {
        return try {
            getAIVersion()
        } catch (e: UnsatisfiedLinkError) {
            "Genesis-OS AI Platform 1.0 (Native library not available)"
        }
    }

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

    /**
     * Retrieve the AI version reported by the native substrate, with a safe fallback when the native library is not present.
     *
     * @return The native AI version string, or "Aurakai ReGenesis 1.1.0-STUB" if the native symbol cannot be loaded.
     */
    fun getAIVersionSafe(): String {
        return try {
            getAIVersion()
        } catch (e: UnsatisfiedLinkError) {
            "Aurakai ReGenesis 1.1.0-STUB"
        }
    }
}