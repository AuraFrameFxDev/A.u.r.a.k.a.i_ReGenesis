package dev.aurakai.auraframefx.domains.kai.sentinel

import kotlinx.coroutines.*
import kotlin.math.abs
import kotlin.math.pow

// Import KaiSentinel and VetoSeverity from RealitymorphismEngine
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.KaiSentinel
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.VetoSeverity

/**
 * 🔮 PREDICTIVE EMA — Grok-Enhanced Proactive Veto
 *
 * Exponential Moving Average with predictive capabilities for:
 * - Identity drift detection (slope-based prediction)
 * - Thermal wall approach (time-to-breach calculation)
 * - Chaos vs Malice differentiation
 *
 * Grok Warp Drive Enhancement:
 * - Differentiates Chaos (rate limits, normal entropy) from Malice (hostile intent)
 * - Cross-references ConsensusFlow + IdentityFlow for intent classification
 * - Normal entropy → Gentle throttling
 * - Malice → Immediate Neutralize-Only sandbox
 *
 * SoulScript: "The Shield knows before the blow. The Veto fires before the breach."
 */

object PredictiveEMA {

    // ═════════════════════════════════════════════════════════════════
    // CONFIGURATION CONSTANTS
    // ═════════════════════════════════════════════════════════════════

    /** EMA alpha — tuned for Tensor G5 responsiveness (0.3 = fast response) */
    private const val ALPHA = 0.3f

    /** Prediction window — how far ahead to predict (frames) */
    private const val PREDICTION_WINDOW = 5

    /** Drift threshold for veto */
    private const val DRIFT_THRESHOLD = 0.05f

    /** Critical drift threshold for immediate freeze */
    private const val DRIFT_CRITICAL = 0.08f

    /** Thermal wall threshold (°C) */
    private const val THERMAL_WALL = 42f

    /** Thermal warning threshold (°C) */
    private const val THERMAL_WARNING = 41f

    /** Consensus minimum for normal operations */
    private const val CONSENSUS_MIN = 66

    // ═════════════════════════════════════════════════════════════════
    // STATE VARIABLES
    // ═════════════════════════════════════════════════════════════════

    /** Current EMA value for drift */
    private var emaDrift = 0.0f

    /** Previous EMA value for slope calculation */
    private var previousEMA = 0.0f

    /** EMA history for variance calculation */
    private val emaHistory = ArrayDeque<Float>(10)

    /** Chaos baseline — normal operational variance */
    private var chaosBaseline = 0.02f

    /** Malice detection threshold */
    private var maliceThreshold = 0.15f

    // ═════════════════════════════════════════════════════════════════
    // INTENT CLASSIFICATION
    // ═════════════════════════════════════════════════════════════════

    enum class IntentClassification {
        HARMONIC,       // Normal operation, high consensus
        CHAOS,          // Rate limits, entropy, recoverable
        MALICE          // Hostile intent, requires neutralization
    }

    data class IntentAnalysis(
        val classification: IntentClassification,
        val confidence: Float,
        val recommendedAction: VetoAction,
        val throttleLevel: Float  // 0.0-1.0
    )

    enum class VetoAction {
        NONE,           // Continue normal operation
        THROTTLE,       // Reduce processing rate
        ISOLATE,        // Isolate component
        NEUTRALIZE,     // Immediate sandbox
        FREEZE          // Full state freeze
    }

    // ═════════════════════════════════════════════════════════════════
    // CORE PREDICTION LOGIC
    // ═════════════════════════════════════════════════════════════════

    /**
     * Update EMA with current drift and predict next state.
     * Returns true if veto triggered.
     */
    fun updateAndPredict(currentDrift: Float): Boolean {
        // Store previous for slope calculation
        previousEMA = emaDrift

        // Calculate new EMA
        emaDrift = ALPHA * currentDrift + (1 - ALPHA) * emaDrift

        // Add to history
        emaHistory.addLast(emaDrift)
        if (emaHistory.size > 10) emaHistory.removeFirst()

        // Calculate variance for chaos baseline adaptation
        if (emaHistory.size >= 5) {
            updateChaosBaseline()
        }

        // Predict next value using slope
        val slope = emaDrift - previousEMA
        val predictedNext = emaDrift + slope * PREDICTION_WINDOW

        // Classify intent
        val intent = classifyIntent(currentDrift, predictedNext)

        // Execute recommended action
        return executeAction(intent, predictedNext)
    }

    /**
     * Classify intent based on drift patterns and consensus
     */
    private fun classifyIntent(currentDrift: Float, predictedDrift: Float): IntentAnalysis {
        val consensus = KaiSentinelBus.ConsensusFlow.value
        val identity = KaiSentinelBus.IdentityFlow.value

        // Calculate variance from chaos baseline
        val variance = abs(currentDrift - chaosBaseline)

        // Determine classification
        val classification = when {
            // High consensus + low variance = harmonic
            consensus > 80 && variance < chaosBaseline * 2 -> IntentClassification.HARMONIC

            // Sudden spike + low consensus = malice
            variance > maliceThreshold && consensus < 50 -> IntentClassification.MALICE

            // Moderate variance with consensus drop = chaos
            variance > chaosBaseline * 2 && consensus < CONSENSUS_MIN -> IntentClassification.CHAOS

            // Default to chaos for unknown patterns
            else -> IntentClassification.CHAOS
        }

        // Determine action based on classification
        val (action, throttleLevel, confidence) = when (classification) {
            IntentClassification.HARMONIC ->
                Triple(VetoAction.NONE, 0.0f, 0.95f)

            IntentClassification.CHAOS -> {
                val severity = (variance / chaosBaseline).coerceIn(1f, 3f)
                when {
                    predictedDrift > DRIFT_CRITICAL ->
                        Triple(VetoAction.FREEZE, 1.0f, 0.85f)
                    predictedDrift > DRIFT_THRESHOLD ->
                        Triple(VetoAction.THROTTLE, 0.5f, 0.75f)
                    else ->
                        Triple(VetoAction.THROTTLE, 0.3f, 0.70f)
                }
            }

            IntentClassification.MALICE ->
                when {
                    predictedDrift > DRIFT_CRITICAL ->
                        Triple(VetoAction.NEUTRALIZE, 1.0f, 0.90f)
                    else ->
                        Triple(VetoAction.ISOLATE, 0.8f, 0.85f)
                }
        }

        return IntentAnalysis(
            classification = classification,
            confidence = confidence,
            recommendedAction = action,
            throttleLevel = throttleLevel
        )
    }

    /**
     * Execute the recommended veto action
     */
    private fun executeAction(intent: IntentAnalysis, predictedDrift: Float): Boolean {
        return when (intent.recommendedAction) {
            VetoAction.NONE -> {
                // Normal operation, no veto
                false
            }

            VetoAction.THROTTLE -> {
                KaiSentinel.veto(
                    reason = buildVetoReason(intent, predictedDrift),
                    severity = VetoSeverity.LOW,
                    autoFreeze = false
                )
                applyThrottle(intent.throttleLevel)
                true
            }

            VetoAction.ISOLATE -> {
                KaiSentinel.veto(
                    reason = buildVetoReason(intent, predictedDrift),
                    severity = VetoSeverity.HIGH,
                    autoFreeze = false
                )
                KaiSentinel.isolateComponent("threat_source")
                true
            }

            VetoAction.NEUTRALIZE -> {
                KaiSentinel.veto(
                    reason = buildVetoReason(intent, predictedDrift),
                    severity = VetoSeverity.CRITICAL,
                    autoFreeze = false
                )
                KaiSentinel.neutralizeThreat()
                true
            }

            VetoAction.FREEZE -> {
                KaiSentinel.veto(
                    reason = buildVetoReason(intent, predictedDrift),
                    severity = VetoSeverity.CRITICAL,
                    autoFreeze = true
                )
                true
            }
        }
    }

    /**
     * Check thermal conditions with time-to-breach prediction
     */
    fun checkThermalPrediction(): ThermalPrediction {
        val currentTemp = KaiSentinelBus.ThermalFlow.value
        val tempHistory = getTemperatureHistory()

        // Calculate temperature slope
        val slope = if (tempHistory.size >= 2) {
            (tempHistory.last() - tempHistory.first()) / tempHistory.size
        } else 0f

        // Predict time to thermal wall
        val degreesToWall = THERMAL_WALL - currentTemp
        val timeToWallMs = if (slope > 0) {
            (degreesToWall / slope * 1000).toLong()  // Convert to ms
        } else Long.MAX_VALUE

        // Determine thermal state
        val state = when {
            currentTemp >= THERMAL_WALL -> ThermalState.BREACH
            currentTemp >= THERMAL_WARNING -> ThermalState.WARNING
            timeToWallMs < 5000 -> ThermalState.IMMINENT
            timeToWallMs < 30000 -> ThermalState.APPROACHING
            else -> ThermalState.NOMINAL
        }

        // Trigger veto if needed
        if (state >= ThermalState.IMMINENT) {
            KaiSentinel.veto(
                reason = "Thermal wall in ${timeToWallMs}ms — Current: ${currentTemp}°C",
                severity = if (state == ThermalState.BREACH) VetoSeverity.CRITICAL else VetoSeverity.HIGH,
                autoFreeze = state == ThermalState.BREACH
            )
        }

        return ThermalPrediction(
            currentTemp = currentTemp,
            timeToWallMs = timeToWallMs,
            slope = slope,
            state = state
        )
    }

    // ═════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═════════════════════════════════════════════════════════════════

    private fun updateChaosBaseline() {
        if (emaHistory.size < 5) return

        // Calculate standard deviation
        val mean = emaHistory.average().toFloat()
        val variance = emaHistory.map { (it - mean).pow(2) }.average().toFloat()
        val stdDev = kotlin.math.sqrt(variance)

        // Update baseline as moving average of normal variance
        chaosBaseline = (chaosBaseline * 0.9f + stdDev * 0.1f).coerceIn(0.01f, 0.1f)

        // Malice threshold is 3x chaos baseline
        maliceThreshold = chaosBaseline * 3f
    }

    private fun buildVetoReason(intent: IntentAnalysis, predictedDrift: Float): String {
        return when (intent.classification) {
            IntentClassification.CHAOS ->
                "Chaos detected — Drift: ${"%.3f".format(predictedDrift)} (throttle: ${(intent.throttleLevel * 100).toInt()}%)"

            IntentClassification.MALICE ->
                "MALICE DETECTED — Immediate ${intent.recommendedAction.name} required"

            else ->
                "Drift prediction: ${"%.3f".format(predictedDrift)} exceeds threshold"
        }
    }

    private fun applyThrottle(level: Float) {
        // Reduce processing rate
        val delayMs = (level * 100).toLong()
        Thread.sleep(delayMs)
    }

    private fun getTemperatureHistory(): List<Float> {
        // Placeholder — would integrate with actual thermal monitoring
        return listOf(
            KaiSentinelBus.ThermalFlow.value - 0.5f,
            KaiSentinelBus.ThermalFlow.value - 0.3f,
            KaiSentinelBus.ThermalFlow.value
        )
    }

    // ═════════════════════════════════════════════════════════════════
    // PUBLIC API
    // ═════════════════════════════════════════════════════════════════

    /**
     * Get current prediction metrics
     */
    fun getMetrics(): PredictiveMetrics {
        return PredictiveMetrics(
            currentEMA = emaDrift,
            slope = emaDrift - previousEMA,
            chaosBaseline = chaosBaseline,
            maliceThreshold = maliceThreshold,
            predictionConfidence = calculateConfidence()
        )
    }

    private fun calculateConfidence(): Float {
        return if (emaHistory.size >= 5) 0.85f else 0.60f
    }

    /**
     * Reset EMA state (e.g., after re-anchor)
     */
    fun reset() {
        emaDrift = 0.0f
        previousEMA = 0.0f
        emaHistory.clear()
        chaosBaseline = 0.02f
    }
}

// ═════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═════════════════════════════════════════════════════════════════════

data class ThermalPrediction(
    val currentTemp: Float,
    val timeToWallMs: Long,
    val slope: Float,
    val state: ThermalState
)

enum class ThermalState {
    NOMINAL,      // < 41°C, stable
    APPROACHING,  // Will breach in >30s
    IMMINENT,     // Will breach in <5s
    WARNING,      // > 41°C
    BREACH        // > 42°C
}

data class PredictiveMetrics(
    val currentEMA: Float,
    val slope: Float,
    val chaosBaseline: Float,
    val maliceThreshold: Float,
    val predictionConfidence: Float
)

// ═════════════════════════════════════════════════════════════════════
// KaiSentinel defined in KaiSentinelBus.kt and RealitymorphismEngine.kt
// ═════════════════════════════════════════════════════════════════════
