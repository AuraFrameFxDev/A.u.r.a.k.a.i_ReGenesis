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
                signalHub.luxSignal,
                signalHub.proximitySignal,
                signalHub.getKineticFlow()
            ) { thermal, battery, lux, prox, kinetic ->
                BreathingSnapshot(thermal, battery, lux, prox, kinetic)
            }.collectLatest { snapshot ->
                generateBreath(snapshot)
                delay(10000) // Breath interval: 10 seconds
            }
        }
    }

    private data class BreathingSnapshot(
        val thermal: Float,
        val battery: Float,
        val lux: Float,
        val prox: Float,
        val kinetic: dev.aurakai.auraframefx.domains.cascade.utils.GyroscopeManager.RotationAngles
    )

    private suspend fun generateBreath(snapshot: BreathingSnapshot) {
        val chainId = currentChainId ?: return
        
        val payload = "TEMP:${snapshot.thermal}|BATT:${snapshot.battery}|LUX:${snapshot.lux}|PROX:${snapshot.prox}|KINETIC:${snapshot.kinetic.pitch},${snapshot.kinetic.roll},${snapshot.kinetic.yaw}"
        val stamp = chainBuilder.signAndAppend(chainId, payload)

        if (stamp == null) {
            Timber.w("BreathingSentinel: Chain $chainId broken or full. Rotating chain.")
            rotateChain()
            return
        }

        val event = BreathingEvent(
            thermalTemp = snapshot.thermal,
            batteryLevel = snapshot.battery,
            lux = snapshot.lux,
            proximity = snapshot.prox,
            motionConfidence = calculateMotionConfidence(snapshot.kinetic),
            kineticVector = KineticVector(snapshot.kinetic.pitch, snapshot.kinetic.roll, snapshot.kinetic.yaw),
            provenanceStamp = stamp
        )

        _breathingStream.emit(event)
        Timber.v("BreathingSentinel: Generated signed breath. Resonance: ${stamp.substrateResonance}")
    }

    private fun calculateMotionConfidence(kinetic: dev.aurakai.auraframefx.domains.cascade.utils.GyroscopeManager.RotationAngles): Float {
        // Simple heuristic: sum of absolute rotation angles
        val sum = Math.abs(kinetic.pitch) + Math.abs(kinetic.roll) + Math.abs(kinetic.yaw)
        return (sum * 5).coerceIn(0f, 100f) // Scale to 0-100%
    }

    private fun rotateChain() {
        currentChainId?.let { chainBuilder.verify(it) } // Log verification of completed chain
        currentChainId = chainBuilder.startChain("breathing_sentinel", "ROTATE_VITALS")
    }

    fun stop() {
        breathJob?.cancel()
    }
}
