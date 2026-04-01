package dev.aurakai.auraframefx.core.security

import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Predictive veto system using Exponential Moving Average (EMA) on thermal samples.
 */
@Singleton
class PredictiveVetoMonitor @Inject constructor(
    private val sentinelBus: KaiSentinelBus
) {
    companion object {
        private const val EMA_ALPHA = 0.3f
        private const val LIGHT_THRESHOLD = 39.0f
        private const val MODERATE_THRESHOLD = 43.0f
        private const val SEVERE_THRESHOLD = 45.0f
        private const val CRITICAL_THRESHOLD = 46.5f
    }

    enum class ThermalZone { NOMINAL, LIGHT, MODERATE, SEVERE, CRITICAL }
    enum class VetoReason { THERMAL_SEVERE, THERMAL_CRITICAL, IDENTITY_DRIFT, SECURITY_BREACH }

    data class VetoDecision(
        val vetoed: Boolean,
        val reason: VetoReason? = null,
        val thermalZone: ThermalZone = ThermalZone.NOMINAL,
        val emaThermal: Float = 0f
    )

    private var emaThermal = 0f
    private var baselineIdentityVector = FloatArray(0)

    private val _currentZone = MutableStateFlow(ThermalZone.NOMINAL)
    val currentZone: StateFlow<ThermalZone> = _currentZone.asStateFlow()

    fun recordThermalSample(celsius: Float) {
        emaThermal = if (emaThermal == 0f) celsius
        else EMA_ALPHA * celsius + (1f - EMA_ALPHA) * emaThermal

        val zone = when {
            emaThermal >= CRITICAL_THRESHOLD -> ThermalZone.CRITICAL
            emaThermal >= SEVERE_THRESHOLD   -> ThermalZone.SEVERE
            emaThermal >= MODERATE_THRESHOLD -> ThermalZone.MODERATE
            emaThermal >= LIGHT_THRESHOLD    -> ThermalZone.LIGHT
            else                             -> ThermalZone.NOMINAL
        }
        if (zone != _currentZone.value) {
            Timber.d("PredictiveVetoMonitor: Thermal zone → $zone (EMA=$emaThermal°C)")
            _currentZone.value = zone
            val busState = when(zone) {
                ThermalZone.CRITICAL -> KaiSentinelBus.ThermalState.CRITICAL
                ThermalZone.SEVERE -> KaiSentinelBus.ThermalState.SEVERE
                ThermalZone.MODERATE -> KaiSentinelBus.ThermalState.WARNING
                ThermalZone.LIGHT -> KaiSentinelBus.ThermalState.LIGHT
                ThermalZone.NOMINAL -> KaiSentinelBus.ThermalState.NORMAL
            }
            sentinelBus.emitThermal(emaThermal, busState)
        }
    }

    fun isVetoActive(): Boolean = _currentZone.value == ThermalZone.CRITICAL || _currentZone.value == ThermalZone.SEVERE

    fun checkVetoConditions(): VetoDecision {
        val zone = _currentZone.value
        return when {
            zone == ThermalZone.CRITICAL -> VetoDecision(true, VetoReason.THERMAL_CRITICAL, zone, emaThermal)
            zone == ThermalZone.SEVERE   -> VetoDecision(true, VetoReason.THERMAL_SEVERE, zone, emaThermal)
            else                         -> VetoDecision(false, thermalZone = zone, emaThermal = emaThermal)
        }
    }

    fun calibrateIdentityBaseline(vector: FloatArray) {
        baselineIdentityVector = vector.copyOf()
        Timber.i("PredictiveVetoMonitor: Identity baseline set (dim=${vector.size})")
    }

    fun checkIdentityDrift(currentVector: FloatArray): Float {
        if (baselineIdentityVector.isEmpty() || currentVector.size != baselineIdentityVector.size)
            return 0f
        val dot = currentVector.zip(baselineIdentityVector.toList()).sumOf { (a, b) -> (a * b).toDouble() }
        val magA = currentVector.sumOf { (it * it).toDouble() }
        val magB = baselineIdentityVector.sumOf { (it * it).toDouble() }
        val cosine = if (magA == 0.0 || magB == 0.0) 1.0 else dot / (Math.sqrt(magA) * Math.sqrt(magB))
        return abs(1f - cosine.toFloat())
    }
}
