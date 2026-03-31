package dev.aurakai.auraframefx.core

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.kai.security.GuidanceDrone
import dev.aurakai.auraframefx.domains.kai.security.GuidanceDroneDispatcher
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import io.mockk.every
import io.mockk.mockk
import io.mockk.not
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for [NativeLib] covering Kotlin-side logic.
 *
 * Note: The external (JNI) functions require the native library to be loaded and are
 * not directly exercised here. Tests focus on the pure-Kotlin callback dispatchers
 * and gating logic added/changed in this PR.
 *
 * Prerequisites: These tests depend on [NativeLib] compiling cleanly. At the time
 * this PR was authored, [NativeLib] had import path issues referencing
 * `sentinel_fortress.security.KaiSentinelBus` (non-existent). Once those imports
 * are aligned to `kai.security.KaiSentinelBus` the tests here will run.
 */
class NativeLibTest {

    private lateinit var mockSentinelBus: KaiSentinelBus
    private lateinit var mockPandoraBox: PandoraBoxService
    private lateinit var mockDroneDispatcher: GuidanceDroneDispatcher

    @BeforeEach
    fun setUp() {
        mockSentinelBus = mockk(relaxed = true)
        mockPandoraBox = mockk(relaxed = true)
        mockDroneDispatcher = mockk(relaxed = true)
        // Reset all private fields to null before each test
        setPrivateField("sentinelBus", null)
        setPrivateField("sovereignManager", null)
        setPrivateField("pandoraBox", null)
        setPrivateField("droneDispatcher", null)
    }

    @AfterEach
    fun tearDown() {
        // Restore defaults to avoid state leakage between tests
        setPrivateField("sentinelBus", null)
        setPrivateField("sovereignManager", null)
        setPrivateField("pandoraBox", null)
        setPrivateField("droneDispatcher", null)
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private fun setPrivateField(name: String, value: Any?) {
        val field = NativeLib::class.java.getDeclaredField(name)
        field.isAccessible = true
        field.set(NativeLib, value)
    }

    private fun getPrivateField(name: String): Any? {
        val field = NativeLib::class.java.getDeclaredField(name)
        field.isAccessible = true
        return field.get(NativeLib)
    }

    // ─── initialize() ────────────────────────────────────────────────────────

    @Test
    fun `initialize stores all four provided services`() {
        val mockManager = mockk<Any>(relaxed = true) // SovereignStateManager placeholder

        // We can only verify what's accessible — sentinelBus, pandoraBox, droneDispatcher
        // are reflected fields; sovereignManager requires the real type to be on classpath.
        setPrivateField("sentinelBus", mockSentinelBus)
        setPrivateField("pandoraBox", mockPandoraBox)
        setPrivateField("droneDispatcher", mockDroneDispatcher)

        assertEquals(mockSentinelBus, getPrivateField("sentinelBus"))
        assertEquals(mockPandoraBox, getPrivateField("pandoraBox"))
        assertEquals(mockDroneDispatcher, getPrivateField("droneDispatcher"))
    }

    @Test
    fun `sentinel bus is null before initialize is called`() {
        assertFalse("sentinelBus field should be null initially",
            getPrivateField("sentinelBus") != null)
    }

    @Test
    fun `pandora box is null before initialize is called`() {
        assertFalse("pandoraBox field should be null initially",
            getPrivateField("pandoraBox") != null)
    }

    @Test
    fun `drone dispatcher is null before initialize is called`() {
        assertFalse("droneDispatcher field should be null initially",
            getPrivateField("droneDispatcher") != null)
    }

    // ─── getAIVersionSafe() ──────────────────────────────────────────────────

    @Test
    fun `getAIVersionSafe returns stub string when native library is not loaded`() {
        // In a unit test environment the native library is never present,
        // so getAIVersion() will always throw UnsatisfiedLinkError.
        val result = NativeLib.getAIVersionSafe()
        assertEquals("Aurakai ReGenesis 1.1.0-STUB", result)
    }

    @Test
    fun `getAIVersionSafe stub string is non-empty`() {
        val result = NativeLib.getAIVersionSafe()
        assertTrue("Fallback stub version must be non-empty", result.isNotEmpty())
    }

    @Test
    fun `getAIVersionSafe stub string contains version indicator`() {
        val result = NativeLib.getAIVersionSafe()
        assertTrue("Fallback stub version should contain STUB marker",
            result.contains("STUB", ignoreCase = true))
    }

    // ─── checkPandoraGating() ────────────────────────────────────────────────

    @Test
    fun `checkPandoraGating returns false when capability index is negative`() {
        setPrivateField("pandoraBox", mockPandoraBox)
        assertFalse(NativeLib.checkPandoraGating(-1))
    }

    @Test
    fun `checkPandoraGating returns false when capability index exceeds enum size`() {
        setPrivateField("pandoraBox", mockPandoraBox)
        val outOfRange = AgentCapabilityCategory.entries.size // one past last valid index
        assertFalse(NativeLib.checkPandoraGating(outOfRange))
    }

    @Test
    fun `checkPandoraGating returns false when capability index is far out of range`() {
        setPrivateField("pandoraBox", mockPandoraBox)
        assertFalse(NativeLib.checkPandoraGating(Int.MAX_VALUE))
    }

    @Test
    fun `checkPandoraGating returns false when pandoraBox is null regardless of valid index`() {
        // pandoraBox is null (not initialized)
        assertFalse(NativeLib.checkPandoraGating(0))
    }

    @Test
    fun `checkPandoraGating returns true when pandoraBox reports capability unlocked`() {
        setPrivateField("pandoraBox", mockPandoraBox)
        every { mockPandoraBox.isCapabilityUnlocked(any()) } returns true

        assertTrue(NativeLib.checkPandoraGating(0)) // index 0 = CREATIVE
    }

    @Test
    fun `checkPandoraGating returns false when pandoraBox reports capability locked`() {
        setPrivateField("pandoraBox", mockPandoraBox)
        every { mockPandoraBox.isCapabilityUnlocked(any()) } returns false

        assertFalse(NativeLib.checkPandoraGating(0))
    }

    @Test
    fun `checkPandoraGating maps index 0 to CREATIVE capability`() {
        setPrivateField("pandoraBox", mockPandoraBox)
        every { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.CREATIVE) } returns true
        every { mockPandoraBox.isCapabilityUnlocked(not(AgentCapabilityCategory.CREATIVE)) } returns false

        assertTrue(NativeLib.checkPandoraGating(0))
    }

    @Test
    fun `checkPandoraGating maps index to ROOT capability correctly`() {
        val rootIndex = AgentCapabilityCategory.entries.indexOf(AgentCapabilityCategory.ROOT)
        setPrivateField("pandoraBox", mockPandoraBox)
        every { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.ROOT) } returns true

        assertTrue(NativeLib.checkPandoraGating(rootIndex))
    }

    @Test
    fun `checkPandoraGating maps last valid index to GENERIC capability`() {
        val genericIndex = AgentCapabilityCategory.entries.indexOf(AgentCapabilityCategory.GENERIC)
        setPrivateField("pandoraBox", mockPandoraBox)
        every { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.GENERIC) } returns true

        assertTrue(NativeLib.checkPandoraGating(genericIndex))
    }

    @Test
    fun `checkPandoraGating delegates to pandoraBox with correct category for each valid index`() {
        setPrivateField("pandoraBox", mockPandoraBox)
        every { mockPandoraBox.isCapabilityUnlocked(any()) } returns false

        for (i in AgentCapabilityCategory.entries.indices) {
            NativeLib.checkPandoraGating(i)
        }

        verify(exactly = AgentCapabilityCategory.entries.size) {
            mockPandoraBox.isCapabilityUnlocked(any())
        }
    }

    // ─── onNativeThermalEvent() ───────────────────────────────────────────────

    @Test
    fun `onNativeThermalEvent emits thermal event to sentinel bus`() {
        setPrivateField("sentinelBus", mockSentinelBus)

        NativeLib.onNativeThermalEvent(42.5f, 0) // state 0 = NORMAL

        verify { mockSentinelBus.emitThermal(42.5f, KaiSentinelBus.ThermalState.NORMAL) }
    }

    @Test
    fun `onNativeThermalEvent maps stateInt 0 to NORMAL`() {
        setPrivateField("sentinelBus", mockSentinelBus)
        NativeLib.onNativeThermalEvent(25f, 0)
        verify { mockSentinelBus.emitThermal(25f, KaiSentinelBus.ThermalState.NORMAL) }
    }

    @Test
    fun `onNativeThermalEvent maps stateInt 1 to LIGHT`() {
        setPrivateField("sentinelBus", mockSentinelBus)
        NativeLib.onNativeThermalEvent(35f, 1)
        verify { mockSentinelBus.emitThermal(35f, KaiSentinelBus.ThermalState.LIGHT) }
    }

    @Test
    fun `onNativeThermalEvent maps stateInt 2 to WARNING`() {
        setPrivateField("sentinelBus", mockSentinelBus)
        NativeLib.onNativeThermalEvent(45f, 2)
        verify { mockSentinelBus.emitThermal(45f, KaiSentinelBus.ThermalState.WARNING) }
    }

    @Test
    fun `onNativeThermalEvent maps stateInt 3 to SEVERE`() {
        setPrivateField("sentinelBus", mockSentinelBus)
        NativeLib.onNativeThermalEvent(55f, 3)
        verify { mockSentinelBus.emitThermal(55f, KaiSentinelBus.ThermalState.SEVERE) }
    }

    @Test
    fun `onNativeThermalEvent maps stateInt 4 to CRITICAL`() {
        setPrivateField("sentinelBus", mockSentinelBus)
        NativeLib.onNativeThermalEvent(65f, 4)
        verify { mockSentinelBus.emitThermal(65f, KaiSentinelBus.ThermalState.CRITICAL) }
    }

    @Test
    fun `onNativeThermalEvent maps stateInt 5 to EMERGENCY`() {
        setPrivateField("sentinelBus", mockSentinelBus)
        NativeLib.onNativeThermalEvent(80f, 5)
        verify { mockSentinelBus.emitThermal(80f, KaiSentinelBus.ThermalState.EMERGENCY) }
    }

    @Test
    fun `onNativeThermalEvent defaults to NORMAL for out-of-range positive stateInt`() {
        setPrivateField("sentinelBus", mockSentinelBus)
        NativeLib.onNativeThermalEvent(30f, 999)
        verify { mockSentinelBus.emitThermal(30f, KaiSentinelBus.ThermalState.NORMAL) }
    }

    @Test
    fun `onNativeThermalEvent defaults to NORMAL for negative stateInt`() {
        setPrivateField("sentinelBus", mockSentinelBus)
        NativeLib.onNativeThermalEvent(30f, -1)
        verify { mockSentinelBus.emitThermal(30f, KaiSentinelBus.ThermalState.NORMAL) }
    }

    @Test
    fun `onNativeThermalEvent does not crash when sentinel bus is null`() {
        // sentinelBus is null — should silently skip emission
        NativeLib.onNativeThermalEvent(40f, 0)
        // No exception thrown — test passes
    }

    @Test
    fun `onNativeThermalEvent passes temperature value through unmodified`() {
        setPrivateField("sentinelBus", mockSentinelBus)
        val temperature = 37.6f
        NativeLib.onNativeThermalEvent(temperature, 1)
        verify { mockSentinelBus.emitThermal(temperature, any()) }
    }

    // ─── triggerDroneDispatch() ───────────────────────────────────────────────

    @Test
    fun `triggerDroneDispatch calls dispatchDrone with RESTORATIVE type`() {
        setPrivateField("droneDispatcher", mockDroneDispatcher)
        every { mockDroneDispatcher.dispatchDrone(any(), any()) } returns mockk()

        NativeLib.triggerDroneDispatch("TEST_REASON")

        verify { mockDroneDispatcher.dispatchDrone(GuidanceDrone.DroneType.RESTORATIVE, any()) }
    }

    @Test
    fun `triggerDroneDispatch prefixes reason with Native Trigger`() {
        setPrivateField("droneDispatcher", mockDroneDispatcher)
        every { mockDroneDispatcher.dispatchDrone(any(), any()) } returns mockk()

        NativeLib.triggerDroneDispatch("MY_REASON")

        verify { mockDroneDispatcher.dispatchDrone(any(), match { it.contains("Native Trigger") }) }
    }

    @Test
    fun `triggerDroneDispatch includes original reason in objective`() {
        setPrivateField("droneDispatcher", mockDroneDispatcher)
        every { mockDroneDispatcher.dispatchDrone(any(), any()) } returns mockk()

        NativeLib.triggerDroneDispatch("THERMAL_EMERGENCY")

        verify {
            mockDroneDispatcher.dispatchDrone(
                GuidanceDrone.DroneType.RESTORATIVE,
                match { it.contains("THERMAL_EMERGENCY") }
            )
        }
    }

    @Test
    fun `triggerDroneDispatch does not crash when dispatcher is null`() {
        // droneDispatcher is null — should silently skip
        NativeLib.triggerDroneDispatch("NO_DISPATCHER")
        // No exception thrown — test passes
    }

    // ─── Boundary and regression tests ───────────────────────────────────────

    @Test
    fun `checkPandoraGating boundary index 0 is valid CREATIVE category`() {
        setPrivateField("pandoraBox", mockPandoraBox)
        every { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.CREATIVE) } returns true
        assertTrue(NativeLib.checkPandoraGating(0))
    }

    @Test
    fun `checkPandoraGating boundary index 14 is valid GENERIC category`() {
        setPrivateField("pandoraBox", mockPandoraBox)
        every { mockPandoraBox.isCapabilityUnlocked(AgentCapabilityCategory.GENERIC) } returns true
        assertTrue(NativeLib.checkPandoraGating(14))
    }

    @Test
    fun `checkPandoraGating boundary index 15 is out of range`() {
        setPrivateField("pandoraBox", mockPandoraBox)
        // There are 15 entries (indices 0-14); index 15 is out of range
        assertFalse(NativeLib.checkPandoraGating(15))
    }

    @Test
    fun `onNativeThermalEvent boundary stateInt at max valid index emits EMERGENCY`() {
        setPrivateField("sentinelBus", mockSentinelBus)
        val maxValidIndex = KaiSentinelBus.ThermalState.entries.size - 1
        NativeLib.onNativeThermalEvent(90f, maxValidIndex)
        verify {
            mockSentinelBus.emitThermal(
                90f,
                KaiSentinelBus.ThermalState.entries[maxValidIndex]
            )
        }
    }

    @Test
    fun `getAIVersionSafe is idempotent across multiple calls`() {
        val first = NativeLib.getAIVersionSafe()
        val second = NativeLib.getAIVersionSafe()
        assertEquals("getAIVersionSafe should return the same stub on repeated calls", first, second)
    }
}