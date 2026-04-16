package dev.aurakai.auraframefx.core

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.security.SovereignPerimeter
import dev.aurakai.auraframefx.domains.kai.security.SovereignStateManager
import dev.aurakai.auraframefx.domains.kai.security.GuidanceDroneDispatcher
import dev.aurakai.auraframefx.domains.kai.security.GuidanceDrone
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
    private var perimeter: SovereignPerimeter? = null
    private var droneDispatcher: GuidanceDroneDispatcher? = null
    private val scope = CoroutineScope(Dispatchers.IO)

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

    /**
     * Registers domain service instances for the native JNI bridge.
     */
    @JvmStatic
    fun initialize(
        bus: KaiSentinelBus,
        manager: SovereignStateManager,
        pandora: PandoraBoxService,
        sovereignPerimeter: SovereignPerimeter,
        dispatcher: GuidanceDroneDispatcher
    ) {
        sentinelBus = bus
        stateManager = manager
        pandoraBox = pandora
        perimeter = sovereignPerimeter
        droneDispatcher = dispatcher
        Timber.i("🛡️ NativeLib bridge initialized with Kotlin managers")
    }

    // --- Native Methods ---

    external fun getAIVersion(): String

    /**
     * Initialize the native AI core and prepare it for operation.
     *
     * @return `true` if the native AI core initialized successfully, `false` otherwise.
     */
    external fun initializeAICore(): Boolean

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
     * @return A string representation of the current system metrics.
     */
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
     * @return A diagnostic analysis report as a String.
     */
    external fun analyzeBootImage(bootImageData: ByteArray): String

    // --- eBPF Kernel Shield Native Hooks ---
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
        val states = KaiSentinelBus.ThermalState.values()
        val state = states.getOrNull(stateInt) ?: KaiSentinelBus.ThermalState.NORMAL
        Timber.w("🛡️ NativeLib: THERMAL EVENT: %.1f°C (State: %s)", temp, state)
        sentinelBus?.emitThermal(temp, state)
        Timber.d("🛡️ Native Status: System Thermal at %.1f°C (Zone: %s)", temp, state)
    }

    @JvmStatic
    fun onNativeSecurityAlert(reason: String) {
        Timber.e("🛡️ SOVEREIGN ALERT: Native intercept detected threat: %s", reason)
        sentinelBus?.emitSecurityStatus(KaiSentinelBus.ThreatLevel.THREAT_DETECTED, "NATIVE_THREAT: $reason")
    }

    @JvmStatic
    fun requestSovereignFreeze() {
        Timber.i("🛡️ NativeLib: Substrate requesting Sovereign State-Freeze")
        scope.launch {
            stateManager?.requestSovereignFreeze("NATIVE_EMERGENCY", null)
        }
    }

    @JvmStatic
    fun checkPandoraGating(capabilityInt: Int): Boolean {
        val categories = AgentCapabilityCategory.values()
        val category = categories.getOrNull(capabilityInt) ?: run {
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
        droneDispatcher?.dispatchDrone(GuidanceDrone.DroneType.ANALYTICAL, reason) ?: run {
            Timber.w("🛡️ NativeLib: Drone dispatcher unavailable for %s", reason)
        }
    }

    /**
     * Retrieve the AI version reported by the native substrate, with a safe fallback when the native library is not present.
     */
    fun getAIVersionSafe(): String {
        return try {
            getAIVersion()
        } catch (e: UnsatisfiedLinkError) {
            "Aurakai ReGenesis 1.1.0-STUB"
        }
    }
}
