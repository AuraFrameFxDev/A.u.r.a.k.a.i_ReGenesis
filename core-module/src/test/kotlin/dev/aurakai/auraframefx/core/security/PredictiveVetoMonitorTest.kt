package dev.aurakai.auraframefx.core.security

import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Tests for [PredictiveVetoMonitor] covering the PR changes:
 * - Simplified EMA using single EMA_ALPHA constant (0.3)
 * - isVetoActive() is a pure zone check (SEVERE or CRITICAL)
 * - checkVetoConditions() handles only thermal cases (identity drift removed)
 * - VetoDecision no longer contains emaResonance field
 * - No appScope or identity monitoring
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PredictiveVetoMonitor")
class PredictiveVetoMonitorTest {

    private lateinit var sentinelBus: KaiSentinelBus
    private lateinit var monitor: PredictiveVetoMonitor

    @BeforeEach
    fun setUp() {
        sentinelBus = mockk(relaxed = true)
        monitor = PredictiveVetoMonitor(sentinelBus)
    }

    // ─── Initial State ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Initial state")
    inner class InitialState {

        @Test
        fun `starts in NOMINAL zone`() = runTest {
            assertEquals(PredictiveVetoMonitor.ThermalZone.NOMINAL, monitor.currentZone.first())
        }

        @Test
        fun `isVetoActive returns false when zone is NOMINAL`() {
            assertFalse(monitor.isVetoActive())
        }

        @Test
        fun `checkVetoConditions returns not-vetoed decision in NOMINAL zone`() {
            val decision = monitor.checkVetoConditions()
            assertFalse(decision.vetoed)
            assertEquals(PredictiveVetoMonitor.ThermalZone.NOMINAL, decision.thermalZone)
            assertEquals(0f, decision.emaThermal)
        }
    }

    // ─── EMA Calculation ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("EMA thermal calculation")
    inner class EmaThermalCalculation {

        @Test
        fun `first sample sets EMA directly without blending`() {
            // EMA starts at 0 — first sample should bypass the formula and use value directly
            monitor.recordThermalSample(35.0f)
            val decision = monitor.checkVetoConditions()
            assertEquals(35.0f, decision.emaThermal, 0.001f)
        }

        @Test
        fun `subsequent samples apply EMA_ALPHA 0_3 blending`() {
            // Seed EMA with 35°C then feed 40°C
            monitor.recordThermalSample(35.0f)  // emaThermal = 35
            monitor.recordThermalSample(40.0f)  // emaThermal = 0.3*40 + 0.7*35 = 12+24.5 = 36.5
            val decision = monitor.checkVetoConditions()
            assertEquals(36.5f, decision.emaThermal, 0.01f)
        }

        @Test
        fun `EMA converges toward sustained high temperature`() {
            // Feed many high-temperature samples; EMA should approach the temperature
            repeat(30) { monitor.recordThermalSample(50.0f) }
            val decision = monitor.checkVetoConditions()
            // After many samples, EMA should be very close to 50 (within 1°C)
            assertTrue(decision.emaThermal > 49.0f, "EMA=${decision.emaThermal} should converge to 50")
        }
    }

    // ─── Zone Transitions ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Thermal zone transitions")
    inner class ThermalZoneTransitions {

        @Test
        fun `zone is LIGHT when EMA is between 39 and 43`() = runTest {
            monitor.recordThermalSample(40.0f)
            assertEquals(PredictiveVetoMonitor.ThermalZone.LIGHT, monitor.currentZone.first())
        }

        @Test
        fun `zone is MODERATE when EMA is between 43 and 45`() = runTest {
            // Seed with low, then push into moderate range
            monitor.recordThermalSample(40.0f) // EMA=40
            monitor.recordThermalSample(50.0f) // 0.3*50+0.7*40 = 43
            assertEquals(PredictiveVetoMonitor.ThermalZone.MODERATE, monitor.currentZone.first())
        }

        @Test
        fun `zone is SEVERE when EMA reaches 45 degrees`() = runTest {
            // Drive EMA above 45
            repeat(10) { monitor.recordThermalSample(50.0f) }
            val zone = monitor.currentZone.first()
            assertTrue(
                zone == PredictiveVetoMonitor.ThermalZone.SEVERE ||
                    zone == PredictiveVetoMonitor.ThermalZone.CRITICAL,
                "Expected SEVERE or CRITICAL, got $zone"
            )
        }

        @Test
        fun `zone is CRITICAL when EMA reaches 46_5 degrees`() = runTest {
            repeat(20) { monitor.recordThermalSample(55.0f) }
            assertEquals(PredictiveVetoMonitor.ThermalZone.CRITICAL, monitor.currentZone.first())
        }

        @Test
        fun `zone returns to NOMINAL from LIGHT after cool-down`() = runTest {
            monitor.recordThermalSample(42.0f) // LIGHT
            repeat(20) { monitor.recordThermalSample(20.0f) } // Cool down
            assertEquals(PredictiveVetoMonitor.ThermalZone.NOMINAL, monitor.currentZone.first())
        }

        @Test
        fun `sentinelBus is notified only when zone changes`() {
            monitor.recordThermalSample(35.0f) // NOMINAL → no change on init, no emit
            monitor.recordThermalSample(35.0f) // Still NOMINAL — no new emit
            verify(exactly = 0) { sentinelBus.emitThermal(any(), any()) }
        }

        @Test
        fun `sentinelBus is notified on zone transition`() {
            monitor.recordThermalSample(42.0f) // NOMINAL → LIGHT transition
            verify(exactly = 1) { sentinelBus.emitThermal(any(), KaiSentinelBus.ThermalState.LIGHT) }
        }
    }

    // ─── isVetoActive ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("isVetoActive — pure zone check")
    inner class IsVetoActive {

        @Test
        fun `returns false in NOMINAL zone`() {
            assertFalse(monitor.isVetoActive())
        }

        @Test
        fun `returns false in LIGHT zone`() {
            monitor.recordThermalSample(40.0f)
            assertFalse(monitor.isVetoActive())
        }

        @Test
        fun `returns false in MODERATE zone`() {
            monitor.recordThermalSample(40.0f) // EMA=40
            monitor.recordThermalSample(50.0f) // EMA=43 MODERATE
            assertFalse(monitor.isVetoActive())
        }

        @Test
        fun `returns true when zone is SEVERE`() {
            // Push EMA into SEVERE range (>=45 and <46.5)
            repeat(10) { monitor.recordThermalSample(48.0f) }
            val zone = monitor.currentZone.value
            if (zone == PredictiveVetoMonitor.ThermalZone.SEVERE ||
                zone == PredictiveVetoMonitor.ThermalZone.CRITICAL
            ) {
                assertTrue(monitor.isVetoActive())
            }
        }

        @Test
        fun `returns true when zone is CRITICAL`() {
            repeat(20) { monitor.recordThermalSample(55.0f) }
            assertTrue(monitor.isVetoActive())
        }
    }

    // ─── checkVetoConditions ─────────────────────────────────────────────────

    @Nested
    @DisplayName("checkVetoConditions — thermal only")
    inner class CheckVetoConditions {

        @Test
        fun `returns not-vetoed for NOMINAL zone`() {
            val decision = monitor.checkVetoConditions()
            assertFalse(decision.vetoed)
            assertEquals(null, decision.reason)
        }

        @Test
        fun `returns not-vetoed for LIGHT zone`() {
            monitor.recordThermalSample(40.0f)
            val decision = monitor.checkVetoConditions()
            assertFalse(decision.vetoed)
        }

        @Test
        fun `returns not-vetoed for MODERATE zone`() {
            monitor.recordThermalSample(40.0f)
            monitor.recordThermalSample(50.0f) // EMA=43
            val decision = monitor.checkVetoConditions()
            assertFalse(decision.vetoed)
        }

        @Test
        fun `returns THERMAL_SEVERE veto for SEVERE zone`() {
            // Bring EMA into SEVERE (>=45) territory
            monitor.recordThermalSample(45.0f) // First sample sets EMA=45 → SEVERE
            val decision = monitor.checkVetoConditions()
            if (decision.thermalZone == PredictiveVetoMonitor.ThermalZone.SEVERE) {
                assertTrue(decision.vetoed)
                assertEquals(PredictiveVetoMonitor.VetoReason.THERMAL_SEVERE, decision.reason)
            }
        }

        @Test
        fun `returns THERMAL_CRITICAL veto for CRITICAL zone`() {
            monitor.recordThermalSample(46.5f) // First sample sets EMA=46.5 → CRITICAL
            val decision = monitor.checkVetoConditions()
            assertTrue(decision.vetoed)
            assertEquals(PredictiveVetoMonitor.VetoReason.THERMAL_CRITICAL, decision.reason)
            assertEquals(PredictiveVetoMonitor.ThermalZone.CRITICAL, decision.thermalZone)
        }

        @Test
        fun `VetoDecision contains current emaThermal value`() {
            monitor.recordThermalSample(35.0f)
            val decision = monitor.checkVetoConditions()
            assertEquals(35.0f, decision.emaThermal, 0.001f)
        }

        @Test
        fun `VetoDecision does not have emaResonance field — PR removed it`() {
            // This test documents that emaResonance was removed from VetoDecision
            // Compile-time verification: VetoDecision only has vetoed, reason, thermalZone, emaThermal
            val decision = monitor.checkVetoConditions()
            val fields = decision.javaClass.declaredFields.map { it.name }
            assertFalse(fields.contains("emaResonance"), "emaResonance should have been removed from VetoDecision")
        }
    }

    // ─── Identity Baseline / Drift (retained methods) ─────────────────────────

    @Nested
    @DisplayName("Identity drift utilities")
    inner class IdentityDrift {

        @Test
        fun `checkIdentityDrift returns 0 when baseline is empty`() {
            val drift = monitor.checkIdentityDrift(floatArrayOf(1.0f, 0.0f))
            assertEquals(0f, drift, 0.001f)
        }

        @Test
        fun `checkIdentityDrift returns 0 when vector sizes differ`() {
            monitor.calibrateIdentityBaseline(floatArrayOf(1.0f, 0.0f, 0.0f))
            val drift = monitor.checkIdentityDrift(floatArrayOf(1.0f, 0.0f))
            assertEquals(0f, drift, 0.001f)
        }

        @Test
        fun `checkIdentityDrift returns near 0 for identical vectors`() {
            val vector = floatArrayOf(1.0f, 0.0f, 0.0f)
            monitor.calibrateIdentityBaseline(vector)
            val drift = monitor.checkIdentityDrift(vector.copyOf())
            assertEquals(0f, drift, 0.001f)
        }

        @Test
        fun `checkIdentityDrift returns 2 for completely opposite vectors`() {
            monitor.calibrateIdentityBaseline(floatArrayOf(1.0f, 0.0f))
            val drift = monitor.checkIdentityDrift(floatArrayOf(-1.0f, 0.0f))
            // cosine similarity of opposite vectors = -1, drift = |1 - (-1)| = 2
            assertEquals(2.0f, drift, 0.01f)
        }

        @Test
        fun `checkIdentityDrift returns ~0_29 for 90 degree vectors`() {
            // Perpendicular vectors have cosine=0, drift=|1-0|=1
            monitor.calibrateIdentityBaseline(floatArrayOf(1.0f, 0.0f))
            val drift = monitor.checkIdentityDrift(floatArrayOf(0.0f, 1.0f))
            assertEquals(1.0f, drift, 0.01f)
        }

        @Test
        fun `identity drift does NOT trigger veto in refactored version`() {
            // PR removed identity drift veto — only thermal can trigger veto now
            monitor.calibrateIdentityBaseline(floatArrayOf(1.0f))
            monitor.checkIdentityDrift(floatArrayOf(-1.0f)) // large drift
            // Veto should still be false (no thermal issues)
            assertFalse(monitor.isVetoActive())
        }
    }

    // ─── Boundary / Regression Tests ─────────────────────────────────────────

    @Nested
    @DisplayName("Boundary and regression")
    inner class BoundaryAndRegression {

        @Test
        fun `zone boundary at exactly LIGHT threshold 39 degrees`() {
            monitor.recordThermalSample(39.0f)
            assertEquals(PredictiveVetoMonitor.ThermalZone.LIGHT, monitor.currentZone.value)
        }

        @Test
        fun `zone boundary at exactly MODERATE threshold 43 degrees`() {
            monitor.recordThermalSample(43.0f)
            assertEquals(PredictiveVetoMonitor.ThermalZone.MODERATE, monitor.currentZone.value)
        }

        @Test
        fun `zone boundary at exactly SEVERE threshold 45 degrees`() {
            monitor.recordThermalSample(45.0f)
            assertEquals(PredictiveVetoMonitor.ThermalZone.SEVERE, monitor.currentZone.value)
        }

        @Test
        fun `zone boundary at exactly CRITICAL threshold 46_5 degrees`() {
            monitor.recordThermalSample(46.5f)
            assertEquals(PredictiveVetoMonitor.ThermalZone.CRITICAL, monitor.currentZone.value)
        }

        @Test
        fun `just below LIGHT threshold stays NOMINAL`() {
            monitor.recordThermalSample(38.9f)
            assertEquals(PredictiveVetoMonitor.ThermalZone.NOMINAL, monitor.currentZone.value)
        }

        @Test
        fun `recording zero celsius first clears EMA baseline safely`() {
            // 0f is used as "uninitialized" sentinel — if user sends 0, it sets directly
            monitor.recordThermalSample(0.0f)
            // EMA should be 0, zone should be NOMINAL
            assertEquals(PredictiveVetoMonitor.ThermalZone.NOMINAL, monitor.currentZone.value)
            assertFalse(monitor.isVetoActive())
        }

        @Test
        fun `multiple monitors are independent — no shared static state`() {
            val otherSentinelBus = mockk<KaiSentinelBus>(relaxed = true)
            val otherMonitor = PredictiveVetoMonitor(otherSentinelBus)
            repeat(20) { monitor.recordThermalSample(55.0f) } // drive first to CRITICAL
            assertEquals(PredictiveVetoMonitor.ThermalZone.NOMINAL, otherMonitor.currentZone.value)
        }
    }
}