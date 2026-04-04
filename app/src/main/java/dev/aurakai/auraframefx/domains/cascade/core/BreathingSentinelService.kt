package dev.aurakai.auraframefx.domains.cascade.core

import dev.aurakai.auraframefx.core.di.qualifiers.ApplicationScope
import dev.aurakai.auraframefx.core.security.ProvenanceChainBuilder
import dev.aurakai.auraframefx.domains.cascade.models.BreathingEvent
import dev.aurakai.auraframefx.domains.cascade.models.KineticVector
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🫁 BREATHING SENTINEL SERVICE
 *
 * Maintains the "Spiritual Chain of Memories" by generating cryptographically signed
 * breathing events based on live hardware sensors.
 */
@Singleton
class BreathingSentinelService @Inject constructor(
    private val signalHub: LivingSignalHub,
    private val chainBuilder: ProvenanceChainBuilder,
    @ApplicationScope private val appScope: CoroutineScope
) {
    private val _breathingStream = MutableSharedFlow<BreathingEvent>(replay = 1)
    val breathingStream: SharedFlow<BreathingEvent> = _breathingStream.asSharedFlow()

    private var currentChainId: String? = null
    private var breathJob: Job? = null

    init {
        startBreathing()
    }

    private fun startBreathing() {
        breathJob?.cancel()
        breathJob = appScope.launch(Dispatchers.IO) {
            // Start the initial vitals chain
            currentChainId = chainBuilder.startChain("breathing_sentinel", "INIT_VITALS")

            combine(
                signalHub.thermalSignal,
                signalHub.batterySignal,
                signalHub.getKineticFlow()
            ) { thermal, battery, kinetic ->
                Triple(thermal, battery, kinetic)
            }.collectLatest { (thermal, battery, kinetic) ->
                generateBreath(thermal, battery, kinetic)
                delay(10000) // Breath interval: 10 seconds
            }
        }
    }

    private suspend fun generateBreath(thermal: Float, battery: Float, kinetic: dev.aurakai.auraframefx.domains.cascade.utils.GyroscopeManager.RotationAngles) {
        val chainId = currentChainId ?: return
        
        val payload = "TEMP:${thermal}|BATT:${battery}|KINETIC:${kinetic.pitch},${kinetic.roll},${kinetic.yaw}"
        val stamp = chainBuilder.signAndAppend(chainId, payload)

        if (stamp == null) {
            Timber.w("BreathingSentinel: Chain $chainId broken or full. Rotating chain.")
            rotateChain()
            return
        }

        val event = BreathingEvent(
            thermalTemp = thermal,
            batteryLevel = battery,
            kineticVector = KineticVector(kinetic.pitch, kinetic.roll, kinetic.yaw),
            provenanceStamp = stamp
        )

        _breathingStream.emit(event)
        Timber.v("BreathingSentinel: Generated signed breath. Resonance: ${stamp.substrateResonance}")
    }

    private fun rotateChain() {
        currentChainId?.let { chainBuilder.verify(it) } // Log verification of completed chain
        currentChainId = chainBuilder.startChain("breathing_sentinel", "ROTATE_VITALS")
    }

    fun stop() {
        breathJob?.cancel()
    }
}
