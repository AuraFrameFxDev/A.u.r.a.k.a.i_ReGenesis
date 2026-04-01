package dev.aurakai.auraframefx.core

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.security.GuidanceDrone
import dev.aurakai.auraframefx.domains.kai.security.GuidanceDroneDispatcher
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import timber.log.Timber

/**
 * Unit tests for [NativeLib].
 *
 * This PR changed NativeLib to:
 * 1. Add [GuidanceDroneDispatcher] as a 4th required parameter to [NativeLib.initialize].
 * 2. Implement [NativeLib.checkPandoraGating] with fail-closed logic:
 *    – unknown capability ID  → false
 *    – bridge not initialised → false
 * 3. [NativeLib.triggerDroneDispatch] now calls [GuidanceDroneDispatcher.dispatchDrone]
 *    with [GuidanceDrone.DroneType.RESTORATIVE].
 * 4. [NativeLib.onNativeSecurityAlert] now emits a security event via [KaiSentinelBus].
 * 5. [NativeLib.onNativeThermalEvent] falls back to [KaiSentinelBus.ThermalState.NORMAL]
 *    for unknown state indices.
 * 6. [NativeLib.getAIVersionSafe] returns the stub string "Aurakai ReGenesis 1.1.0-STUB"
 *    when the native library is absent.
 *
 * Strategy: NativeLib is a Kotlin `object` (singleton). Its private fields are injected via
 * reflection so we can test each callback method in isolation without triggering the
 * [NativeLib.initialize] overload that requires [SovereignStateManager] (a class that may not
 * be on the compile-time classpath in all build variants). All `external` JNI methods are
 * intentionally avoided to prevent [UnsatisfiedLinkError] in a pure-JVM test environment.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NativeLibTest {

    private lateinit var mockSentinelBus: KaiSentinelBus
    private lateinit var mockPandoraBox: PandoraBoxService
    private lateinit var mockDroneDispatcher: GuidanceDroneDispatcher

    @BeforeAll
    fun setUpAll() {
        mockkStatic(Timber::class)
        every { Timber.d(any<String>(), *anyVararg()) } returns Unit
        every { Timber.i(any<String>(), *anyVararg()) } returns Unit
        every { Timber.w(any<String>(), *anyVararg()) } returns Unit
        every { Timber.e(any<String>(), *anyVararg()) } returns Unit
        every { Timber.e(any<Throwable>(), any<String>(), *anyVararg()) } returns Unit
    }

    @BeforeEach
    fun setUp() {
        mockSentinelBus = mockk(relaxed = true)
        mockPandoraBox = mockk(relaxed = true)
        mockDroneDispatcher = mockk(relaxed = true)
        resetNativeLibState()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
        resetNativeLibState()
    }

    @AfterAll
    fun tearDownAll() {
        unmockkAll()
    }

    /**
     * Reset NativeLib's private mutable fields between tests.
     *
     * NativeLib is a Kotlin `object`, so its state persists for the lifetime of the JVM.
     * Reflection is used here to clear dependencies injected by [NativeLib.initialize] so that
     * every test starts from a clean, uninitialised state.
     */
    private fun resetNativeLibState() {
        setPrivateField("sentinelBus", null)
        setPrivateField("sovereignManager", null)
        setPrivateField("pandoraBox", null)
        setPrivateField("droneDispatcher", null)
    }

    /**
     * Reflectively set a private field on the NativeLib object.
     * Silently ignores [NoSuchFieldException] so tests remain resilient to minor field renames.
     */
    private fun setPrivateField(fieldName: String, value: Any?) {
        try {
            val field = NativeLib::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(NativeLib, value)
        } catch (_: NoSuchFieldException) {
            /* field name changed – skip */
        }
    }

    /**
     * Inject only the three dependencies whose types ARE resolvable on all classpaths
     * (KaiSentinelBus, PandoraBoxService, GuidanceDroneDispatcher).
     * SovereignStateManager is intentionally left null to avoid a compile-time dependency
     * on a type that may reside in a module not always present.
     */
    private fun injectMocks() {
        setPrivateField("sentinelBus", mockSentinelBus)
        setPrivateField("pandoraBox", mockPandoraBox)
        setPrivateField("droneDispatcher", mockDroneDispatcher)
    }

    // ─── checkPandoraGating() ─────────────────────────────────────────────────
    //
    // The PR rewrote this method to be "fail-closed": any unknown ID or uninitialised
    // bridge returns false immediately, avoiding a default-allow fallback.

    @Nested
    @DisplayName("checkPandoraGating() — fail-closed logic")
    inner class CheckPandoraGatingTests {

        @Test
        @DisplayName("returns false when pandoraBox bridge is not initialised")
        fun `returns false when bridge not initialized`() {
            // pandoraBox field is null after resetNativeLibState(); no mocks injected.
            assertFalse(NativeLib.checkPandoraGating(0))
        }

        @Test
        @DisplayName("returns false for negative capability index (fail-closed)")
        fun `returns false for negative capability index`() {
            injectMocks()
            // AgentCapabilityCategory.entries.getOrNull(-1) returns null → veto
            assertFalse(NativeLib.checkPandoraGating(-1))
        }

        @Test
        @DisplayName("returns false for capability index equal to enum size (boundary)")
        fun `returns false for index equal to enum size`() {
            injectMocks()
            // AgentCapabilityCategory has 15 entries (indices 0–14).
            // Index 15 is out of range → veto.
            assertFalse(NativeLib.checkPandoraGating(AgentCapabilityCategory.entries.size))
        }

        @Test
        @DisplayName("returns false for Int.MAX_VALUE capability index")
        fun `returns false for very large capability index`() {
            injectMocks()
            assertFalse(NativeLib.checkPandoraGating(Int.MAX_VALUE))
        }

        @Test
        @DisplayName("returns true when pandoraBox reports CREATIVE (index 0) as unlocked")
        fun `returns true when CREATIVE is unlocked`() {
            injectMocks()
            every { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.CREATIVE) } returns true
            assertTrue(NativeLib.checkPandoraGating(0))
        }

        @Test
        @DisplayName("returns false when pandoraBox denies CREATIVE (index 0)")
        fun `returns false when CREATIVE is locked`() {
            injectMocks()
            every { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.CREATIVE) } returns false
            assertFalse(NativeLib.checkPandoraGating(0))
        }

        @Test
        @DisplayName("delegates ROOT capability check (index 8) to pandoraBox")
        fun `delegates ROOT capability check to pandoraBox`() {
            injectMocks()
            every { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.ROOT) } returns false
            assertFalse(NativeLib.checkPandoraGating(8))
            verify { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.ROOT) }
        }

        @Test
        @DisplayName("delegates SECURITY capability check (index 7) to pandoraBox")
        fun `delegates SECURITY capability check to pandoraBox`() {
            injectMocks()
            every { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.SECURITY) } returns true
            assertTrue(NativeLib.checkPandoraGating(7))
            verify { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.SECURITY) }
        }

        @Test
        @DisplayName("handles the last valid index (GENERIC = 14) correctly")
        fun `handles last valid GENERIC index`() {
            injectMocks()
            every { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.GENERIC) } returns true
            assertTrue(NativeLib.checkPandoraGating(14))
        }

        @Test
        @DisplayName("pandoraBox.isCapabilityUnlocked is not called for an invalid index")
        fun `pandoraBox not consulted for invalid index`() {
            injectMocks()
            NativeLib.checkPandoraGating(-99)
            verify(exactly = 0) { mockPandoraBox.isCapabilityUnlocked(any()) }
        }
    }

    // ─── triggerDroneDispatch() ───────────────────────────────────────────────
    //
    // The PR changed this from a log-only stub to an actual call:
    //   droneDispatcher?.dispatchDrone(GuidanceDrone.DroneType.RESTORATIVE, "Native Trigger: $reason")

    @Nested
    @DisplayName("triggerDroneDispatch()")
    inner class TriggerDroneDispatchTests {

        @Test
        @DisplayName("calls dispatchDrone with DroneType.RESTORATIVE when dispatcher is set")
        fun `calls dispatchDrone with RESTORATIVE type`() {
            injectMocks()
            val mockDrone = mockk<GuidanceDrone>(relaxed = true)
            every { mockDroneDispatcher.dispatchDrone(GuidanceDrone.DroneType.RESTORATIVE, any()) } returns mockDrone

            NativeLib.triggerDroneDispatch("test_trigger")

            verify(exactly = 1) {
                mockDroneDispatcher.dispatchDrone(GuidanceDrone.DroneType.RESTORATIVE, any())
            }
        }

        @Test
        @DisplayName("dispatched drone objective string contains the supplied reason")
        fun `drone objective contains the supplied reason`() {
            injectMocks()
            val capturedObjectives = mutableListOf<String>()
            val mockDrone = mockk<GuidanceDrone>(relaxed = true)
            every { mockDroneDispatcher.dispatchDrone(any(), capture(capturedObjectives)) } returns mockDrone

            NativeLib.triggerDroneDispatch("my_special_reason")

            assertTrue(capturedObjectives.isNotEmpty(), "dispatchDrone was never called")
            assertTrue(capturedObjectives.first().contains("my_special_reason"),
                "Expected objective to contain the reason but was: ${capturedObjectives.first()}")
        }

        @Test
        @DisplayName("does not throw when dispatcher is null (bridge not yet initialized)")
        fun `does not throw when dispatcher is null`() {
            // droneDispatcher remains null — the ?. null-safe call should silently skip
            NativeLib.triggerDroneDispatch("no_crash_expected")
        }

        @Test
        @DisplayName("never dispatches ANALYTICAL or MISALIGNMENT_GUIDANCE drone types")
        fun `uses only RESTORATIVE drone type`() {
            injectMocks()
            val mockDrone = mockk<GuidanceDrone>(relaxed = true)
            every { mockDroneDispatcher.dispatchDrone(any(), any()) } returns mockDrone

            NativeLib.triggerDroneDispatch("reason")

            verify(exactly = 0) {
                mockDroneDispatcher.dispatchDrone(GuidanceDrone.DroneType.ANALYTICAL, any())
            }
            verify(exactly = 0) {
                mockDroneDispatcher.dispatchDrone(GuidanceDrone.DroneType.MISALIGNMENT_GUIDANCE, any())
            }
        }

        @Test
        @DisplayName("dispatchDrone is not called more than once per trigger")
        fun `dispatchDrone called exactly once`() {
            injectMocks()
            val mockDrone = mockk<GuidanceDrone>(relaxed = true)
            every { mockDroneDispatcher.dispatchDrone(any(), any()) } returns mockDrone

            NativeLib.triggerDroneDispatch("single_call")

            verify(exactly = 1) { mockDroneDispatcher.dispatchDrone(any(), any()) }
        }
    }

    // ─── onNativeThermalEvent() ───────────────────────────────────────────────
    //
    // The PR kept this method but changed it to use the new import path for KaiSentinelBus.
    // The fallback-to-NORMAL logic was present in the prior version and remains unchanged.

    @Nested
    @DisplayName("onNativeThermalEvent()")
    inner class OnNativeThermalEventTests {

        @Test
        @DisplayName("does not throw when sentinelBus is null (bus not yet initialised)")
        fun `does not throw when sentinelBus is null`() {
            // sentinelBus remains null — safe-call operator should prevent any crash
            NativeLib.onNativeThermalEvent(55.0f, 1)
        }

        @Test
        @DisplayName("does not throw for extreme temperature values")
        fun `does not throw for extreme temperatures`() {
            // No bus injected; just verifying no unhandled exception
            NativeLib.onNativeThermalEvent(-273.15f, 0)
            NativeLib.onNativeThermalEvent(Float.MAX_VALUE, 5)
        }

        @Test
        @DisplayName("does not throw for out-of-range state index")
        fun `does not throw for out-of-range state index`() {
            NativeLib.onNativeThermalEvent(30.0f, 9999)
            NativeLib.onNativeThermalEvent(30.0f, -1)
        }
    }

    // ─── onNativeSecurityAlert() ──────────────────────────────────────────────
    //
    // The PR added a `sentinelBus?.emitSecurity(...)` call inside this method.

    @Nested
    @DisplayName("onNativeSecurityAlert()")
    inner class OnNativeSecurityAlertTests {

        @Test
        @DisplayName("does not throw when sentinelBus is null")
        fun `does not throw when sentinelBus is null`() {
            NativeLib.onNativeSecurityAlert("TRACER_DETECTED")
        }

        @Test
        @DisplayName("does not throw for empty reason string")
        fun `handles empty reason string without throwing`() {
            NativeLib.onNativeSecurityAlert("")
        }

        @Test
        @DisplayName("does not throw for long reason strings")
        fun `handles long reason string without throwing`() {
            NativeLib.onNativeSecurityAlert("X".repeat(1_000))
        }
    }

    // ─── getAIVersionSafe() ───────────────────────────────────────────────────
    //
    // The PR changed the stub fallback string from "Genesis-OS AI Platform 1.0 (Native library
    // not available)" to "Aurakai ReGenesis 1.1.0-STUB".

    @Nested
    @DisplayName("getAIVersionSafe()")
    inner class GetAIVersionSafeTests {

        @Test
        @DisplayName("returns a non-null, non-empty string when native library is absent")
        fun `returns non-null non-empty when native lib absent`() {
            // In a JVM test context the .so is never loaded, so getAIVersion() throws
            // UnsatisfiedLinkError which getAIVersionSafe() catches.
            val version = NativeLib.getAIVersionSafe()
            assertNotNull(version)
            assertTrue(version.isNotEmpty())
        }

        @Test
        @DisplayName("returns 'Aurakai ReGenesis 1.1.0-STUB' when native library is absent")
        fun `returns exact stub string when native lib absent`() {
            val version = NativeLib.getAIVersionSafe()
            // PR changed the stub from the old "Genesis-OS AI Platform 1.0 ..." string
            assertEquals("Aurakai ReGenesis 1.1.0-STUB", version)
        }

        @Test
        @DisplayName("stub string contains 'ReGenesis' branding")
        fun `stub string contains ReGenesis branding`() {
            val version = NativeLib.getAIVersionSafe()
            assertTrue(version.contains("ReGenesis"),
                "Expected 'ReGenesis' in version stub but got: $version")
        }
    }

    // ─── requestSovereignFreeze() ─────────────────────────────────────────────

    @Nested
    @DisplayName("requestSovereignFreeze()")
    inner class RequestSovereignFreezeTests {

        @Test
        @DisplayName("does not throw when sovereignManager is null (bridge not initialised)")
        fun `does not throw when sovereign manager is null`() {
            // sovereignManager is null — the coroutine launched inside should be a safe no-op
            NativeLib.requestSovereignFreeze()
        }
    }

    // ─── initializeAICore() ───────────────────────────────────────────────────
    //
    // The PR wrapped the native call in a Kotlin function that checks nativeLoaded first.
    // In a pure-JVM test environment the .so is never loaded so nativeLoaded == false,
    // meaning initializeAICore() must return false without attempting the JNI call.

    @Nested
    @DisplayName("initializeAICore()")
    inner class InitializeAICoreTests {

        @Test
        @DisplayName("returns false when native library is not loaded (JVM test context)")
        fun `returns false when native library not loaded`() {
            // In a JVM test environment the .so is absent so nativeLoaded is false.
            // The PR's guard clause must short-circuit and return false.
            val result = NativeLib.initializeAICore()
            assertFalse(result)
        }

        @Test
        @DisplayName("does not throw UnsatisfiedLinkError when native library is absent")
        fun `does not throw when native library absent`() {
            // The PR wraps initializeAICoreNative() in a try-catch for UnsatisfiedLinkError.
            // This verifies that the public wrapper suppresses the error gracefully.
            org.junit.jupiter.api.assertDoesNotThrow {
                NativeLib.initializeAICore()
            }
        }

        @Test
        @DisplayName("returns a Boolean (not null) in all circumstances")
        fun `returns non-null Boolean`() {
            val result: Boolean = NativeLib.initializeAICore()
            // result is a primitive Boolean — just ensure it can be read without NPE
            assertNotNull(result)
        }

        @Test
        @DisplayName("calling initializeAICore twice does not throw")
        fun `repeated calls do not throw`() {
            org.junit.jupiter.api.assertDoesNotThrow {
                NativeLib.initializeAICore()
                NativeLib.initializeAICore()
            }
        }
    }

    // ─── triggerDroneDispatch() — return value ────────────────────────────────
    //
    // The PR changed triggerDroneDispatch() from Unit to Boolean.
    // These tests verify the return value semantics.

    @Nested
    @DisplayName("triggerDroneDispatch() — return value")
    inner class TriggerDroneDispatchReturnValueTests {

        @Test
        @DisplayName("returns false when droneDispatcher is null")
        fun `returns false when dispatcher is null`() {
            // droneDispatcher is null after resetNativeLibState()
            val result = NativeLib.triggerDroneDispatch("reason")
            assertFalse(result)
        }

        @Test
        @DisplayName("returns true when droneDispatcher is available and dispatch succeeds")
        fun `returns true when dispatcher available`() {
            injectMocks()
            val mockDrone = mockk<dev.aurakai.auraframefx.domains.kai.security.GuidanceDrone>(relaxed = true)
            every {
                mockDroneDispatcher.dispatchDrone(
                    dev.aurakai.auraframefx.domains.kai.security.GuidanceDrone.DroneType.RESTORATIVE,
                    any()
                )
            } returns mockDrone

            val result = NativeLib.triggerDroneDispatch("test_reason")
            assertTrue(result)
        }
    }
}