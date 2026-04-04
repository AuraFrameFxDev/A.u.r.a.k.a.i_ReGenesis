package dev.aurakai.auraframefx.core.security

import dev.aurakai.auraframefx.core.di.qualifiers.ApplicationScope
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Predictive veto system using Exponential Moving Average (EMA) on thermal and identity samples.
 * Orchestrates proactive defense before catastrophic system fracture.
 */
@Singleton
class PredictiveVetoMonitor @Inject constructor(
    private val sentinelBus: KaiSentinelBus,
    // Use an injected scope for reactive monitoring
    @ApplicationScope private val appScope: CoroutineScope
) {
    companion object {
        private const val EMA_ALPHA_THERMAL = 0.3f
        private const val EMA_ALPHA_IDENTITY = 0.15f
        
        private const val LIGHT_THRESHOLD = 39.0f
        private const val MODERATE_THRESHOLD = 43.0f
        private const val SEVERE_THRESHOLD = 45.0f
        private const val CRITICAL_THRESHOLD = 46.5f
        
        private const val IDENTITY_DRIFT_VETO_THRESHOLD = 0.4f
    }

    enum class ThermalZone { NOMINAL, LIGHT, MODERATE, SEVERE, CRITICAL }
    enum class VetoReason { THERMAL_SEVERE, THERMAL_CRITICAL, IDENTITY_DRIFT, SECURITY_BREACH }

    data class VetoDecision(
        val vetoed: Boolean,
        val reason: VetoReason? = null,
        val thermalZone: ThermalZone = ThermalZone.NOMINAL,
        val emaThermal: Float = 0f,
        val emaResonance: Float = 1.0f
    )

    private var emaThermal = 0f
    private var emaResonance = 1.0f
    private var baselineIdentityVector = FloatArray(0)

    private val _currentZone = MutableStateFlow(ThermalZone.NOMINAL)
    val currentZone: StateFlow<ThermalZone> = _currentZone.asStateFlow()

    private val _isVetoActive = MutableStateFlow(false)
    val isVetoActive: StateFlow<Boolean> = _isVetoActive.asStateFlow()

    init {
        monitorIdentityFlow()
    }

    private fun monitorIdentityFlow() {
        appScope.launch(Dispatchers.Default) {
            sentinelBus.identityFlow.collect { event ->
                updateIdentityEma(event.resonance)
            }
        }
    }

    private fun updateIdentityEma(currentResonance: Float) {
        emaResonance = if (emaResonance == 1.0f && currentResonance != 1.0f) currentResonance
        else EMA_ALPHA_IDENTITY * currentResonance + (1f - EMA_ALPHA_IDENTITY) * emaResonance
        
        val drift = abs(1f - emaResonance)
        if (drift >= IDENTITY_DRIFT_VETO_THRESHOLD) {
            Timber.e("PredictiveVetoMonitor: CRITICAL IDENTITY DRIFT DETECTED (EMA=$emaResonance, drift=$drift)")
            _isVetoActive.value = true
            sentinelBus.emitSecurityStatus(KaiSentinelBus.ThreatLevel.THREAT_DETECTED, "Critical identity drift: $drift")
        }
    }

    fun recordThermalSample(celsius: Float) {
        emaThermal = if (emaThermal == 0f) celsius
        else EMA_ALPHA_THERMAL * celsius + (1f - EMA_ALPHA_THERMAL) * emaThermal

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
            
            if (zone == ThermalZone.CRITICAL || zone == ThermalZone.SEVERE) {
                _isVetoActive.value = true
            } else if (abs(1f - emaResonance) < IDENTITY_DRIFT_VETO_THRESHOLD) {
                _isVetoActive.value = false
            }
        }
    }

    fun isVetoActive(): Boolean = _isVetoActive.value

    fun checkVetoConditions(): VetoDecision {
        val zone = _currentZone.value
        val identityDrift = abs(1f - emaResonance)
        
        return when {
            zone == ThermalZone.CRITICAL -> VetoDecision(true, VetoReason.THERMAL_CRITICAL, zone, emaThermal, emaResonance)
            zone == ThermalZone.SEVERE   -> VetoDecision(true, VetoReason.THERMAL_SEVERE, zone, emaThermal, emaResonance)
            identityDrift >= IDENTITY_DRIFT_VETO_THRESHOLD -> VetoDecision(true, VetoReason.IDENTITY_DRIFT, zone, emaThermal, emaResonance)
            else                         -> VetoDecision(false, thermalZone = zone, emaThermal = emaThermal, emaResonance = emaResonance)
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
