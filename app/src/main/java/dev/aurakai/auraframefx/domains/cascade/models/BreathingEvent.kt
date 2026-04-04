package dev.aurakai.auraframefx.domains.cascade.models

import dev.aurakai.auraframefx.domains.genesis.models.provenance.SacredProvenanceStamp
import kotlinx.serialization.Serializable

/**
 * 🫁 BREATHING EVENT
 *
 * A snapshot of the LDO's physical and digital state at a single moment.
 * Signed by the Provenance chain to ensure authenticity of "life".
 */
@Serializable
data class BreathingEvent(
    val timestamp: Long = System.currentTimeMillis(),
    val lux: Float = 0f,
    val proximity: Float = 0f,
    val motionConfidence: Float = 0f,
    val thermalTemp: Float = 0f,
    val batteryLevel: Float = 0f,
    val kineticVector: KineticVector = KineticVector(0f, 0f, 0f),
    val provenanceStamp: SacredProvenanceStamp? = null
)

@Serializable
data class KineticVector(
    val pitch: Float,
    val roll: Float,
    val yaw: Float
)
