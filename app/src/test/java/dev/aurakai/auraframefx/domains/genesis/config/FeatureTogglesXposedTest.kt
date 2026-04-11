package dev.aurakai.auraframefx.domains.genesis.config

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests for the Xposed / YukiHook feature-flag constants in [FeatureToggles].
 *
 * The staging file `.staging/reactive/…/StagingGenesisHookEntry.kt` (added in this PR)
 * gates all hook registration behind these flags. The most safety-critical invariant is
 * that `XPOSED_HOOKS_ENABLED` defaults to `false` so that the hook system is **off** unless
 * the developer explicitly opts in on a rooted AOSP build.
 *
 * These tests serve as regression guards against accidental changes to the default values.
 */
@DisplayName("FeatureToggles — Xposed Gate Tests")
class FeatureTogglesXposedTest {

    // ── Master switch ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("XPOSED_HOOKS_ENABLED — master switch")
    inner class MasterSwitch {

        @Test
        @DisplayName("is FALSE by default (hooks inactive unless explicitly enabled)")
        fun masterSwitchIsOffByDefault() {
            assertFalse(
                FeatureToggles.XPOSED_HOOKS_ENABLED,
                "XPOSED_HOOKS_ENABLED must default to false to prevent unintended system hooking"
            )
        }

        @Test
        @DisplayName("is a compile-time constant (Boolean type)")
        fun masterSwitchIsBoolean() {
            // The value must be assignable to Boolean; this confirms the type contract.
            val value: Boolean = FeatureToggles.XPOSED_HOOKS_ENABLED
            assertFalse(value)
        }
    }

    // ── Per-hooker gates (individual feature flags) ───────────────────────────

    @Nested
    @DisplayName("Individual hook flags — default values")
    inner class IndividualHookFlags {

        @Test
        @DisplayName("XPOSED_GENESIS_SYSTEM_HOOKS is true by default")
        fun genesisSystemHooksDefaultTrue() {
            assertTrue(FeatureToggles.XPOSED_GENESIS_SYSTEM_HOOKS)
        }

        @Test
        @DisplayName("XPOSED_GENESIS_UI_HOOKS is true by default")
        fun genesisUiHooksDefaultTrue() {
            assertTrue(FeatureToggles.XPOSED_GENESIS_UI_HOOKS)
        }

        @Test
        @DisplayName("XPOSED_GENESIS_ZYGOTE_HOOKS is true by default")
        fun genesisZygoteHooksDefaultTrue() {
            assertTrue(FeatureToggles.XPOSED_GENESIS_ZYGOTE_HOOKS)
        }

        @Test
        @DisplayName("XPOSED_UNIVERSAL_COMPONENT_HOOKS is false by default (opt-in)")
        fun universalComponentHooksDefaultFalse() {
            assertFalse(
                FeatureToggles.XPOSED_UNIVERSAL_COMPONENT_HOOKS,
                "Universal component hooks are broad; they must be opt-in (false by default)"
            )
        }

        @Test
        @DisplayName("XPOSED_GENESIS_SELF_HOOKS is true by default")
        fun genesisSelfHooksDefaultTrue() {
            assertTrue(FeatureToggles.XPOSED_GENESIS_SELF_HOOKS)
        }

        @Test
        @DisplayName("XPOSED_NOTCH_BAR_HOOKER is true by default")
        fun notchBarHookerDefaultTrue() {
            assertTrue(FeatureToggles.XPOSED_NOTCH_BAR_HOOKER)
        }

        @Test
        @DisplayName("XPOSED_QS_HOOKER is true by default")
        fun qsHookerDefaultTrue() {
            assertTrue(FeatureToggles.XPOSED_QS_HOOKER)
        }

        @Test
        @DisplayName("XPOSED_CHROMA_CORE_HOOKER is true by default")
        fun chromaCoreHookerDefaultTrue() {
            assertTrue(FeatureToggles.XPOSED_CHROMA_CORE_HOOKER)
        }

        @Test
        @DisplayName("XPOSED_LOCKSCREEN_HOOKS is false by default (stub, not implemented)")
        fun lockscreenHooksDefaultFalse() {
            assertFalse(
                FeatureToggles.XPOSED_LOCKSCREEN_HOOKS,
                "Lockscreen hooks are a stub; they must remain false until fully implemented"
            )
        }
    }

    // ── Safety invariants ─────────────────────────────────────────────────────

    /**
     * Even when every individual flag is `true`, the master switch being `false` means the
     * [StagingGenesisHookEntry.onHook] implementation will skip all hook loading.
     *
     * This test documents the expected logical behaviour of the early-return guard:
     *
     * ```kotlin
     * if (!FeatureToggles.XPOSED_HOOKS_ENABLED) return@encase
     * ```
     */
    @Nested
    @DisplayName("Safety Invariants")
    inner class SafetyInvariants {

        @Test
        @DisplayName("master switch OFF prevents all hooks regardless of individual flags")
        fun masterSwitchOffPreventsAllHooks() {
            // Simulate the guard condition in StagingGenesisHookEntry.onHook()
            val masterEnabled = FeatureToggles.XPOSED_HOOKS_ENABLED
            // The master gate: if false, no hooks should be registered
            val wouldSkipAllHooks = !masterEnabled
            assertTrue(wouldSkipAllHooks,
                "With XPOSED_HOOKS_ENABLED=false the early return should be taken, skipping ALL hooks")
        }

        @Test
        @DisplayName("individual hook flags are only meaningful when master switch is ON")
        fun individualFlagsIrrelevantWhenMasterOff() {
            // If master is off, the values of individual flags do not matter
            val individualFlagsWouldBeReached = FeatureToggles.XPOSED_HOOKS_ENABLED
            assertFalse(individualFlagsWouldBeReached,
                "Individual hook flags should never be evaluated when master switch is false")
        }

        @Test
        @DisplayName("XPOSED_ENABLED flag is also false (legacy compat)")
        fun xposedEnabledFlagIsAlsoFalse() {
            assertFalse(FeatureToggles.XPOSED_ENABLED)
        }
    }

    // ── Other core module toggles (unchanged by this PR, regression guard) ────

    @Nested
    @DisplayName("Core module toggles — unchanged default regression")
    inner class CoreModuleToggles {

        @Test
        @DisplayName("NEXUS_ENABLED is true")
        fun nexusEnabled() {
            assertTrue(FeatureToggles.NEXUS_ENABLED)
        }

        @Test
        @DisplayName("ORACLE_DRIVE_ENABLED is true")
        fun oracleDriveEnabled() {
            assertTrue(FeatureToggles.ORACLE_DRIVE_ENABLED)
        }

        @Test
        @DisplayName("GENESIS_ORCHESTRATOR_ENABLED is true")
        fun genesisOrchestratorEnabled() {
            assertTrue(FeatureToggles.GENESIS_ORCHESTRATOR_ENABLED)
        }

        @Test
        @DisplayName("CASCADE_ANALYTICS_ENABLED is true")
        fun cascadeAnalyticsEnabled() {
            assertTrue(FeatureToggles.CASCADE_ANALYTICS_ENABLED)
        }

        @Test
        @DisplayName("AURA_CREATIVE_ENABLED is true")
        fun auraCreativeEnabled() {
            assertTrue(FeatureToggles.AURA_CREATIVE_ENABLED)
        }

        @Test
        @DisplayName("KAI_SECURITY_ENABLED is true")
        fun kaiSecurityEnabled() {
            assertTrue(FeatureToggles.KAI_SECURITY_ENABLED)
        }

        @Test
        @DisplayName("QUANTUM_ENTANGLEMENT_RESEARCH is false (experimental, off by default)")
        fun quantumResearchOff() {
            assertFalse(FeatureToggles.QUANTUM_ENTANGLEMENT_RESEARCH)
        }

        @Test
        @DisplayName("DIMENSIONAL_SHIFT_SIMULATION is false (experimental, off by default)")
        fun dimensionalShiftOff() {
            assertFalse(FeatureToggles.DIMENSIONAL_SHIFT_SIMULATION)
        }

        @Test
        @DisplayName("FUSION_MEMORY_INDEXING is true")
        fun fusionMemoryIndexingEnabled() {
            assertTrue(FeatureToggles.FUSION_MEMORY_INDEXING)
        }
    }
}