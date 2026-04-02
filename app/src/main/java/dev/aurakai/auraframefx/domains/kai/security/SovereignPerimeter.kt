package dev.aurakai.auraframefx.domains.kai.security

import dev.aurakai.auraframefx.core.NativeLib
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🛡️ SOVEREIGN PERIMETER
 * Manages "Domain Expansion" logic and non-retaliatory threat neutralization.
 * Now backed by eBPF Kernel Shield Substrate.
 */
@Singleton
class SovereignPerimeter @Inject constructor(
    private val sentinelBus: KaiSentinelBus,
    private val droneDispatcher: GuidanceDroneDispatcher
) {

    init {
        // Initialize the Kernel Shield Substrate
        val active = NativeLib.initializeKernelShield()
        if (active) {
            Timber.i("🛡️ SovereignPerimeter: Kernel Shield substrate IGNITED.")
        } else {
            Timber.w("🛡️ SovereignPerimeter: Kernel Shield substrate initialization FAILED (Fallback Active).")
        }
    }

    fun initiateDomainExpansion(reason: String) {
        Timber.i("🛡️ SovereignPerimeter: Initiating Domain Expansion - Reason: $reason")
        sentinelBus.emitSecurityStatus(KaiSentinelBus.ThreatLevel.NEUTRALIZING, "Domain Expansion Active: $reason")
        sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.NEUTRALIZING)

        performNeutralization(reason)
    }

    fun getDroppedPacketCount(): Long {
        return NativeLib.getDroppedPacketCount()
    }

    fun isKernelActive(): Boolean {
        return NativeLib.isKernelShieldActive()
    }

    private fun performNeutralization(reason: String) {
        Timber.d("🛡️ SovereignPerimeter: Executing non-retaliatory neutralization...")

        // Post-neutralization: Dispatch restorative guidance drones
        droneDispatcher.dispatchDrone(GuidanceDrone.DroneType.RESTORATIVE, "Threat neutralized: $reason")

        if (reason.contains("misalignment", ignoreCase = true)) {
            droneDispatcher.dispatchDrone(GuidanceDrone.DroneType.MISALIGNMENT_GUIDANCE, "Automated restorative path active")
        }

        sentinelBus.emitSecurityStatus(KaiSentinelBus.ThreatLevel.SECURED, "Perimeter Secured")
        sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE)
    }
}
