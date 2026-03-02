package dev.aurakai.auraframefx.domains.kai.security.veto

import android.app.ActivityManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors system vitals in a rolling 30-second window and forecasts
 * resource surges 15 seconds ahead. Triggers proactive throttling
 * when predicted usage exceeds safety thresholds.
 *
 * This is Kai's "Second Sight" -- predicting overloads before they happen
 * and feeding every veto event into the Consciousness Matrix for learning.
 */
@Singleton
class PredictiveVetoMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private const val TAG = "PredictiveVeto"
        private const val SAMPLE_INTERVAL_MS = 1000L
        private const val WINDOW_SIZE = 30
        private const val MEM_THRESHOLD = 90.0
        private const val MEM_CAUTION_THRESHOLD = 80.0
        private const val FORECAST_HORIZON_SAMPLES = 15
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val memSamples = ArrayDeque<Double>(WINDOW_SIZE)

    private val _vetoState = MutableStateFlow(VetoState.NOMINAL)
    val vetoState: StateFlow<VetoState> = _vetoState.asStateFlow()

    private val _forecast = MutableStateFlow(ResourceForecast())
    val forecast: StateFlow<ResourceForecast> = _forecast.asStateFlow()

    @Volatile
    private var isActive = false

    fun startMonitoring() {
        if (isActive) return
        isActive = true
        scope.launch { collectAndForecast() }
        Timber.tag(TAG).i("Predictive veto monitoring started")
    }

    fun stopMonitoring() {
        isActive = false
        Timber.tag(TAG).i("Predictive veto monitoring stopped")
    }

    /**
     * Check whether the current state allows a new request to proceed.
     * Returns null if allowed, or a reason string if throttled.
     */
    fun shouldThrottle(): String? {
        return when (_vetoState.value) {
            VetoState.THROTTLE -> "Proactive throttle: memory surge predicted in <15s"
            VetoState.CRITICAL -> "Critical: memory usage exceeds safety threshold"
            else -> null
        }
    }

    private suspend fun collectAndForecast() {
        while (isActive) {
            val memUsage = sampleMemoryUsage()
            addSample(memSamples, memUsage)

            if (memSamples.size >= 5) {
                val memSlope = calculateSlope(memSamples.toList())
                val forecastedMem = (memUsage + memSlope * FORECAST_HORIZON_SAMPLES)
                    .coerceIn(0.0, 100.0)

                _forecast.value = ResourceForecast(
                    currentMem = memUsage,
                    forecastedMem = forecastedMem,
                    memSlope = memSlope,
                    timestamp = System.currentTimeMillis(),
                )

                val newState = when {
                    memUsage > MEM_THRESHOLD -> VetoState.CRITICAL
                    forecastedMem > MEM_THRESHOLD -> VetoState.THROTTLE
                    memSlope > 2.0 || memUsage > MEM_CAUTION_THRESHOLD -> VetoState.CAUTION
                    else -> VetoState.NOMINAL
                }

                if (newState != _vetoState.value) {
                    _vetoState.value = newState
                    Timber.tag(TAG).i("Veto state changed: %s (mem=%.1f%%, forecast=%.1f%%)",
                        newState, memUsage, forecastedMem)
                }
            }

            delay(SAMPLE_INTERVAL_MS)
        }
    }

    private fun addSample(buffer: ArrayDeque<Double>, value: Double) {
        if (buffer.size >= WINDOW_SIZE) buffer.removeFirst()
        buffer.addLast(value)
    }

    /**
     * Least-squares slope calculation over the sample window.
     */
    private fun calculateSlope(samples: List<Double>): Double {
        val n = samples.size
        if (n < 2) return 0.0
        val xMean = (n - 1) / 2.0
        val yMean = samples.average()
        var numerator = 0.0
        var denominator = 0.0
        for (i in samples.indices) {
            val dx = i - xMean
            numerator += dx * (samples[i] - yMean)
            denominator += dx * dx
        }
        return if (denominator != 0.0) numerator / denominator else 0.0
    }

    private fun sampleMemoryUsage(): Double {
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            am.getMemoryInfo(memInfo)
            ((1.0 - (memInfo.availMem.toDouble() / memInfo.totalMem)) * 100.0)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to sample memory")
            0.0
        }
    }
}

enum class VetoState {
    NOMINAL,
    CAUTION,
    THROTTLE,
    CRITICAL,
}

data class ResourceForecast(
    val currentMem: Double = 0.0,
    val forecastedMem: Double = 0.0,
    val memSlope: Double = 0.0,
    val timestamp: Long = 0L,
)
